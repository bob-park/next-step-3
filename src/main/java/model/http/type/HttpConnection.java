package model.http.type;

import java.util.Arrays;

public enum HttpConnection {
  KEEP_ALIVE,
  ;

  public static final HttpConnection parse(String connection) {
    return Arrays.stream(HttpConnection.values())
        .filter(httpConnection -> httpConnection.toString().equals(connection))
        .findAny()
        .orElse(KEEP_ALIVE);
  }
}
