package http.session;

import http.HttpSessions;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

  private final String sessionId;
  private static final Map<String, Object> SESSION_ATTRIBUTES = new HashMap<>();

  public HttpSession(String sessionId) {
    this.sessionId = sessionId;
  }

  public void addAttribute(String key, Object value) {
    SESSION_ATTRIBUTES.put(key, value);
  }

  public Object getAttribute(String key) {
    return SESSION_ATTRIBUTES.get(key);
  }

  public void invalidate(){
    HttpSessions.remove(sessionId);
  }
}
