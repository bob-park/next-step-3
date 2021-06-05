package model.http.header;

import java.util.ArrayList;
import java.util.List;

public class HttpHeaders {

  private final List<MediaType> accepts = new ArrayList<>();

  public HttpHeaders() {}

  public HttpHeaders addAccept(MediaType accept) {
    this.accepts.add(accept);
    return this;
  }
}
