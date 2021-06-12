package model.http.header;

import util.CommonUtils;

import java.util.ArrayList;
import java.util.List;

public class HttpCookies {

  private final List<HttpCookie> cookies = new ArrayList<>();

  public HttpCookies addCookie(HttpCookie cookie) {
    this.cookies.add(cookie);
    return this;
  }

  public HttpCookies addCookie(String key, String value) {
    return addCookie(key, value, null);
  }

  public HttpCookies addCookie(String key, String value, String path) {
    return addCookie(new HttpCookie(key, value, path));
  }

  public List<HttpCookie> getCookies() {
    return cookies;
  }

  public HttpCookie getCookie(String key) {
    return cookies.stream()
        .filter(cookie -> CommonUtils.equals(cookie.getKey(), key))
        .findAny()
        .orElse(null);
  }
}
