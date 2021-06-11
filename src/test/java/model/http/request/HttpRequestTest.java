package model.http.request;

import model.http.header.HttpHeader;
import model.http.type.HttpConnection;
import model.http.type.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HttpRequestTest {

  private final String testDirectory = "./src/test/resources";

  @DisplayName("http get test")
  @ParameterizedTest
  @ValueSource(strings = "HTTP_GET.txt")
  void requestGet(String fileName) throws IOException {

    InputStream in = new FileInputStream(testDirectory + "/" + fileName);

    HttpRequest request = HttpRequest.builder(in).build();

    assertAll(
        () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
        () -> assertThat(request.getRequestURI()).isEqualTo("/user/create"),
        () ->
            assertThat(request.getHeaders().getHeader(HttpHeader.CONNECTION.getName()))
                .isEqualTo("keep-alive"),
        () -> assertThat(request.getRequestParam("userId")).isEqualTo("javajigi"));
  }
}
