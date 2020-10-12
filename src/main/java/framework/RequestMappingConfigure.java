package framework;

import java.util.ArrayList;
import java.util.List;

public class RequestMappingConfigure {

  private final List<RequestMapping> requestMappingList = new ArrayList<>();

  public RequestMappingConfigure addRequestMapping(RequestMapping requestMapping) {
    requestMappingList.add(requestMapping);
    return this;
  }

  public List<RequestMapping> getRequestMappingList() {
    return requestMappingList;
  }
}
