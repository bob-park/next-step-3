package controller.error;

import controller.impl.AbstractController;
import model.http.request.HttpRequest;
import model.http.response.HttpResponse;
import model.http.type.HttpStatus;

public class InternalServerErrorController extends AbstractController {

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) {
    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected void doPost(HttpRequest request, HttpResponse response) {
    response.sendError(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
