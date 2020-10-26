package controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public class ResourceController extends AbstractController {

  @Override
  protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    response.forward(request.getRequestPath());
  }
}
