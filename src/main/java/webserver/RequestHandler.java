package webserver;

import controller.Controller;
import controller.ResourceController;
import controller.error.InternalServerErrorController;
import controller.error.MethodNotAllowedErrorController;
import controller.error.NotFoundErrorController;
import controller.user.CreateUserController;
import controller.user.LoginController;
import controller.user.UserListController;
import exception.NotFoundException;
import exception.http.method.NotSupportHttpMethodException;
import model.http.header.HttpCookie;
import model.http.request.HttpRequest;
import model.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import util.FilenameUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static util.CommonUtils.isEmpty;
import static util.CommonUtils.isNotBlank;

public class RequestHandler extends Thread {

  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  private final Socket connection;

  private static final Map<String, Controller> CONTROLLER_MAP = new HashMap<>();
  private static final ResourceController RESOURCE_CONTROLLER = new ResourceController();
  private static final NotFoundErrorController NOT_FOUND_CONTROLLER = new NotFoundErrorController();
  private static final InternalServerErrorController INTERNAL_SERVER_CONTROLLER =
      new InternalServerErrorController();
  private static final MethodNotAllowedErrorController METHOD_NOT_ALLOWED_CONTROLLER =
      new MethodNotAllowedErrorController();

  public RequestHandler(Socket connectionSocket) {
    this.connection = connectionSocket;

    var userService = new UserService();

    CONTROLLER_MAP.put("/user/create", new CreateUserController(userService));
    CONTROLLER_MAP.put("/user/login", new LoginController(userService));
    CONTROLLER_MAP.put("/user/list", new UserListController(userService));
  }

  @Override
  public void run() {
    log.debug(
        "New Client Connect! Connected IP : {}, Port : {}",
        connection.getInetAddress(),
        connection.getPort());

    try (var in = connection.getInputStream();
        var out = connection.getOutputStream()) {

      var httpRequest = HttpRequest.builder(in).build();

      requestMapping(httpRequest, HttpResponse.builder(httpRequest, out).build());

    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void requestMapping(HttpRequest request, HttpResponse response) {

    String uri = request.getRequestURI();

    log.debug("method : {}, uri: {}", request.getMethod(), uri);

    var responseBuilder = HttpResponse.builder(response);

    // * session id 가 존재 하지 않는 경우
    if (isEmpty(request.getHeaders().getCookie(HttpCookie.SESSION_COOKIE_NAME))) {
      String sessionId = generateSessionId();

      var requestHeader = request.getHeaders();
      requestHeader.getCookies().addCookie(HttpCookie.SESSION_COOKIE_NAME, sessionId, "/");
      responseBuilder.addCookie(HttpCookie.SESSION_COOKIE_NAME, sessionId, "/");
    }

    response = responseBuilder.build();

    boolean isResource = isNotBlank(FilenameUtils.getExtension(uri));

    if (isResource) {
      RESOURCE_CONTROLLER.service(request, response);
      return;
    }

    try {
      getController(uri).service(request, response);
    } catch (NotFoundException e) {
      log.warn(e.getMessage());
      NOT_FOUND_CONTROLLER.service(request, response);
    } catch (NotSupportHttpMethodException e) {
      log.warn(e.getMessage());
      METHOD_NOT_ALLOWED_CONTROLLER.service(request, response);
    } catch (Exception e) {
      log.error("Server Error - {}", e.getMessage(), e);
      INTERNAL_SERVER_CONTROLLER.service(request, response);
    }
  }

  private Controller getController(String uri) {

    var controller = CONTROLLER_MAP.get(uri);

    if (isEmpty(controller)) {
      throw new NotFoundException(uri);
    }

    return controller;
  }

  private String generateSessionId() {
    return UUID.randomUUID().toString();
  }
}
