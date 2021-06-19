package model.http.request;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

  private final Map<String, Object> values = new HashMap<>();

  private final String sessionId;

  public HttpSession(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setAttribute(String key, Object value) {
    values.put(key, value);
  }

  public Object getAttribute(String key) {
    return values.get(key);
  }

  public void removeAttribute(String key) {
    values.remove(key);
  }

  public void invalidate() {
    HttpSessions.remove(sessionId);
  }
}
