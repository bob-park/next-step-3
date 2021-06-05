package model.http.header;

import java.util.Arrays;

public enum HttpVersion {
  HTTP_1_1,
  ;

  public static HttpVersion parse(String versionString) {
    return Arrays.stream(HttpVersion.values())
        .filter(version -> version.toString().equals(versionString))
        .findAny()
        .orElse(HTTP_1_1);
  }
}
