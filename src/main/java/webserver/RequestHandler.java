package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import model.http.header.*;
import model.http.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {

  private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

  private static final String HTTP_REQUEST_LINE_SEPARATOR_REGEX = "\\s";
  private static final String HTTP_HEADER_ACCEPT_SEPARATOR_REGEX = ";";
  private static final String HTTP_REQUEST_QUERY_STRING_SEPARATOR = "\\?";

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

      var httpRequest = readRequest(in);

      var dos = new DataOutputStream(out);

      byte[] body;

      if ("/index.html".equals(httpRequest.getRequestURI())
          && HttpMethod.GET == httpRequest.getMethod()) {
        body = getResponseResourceData(httpRequest.getRequestURI());
      } else if ("/user/form.html".equals(httpRequest.getRequestURI())
          && HttpMethod.GET == httpRequest.getMethod()) {
        body = getResponseResourceData(httpRequest.getRequestURI());
      } else if ("/user/create".equals(httpRequest.getRequestURI())) {

        // TODO 사용자 추가
        httpRequest.getRequestParams().forEach((key, value) -> log.debug("{} : {}", key, value));

        body = new byte[0];
      } else {
        body = "Hello World".getBytes();
      }

      response200Header(dos, body.length);
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

  private void responseBody(DataOutputStream dos, byte[] body) {
    try {
      dos.write(body, 0, body.length);
      dos.flush();
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  private HttpRequest readRequest(InputStream in) throws IOException {

    var requestBuilder = HttpRequest.builder();

    var headers = new HttpHeaders();

    BufferedReader br = new BufferedReader(new InputStreamReader(in));

    String data;

    while (!checkRequestEnd(data = br.readLine())) {

      log.debug("request : {}", data);
      String[] tokens = data.split(HTTP_REQUEST_LINE_SEPARATOR_REGEX);

      // request line
      if (tokens.length == 3) {

        String methodStr = tokens[0];
        String uriStr = tokens[1];
        String versionStr = tokens[2];

        String[] uriTokens = uriStr.split(HTTP_REQUEST_QUERY_STRING_SEPARATOR);

        requestBuilder
            .method(HttpMethod.parse(methodStr))
            .requestURI(uriTokens[0])
            .version(HttpVersion.parse(versionStr));

        if (uriTokens.length > 1) {
          Map<String, String> requestParamsMap = HttpRequestUtils.parseQueryString(uriTokens[1]);

          requestParamsMap.forEach(requestBuilder::addRequestParam);
        }

      } else {
        var headerPair = HttpRequestUtils.parseHeader(data);

        if (headerPair == null) {
          continue;
        }

        var header = HttpHeader.parse(headerPair.getKey());

        switch (header) {
          case HOST:
            requestBuilder.requestHost(headerPair.getValue());
            break;

          case CONNECTION:
            requestBuilder.connection(HttpConnection.parse(headerPair.getValue()));
            break;
          case ACCEPT:
            String[] acceptTokens = headerPair.getValue().split(HTTP_REQUEST_LINE_SEPARATOR_REGEX);

            for (String acceptToken : acceptTokens) {
              headers.addAccept(MediaType.parse(acceptToken));
            }

            break;
          default:
            break;
        }
      }
    }

    requestBuilder.headers(headers);

    return requestBuilder.build();
  }

  private boolean checkRequestEnd(String data) {
    return data == null || "".equals(data) || "\r\n".equals(data);
  }
}
