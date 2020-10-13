package http;

import constants.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HttpRequest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private HttpMethod method;
  private String requestPath;
  private String queryString;
  private Map<String, String> requestParam;
  private HttpHeaders headers;
  private String body;

  public HttpRequest(InputStream in) throws IOException {

    StringBuilder requestHeaders = new StringBuilder();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

    String line = null;

    while (checkRequestEnd(line = bufferedReader.readLine())) {
      requestHeaders.append(line).append("\n");
    }

    logger.debug("Request Headers : \n{}", requestHeaders);

    if (!requestHeaders.toString().isBlank()) {
      String[] lines = requestHeaders.toString().split("\n");

      String[] requestItem = lines[0].split(" ");
      String headerString = requestHeaders.substring(requestHeaders.indexOf("\n") + 1);

      String requestUrl = requestItem[1];

      int indexOfQueryString = requestUrl.indexOf("?");

      String path = requestUrl;
      String param = null;

      if (indexOfQueryString > 0) {
        path = requestUrl.substring(0, requestUrl.indexOf("?"));
        param = requestUrl.substring(requestUrl.indexOf("?") + 1);
      }

      setMethod(HttpMethod.findByMethod(requestItem[0]));
      setRequestPath(path);
      setQueryString(param);
      setRequestParam(HttpRequestUtils.parseQueryString(param));
      setHeaders(new HttpHeaders(headerString));
      setBody(IOUtils.readData(bufferedReader, headers.getContentLength()));
    }
  }

  public HttpMethod getMethod() {
    return method;
  }

  public void setMethod(HttpMethod method) {
    this.method = method;
  }

  public String getRequestPath() {
    return requestPath;
  }

  public void setRequestPath(String requestPath) {
    this.requestPath = requestPath;
  }

  public String getQueryString() {
    return queryString;
  }

  public void setQueryString(String queryString) {
    this.queryString = queryString;
  }

  public Map<String, String> getRequestParam() {
    return requestParam;
  }

  public void setRequestParam(Map<String, String> requestParam) {
    this.requestParam = requestParam;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public void setHeaders(HttpHeaders headers) {
    this.headers = headers;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  private boolean checkRequestEnd(String line) {

    return line != null && !line.isBlank() && !line.equals("\r\n");
  }
}
