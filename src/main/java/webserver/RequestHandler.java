package webserver;

import http.HttpCookies;
import http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.user.UserService;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RequestHandler extends Thread {
  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  private final Socket connection;

  /*
   * Service
   */
  private final UserService userService;

  public RequestHandler(Socket connectionSocket) {
    this.connection = connectionSocket;
    this.userService = new UserService();
  }

  public void run() {
    log.debug(
        "New Client Connect! Connected IP : {}, Port : {}",
        connection.getInetAddress(),
        connection.getPort());

    try (InputStream in = connection.getInputStream();
        OutputStream out = connection.getOutputStream()) {

      DataOutputStream dos = new DataOutputStream(out);

      HttpRequest httpRequest = new HttpRequest(in);

      byte[] body = new byte[0];

      if ("/".equals(httpRequest.getRequestPath())) {
        body = "Hello World".getBytes();
        response200Header(dos, body.length);
      } else if ("/index.html".equals(httpRequest.getRequestPath())) {
        body = Files.readAllBytes(Paths.get("./webapp/index.html"));
        response200Header(dos, body.length);
      } else if ("/user/form.html".equals(httpRequest.getRequestPath())) {
        body = Files.readAllBytes(Paths.get("./webapp/user/form.html"));
        response200Header(dos, body.length);
      } else if ("/user/create".equals(httpRequest.getRequestPath())) {
        userService.saveUser(httpRequest);
        response302Header(dos, "/index.html");
      } else if ("/user/login.html".equals(httpRequest.getRequestPath())) {
        body = Files.readAllBytes(Paths.get("./webapp/user/login.html"));
        response200Header(dos, body.length);
      } else if ("/user/login".equals(httpRequest.getRequestPath())) {

        boolean logined = userService.login(httpRequest);

        HttpCookies cookies = new HttpCookies();
        cookies.setPath("/");
        cookies.addCookie("logined", String.valueOf(logined));

        String redirect = logined ? "/index.html" : "/user/login_failed.html";

        response302Header(dos, redirect, cookies);

      } else if ("/user/login_failed.html".equals(httpRequest.getRequestPath())) {
        body = Files.readAllBytes(Paths.get("./webapp/user/login_failed.html"));
        response200Header(dos, body.length);
      }

      responseBody(dos, body);
    } catch (IOException e) {
      log.error(e.getMessage());
    } catch (Exception e) {
      log.error("Exception - {}", e.getMessage(), e);
    }
  }

  private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
    response200Header(dos, lengthOfBodyContent, null);
  }

  private void response200Header(
      DataOutputStream dos, int lengthOfBodyContent, HttpCookies cookies) {
    try {
      dos.writeBytes("HTTP/1.1 200 OK \r\n");
      dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
      dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");

      if (cookies != null) {
        dos.writeBytes("Set-Cookie: " + cookies.toString() + "\r\n");
      }

      dos.writeBytes("\r\n");
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private void response302Header(DataOutputStream dos, String redirect) {
    response302Header(dos, redirect, null);
  }

  private void response302Header(DataOutputStream dos, String redirect, HttpCookies cookies) {
    try {
      dos.writeBytes("HTTP/1.1 302 Redirect \r\n");

      if (cookies != null) {
        dos.writeBytes("Set-Cookie: " + cookies.toString() + "\r\n");
      }

      dos.writeBytes("Location: " + redirect + "\r\n");

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
