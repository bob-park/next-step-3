package controller.impl;

import controller.Controller;
import exception.http.method.NotSupportHttpMethodException;
import model.http.request.HttpRequest;
import model.http.response.HttpResponse;
import model.http.type.HttpMethod;

public abstract class AbstractController implements Controller {

  @Override
  public void service(HttpRequest request, HttpResponse response) {

    HttpMethod method = request.getMethod();

    if (method == HttpMethod.GET) {
      doGet(request, response);
    } else if (method == HttpMethod.POST) {
      doPost(request, response);
    } else {
      throw new NotSupportHttpMethodException(method.name());
    }
  }

  /**
   * get
   *
   * @param request {@code HttpRequest} http request
   * @param response {@code HttpResponse} http response
   */
  protected abstract void doGet(HttpRequest request, HttpResponse response);

  /**
   * post
   *
   * @param request {@code HttpRequest} http request
   * @param response {@code HttpResponse} http response
   */
  protected abstract void doPost(HttpRequest request, HttpResponse response);
}
