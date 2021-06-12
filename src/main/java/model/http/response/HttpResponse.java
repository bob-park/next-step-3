package model.http.response;

import model.http.header.HttpHeader;
import model.http.header.HttpHeaders;
import model.http.header.HttpCookie;
import model.http.header.HttpCookies;
import model.http.request.HttpRequest;
import model.http.type.HttpStatus;
import model.http.type.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static util.CommonUtils.*;

public class HttpResponse {

  private static final String WEP_APP_DIR = "./webapp";

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final HttpRequest request;
  private final DataOutputStream dos;
  private final HttpHeaders headers;

  private HttpResponse(Builder builder) {
    this(builder.request, builder.out, builder.headers);
  }

  private HttpResponse(HttpRequest request, OutputStream out, HttpHeaders headers) {
    this.request = request;
    this.dos = new DataOutputStream(out);
    this.headers = defaultIfNull(headers, new HttpHeaders());
  }

  public static Builder builder(HttpResponse response) {
    return new Builder(response);
  }

  public static Builder builder(HttpRequest request, OutputStream out) {
    return new Builder(request, out);
  }

  /**
   * resource 를 반환하는 메소드
   *
   * @param forward
   */
  public void forward(String forward) {

    try {
      send(HttpStatus.OK, null, Files.readAllBytes(Path.of(WEP_APP_DIR + forward)));
    } catch (NoSuchFileException e) {
      send(HttpStatus.NOT_FOUND, null, null);
    } catch (IOException e) {
      send(HttpStatus.INTERNAL_SERVER_ERROR, null, null);
    }
  }

  /**
   * response body 에 contents를 보내는 메소드
   *
   * @param contents
   */
  public void sendBody(String contents) {
    send(HttpStatus.OK, null, contents.getBytes());
  }

  /** response 를 보내는 메소드 */
  public void sendBody(byte[] body) {
    send(HttpStatus.OK, null, body);
  }

  /**
   * redirect 하는 메소드
   *
   * @param redirect
   */
  public void sendRedirect(String redirect) {
    send(HttpStatus.LOCATION, redirect, null);
  }

  /**
   * error 내보내는 메소드
   *
   * @param status
   */
  public void sendError(HttpStatus status) {
    send(status, null, null);
  }

  /**
   * 실제로 response 를 조합하여 보내는 메소드
   *
   * @param status
   * @param location
   */
  private void send(HttpStatus status, String location, byte[] body) {
    String contentTypeStr = null;

    Collection<MediaType> contentTypes =
        isEmpty(headers.getContentType())
            ? request.getHeaders().getAccept()
            : Collections.singletonList(headers.getContentType());

    var contentTypeBuilder = new StringBuilder();

    contentTypes.forEach(accept -> contentTypeBuilder.append(accept.getValue()).append(","));

    if (contentTypeBuilder.lastIndexOf(",") == contentTypeBuilder.length() - 1) {
      contentTypeBuilder.deleteCharAt(contentTypeBuilder.lastIndexOf(","));
    }

    contentTypeStr = contentTypeBuilder.toString();

    try {

      // * status line
      dos.writeBytes(
          String.format(
              "%s %s %s\r\n", request.getVersion(), status.getCode(), status.getMessage()));

      // * entity header field
      if (isNotBlank(location)) {
        dos.writeBytes(String.format("Location: %s\r\n", location));
      }

      dos.writeBytes(String.format("Content-Type: %s\r\n", contentTypeStr));

      if (isNotEmpty(body)) {
        dos.writeBytes(String.format("Content-Length: %s\r\n", body.length));
      }

      // * 나머지 header
      for (Map.Entry<String, String> headerEntry : headers.getHeaders().entrySet()) {
        if (isNotBlank(headerEntry.getValue())) {
          dos.writeBytes(String.format("%s: %s\r\n", headerEntry.getKey(), headerEntry.getValue()));
        }
      }

      // * Cookies
      if (!headers.getCookies().getCookies().isEmpty()) {

        var cookiesStr = new StringBuilder();

        for (HttpCookie cookie : headers.getCookies().getCookies()) {
          if (isNotBlank(cookie.getValue())) {
            cookiesStr.append(cookie.getKey()).append("=").append(cookie.getValue());

            if (isNotBlank(cookie.getPath())) {
              cookiesStr.append(";").append("Path=").append(cookie.getPath());
            }
          }
        }

        dos.writeBytes(String.format("Set-Cookie: %s\r\n", cookiesStr));
      }

      dos.writeBytes("\r\n");

      if (isNotEmpty(body)) {
        dos.write(body, 0, body.length);
      }

      dos.flush();

    } catch (IOException e) {
      logger.error(e.getMessage());
    }
  }

  public HttpRequest getRequest() {
    return request;
  }

  public OutputStream getOutputStream() {
    return dos;
  }

  public HttpHeaders getHeaders() {
    return headers;
  }

  public static class Builder {
    private final HttpRequest request;
    private final OutputStream out;

    private HttpHeaders headers;

    private Builder(HttpResponse response) {
      this.request = response.getRequest();
      this.out = response.getOutputStream();
      this.headers = response.getHeaders();
    }

    private Builder(HttpRequest request, OutputStream out) {
      this.request = request;
      this.out = out;

      this.headers = new HttpHeaders();
    }

    public Builder headers(HttpHeaders headers) {
      this.headers = headers;
      return this;
    }

    public Builder addHeader(String header, String value) {
      headers.addHeader(header, value);
      return this;
    }

    public Builder addHeader(HttpHeader header, String value) {
      return addHeader(header.getName(), value);
    }

    public Builder cookies(HttpCookies cookies) {
      headers.setCookies(cookies);
      return this;
    }

    public Builder addCookie(String key, String value, String path) {
      headers.getCookies().addCookie(key, value, path);
      return this;
    }

    public Builder addCookie(String key, String value) {
      return addCookie(key, value, request.getRequestURI());
    }

    public HttpResponse build() {
      return new HttpResponse(this);
    }
  }
}
