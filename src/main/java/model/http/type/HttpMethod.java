package model.http.type;

import exception.http.method.NotSupportHttpMethodException;

import java.util.Arrays;

public enum HttpMethod {
  GET,
  POST,
  PUT,
  DELETE,
  OPTION;

  public static HttpMethod parse(String method) {
    return Arrays.stream(HttpMethod.values())
        .filter(httpMethod -> httpMethod.toString().equals(method))
        .findAny()
        .orElseThrow(() -> new NotSupportHttpMethodException(method));
  }

  @Override
  public String toString() {
    return this.name();
  }
}
