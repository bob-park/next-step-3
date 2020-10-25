package util;

import http.HttpRequest;
import http.constants.HttpHeader;
import http.constants.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import util.HttpRequestUtils.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestUtilsTest {

  private static final String TEST_DIRECTORY = "./src/test/resources/";

  @Test
  void parseQueryString() {
    String queryString = "userId=javajigi";
    Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);

    assertThat(parameters).containsEntry("userId", "javajigi").doesNotContainKey("password");

    queryString = "userId=javajigi&password=password2";
    parameters = HttpRequestUtils.parseQueryString(queryString);

    assertThat(parameters)
        .containsEntry("userId", "javajigi")
        .containsEntry("password", "password2");
  }

  @Test
  void parseQueryString_null() {
    Map<String, String> parameters = HttpRequestUtils.parseQueryString(null);
    assertThat(parameters.isEmpty()).isEqualTo(true);

    parameters = HttpRequestUtils.parseQueryString("");
    assertThat(parameters.isEmpty()).isEqualTo(true);

    parameters = HttpRequestUtils.parseQueryString(" ");
    assertThat(parameters.isEmpty()).isEqualTo(true);
  }

  @Test
  void parseQueryString_invalid() {
    String queryString = "userId=javajigi&password";
    Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);

    assertThat(parameters).containsEntry("userId", "javajigi").doesNotContainKey("password");
  }

  @Test
  void parseCookies() {
    String cookies = "logined=true; JSessionId=1234";
    Map<String, String> parameters = HttpRequestUtils.parseCookies(cookies);

    assertThat(parameters)
        .containsEntry("logined", "true")
        .containsEntry("JSessionId", "1234")
        .doesNotContainKey("session");
  }

  @Test
  void getKeyValue() {
    Pair pair = HttpRequestUtils.getKeyValue("userId=javajigi", "=");

    assertThat(pair).isEqualTo(new Pair("userId", "javajigi"));
  }

  @Test
  void getKeyValue_invalid() {
    Pair pair = HttpRequestUtils.getKeyValue("userId", "=");

    assertThat(pair).isNull();
  }

  @Test
  void parseHeader() {
    String header = "Content-Length: 59";
    Pair pair = HttpRequestUtils.parseHeader(header);

    assertThat(pair).isEqualTo(new Pair("Content-Length", "59"));
  }
}
