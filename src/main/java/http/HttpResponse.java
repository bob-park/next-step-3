package http;

import http.constants.HttpHeader;
import http.constants.HttpMediaType;
import http.constants.HttpStatus;
import http.header.HttpHeaders;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {

  /*
   * Http Request
   */
  private final HttpRequest request;

  /*
   * Response Headers
   */
  private final HttpHeaders headers = new HttpHeaders();

  /*
   * Response Body
   */
  private final DataOutputStream outputStream;

  public HttpResponse(HttpRequest request, DataOutputStream outputStream) {
    this.request = request;
    this.outputStream = outputStream;
  }

  public HttpRequest getRequest() {
    return request;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public DataOutputStream getOutputStream() {
    return outputStream;
  }

  public HttpResponse setContentType(HttpMediaType contentType) {
    headers.addHeader(HttpHeader.GENERAL_HEADER_CONTENT_TYPE, contentType);
    return this;
  }

  public void send(byte[] body) throws IOException {
    send(HttpStatus.OK, body);
  }

  public void sendRedirect(String redirectPath) throws IOException {

    this.headers.addHeader(HttpHeader.RESPONSE_HEADER_LOCATION, redirectPath);

    send(HttpStatus.FOUND, null);
  }

  public void send(HttpStatus status, byte[] body) throws IOException {

    outputStream.writeBytes(
        String.format(
            "%s %s %s \r%n", request.getHttpVersion(), status.getCode(), status.getMessage()));

    if (body != null) {
      headers.addHeader(HttpHeader.GENERAL_HEADER_CONTENT_LENGTH, body.length);
    }

    headers
        .getHeaders()
        .forEach(
            (key, value) -> {
              try {
                outputStream.writeBytes(String.format("%s: %s\r%n", key, value));
              } catch (IOException e) {
                e.printStackTrace();
              }
            });

    if (!headers.getCookies().getCookies().isEmpty()) {
      outputStream.writeBytes(String.format("Set-Cookie: %s", headers.getCookies()));
    }

    outputStream.writeBytes("\r\n");

    if (body != null) {
      outputStream.write(body, 0, body.length);
    }

    outputStream.flush();
  }
}
