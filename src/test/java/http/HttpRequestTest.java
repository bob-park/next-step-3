package http;

import http.constants.HttpHeader;
import http.constants.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

  private static final String TEST_DIRECTORY = "./src/test/resources";

  @DisplayName("User Create Test (GET)")
  @ParameterizedTest
  @ValueSource(strings = TEST_DIRECTORY + "/user/create/Http_GET.txt")
  void request_GET(String path) throws IOException {

    InputStream in = new FileInputStream(new File(path));

    HttpRequest request = new HttpRequest(in);

    assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
    assertThat(request.getRequestPath()).isEqualTo("/user/create");
    assertThat(request.getHeaders().getHeader(HttpHeader.GENERAL_HEADER_CONNECTION))
        .isEqualTo("keep-alive");
    assertThat(request.getParameter("userId")).isEqualTo("javajigi");
  }

  @DisplayName("User Create Test (POST)")
  @ParameterizedTest
  @ValueSource(strings = TEST_DIRECTORY + "/user/create/Http_POST.txt")
  void request_post(String path) throws IOException {

    InputStream in = new FileInputStream(new File(path));

    HttpRequest request = new HttpRequest(in);

    assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
    assertThat(request.getRequestPath()).isEqualTo("/user/create");
    assertThat(request.getHeaders().getHeader(HttpHeader.GENERAL_HEADER_CONNECTION))
        .isEqualTo("keep-alive");
    assertThat(request.getParameter("userId")).isEqualTo("javajigi");
  }
}
