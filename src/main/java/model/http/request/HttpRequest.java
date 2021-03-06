package model.http.request;

import model.http.header.HttpCookie;
import model.http.header.HttpCookies;
import model.http.header.HttpHeader;
import model.http.header.HttpHeaders;
import model.http.type.HttpConnection;
import model.http.type.HttpMethod;
import model.http.type.HttpVersion;
import model.http.type.MediaType;
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

  private static final String HTTP_REQUEST_LINE_SEPARATOR_REGEX = "\\s";
  private static final String HTTP_REQUEST_QUERY_STRING_SEPARATOR = "\\?";

  private final InputStream in;

  // * Request Line
  private RequestLine requestLine;

  private final Map<String, String> requestParams = new HashMap<>();

  // * general header
  private String requestHost;

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
    return requestLine.getMethod();
  }

  public String getRequestURI() {
    return requestLine.getUri();
  }

  public Map<String, String> getRequestParams() {
    return requestParams;
  }

  public String getRequestParam(String param) {
    return requestParams.get(param);
  }

  public HttpVersion getVersion() {
    return requestLine.getVersion();
  }

  public String getRequestHost() {
    return requestHost;
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

  public HttpSession getSession() {

    return HttpSessions.getSession(
        getHeaders().getCookie(HttpCookie.SESSION_COOKIE_NAME).getValue());
  }

  private void addRequestParam(String key, String value) {
    requestParams.put(key, value);
  }

  private void addRequestParamAll(Map<String, String> params) {
    this.requestParams.putAll(params);
  }

  private void readRequest() throws IOException {

    var httpHeaders = new HttpHeaders();

    var br = new BufferedReader(new InputStreamReader(in));

    String data;

    var isRequestLine = true;

    while (!checkRequestEnd(data = br.readLine())) {

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

    if (MediaType.APPLICATION_X_WWW_FORM_URLENCODED == this.headers.getContentType()) {
      addRequestParamAll(
          HttpRequestUtils.parseQueryString(
              URLDecoder.decode(this.contents, StandardCharsets.UTF_8)));
    }
  }

  private void setRequestLine(String[] tokens) {

    String[] uriTokens = tokens[1].split(HTTP_REQUEST_QUERY_STRING_SEPARATOR);

    this.requestLine = new RequestLine(tokens[0], uriTokens[0], tokens[2]);

    if (uriTokens.length > 1) {
      Map<String, String> requestParamsMap = HttpRequestUtils.parseQueryString(uriTokens[1]);

      requestParamsMap.forEach(this::addRequestParam);
    }
  }

  private void setHeaders(HttpHeaders httpHeaders, HttpRequestUtils.Pair headerPair) {

    var header = HttpHeader.parse(headerPair.getKey());

    if (header == HttpHeader.HOST) {
      this.requestHost = headerPair.getValue();
    } else if (header == HttpHeader.COOKIE) {

      var cookies = new HttpCookies();

      Map<String, String> cookieMap = HttpRequestUtils.parseCookies(headerPair.getValue());

      for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
        cookies.addCookie(entry.getKey(), entry.getValue());
      }

      httpHeaders.setCookies(cookies);
    } else {
      httpHeaders.addHeader(headerPair.getKey(), headerPair.getValue());
    }
  }

  private String getContents(BufferedReader br, long contentLength) throws IOException {
    return IOUtils.readData(br, (int) contentLength);
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
