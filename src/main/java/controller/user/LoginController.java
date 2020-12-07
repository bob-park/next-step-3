package controller.user;

import controller.AbstractController;
import http.HttpRequest;
import http.HttpResponse;
import http.cookie.HttpCookies;
import model.User;
import service.user.UserService;

import java.io.IOException;

public class LoginController extends AbstractController {

  private final UserService userService;

  public LoginController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) throws IOException {

    User user = userService.login(request);

    if (user != null) {
      request.getSession().addAttribute("user", user);
    }

    String redirect = user != null ? "/index.html" : "/user/login_failed.html";

    response.sendRedirect(redirect);
  }
}
