package http;

import exception.http.InvalidRequestLineException;
import exception.http.InvalidRequestUriFormatException;
import http.constants.HttpMethod;
import http.constants.HttpVersion;
import http.header.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {

  private static final String REQUEST_LINE_SEPARATOR = " ";
  private static final String URI_REGEX = "([^\\?]+)(\\?.*)?";
  private static final Pattern URI_PATTERN = Pattern.compile(URI_REGEX);

  private static final int URI_PATTERN_URI_GROUP = 1;
  private static final int URI_PATTERN_QUERY_STRING_GROUP = 2;
  private static final String BEGIN_QUERY_STRING = "?";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /*
   * Request Line
   */
  private HttpMethod method;
  private String requestPath;
  private HttpVersion httpVersion;

  /*
   * Request Headers
   */
  private HttpHeaders headers;

  /*
   * Request Parameter
   */
  private final Map<String, String> params = new HashMap<>();

  /*
   * Message Body
   */
  private String body;

  public HttpRequest(InputStream in) throws IOException {

    StringBuilder requestHeaders = new StringBuilder();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

    String headerline = null;

    while (checkRequestEnd(headerline = bufferedReader.readLine())) {
      requestHeaders.append(headerline).append("\n");
    }

    logger.debug("Request Headers : \n{}", requestHeaders);

    if (requestHeaders.length() > 0) {
      String[] lines = requestHeaders.toString().split("\n");

      if (lines.length > 0) {
        setRequestLine(lines[0]);

        this.headers = new HttpHeaders(requestHeaders.substring(requestHeaders.indexOf("\n") + 1));

        setBody(bufferedReader);
      }
    }
  }

  public HttpMethod getMethod() {
    return method;
  }

  public String getRequestPath() {
    return requestPath;
  }

  public HttpVersion getHttpVersion() {
    return httpVersion;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public Map<String, String> getParams() {
    return params;
  }

  public String getBody() {
    return body;
  }

  private void setRequestLine(String requestLineStr) {
    String[] requestLines = requestLineStr.split(REQUEST_LINE_SEPARATOR);

    // check request-line format
    if (requestLines.length != 3) {
      throw new InvalidRequestLineException(requestLineStr);
    }

    this.method = HttpMethod.findByMethod(requestLines[0]);

    String requestUri = requestLines[1];

    this.httpVersion = HttpVersion.findByVersion(requestLines[2]);

    // check request-uri
    Matcher uriMatcher = URI_PATTERN.matcher(requestUri);

    if (!uriMatcher.matches()) {
      throw new InvalidRequestUriFormatException(requestUri);
    }

    this.requestPath = uriMatcher.group(URI_PATTERN_URI_GROUP);

    String queryString = uriMatcher.group(URI_PATTERN_QUERY_STRING_GROUP);

    if (queryString != null) {

      int beginQueryStringIndex = queryString.indexOf(BEGIN_QUERY_STRING);

      if (beginQueryStringIndex > 0) {
        this.params.putAll(
            HttpRequestUtils.parseQueryString(queryString.substring(beginQueryStringIndex)));
      }
    }
  }

  public void setBody(BufferedReader reader) throws IOException {

    // TODO body 가 query string format이 아닌 경우를 생각해야한다.
    this.body = IOUtils.readData(reader, (int) headers.getContentsLength());

    this.params.putAll(HttpRequestUtils.parseQueryString(this.body));
  }

  private boolean checkRequestEnd(String line) {
    return line != null && !line.isBlank() && !line.equals("\r\n");
  }
}
