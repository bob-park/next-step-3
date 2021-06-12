package webserver;

import model.User;
import model.http.header.HttpHeaders;
import model.http.request.HttpCookie;
import model.http.request.HttpResponse;
import model.http.type.HttpMethod;
import model.http.request.HttpRequest;
import model.http.type.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import util.HttpRequestUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import static util.CommonUtils.isNotBlank;
import static util.CommonUtils.isNotEmpty;

public class RequestHandler extends Thread {

  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  private final Socket connection;

  private final UserService userService;

  public RequestHandler(Socket connectionSocket) {
    this.connection = connectionSocket;

    this.userService = new UserService();
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
      var requestHeader = httpRequest.getHeaders();

      if ("/user/create".equals(httpRequest.getRequestURI())
          && HttpMethod.POST == httpRequest.getMethod()) {

        userService.save(
            new User(
                httpRequest.getRequestParam("userId"),
                httpRequest.getRequestParam("password"),
                httpRequest.getRequestParam("name"),
                httpRequest.getRequestParam("email")));

        HttpResponse.builder(httpRequest, out).build().sendRedirect("/index.html");

      } else if ("/user/login".equals(httpRequest.getRequestURI())
          && HttpMethod.POST == httpRequest.getMethod()) {

        boolean isLoggedIn =
            userService.login(
                httpRequest.getRequestParam("userId"), httpRequest.getRequestParam("password"));

        HttpResponse.builder(httpRequest, out)
            .addCookie("logined", isLoggedIn ? "true" : "false", "/")
            .build()
            .sendRedirect(isLoggedIn ? "/index.html" : "/user/login_failed.html");

      } else if ("/user/list".equals(httpRequest.getRequestURI())
          && HttpMethod.GET == httpRequest.getMethod()) {

        var httpCookie = requestHeader.getCookie("logined");

        boolean isLoggedIn = isNotEmpty(httpCookie) && Boolean.parseBoolean(httpCookie.getValue());

        var httpResponse = HttpResponse.builder(httpRequest, out).build();

        if (isLoggedIn) {
          var userListBuilder = new StringBuilder();

          userListBuilder.append(
              "<table border='1'><tr><th>userId</th><th>password</th><th>name</th><th>email</th></tr>");

          userService
              .getUserList()
              .forEach(
                  user ->
                      userListBuilder
                          .append("<tr>")
                          .append("<td>")
                          .append(user.getUserId())
                          .append("</td>")
                          .append("<td>")
                          .append(user.getPassword())
                          .append("</td>")
                          .append("<td>")
                          .append(user.getName())
                          .append("</td>")
                          .append("<td>")
                          .append(user.getEmail())
                          .append("</td>")
                          .append("</tr>"));

          userListBuilder.append("</table>");

          httpResponse.sendBody(userListBuilder.toString());

        } else {
          httpResponse.sendRedirect("/user/login.html");
        }

      } else {
        HttpResponse.builder(httpRequest, out).build().forward(httpRequest.getRequestURI());
      }

    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
