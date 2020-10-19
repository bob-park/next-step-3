package http.header;

import http.constants.HttpHeader;
import http.constants.HttpMediaType;
import http.cookie.HttpCookies;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

  private final Map<String, String> headers = new HashMap<>();

  private final HttpCookies cookies = new HttpCookies();

  public HttpHeaders(String requestHeaders) {

    String[] headerLines = requestHeaders.split("\n");

    for (String headerLine : headerLines) {

      HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(headerLine);

      if ("Cookie".equals(pair.getKey())) {
        this.cookies.addCookieAll(HttpRequestUtils.parseCookies(pair.getValue()));
      }

      addHeader(pair.getKey(), pair.getValue());
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

  public String getContentsType() {
    return getHeader(HttpHeader.GENERAL_HEADER_CONTENT_TYPE);
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

  public HttpMediaType getAccept() {

    String accepts = getHeader(HttpHeader.REQUEST_HEADER_ACCEPT);

    HttpMediaType accept = null;

    if (accepts != null) {
      accept = HttpMediaType.findByTypeName(accepts.split(",")[0]);
    }

    return accept;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public HttpCookies getCookies() {
    return cookies;
  }

  /*
   * General Headers
   */
  public String getCacheControl() {
    return getHeader("Cache-Control");
  }
}
