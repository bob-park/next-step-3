package util;

public class CommonUtils {

  private CommonUtils() {}

  public static boolean isBlank(String str) {
    if (str == null) {
      return true;
    }

    return str.isBlank();
  }

  public static boolean isNotBlank(String str) {
    return !isBlank(str);
  }

  public static boolean equalsIgnoreCase(String s1, String s2) {

    if (s1 == null) {
      return false;
    }

    if (s2 == null) {
      return false;
    }

    return s1.equalsIgnoreCase(s2);
  }
}
