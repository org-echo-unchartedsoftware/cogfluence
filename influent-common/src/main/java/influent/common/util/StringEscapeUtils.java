package influent.common.util;

/**
 * String escape utility methods to replace Apache Commons Lang StringEscapeUtils. Provides basic
 * HTML and XML escaping/unescaping.
 */
public class StringEscapeUtils {

  /**
   * Escapes special HTML characters in a String.
   *
   * @param str the String to escape
   * @return the escaped String
   */
  public static String escapeHtml4(String str) {
    if (str == null) {
      return null;
    }
    StringBuilder result = new StringBuilder(str.length() + 20);
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
        case '<':
          result.append("&lt;");
          break;
        case '>':
          result.append("&gt;");
          break;
        case '&':
          result.append("&amp;");
          break;
        case '"':
          result.append("&quot;");
          break;
        case '\'':
          result.append("&#39;");
          break;
        default:
          result.append(c);
      }
    }
    return result.toString();
  }

  /**
   * Unescapes HTML entities in a String.
   *
   * @param str the String to unescape
   * @return the unescaped String
   */
  public static String unescapeHtml4(String str) {
    if (str == null) {
      return null;
    }
    return str.replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&apos;", "'");
  }

  /**
   * Escapes special XML characters in a String.
   *
   * @param str the String to escape
   * @return the escaped String
   */
  public static String escapeXml(String str) {
    return escapeHtml4(str); // XML escaping is similar to HTML
  }

  /**
   * Unescapes XML entities in a String.
   *
   * @param str the String to unescape
   * @return the unescaped String
   */
  public static String unescapeXml(String str) {
    return unescapeHtml4(str); // XML unescaping is similar to HTML
  }

  /**
   * Escapes special Java characters in a String.
   *
   * @param str the String to escape
   * @return the escaped String
   */
  public static String escapeJava(String str) {
    if (str == null) {
      return null;
    }
    StringBuilder result = new StringBuilder(str.length() + 20);
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
        case '\b':
          result.append("\\b");
          break;
        case '\n':
          result.append("\\n");
          break;
        case '\t':
          result.append("\\t");
          break;
        case '\f':
          result.append("\\f");
          break;
        case '\r':
          result.append("\\r");
          break;
        case '\"':
          result.append("\\\"");
          break;
        case '\\':
          result.append("\\\\");
          break;
        default:
          result.append(c);
      }
    }
    return result.toString();
  }

  /**
   * Unescapes Java escape sequences in a String.
   *
   * @param str the String to unescape
   * @return the unescaped String
   */
  public static String unescapeJava(String str) {
    if (str == null) {
      return null;
    }
    StringBuilder result = new StringBuilder(str.length());
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c == '\\' && i + 1 < str.length()) {
        char next = str.charAt(i + 1);
        switch (next) {
          case 'b':
            result.append('\b');
            i++;
            break;
          case 'n':
            result.append('\n');
            i++;
            break;
          case 't':
            result.append('\t');
            i++;
            break;
          case 'f':
            result.append('\f');
            i++;
            break;
          case 'r':
            result.append('\r');
            i++;
            break;
          case '"':
            result.append('"');
            i++;
            break;
          case '\\':
            result.append('\\');
            i++;
            break;
          default:
            result.append(c);
        }
      } else {
        result.append(c);
      }
    }
    return result.toString();
  }
}
