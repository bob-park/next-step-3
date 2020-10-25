package http;

import exception.http.InvalidRequestLineException;
import exception.http.InvalidRequestUriFormatException;
import http.constants.HttpMediaType;
import http.constants.HttpMethod;
import http.constants.HttpVersion;
import http.header.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /*
   * Separator
   */
  private static final String REQUEST_LINE_SEPARATOR = " ";
  private static final String BEGIN_QUERY_STRING = "?";

  /*
   * URI
   */
  private static final String URI_REGEX = "([^\\?]+)(\\?.*)?";
  private static final int URI_PATTERN_URI_GROUP = 1;
  private static final int URI_PATTERN_QUERY_STRING_GROUP = 2;
  private static final Pattern URI_PATTERN = Pattern.compile(URI_REGEX);

  /*
   * Query String
   */
  private static final String QUERY_STRING_REGEX = "[&]?([\\w]+)(=([^&=]+))?";
  private static final int QUERY_STRING_KEY_GROUP = 1;
  private static final int QUERY_STRING_VALUE_GROUP = 3;
  private static final Pattern QUERY_STRING_PATTERN = Pattern.compile(QUERY_STRING_REGEX);

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

  public String getParameter(String name) {
    return params.get(name);
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

      if (beginQueryStringIndex >= 0) {
        this.params.putAll(
            HttpRequestUtils.parseQueryString(queryString.substring(beginQueryStringIndex + 1)));
      }
    }
  }

  public void setBody(BufferedReader reader) throws IOException {

    if (HttpMediaType.APPLICATION_FORM_URLENCODED == headers.getContentsType()) {
      this.body = IOUtils.readData(reader, (int) headers.getContentsLength());

      Matcher matcher = QUERY_STRING_PATTERN.matcher(body);

      while (matcher.find()) {
        this.params.put(
            matcher.group(QUERY_STRING_KEY_GROUP),
            URLDecoder.decode(matcher.group(QUERY_STRING_VALUE_GROUP), StandardCharsets.UTF_8));
      }
    }
  }

  private boolean checkRequestEnd(String line) {
    return line != null && !line.isBlank() && !line.equals("\r\n");
  }
}
