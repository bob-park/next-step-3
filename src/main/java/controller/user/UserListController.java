package controller.user;

import controller.impl.AbstractController;
import exception.http.method.NotSupportHttpMethodException;
import model.http.request.HttpRequest;
import model.http.response.HttpResponse;
import service.UserService;

import static util.CommonUtils.*;

public class UserListController extends AbstractController {

  private final UserService userService;

  public UserListController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {

    var session = request.getSession();

    var isLoggedIn = isNotEmpty(session.getAttribute("user"));

    if (!isLoggedIn) {
      response.sendRedirect("/user/login.html");
      return;
    }

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

    response.sendBody(userListBuilder.toString());
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {
    throw new NotSupportHttpMethodException(request.getMethod().name());
  }
}
