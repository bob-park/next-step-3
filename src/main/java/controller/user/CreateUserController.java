package controller.user;

import controller.impl.AbstractController;
import exception.http.method.NotSupportHttpMethodException;
import model.User;
import model.http.request.HttpRequest;
import model.http.response.HttpResponse;
import service.UserService;

public class CreateUserController extends AbstractController {

  private final UserService userService;

  public CreateUserController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {
    throw new NotSupportHttpMethodException(request.getMethod().name());
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {
    userService.save(
        new User(
            request.getRequestParam("userId"),
            request.getRequestParam("password"),
            request.getRequestParam("name"),
            request.getRequestParam("email")));

    response.sendRedirect("/index.html");
  }
}
