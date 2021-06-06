package model.http.header;

import java.util.*;

public class HttpHeaders {

  private static final String HTTP_HEADER_SEPARATOR = ";";

  private final Map<String, String> headers = new HashMap<>();

  public HttpHeaders addHeader(String header, String value) {
    headers.put(header, value);
    return this;
  }

  public List<MediaType> getAccept() {

    String accepts = headers.get(HttpHeader.ACCEPT.getName());

    if (accepts != null) {

      List<MediaType> result = new ArrayList<>();

      String[] tokens = accepts.split(HTTP_HEADER_SEPARATOR);

      for (String token : tokens) {
        MediaType accept = MediaType.parse(token);

        result.add(accept);
      }

      return result;
    }

    return Collections.emptyList();
  }

  public long getContentLength() {

    String contentLength = headers.get(HttpHeader.CONTENT_LENGTH.getName());

    if (contentLength == null || "".equals(contentLength)) {
      return 0;
    }

    return Long.parseLong(contentLength);
  }

  public Map<String, String> getHeaders() {
    return headers;
  }
}
