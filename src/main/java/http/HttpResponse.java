package http;

import http.constants.HttpHeader;
import http.constants.HttpMediaType;
import http.constants.HttpStatus;
import http.header.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponse {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private static final String WEBAPP_DIRECTORY = "./webapp";

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
  private HttpStatus status = HttpStatus.OK;
  private final OutputStream outputStream;
  private byte[] body;

  public HttpResponse(HttpRequest request, OutputStream outputStream) {
    this.request = request;
    this.outputStream = outputStream;
  }

  public HttpRequest getRequest() {
    return request;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public HttpResponse setContentType(HttpMediaType contentType) {
    headers.addHeader(HttpHeader.GENERAL_HEADER_CONTENT_TYPE, contentType);
    return this;
  }

  public HttpStatus status() {
    return this.status;
  }

  public HttpResponse status(HttpStatus status) {
    this.status = status;
    return this;
  }

  public HttpResponse body(byte[] body) {
    this.body = body;
    return this;
  }

  public byte[] body() {
    return this.body;
  }

  public void forward(String path) throws IOException {

    setContentType(HttpMediaType.parseMediaTypeByFileName(path));

    body(Files.readAllBytes(Paths.get(WEBAPP_DIRECTORY + path)));

    send();
  }

  public void sendRedirect(String redirectPath) throws IOException {

    this.headers.addHeader(HttpHeader.RESPONSE_HEADER_LOCATION, redirectPath);

    status(HttpStatus.FOUND).send();
  }

  public void send() throws IOException {

    writeBytes(getStatusLine());

    if (body != null) {
      headers.addHeader(HttpHeader.GENERAL_HEADER_CONTENT_LENGTH, body.length);
    }

    writeBytes(headers.toString());
    writeBytes("\r\n");

    if (body != null) {
      outputStream.write(body, 0, body.length);
    }

    outputStream.flush();

    String noneBodyResponseStr = toStringNoneBody();

    logger.debug("Response : \n{}", noneBodyResponseStr);
  }

  public String toStringNoneBody() {
    return getStatusLine() + headers.toString();
  }

  public String toString() {
    return toStringNoneBody() + "\r\n" + new String(body);
  }

  private void writeBytes(String data) throws IOException {
    this.outputStream.write(data.getBytes());
  }

  private String getStatusLine() {
    return String.format(
        "%s %s %s\r%n",
        this.request.getHttpVersion(), this.status.getCode(), this.status.getMessage());
  }
}
