package model.http.type;

import java.util.Arrays;

public enum MediaType {
  ALL("*/*"),
  TEXT_HTML("text/html"),
  TEXT_CSS("text/css"),
  TEXT_JS("text/js"),
  APPLICATION_JSON("application/json"),
  ;

  private final String value;

  MediaType(String value) {
    this.value = value;
  }

  public static MediaType parse(String mediaType) {
    return Arrays.stream(MediaType.values())
        .filter(type -> type.getValue().equals(mediaType))
        .findAny()
        .orElse(ALL);
  }

  public String getValue() {
    return value;
  }
}
