package controller;

import model.http.request.HttpRequest;
import model.http.response.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse response);

}
