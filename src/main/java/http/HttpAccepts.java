package http;

import java.util.ArrayList;
import java.util.List;

public class HttpAccepts {

  private final List<String> accepts = new ArrayList<>();

  public HttpAccepts() {}

  public HttpAccepts(String accept) {

    if (accept != null) {
      for (String token : accept.split(",")) {
        accepts.add(token.trim());
      }
    }
  }

  public List<String> getAccepts() {
    return accepts;
  }

  public String getFirst() {
    return accepts.get(0);
  }

  public boolean contain(String accept) {
    return accepts.contains(accept);
  }
}
