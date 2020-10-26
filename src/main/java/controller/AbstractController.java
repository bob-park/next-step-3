package controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public abstract class AbstractController implements Controller {

  protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
    response.send();
  }

  protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
    response.send();
  }

  @Override
  public void service(HttpRequest request, HttpResponse response) throws IOException {
    switch (request.getMethod()) {
      case GET:
        doGet(request, response);
        break;

      case POST:
        doPost(request, response);
        break;

      default:
        break;
    }
  }
}
