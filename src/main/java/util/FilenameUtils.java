package util;

public class FilenameUtils {

  private static final String EXTENSION_SEPARATOR = ".";

  public static String getExtension(String path) {
    return path.substring(path.lastIndexOf(EXTENSION_SEPARATOR) + 1);
  }
}
