package util;

import framework.HttpRequest;
import framework.RequestMapping;
import framework.RequestMappingConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestMappingUtils {

  private static final Logger logger = LoggerFactory.getLogger(RequestMappingUtils.class);

  private static final RequestMappingConfigure requestMappingConfigure =
      new RequestMappingConfigure();

  private RequestMappingUtils() {}

  public static byte[] getBody(HttpRequest httpRequest) {

    RequestMapping requestMapping =
        requestMappingConfigure.getRequestMappingList().stream()
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
  }

  public static RequestMappingConfigure getRequestMappingConfigure() {
    return requestMappingConfigure;
  }
}
