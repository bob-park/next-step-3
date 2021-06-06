package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import model.User;
import model.http.header.*;
import model.http.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {

  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  private Socket connection;

  public RequestHandler(Socket connectionSocket) {
    this.connection = connectionSocket;
  }

  public void run() {
    log.debug(
        "New Client Connect! Connected IP : {}, Port : {}",
        connection.getInetAddress(),
        connection.getPort());

    try (InputStream in = connection.getInputStream();
        OutputStream out = connection.getOutputStream()) {
      // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

      var httpRequest = HttpRequest.builder(in).build();

      var dos = new DataOutputStream(out);

      byte[] body;

      if ("/index.html".equals(httpRequest.getRequestURI())
          && HttpMethod.GET == httpRequest.getMethod()) {
        body = getResponseResourceData(httpRequest.getRequestURI());
        response200Header(dos, body.length);
      } else if ("/user/form.html".equals(httpRequest.getRequestURI())
          && HttpMethod.GET == httpRequest.getMethod()) {
        body = getResponseResourceData(httpRequest.getRequestURI());
        response200Header(dos, body.length);
      } else if ("/user/create".equals(httpRequest.getRequestURI())
          && HttpMethod.POST == httpRequest.getMethod()) {
        body = new byte[0];

        Map<String, String> requestParam =
            HttpRequestUtils.parseQueryString(httpRequest.getContents());

        var user =
            new User(
                requestParam.get("userId"),
                requestParam.get("password"),
                requestParam.get("name"),
                requestParam.get("email"));

        log.debug("user : {}", user);

        response302Header(dos);
      } else {
        body = "Hello World".getBytes();
        response200Header(dos, body.length);
      }

      responseBody(dos, body);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private byte[] getResponseResourceData(String path) throws IOException {
    return Files.readAllBytes(Path.of("./webapp" + path));
  }

  private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
    try {
      dos.writeBytes("HTTP/1.1 200 OK \r\n");
      dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
      dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
      dos.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void response302Header(DataOutputStream dos) {
    try {
      dos.writeBytes("HTTP/1.1 302 OK \r\n");
      dos.writeBytes("Location: /index.html");
      dos.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void responseBody(DataOutputStream dos, byte[] body) {
    try {
      dos.write(body, 0, body.length);
      dos.flush();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }
}
