package model.http.request;

import model.http.header.HttpConnection;
import model.http.header.HttpHeaders;
import model.http.header.HttpMethod;
import model.http.header.HttpVersion;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

  // * Request Line
  private final HttpMethod method;
  private final String requestURI;
  private final HttpVersion version;

  private final Map<String, String> requestParams;

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
        builder.requestParams,
        builder.requestHost,
        builder.connection,
        builder.headers);
  }

  private HttpRequest(
      HttpMethod method,
      String requestURI,
      HttpVersion version,
      Map<String, String> requestParams,
      String requestHost,
      HttpConnection connection,
      HttpHeaders headers) {
    this.method = method;
    this.requestURI = requestURI;
    this.version = version;
    this.requestParams = requestParams == null ? Collections.emptyMap() : requestParams;
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

  public Map<String, String> getRequestParams() {
    return requestParams;
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

    private HttpMethod method;
    private String requestURI;
    private HttpVersion version;

    private final Map<String, String> requestParams = new HashMap<>();

    private String requestHost;
    private HttpConnection connection;

    private HttpHeaders headers;

    private Builder() {}

    public Builder addRequestParam(String key, String value) {

      requestParams.put(key, value);

      return this;
    }

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
