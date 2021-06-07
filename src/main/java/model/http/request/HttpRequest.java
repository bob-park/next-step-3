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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static util.CommonUtils.isBlank;

public class HttpRequest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String HTTP_REQUEST_LINE_SEPARATOR_REGEX = "\\s";
  private static final String HTTP_HEADER_ACCEPT_SEPARATOR_REGEX = ";";
  private static final String HTTP_REQUEST_QUERY_STRING_SEPARATOR = "\\?";

  private final InputStream in;

  // * Request Line
  private HttpMethod method;
  private String requestURI;
  private HttpVersion version;

  private final Map<String, String> requestParams = new HashMap<>();

  // * general header
  private String requestHost;
  private HttpConnection connection;

  // * request header
  private HttpHeaders headers;

  // * request body
  private String contents;

  private HttpRequest(Builder builder) throws IOException {
    this(builder.in);
  }

  private HttpRequest(InputStream in) throws IOException {
    this.in = in;

    readRequest();
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

  private void addRequestParam(String key, String value) {
    requestParams.put(key, value);
  }

  private void readRequest() throws IOException {

    var httpHeaders = new HttpHeaders();

    var br = new BufferedReader(new InputStreamReader(in));

    String data;

    var isRequestLine = true;

    while (!checkRequestEnd(data = br.readLine())) {

      logger.debug("request data : {}", data);

      String[] tokens = data.split(HTTP_REQUEST_LINE_SEPARATOR_REGEX);

      // request line
      if (isRequestLine && tokens.length == 3) {
        setRequestLine(tokens);
        isRequestLine = false;
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
    return URLDecoder.decode(IOUtils.readData(br, (int) contentLength), StandardCharsets.UTF_8);
  }

  private boolean checkRequestEnd(String data) {
    return isBlank(data) || "\r\n".equals(data);
  }

  public static class Builder {

    private final InputStream in;

    private Builder(InputStream in) {
      this.in = in;
    }

    public HttpRequest build() throws IOException {
      return new HttpRequest(this);
    }
  }
}
