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
}
