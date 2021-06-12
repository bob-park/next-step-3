package controller.user;

import controller.impl.AbstractController;
import exception.http.method.NotSupportHttpMethodException;
import model.http.request.HttpRequest;
import model.http.response.HttpResponse;
import service.UserService;

public class LoginController extends AbstractController {

  private final UserService userService;

  public LoginController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {
    throw new NotSupportHttpMethodException(request.getMethod().name());
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {

    boolean isLoggedIn =
        userService.login(request.getRequestParam("userId"), request.getRequestParam("password"));

    response =
        HttpResponse.builder(response)
            .addCookie("logined", isLoggedIn ? "true" : "false", "/")
            .build();

    response.sendRedirect(isLoggedIn ? "/index.html" : "/user/login_failed.html");
  }
}
