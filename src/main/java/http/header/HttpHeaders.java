package http.header;

import http.constants.HttpHeader;
import http.constants.HttpMediaType;
import http.cookie.HttpCookies;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

  private final Map<String, String> headers = new HashMap<>();

  private HttpCookies cookies = new HttpCookies();

  public HttpHeaders() {
    this("");
  }

  public HttpHeaders(String requestHeaders) {

    if (!requestHeaders.isBlank()) {
      String[] headerLines = requestHeaders.split("\n");

      for (String headerLine : headerLines) {

        HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(headerLine);

        if ("Cookie".equals(pair.getKey())) {
          this.cookies.addCookieAll(HttpRequestUtils.parseCookies(pair.getValue()));
        }

        addHeader(pair.getKey(), pair.getValue());
      }
    }
  }

  public HttpHeaders addHeader(HttpHeader httpHeader, Object value) {
    addHeader(httpHeader.getName(), value);
    return this;
  }

  public HttpHeaders addHeader(String headerName, Object value) {

    headers.put(headerName, String.valueOf(value));

    return this;
  }

  public String getHeader(HttpHeader httpHeader) {
    return getHeader(httpHeader.getName());
  }

  public String getHeader(String headerName) {
    return headers.get(headerName);
  }

  public HttpMediaType getContentsType() {
    return HttpMediaType.findByTypeName(getHeader(HttpHeader.GENERAL_HEADER_CONTENT_TYPE));
  }

  public void setContentType(HttpMediaType mediaType) {
    addHeader(HttpHeader.GENERAL_HEADER_CONTENT_TYPE, mediaType.getType());
  }

  public long getContentsLength() {

    String contentLength = getHeader(HttpHeader.GENERAL_HEADER_CONTENT_LENGTH);

    if (contentLength == null || contentLength.isBlank()) {
      return 0;
    }

    return Long.parseLong(contentLength);
  }

  public void setContentLength(long length) {
    addHeader(HttpHeader.GENERAL_HEADER_CONTENT_LENGTH, length);
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public HttpCookies getCookies() {
    return cookies;
  }

  public void setCookies(HttpCookies cookies) {
    this.cookies = cookies;
  }

  /*
   * General Headers
   */
  public String getCacheControl() {
    return getHeader("Cache-Control");
  }

  @Override
  public String toString() {

    StringBuilder builder = new StringBuilder();

    headers.forEach((key, value) -> builder.append(String.format("%s: %s\r%n", key, value)));

    if (!getCookies().getCookies().isEmpty()) {
      builder.append(String.format("Set-Cookie: %s", getCookies()));
    }

    return builder.toString();
  }
}
