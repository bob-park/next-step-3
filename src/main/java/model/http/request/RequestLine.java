package model.http.request;

import model.http.type.HttpMethod;
import model.http.type.HttpVersion;

public class RequestLine {

  private final HttpMethod method;
  private final String uri;
  private final HttpVersion version;

  public RequestLine(String method, String uri, String version) {
    this.method = HttpMethod.parse(method);
    this.uri = uri;
    this.version = HttpVersion.parse(version);
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getUri() {
    return uri;
  }

  public HttpVersion getVersion() {
    return version;
  }
}
