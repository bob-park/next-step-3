package model.http.header;

import model.http.request.Cookie;
import model.http.request.MediaType;
import util.HttpRequestUtils;

import java.util.*;

import static util.CommonUtils.equalsIgnoreCase;

public class HttpHeaders {

  private static final String HTTP_HEADER_SEPARATOR = ";";
  private static final String HTTP_MEDIA_TYPE_SEPARATOR = ",";

  private static final String HTTP_COOKIES_KEY = "Cookie";

  private final Map<String, String> headers = new HashMap<>();

  public HttpHeaders addHeader(String header, String value) {
    headers.put(header, value);
    return this;
  }

  public MediaType getContentType() {
    String contentType = headers.get(HttpHeader.CONTENT_TYPE.getName());

    return MediaType.parse(contentType);
  }

  public Collection<MediaType> getAccept() {

    String accepts = headers.get(HttpHeader.ACCEPT.getName());

    if (accepts != null) {

      HashSet<MediaType> result = new LinkedHashSet<>();

      String[] tokens = accepts.split(HTTP_MEDIA_TYPE_SEPARATOR);

      for (String token : tokens) {
        var accept = MediaType.parse(token);

        result.add(accept);
      }

      return result;
    }

    return Collections.emptyList();
  }

  public long getContentLength() {

    String contentLength = headers.get(HttpHeader.CONTENT_LENGTH.getName());

    if (contentLength == null || "".equals(contentLength)) {
      return 0;
    }

    return Long.parseLong(contentLength);
  }

  public List<Cookie> getCookies() {

    List<Cookie> cookies = new ArrayList<>();

    String cookieStr = headers.get(HTTP_COOKIES_KEY);

    Map<String, String> cookieMap = HttpRequestUtils.parseCookies(cookieStr);

    cookieMap.forEach((key, value) -> cookies.add(new Cookie(key, value)));

    return cookies;
  }

  public Cookie getCookie(String name) {

    return getCookies().stream()
        .filter(cookie -> equalsIgnoreCase(cookie.getKey(), name))
        .findAny()
        .orElse(null);
  }

  public Map<String, String> getHeaders() {
    return headers;
  }
}
