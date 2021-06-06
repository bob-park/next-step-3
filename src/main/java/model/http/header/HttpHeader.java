package model.http.header;

import java.util.Arrays;

public enum HttpHeader {
  HOST("Host"),
  CONNECTION("Connection"),
  ACCEPT("Accept"),
  CONTENT_LENGTH("Content-Length"),
  CONTENT_TYPE("Content-Type"),
  ETC("");
  ;

  private final String name;

  HttpHeader(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public static HttpHeader parse(String name) {
    return Arrays.stream(HttpHeader.values())
        .filter(httpHeader -> httpHeader.getName().equals(name))
        .findAny()
        .orElse(ETC);
  }
}
