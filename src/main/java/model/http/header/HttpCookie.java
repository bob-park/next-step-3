package model.http.header;

public class HttpCookie {

  public static final String SESSION_COOKIE_NAME = "JSESSIONID";

  private final String key;
  private final String value;
  private final String path;

  public HttpCookie(String key, String value, String path) {
    this.key = key;
    this.value = value;
    this.path = path;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  public String getPath() {
    return path;
  }
}
