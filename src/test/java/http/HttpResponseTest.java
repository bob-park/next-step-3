package http;

import static org.assertj.core.api.Assertions.*;

import http.constants.HttpHeader;
import http.cookie.HttpCookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

class HttpResponseTest {

  private static final String TEST_DIRECTORY = "./src/test/resources";

  @DisplayName("Response Forward Test")
  @ParameterizedTest
  @ValueSource(strings = "/response/http_forward.txt")
  void forwardTest(String path) throws IOException {

    HttpResponse response =
        new HttpResponse(
            new HttpRequest(createInputStream("/user/create/Http_POST.txt")),
            createOutputStream(path));

    response.forward("/index.html");

    assertThat(getSize(path)).isEqualTo(response.toString().getBytes().length);
  }

  @DisplayName("Response Redirect Test")
  @ParameterizedTest
  @ValueSource(strings = "/response/http_redirect.txt")
  void redirectTest(String path) throws IOException {
    HttpResponse response =
        new HttpResponse(
            new HttpRequest(createInputStream("/user/create/Http_POST.txt")),
            createOutputStream(path));

    response.sendRedirect("/index.html");

    assertThat(response.getHeaders().getHeader(HttpHeader.RESPONSE_HEADER_LOCATION))
        .isEqualTo("/index.html");
  }

  @DisplayName("Response Cookies Test")
  @ParameterizedTest
  @ValueSource(strings = "/response/http_Cookies.txt")
  void cookieTest(String path) throws IOException {

    HttpResponse response =
        new HttpResponse(
            new HttpRequest(createInputStream("/user/create/Http_POST.txt")),
            createOutputStream(path));

    HttpCookies cookies = response.getHeaders().getCookies();

    cookies.setPath("/");
    cookies.addCookie("logined", true);

    response.sendRedirect("/index.html");

    assertThat(response.getHeaders().toString()).contains("Set-Cookie: logined=true");
  }

  private OutputStream createOutputStream(String path) throws FileNotFoundException {
    return new FileOutputStream(new File(TEST_DIRECTORY + path));
  }

  private InputStream createInputStream(String path) throws FileNotFoundException {
    return new FileInputStream(new File(TEST_DIRECTORY + path));
  }

  private long getSize(String path) throws IOException {
    return Files.size(Paths.get(TEST_DIRECTORY + path));
  }
}
