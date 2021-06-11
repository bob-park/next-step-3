package webserver;

import model.User;
import model.http.request.Cookie;
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

    try (InputStream in = connection.getInputStream();
        OutputStream out = connection.getOutputStream()) {
      // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

      var httpRequest = HttpRequest.builder(in).build();
      var requestHeader = httpRequest.getHeaders();

      var dos = new DataOutputStream(out);

      byte[] body;

      if ("/user/create".equals(httpRequest.getRequestURI())
          && HttpMethod.POST == httpRequest.getMethod()) {
        body = new byte[0];

        Map<String, String> requestParam =
            HttpRequestUtils.parseQueryString(httpRequest.getContents());

        userService.save(
            new User(
                requestParam.get("userId"),
                requestParam.get("password"),
                requestParam.get("name"),
                requestParam.get("email")));

        response302Header(dos, "/index.html");
      } else if ("/user/login".equals(httpRequest.getRequestURI())
          && HttpMethod.POST == httpRequest.getMethod()) {
        body = new byte[0];

        Map<String, String> requestParam =
            HttpRequestUtils.parseQueryString(httpRequest.getContents());

        if (userService.login(requestParam.get("userId"), requestParam.get("password"))) {
          response302Header(dos, "/index.html", "logined=true; Path=/");
        } else {
          response302Header(dos, "/user/login_failed.html", "logined=false; Path=/");
        }

      } else if ("/user/list".equals(httpRequest.getRequestURI())
          && HttpMethod.GET == httpRequest.getMethod()) {

        Cookie cookie = requestHeader.getCookie("logined");

        boolean isLoggedIn = isNotEmpty(cookie) && Boolean.parseBoolean(cookie.getValue());

        if (isLoggedIn) {
          var userListBuilder = new StringBuilder();

          userListBuilder.append(
              "<table><tr><th>userId</th><th>password</th><th>name</th><th>email</th></tr>");

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

          body = userListBuilder.toString().getBytes();

          response200Header(dos, body.length);

        } else {
          body = new byte[0];
          response302Header(dos, "/user/login.html");
        }

      } else {
        body = getResponseResourceData(httpRequest.getRequestURI());
        response200ResourceHeader(dos, body.length, httpRequest.getHeaders().getAccept());
      }

      responseBody(dos, body);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private byte[] getResponseResourceData(String path) throws IOException {
    return Files.readAllBytes(Path.of("./webapp" + path));
  }

  private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
    try {
      dos.writeBytes("HTTP/1.1 200 OK \r\n");
      dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
      dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
      dos.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void response200ResourceHeader(
      DataOutputStream dos, int length, Collection<MediaType> accepts) {

    var acceptStr = new StringBuilder();

    accepts.forEach(accept -> acceptStr.append(accept.getValue()).append(","));

    if (acceptStr.lastIndexOf(",") == acceptStr.length() - 1) {
      acceptStr.deleteCharAt(acceptStr.lastIndexOf(","));
    }

    try {
      dos.writeBytes("HTTP/1.1 200 OK \r\n");
      dos.writeBytes("Content-Type: " + acceptStr + "\r\n");
      dos.writeBytes("Content-Length: " + length + "\r\n");
      dos.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void response302Header(DataOutputStream dos, String redirect) {
    response302Header(dos, redirect, null);
  }

  private void response302Header(DataOutputStream dos, String redirect, String cookie) {
    try {
      dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
      dos.writeBytes("Location: " + redirect + "\r\n");

      if (isNotBlank(cookie)) {
        dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
      }

      dos.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void responseBody(DataOutputStream dos, byte[] body) {
    try {
      dos.write(body, 0, body.length);
      dos.flush();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
