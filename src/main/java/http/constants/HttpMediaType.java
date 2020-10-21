package http.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public enum HttpMediaType {
  TEXT_HTML("text/html", StandardCharsets.UTF_8),
  TEXT_CSS("text/css", StandardCharsets.UTF_8),
  APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded", null),
  ALL("*/*", StandardCharsets.UTF_8);

  private final String type;
  private final Charset charset;

  HttpMediaType(String type, Charset charset) {
    this.type = type;
    this.charset = charset;
  }

  public String getType() {
    return type;
  }

  public static HttpMediaType findByTypeName(String typeName) {
    return Arrays.stream(HttpMediaType.values())
        .filter(type -> type.getType().equalsIgnoreCase(typeName))
        .findAny()
        .orElse(ALL);
  }

  public static HttpMediaType parseMediaTypeByExtension(String extension) {
    return Arrays.stream(HttpMediaType.values())
        .filter(
            mediaType -> {
              String[] strArr = mediaType.getType().split("/");

              return strArr[1].equalsIgnoreCase(extension);
            })
        .findAny()
        .orElse(ALL);
  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder(this.type);

    if (charset != null) {
      builder.append(";charset=").append(charset.name());
    }

    return builder.toString();
  }
}
