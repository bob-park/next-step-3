package controller;

import controller.impl.AbstractController;
import exception.http.method.NotSupportHttpMethodException;
import model.http.request.HttpRequest;
import model.http.response.HttpResponse;

/** Resource 를 담당하는 Controller */
public class ResourceController extends AbstractController {

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {
    response.forward(request.getRequestURI());
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {
    // ! resource 요청은 모두 GET 요청이므로 doGet method 는 모두 예외처리
    throw new NotSupportHttpMethodException(request.getMethod().name());
  }
}
