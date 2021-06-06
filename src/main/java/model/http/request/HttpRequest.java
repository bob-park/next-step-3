package model.http.request;

import model.http.header.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

  private static final String HTTP_REQUEST_LINE_SEPARATOR_REGEX = "\\s";
  private static final String HTTP_HEADER_ACCEPT_SEPARATOR_REGEX = ";";
  private static final String HTTP_REQUEST_QUERY_STRING_SEPARATOR = "\\?";

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

  // * request body
  private final String contents;

  private HttpRequest(Builder builder) {
    this(
        builder.method,
        builder.requestURI,
        builder.version,
        builder.requestParams,
        builder.requestHost,
        builder.connection,
        builder.headers,
        builder.contents);
  }

  private HttpRequest(
      HttpMethod method,
      String requestURI,
      HttpVersion version,
      Map<String, String> requestParams,
      String requestHost,
      HttpConnection connection,
      HttpHeaders headers,
      String contents) {
    this.method = method;
    this.requestURI = requestURI;
    this.version = version;
    this.requestParams = requestParams == null ? Collections.emptyMap() : requestParams;
    this.requestHost = requestHost;
    this.connection = connection;
    this.headers = headers;
    this.contents = contents;
  }

  public static Builder builder(InputStream in) {
    return new Builder(in);
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

  public String getContents() {
    return contents;
  }

  public long getContentLength() {
    return headers.getContentLength();
  }

  public static class Builder {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final InputStream in;
    private final Map<String, String> requestParams = new HashMap<>();

    private HttpMethod method;
    private String requestURI;
    private HttpVersion version;

    private String requestHost;
    private HttpConnection connection;

    private HttpHeaders headers;

    private String contents;

    private Builder(InputStream in) {
      this.in = in;
    }

    public Builder addRequestParam(String key, String value) {

      requestParams.put(key, value);

      return this;
    }

    private void readRequest() throws IOException {

      var httpHeaders = new HttpHeaders();

      var br = new BufferedReader(new InputStreamReader(in));

      String data;

      while (!checkRequestEnd(data = br.readLine())) {

        logger.debug("request data : {}", data);

        String[] tokens = data.split(HTTP_REQUEST_LINE_SEPARATOR_REGEX);

        // request line
        if (tokens.length == 3) {
          setRequestLine(tokens);
        } else {

          // set header
          var headerPair = HttpRequestUtils.parseHeader(data);

          if (headerPair == null) {
            continue;
          }

          setHeaders(httpHeaders, headerPair);
        }
      }

      this.headers = httpHeaders;

      this.contents = getContents(br, httpHeaders.getContentLength());
    }

    private void setRequestLine(String[] tokens) {
      String methodStr = tokens[0];
      String uriStr = tokens[1];
      String versionStr = tokens[2];

      String[] uriTokens = uriStr.split(HTTP_REQUEST_QUERY_STRING_SEPARATOR);

      this.method = HttpMethod.parse(methodStr);
      this.requestURI = uriTokens[0];
      this.version = HttpVersion.parse(versionStr);

      if (uriTokens.length > 1) {
        Map<String, String> requestParamsMap = HttpRequestUtils.parseQueryString(uriTokens[1]);

        requestParamsMap.forEach(this::addRequestParam);
      }
    }

    private void setHeaders(HttpHeaders httpHeaders, HttpRequestUtils.Pair headerPair) {

      var header = HttpHeader.parse(headerPair.getKey());

      if (header == HttpHeader.HOST) {
        this.requestHost = headerPair.getValue();
      } else {
        httpHeaders.addHeader(headerPair.getKey(), headerPair.getValue());
      }
    }

    private String getContents(BufferedReader br, long contentLength) throws IOException {
      return IOUtils.readData(br, (int) contentLength);
    }

    private boolean checkRequestEnd(String data) {
      return data == null || "".equals(data) || "\r\n".equals(data);
    }

    public HttpRequest build() throws IOException {
      readRequest();

      return new HttpRequest(this);
    }
  }
}
