package model.http.type;

import java.util.Arrays;
import java.util.Optional;

public enum MediaType {
  ALL("*/*"),
  TEXT_HTML("text/html"),
  TEXT_CSS("text/css"),
  TEXT_JS("text/js"),
  APPLICATION_JSON("application/json"),
  APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
  ;

  private final String value;

  MediaType(String value) {
    this.value = value;
  }

  public static Optional<MediaType> parse(String mediaType) {
    return Arrays.stream(MediaType.values())
        .filter(type -> type.getValue().equals(mediaType))
        .findAny();
  }

  public String getValue() {
    return value;
  }
}
