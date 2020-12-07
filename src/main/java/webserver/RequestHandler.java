package webserver;

import controller.ControllerMappings;
import controller.user.CreateUserController;
import controller.user.LoginController;
import controller.user.UserListController;
import http.HttpRequest;
import http.HttpResponse;
import http.HttpSessions;
import http.cookie.HttpCookies;
import http.header.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.user.UserService;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class RequestHandler extends Thread {
  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  private final Socket connection;

  private final ControllerMappings mappings;

  public RequestHandler(Socket connectionSocket) {
    this.connection = connectionSocket;
    this.mappings = new ControllerMappings();

    UserService userService = new UserService();

    mappings
        .addController("/user/create", new CreateUserController(userService))
        .addController("/user/list", new UserListController(userService))
        .addController("/user/login", new LoginController(userService));
  }

  @Override
  public void run() {
    log.debug(
        "New Client Connect! Connected IP : {}, Port : {}",
        connection.getInetAddress(),
        connection.getPort());

    try (InputStream in = connection.getInputStream();
        OutputStream out = connection.getOutputStream()) {

      HttpRequest httpRequest = new HttpRequest(in);
      HttpResponse httpResponse = new HttpResponse(httpRequest, new DataOutputStream(out));

      HttpHeaders headers = httpRequest.getHeaders();

      HttpCookies cookies = headers.getCookies();

      String sessionId = cookies.getCookie(HttpSessions.SESSION_COOKIE_NAME);

      if (sessionId == null || HttpSessions.getSession(sessionId) == null) {

        sessionId = generateSessionId();

        httpResponse
            .getHeaders()
            .getCookies()
            .addCookie(HttpSessions.SESSION_COOKIE_NAME, sessionId);

        HttpSessions.addSession(sessionId);
      }

      httpRequest.setSession(HttpSessions.getSession(sessionId));

      mappings.find(httpRequest.getRequestPath()).service(httpRequest, httpResponse);

    } catch (IOException e) {
      log.error(e.getMessage());
    } catch (Exception e) {
      log.error("Exception - {}", e.getMessage(), e);
    }
  }

  private String generateSessionId() {
    return UUID.randomUUID().toString();
  }
}
