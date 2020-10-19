package http.constants;

import java.util.Arrays;

public enum HttpMethod {
  GET("GET"),
  POST("POST"),
  PUT("PUT"),
  DELETE("DELETE"),
  ;

  private final String method;

  HttpMethod(String method) {
    this.method = method;
  }

  public String getMethod() {
    return method;
  }

  public static HttpMethod findByMethod(String method) {
    return Arrays.stream(HttpMethod.values())
        .filter(httpMethod -> httpMethod.getMethod().equalsIgnoreCase(method))
        .findAny()
        .orElse(null);
  }
}
