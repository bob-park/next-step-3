package util;

public class CommonUtils {

  private CommonUtils() {}

  public static boolean isBlank(String str) {
    if (str == null) {
      return true;
    }

    return str.isBlank();
  }
}
