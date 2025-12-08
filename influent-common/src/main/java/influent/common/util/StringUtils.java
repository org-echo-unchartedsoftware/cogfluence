package influent.common.util;

/**
 * String utility methods to replace Apache Commons Lang StringUtils. Provides basic string
 * manipulation operations.
 */
public class StringUtils {

  /**
   * Checks if a String is empty or null.
   *
   * @param str the String to check
   * @return true if the String is null or empty
   */
  public static boolean isEmpty(String str) {
    return str == null || str.isEmpty();
  }

  /**
   * Checks if a String is not empty and not null.
   *
   * @param str the String to check
   * @return true if the String is not null and not empty
   */
  public static boolean isNotEmpty(String str) {
    return !isEmpty(str);
  }

  /**
   * Checks if a String is blank (null, empty, or whitespace only).
   *
   * @param str the String to check
   * @return true if the String is null, empty, or whitespace only
   */
  public static boolean isBlank(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * Checks if a String is not blank.
   *
   * @param str the String to check
   * @return true if the String is not null, not empty, and not whitespace only
   */
  public static boolean isNotBlank(String str) {
    return !isBlank(str);
  }

  /**
   * Trims a String, returning null if the input is null.
   *
   * @param str the String to trim
   * @return the trimmed String, or null if input was null
   */
  public static String trim(String str) {
    return str == null ? null : str.trim();
  }

  /**
   * Returns a default String if the input is null.
   *
   * @param str the String to check
   * @param defaultStr the default String to return if input is null
   * @return the input String if not null, otherwise the default String
   */
  public static String defaultString(String str, String defaultStr) {
    return str == null ? defaultStr : str;
  }

  /**
   * Returns an empty String if the input is null.
   *
   * @param str the String to check
   * @return the input String if not null, otherwise an empty String
   */
  public static String defaultString(String str) {
    return defaultString(str, "");
  }

  /**
   * Joins an array of Strings with a separator.
   *
   * @param array the array of Strings to join
   * @param separator the separator to use
   * @return the joined String
   */
  public static String join(Object[] array, String separator) {
    if (array == null) {
      return null;
    }
    if (separator == null) {
      separator = "";
    }
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < array.length; i++) {
      if (i > 0) {
        result.append(separator);
      }
      if (array[i] != null) {
        result.append(array[i]);
      }
    }
    return result.toString();
  }

  /**
   * Joins an Iterable of Strings with a separator.
   *
   * @param iterable the Iterable of Strings to join
   * @param separator the separator to use
   * @return the joined String
   */
  public static String join(Iterable<?> iterable, String separator) {
    if (iterable == null) {
      return null;
    }
    if (separator == null) {
      separator = "";
    }
    StringBuilder result = new StringBuilder();
    boolean first = true;
    for (Object obj : iterable) {
      if (!first) {
        result.append(separator);
      }
      if (obj != null) {
        result.append(obj);
      }
      first = false;
    }
    return result.toString();
  }

  /**
   * Capitalizes the first character of a String.
   *
   * @param str the String to capitalize
   * @return the capitalized String
   */
  public static String capitalize(String str) {
    if (isEmpty(str)) {
      return str;
    }
    return Character.toUpperCase(str.charAt(0)) + str.substring(1);
  }

  /**
   * Uncapitalizes the first character of a String.
   *
   * @param str the String to uncapitalize
   * @return the uncapitalized String
   */
  public static String uncapitalize(String str) {
    if (isEmpty(str)) {
      return str;
    }
    return Character.toLowerCase(str.charAt(0)) + str.substring(1);
  }

  /**
   * Repeats a String a specified number of times.
   *
   * @param str the String to repeat
   * @param repeat the number of times to repeat
   * @return the repeated String
   */
  public static String repeat(String str, int repeat) {
    if (str == null) {
      return null;
    }
    if (repeat <= 0) {
      return "";
    }
    StringBuilder result = new StringBuilder(str.length() * repeat);
    for (int i = 0; i < repeat; i++) {
      result.append(str);
    }
    return result.toString();
  }

  /**
   * Replaces all occurrences of a String within another String.
   *
   * @param text the text to search and replace in
   * @param searchString the String to search for
   * @param replacement the String to replace with
   * @return the text with replacements
   */
  public static String replace(String text, String searchString, String replacement) {
    if (isEmpty(text) || isEmpty(searchString) || replacement == null) {
      return text;
    }
    return text.replace(searchString, replacement);
  }

  /**
   * Left pads a String with a specified character.
   *
   * @param str the String to pad
   * @param size the size to pad to
   * @param padChar the character to pad with
   * @return the padded String
   */
  public static String leftPad(String str, int size, char padChar) {
    if (str == null) {
      return null;
    }
    int pads = size - str.length();
    if (pads <= 0) {
      return str;
    }
    return repeat(String.valueOf(padChar), pads) + str;
  }

  /**
   * Right pads a String with a specified character.
   *
   * @param str the String to pad
   * @param size the size to pad to
   * @param padChar the character to pad with
   * @return the padded String
   */
  public static String rightPad(String str, int size, char padChar) {
    if (str == null) {
      return null;
    }
    int pads = size - str.length();
    if (pads <= 0) {
      return str;
    }
    return str + repeat(String.valueOf(padChar), pads);
  }
}
