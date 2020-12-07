package http;

import http.session.HttpSession;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpSessions {

  /*
   * Cookie
   */
  // session cookie
  public static final String SESSION_COOKIE_NAME = "JSESSIONID";

  private static final Map<String, HttpSession> SESSIONS =
      Collections.synchronizedMap(new HashMap<>());

  public static void addSession(String sessionId) {
    SESSIONS.put(sessionId, new HttpSession(sessionId));
  }

  public static HttpSession getSession(String sessionId) {
    return SESSIONS.get(sessionId);
  }

  public static void remove(String sessionId) {
    SESSIONS.remove(sessionId);
  }
}
