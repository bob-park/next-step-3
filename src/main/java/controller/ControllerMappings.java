package controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerMappings {

  private static final Map<String, Controller> MAPPINGS = new HashMap<>();

  private static final ResourceController RESOURCE_CONTROLLER = new ResourceController();

  public ControllerMappings addController(String uri, Controller controller) {
    MAPPINGS.put(uri, controller);
    return this;
  }

  public Controller find(String uri) {
    Controller controller = MAPPINGS.get(uri);

    return controller != null ? controller : RESOURCE_CONTROLLER;
  }
}
