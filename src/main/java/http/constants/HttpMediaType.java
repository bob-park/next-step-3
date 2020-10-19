package http.constants;

import java.util.Arrays;

public enum HttpMediaType {
  TEXT_HTML("text/html"),
  TEXT_CSS("text/css"),
  ;

  private final String type;

  HttpMediaType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static HttpMediaType findByTypeName(String typeName) {
    return Arrays.stream(HttpMediaType.values())
        .filter(type -> type.getType().equalsIgnoreCase(typeName))
        .findAny()
        .orElse(null);
  }

  @Override
  public String toString() {
    return this.type;
  }
}
