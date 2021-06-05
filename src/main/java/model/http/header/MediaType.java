package model.http.header;

import java.util.Arrays;

public enum MediaType {
  APPLICATION_JSON("application/json"),
  ALL("*/*"),
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
