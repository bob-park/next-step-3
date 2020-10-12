package util;

import model.request.HttpRequest;
import model.request.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestMappingUtils {

  private static final Logger logger = LoggerFactory.getLogger(RequestMappingUtils.class);

  private static final List<RequestMapping> REQUEST_MAPPING_LIST =
      Collections.synchronizedList(new ArrayList<>());

  private RequestMappingUtils() {}

  public static byte[] getBody(InputStream in) throws IOException {

    StringBuilder requestHeaders = new StringBuilder();

    BufferedReader bufferedReader =
        new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

    String line = null;

    while (checkRequestEnd((line = bufferedReader.readLine()))) {
      requestHeaders.append(line).append("\n");
    }

    logger.debug("Request Headers : \n {}", requestHeaders);

    return getBody(new HttpRequest(requestHeaders.toString()));
  }

  public static void addRequestMapping(RequestMapping requestMapping) {
    REQUEST_MAPPING_LIST.add(requestMapping);
  }

  private static byte[] getBody(HttpRequest httpRequest) {

    RequestMapping requestMapping =
        REQUEST_MAPPING_LIST.stream()
            .filter(
                mapping ->
                    mapping.getMethod() == httpRequest.getMethod()
                        && mapping.getPath().equals(httpRequest.getUri()))
            .findAny()
            .orElse(null);

    if (requestMapping == null) {
      return new byte[0];
    }

    return requestMapping.getBody();

    //    switch (httpRequest.getUri()) {
    //      case "":
    //      case "/":
    //        if (httpRequest.getMethod() == HttpMethod.GET) {
    //          body = "Hello World".getBytes();
    //        }
    //
    //        break;
    //
    //      case "/index.html":
    //        try {
    //          body = Files.readAllBytes(Paths.get("./webapp/index.html"));
    //        } catch (IOException e) {
    //          logger.error("Error - {}", e.getMessage(), e);
    //        }
    //
    //        break;
    //
    //      default:
    //        body = new byte[0];
    //        break;
    //    }
    //
    //    return body;
  }

  private static boolean checkRequestEnd(String line) {

    return line != null && !line.isBlank() && !line.equals("\r\n");
  }
}
