package controller.user;

import controller.AbstractController;
import http.HttpRequest;
import http.HttpResponse;
import http.constants.HttpMediaType;
import model.User;
import service.user.UserService;

import java.io.IOException;

public class UserListController extends AbstractController {

  private final UserService userService;

  public UserListController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) throws IOException {

    boolean isLogin = Boolean.parseBoolean(request.getHeaders().getCookies().getCookie("logined"));

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

      response.setContentType(HttpMediaType.TEXT_HTML).body(builder.toString().getBytes()).send();
    } else {
      response.sendRedirect("/user/login.html");
    }
  }
}
