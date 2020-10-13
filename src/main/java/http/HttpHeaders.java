package http;

import util.HttpRequestUtils;

public class HttpHeaders {

  private String host;
  private String connection;
  private int contentLength;
  private String contentType;
  private String accept;
  private HttpCookies cookies;

  public HttpHeaders() {}

  public HttpHeaders(String headers) {

    String[] lines = headers.split("\n");

    for (String line : lines) {
      HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);

      if ("Host".equals(pair.getKey())) {
        setHost(pair.getValue());
      } else if ("Connection".equals(pair.getKey())) {
        setConnection(pair.getValue());
      } else if ("Content-Length".equals(pair.getKey())) {
        setContentLength(Integer.parseInt(pair.getValue()));
      } else if ("Content-Type".equals(pair.getKey())) {
        setContentType(pair.getValue());
      } else if ("Accept".equals(pair.getKey())) {
        setAccept(pair.getValue());
      } else if ("Cookie".equals(pair.getKey())) {

        setCookies(new HttpCookies(HttpRequestUtils.parseCookies(pair.getValue())));
      }
    }
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getConnection() {
    return connection;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }

  public int getContentLength() {
    return contentLength;
  }

  public void setContentLength(int contentLength) {
    this.contentLength = contentLength;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getAccept() {
    return accept;
  }

  public void setAccept(String accept) {
    this.accept = accept;
  }

  public HttpCookies getCookies() {
    return cookies;
  }

  public void setCookies(HttpCookies cookies) {
    this.cookies = cookies;
  }
}
