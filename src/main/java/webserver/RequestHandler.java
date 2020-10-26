package webserver;

import controller.ControllerMappings;
import controller.user.CreateUserController;
import controller.user.LoginController;
import controller.user.UserListController;
import http.HttpRequest;
import http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.user.UserService;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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

      mappings.find(httpRequest.getRequestPath()).service(httpRequest, httpResponse);

      //      byte[] body = null;
      //
      //      if ("/".equals(httpRequest.getRequestPath())) {
      //        body = "Hello World".getBytes();
      //        httpResponse.body(body).send();
      //
      //      } else if ("/user/create".equals(httpRequest.getRequestPath())) {
      //        userService.saveUser(httpRequest);
      //        httpResponse.sendRedirect("/index.html");
      //      } else if ("/user/login".equals(httpRequest.getRequestPath())) {
      //
      //        boolean logined = userService.login(httpRequest);
      //
      //        HttpCookies cookies = new HttpCookies();
      //
      //        cookies.addCookie("logined", logined);
      //
      //        String redirect = logined ? "/index.html" : "/user/login_failed.html";
      //
      //        httpResponse.getHeaders().setCookies(cookies);
      //        httpResponse.sendRedirect(redirect);
      //
      //      } else if ("/user/list".equals(httpRequest.getRequestPath())) {
      //
      //        boolean isLogin =
      //
      // Boolean.parseBoolean(httpRequest.getHeaders().getCookies().getCookie("logined"));
      //
      //        if (isLogin) {
      //
      //          StringBuilder builder = new StringBuilder();
      //
      //          builder
      //              .append("<table border=1>")
      //              .append("<thead>")
      //              .append("<tr>")
      //              .append("<th>#</th>")
      //              .append("<th>사용자 아이디</th>")
      //              .append("<th>이름</th>")
      //              .append("<th>이메일</th>")
      //              .append("</tr>")
      //              .append("</thead>")
      //              .append("<tbody>");
      //
      //          int index = 1;
      //          for (User user : userService.findAll()) {
      //            builder
      //                .append("<tr>")
      //                .append(String.format("<td>%s</td>", index++))
      //                .append(String.format("<td>%s</td>", user.getUserId()))
      //                .append(String.format("<td>%s</td>", user.getName()))
      //                .append(String.format("<td>%s</td>", user.getEmail()))
      //                .append("</tr>");
      //          }
      //
      //          builder.append("</tbody>").append("</table>");
      //          body = builder.toString().getBytes();
      //
      //          httpResponse.setContentType(HttpMediaType.TEXT_HTML).body(body).send();
      //
      //        } else {
      //
      //          httpResponse.sendRedirect("/user/login.html");
      //        }
      //      } else {
      //        httpResponse.forward(httpRequest.getRequestPath());
      //      }
    } catch (IOException e) {
      log.error(e.getMessage());
    } catch (Exception e) {
      log.error("Exception - {}", e.getMessage(), e);
    }
  }
}
