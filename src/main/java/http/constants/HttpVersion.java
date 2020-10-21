package http.constants;

import java.util.Arrays;

public enum HttpVersion {
  HTTP_1_1("HTTP/1.1");

  private final String version;

  HttpVersion(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  public static HttpVersion findByVersion(String version) {
    return Arrays.stream(HttpVersion.values())
        .filter(httpVersion -> httpVersion.getVersion().equalsIgnoreCase(version))
        .findAny()
        .orElse(null);
  }

  @Override
  public String toString() {
    return version;
  }
}
