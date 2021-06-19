package model.http.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static util.CommonUtils.isEmpty;

public class HttpSessions {

  private static final Map<String, HttpSession> SESSION_LIST =
      Collections.synchronizedMap(new HashMap<>());

  public static HttpSession getSession(String sessionId) {

    var session = SESSION_LIST.get(sessionId);

    if (isEmpty(session)) {

      session = new HttpSession(sessionId);

      SESSION_LIST.put(sessionId, session);
    }

    return session;
  }

  public static void remove(String sessionId) {
    SESSION_LIST.remove(sessionId);
  }
}
