package webserver;

import http.HttpRequest;
import http.HttpResponse;
import http.constants.HttpMediaType;
import http.cookie.HttpCookies;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.user.UserService;
import util.FilenameUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestHandler extends Thread {
  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  private final Socket connection;

  /*
   * Service
   */
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

      HttpRequest httpRequest = new HttpRequest(in);
      HttpResponse httpResponse = new HttpResponse(httpRequest, new DataOutputStream(out));

      byte[] body = null;

      if ("/".equals(httpRequest.getRequestPath())) {
        body = "Hello World".getBytes();
        httpResponse.send(body);

      } else if ("/user/create".equals(httpRequest.getRequestPath())) {
        userService.saveUser(httpRequest);
        httpResponse.sendRedirect("/index.html");
      } else if ("/user/login".equals(httpRequest.getRequestPath())) {

        boolean logined = userService.login(httpRequest);

        HttpCookies cookies = new HttpCookies();

        cookies.addCookie("logined", logined);

        String redirect = logined ? "/index.html" : "/user/login_failed.html";

        httpResponse.getHeaders().setCookies(cookies);
        httpResponse.sendRedirect(redirect);

      } else if ("/user/list".equals(httpRequest.getRequestPath())) {

        boolean isLogin =
            Boolean.parseBoolean(httpRequest.getHeaders().getCookies().getCookie("logined"));

        if (isLogin) {

          StringBuilder builder = new StringBuilder();

          builder
              .append("<table border=1>")
              .append("<thead>")
              .append("<tr>")
              .append("<th>#</th>")
              .append("<th>사용자 아이디</th>")
              .append("<th>이름</th>")
              .append("<th>이메일</th>")
              .append("</tr>")
              .append("</thead>")
              .append("<tbody>");

          int index = 1;
          for (User user : userService.findAll()) {
            builder
                .append("<tr>")
                .append(String.format("<td>%s</td>", index++))
                .append(String.format("<td>%s</td>", user.getUserId()))
                .append(String.format("<td>%s</td>", user.getName()))
                .append(String.format("<td>%s</td>", user.getEmail()))
                .append("</tr>");
          }

          builder.append("</tbody>").append("</table>");

          body = builder.toString().getBytes();

          httpResponse.setContentType(HttpMediaType.TEXT_HTML).send(body);

        } else {

          httpResponse.sendRedirect("/user/login.html");
        }
      } else {

        body = Files.readAllBytes(Paths.get("./webapp" + httpRequest.getRequestPath()));

        httpResponse
            .setContentType(
                HttpMediaType.parseMediaTypeByExtension(
                    FilenameUtils.getExtension(httpRequest.getRequestPath())))
            .send(body);
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    } catch (Exception e) {
      log.error("Exception - {}", e.getMessage(), e);
    }
  }
}
