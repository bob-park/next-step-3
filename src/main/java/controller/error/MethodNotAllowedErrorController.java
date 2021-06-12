package controller.error;

import controller.impl.AbstractController;
import model.http.request.HttpRequest;
import model.http.response.HttpResponse;
import model.http.type.HttpStatus;

public class MethodNotAllowedErrorController extends AbstractController {

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {
    response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {
    response.sendError(HttpStatus.METHOD_NOT_ALLOWED);
  }
}
