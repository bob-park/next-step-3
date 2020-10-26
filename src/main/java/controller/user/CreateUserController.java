package controller.user;

import controller.AbstractController;
import http.HttpRequest;
import http.HttpResponse;
import service.user.UserService;

import java.io.IOException;

public class CreateUserController extends AbstractController {

  private final UserService userService;

  public CreateUserController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
    userService.saveUser(request);
    response.sendRedirect("/index.html");
  }
}
