package util;

import static util.CommonUtils.isBlank;

public class FilenameUtils {

  private static final String FILE_EXTENSION_SEPARATOR = "\\.";

  private FilenameUtils() {}

  public static String getExtension(String filename) {

    if (isBlank(filename)) {
      return null;
    }

    String[] tokens = filename.split(FILE_EXTENSION_SEPARATOR);

    if (tokens.length < 2) {
      return null;
    }

    return tokens[tokens.length - 1];
  }
}
