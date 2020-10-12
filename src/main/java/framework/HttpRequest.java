package framework;

import constants.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequest {

  private HttpMethod method;
  private String uri;
  private String httpVersion;
  private String remoteHost;
  private String connection;
  private String accept;

  public HttpRequest(InputStream in) throws IOException {

    StringBuilder requestHeaders = new StringBuilder();

    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

    String line = null;

    while (checkRequestEnd(line = bufferedReader.readLine())) {
      requestHeaders.append(line).append("\n");
    }

    if (!requestHeaders.toString().isBlank()) {
      String[] lines = requestHeaders.toString().split("\n");

      String[] requestItem = lines[0].split(" ");

      setMethod(HttpMethod.findByMethod(requestItem[0]));
      setUri(requestItem[1]);
      setHttpVersion(requestItem[2]);

      setRemoteHost(lines[1].split(":")[1].trim());
      setConnection(lines[2].split(":")[1].trim());
      setAccept(lines[3].split(":")[1].trim());
    }
  }

  public HttpMethod getMethod() {
    return method;
  }

  public void setMethod(HttpMethod method) {
    this.method = method;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public void setHttpVersion(String httpVersion) {
    this.httpVersion = httpVersion;
  }

  public String getRemoteHost() {
    return remoteHost;
  }

  public void setRemoteHost(String remoteHost) {
    this.remoteHost = remoteHost;
  }

  public String getConnection() {
    return connection;
  }

  public void setConnection(String connection) {
    this.connection = connection;
  }

  public String getAccept() {
    return accept;
  }

  public void setAccept(String accept) {
    this.accept = accept;
  }

  private boolean checkRequestEnd(String line) {

    return line != null && !line.isBlank() && !line.equals("\r\n");
  }
}
