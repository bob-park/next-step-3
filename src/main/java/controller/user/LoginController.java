package controller.user;

import controller.AbstractController;
import http.HttpRequest;
import http.HttpResponse;
import http.cookie.HttpCookies;
import service.user.UserService;

import java.io.IOException;

public class LoginController extends AbstractController {

  private final UserService userService;

  public LoginController(UserService userService) {
    this.userService = userService;
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) throws IOException {

    boolean logined = userService.login(request);

    HttpCookies cookies = new HttpCookies();

    cookies.addCookie("logined", logined);

    String redirect = logined ? "/index.html" : "/user/login_failed.html";

    response.getHeaders().setCookies(cookies);
    response.sendRedirect(redirect);
  }
}
