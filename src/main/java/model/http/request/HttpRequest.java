package model.http.request;

import model.http.header.HttpConnection;
import model.http.header.HttpHeaders;
import model.http.header.HttpMethod;
import model.http.header.HttpVersion;

public class HttpRequest {

  // * Request Line
  private final HttpMethod method;
  private final String requestURI;
  private final HttpVersion version;

  // * general header
  private final String requestHost;
  private final HttpConnection connection;

  // * request header
  private final HttpHeaders headers;

  private HttpRequest(Builder builder) {
    this(
        builder.method,
        builder.requestURI,
        builder.version,
        builder.requestHost,
        builder.connection,
        builder.headers);
  }

  private HttpRequest(
      HttpMethod method,
      String requestURI,
      HttpVersion version,
      String requestHost,
      HttpConnection connection,
      HttpHeaders headers) {
    this.method = method;
    this.requestURI = requestURI;
    this.version = version;
    this.requestHost = requestHost;
    this.connection = connection;
    this.headers = headers;
  }

  public static Builder builder() {
    return new Builder();
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getRequestURI() {
    return requestURI;
  }

  public HttpVersion getVersion() {
    return version;
  }

  public String getRequestHost() {
    return requestHost;
  }

  public HttpConnection getConnection() {
    return connection;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public static class Builder {
    // * Request Line
    private HttpMethod method;
    private String requestURI;
    private HttpVersion version;

    // * general header
    private String requestHost;
    private HttpConnection connection;

    // * request header
    private HttpHeaders headers;

    private Builder() {}

    public Builder method(HttpMethod method) {
      this.method = method;
      return this;
    }

    public Builder requestURI(String requestURI) {
      this.requestURI = requestURI;
      return this;
    }

    public Builder version(HttpVersion version) {
      this.version = version;
      return this;
    }

    public Builder requestHost(String requestHost) {
      this.requestHost = requestHost;
      return this;
    }

    public Builder connection(HttpConnection connection) {
      this.connection = connection;
      return this;
    }

    public Builder headers(HttpHeaders headers) {
      this.headers = headers;
      return this;
    }

    public HttpRequest build() {
      return new HttpRequest(this);
    }
  }
}
