package util;

public class CommonUtils {

  private CommonUtils() {}

  public static boolean isEmpty(Object o) {
    if (o == null) {
      return true;
    }

    if (o instanceof String) {
      return isBlank((String) o);
    }

    return false;
  }

  public static boolean isNotEmpty(Object o) {
    return !isEmpty(o);
  }

  public static boolean isBlank(String str) {
    if (str == null) {
      return true;
    }

    return str.isBlank();
  }

  public static boolean isNotBlank(String str) {
    return !isBlank(str);
  }

  public static boolean equals(String s1, String s2) {
    if (s1 == null) {
      return false;
    }

    if (s2 == null) {
      return false;
    }

    return s1.equals(s2);
  }

  public static boolean equalsIgnoreCase(String s1, String s2) {
    return equals(defaultIfNull(s1, "").toUpperCase(), defaultIfNull(s2, "").toUpperCase());
  }

  public static <T> T defaultIfNull(T obj, T defaultValue) {
    if (isEmpty(obj)) {
      return defaultValue;
    }

    return obj;
  }
}
