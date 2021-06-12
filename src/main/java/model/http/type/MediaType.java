package model.http.type;

import java.util.Arrays;
import java.util.Optional;

public enum MediaType {
  ALL("*/*", false),
  TEXT_HTML("text/html", true),
  TEXT_CSS("text/css", true),
  TEXT_JS("text/js", true),
  APPLICATION_JSON("application/json", false),
  APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded", false),
  ;

  private final String value;
  private final Boolean isResource;

  MediaType(String value, Boolean isResource) {
    this.value = value;
    this.isResource = isResource;
  }

  public static Optional<MediaType> parse(String mediaType) {
    return Arrays.stream(MediaType.values())
        .filter(type -> type.getValue().equals(mediaType))
        .findAny();
  }

  public String getValue() {
    return value;
  }

  public boolean isResource() {
    return this.isResource;
  }
}
