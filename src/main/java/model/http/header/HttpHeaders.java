package model.http.header;

import model.http.request.HttpCookie;
import model.http.request.HttpCookies;
import model.http.type.MediaType;
import util.HttpRequestUtils;

import java.util.*;

import static util.CommonUtils.equalsIgnoreCase;

public class HttpHeaders {

  private static final String HTTP_HEADER_SEPARATOR = ";";
  private static final String HTTP_MEDIA_TYPE_SEPARATOR = ",";

  private final Map<String, String> headers = new HashMap<>();

  private HttpCookies cookies = new HttpCookies();

  public HttpHeaders addHeader(String header, String value) {
    headers.put(header, value);
    return this;
  }

  public Optional<MediaType> getContentType() {
    String contentType = headers.get(HttpHeader.CONTENT_TYPE.getName());

    return MediaType.parse(contentType);
  }

  public Collection<MediaType> getAccept() {

    String accepts = headers.get(HttpHeader.ACCEPT.getName());

    if (accepts != null) {

      HashSet<MediaType> result = new LinkedHashSet<>();

      String[] tokens = accepts.split(HTTP_MEDIA_TYPE_SEPARATOR);

      for (String token : tokens) {
        var accept = MediaType.parse(token).orElse(MediaType.ALL);

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

  public HttpCookies getCookies() {

    return cookies;
  }

  public HttpCookie getCookie(String name) {
    return cookies.getCookie(name);
  }

  public void setCookies(HttpCookies cookies) {
    this.cookies = cookies;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public String getHeader(String header) {
    return headers.get(header);
  }

  public String getHeader(HttpHeader header) {
    return getHeader(header.getName());
  }
}
