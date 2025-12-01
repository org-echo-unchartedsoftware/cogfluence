package oculus.aperture.common.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Escapes and unescapes Strings for Java, JavaScript, HTML and XML. Replacement for Apache Commons
 * Lang3 StringEscapeUtils.
 */
public final class StringEscapeUtils {

  private StringEscapeUtils() {
    // Utility class
  }

  // HTML and XML entity maps
  private static final Map<String, String> HTML_ENTITIES = new HashMap<>();
  private static final Map<String, String> XML_ENTITIES = new HashMap<>();

  static {
    // Basic XML entities
    XML_ENTITIES.put("&", "&amp;");
    XML_ENTITIES.put("<", "&lt;");
    XML_ENTITIES.put(">", "&gt;");
    XML_ENTITIES.put("\"", "&quot;");
    XML_ENTITIES.put("'", "&apos;");

    // HTML entities (includes XML entities)
    HTML_ENTITIES.putAll(XML_ENTITIES);
    HTML_ENTITIES.put("\u00A0", "&nbsp;");
    HTML_ENTITIES.put("\u00A1", "&iexcl;");
    HTML_ENTITIES.put("\u00A2", "&cent;");
    HTML_ENTITIES.put("\u00A3", "&pound;");
    HTML_ENTITIES.put("\u00A4", "&curren;");
    HTML_ENTITIES.put("\u00A5", "&yen;");
    HTML_ENTITIES.put("\u00A6", "&brvbar;");
    HTML_ENTITIES.put("\u00A7", "&sect;");
    HTML_ENTITIES.put("\u00A8", "&uml;");
    HTML_ENTITIES.put("\u00A9", "&copy;");
    HTML_ENTITIES.put("\u00AA", "&ordf;");
    HTML_ENTITIES.put("\u00AB", "&laquo;");
    HTML_ENTITIES.put("\u00AC", "&not;");
    HTML_ENTITIES.put("\u00AD", "&shy;");
    HTML_ENTITIES.put("\u00AE", "&reg;");
    HTML_ENTITIES.put("\u00AF", "&macr;");
    HTML_ENTITIES.put("\u00B0", "&deg;");
    HTML_ENTITIES.put("\u00B1", "&plusmn;");
    HTML_ENTITIES.put("\u00B2", "&sup2;");
    HTML_ENTITIES.put("\u00B3", "&sup3;");
    HTML_ENTITIES.put("\u00B4", "&acute;");
    HTML_ENTITIES.put("\u00B5", "&micro;");
    HTML_ENTITIES.put("\u00B6", "&para;");
    HTML_ENTITIES.put("\u00B7", "&middot;");
    HTML_ENTITIES.put("\u00B8", "&cedil;");
    HTML_ENTITIES.put("\u00B9", "&sup1;");
    HTML_ENTITIES.put("\u00BA", "&ordm;");
    HTML_ENTITIES.put("\u00BB", "&raquo;");
    HTML_ENTITIES.put("\u00BC", "&frac14;");
    HTML_ENTITIES.put("\u00BD", "&frac12;");
    HTML_ENTITIES.put("\u00BE", "&frac34;");
    HTML_ENTITIES.put("\u00BF", "&iquest;");
  }

  /** Escapes the characters in a String using HTML entities. */
  public static String escapeHtml4(final String input) {
    if (input == null) {
      return null;
    }
    final StringBuilder result = new StringBuilder(input.length() + input.length() / 10);
    for (int i = 0; i < input.length(); i++) {
      final char c = input.charAt(i);
      final String entity = HTML_ENTITIES.get(String.valueOf(c));
      if (entity != null) {
        result.append(entity);
      } else if (c > 0x7F) {
        result.append("&#").append((int) c).append(';');
      } else {
        result.append(c);
      }
    }
    return result.toString();
  }

  /**
   * Unescapes a string containing HTML entity escapes to a string containing the actual Unicode
   * characters.
   */
  public static String unescapeHtml4(final String input) {
    if (input == null) {
      return null;
    }
    final StringBuilder result = new StringBuilder(input.length());
    int i = 0;
    while (i < input.length()) {
      if (input.charAt(i) == '&') {
        final int semicolonIndex = input.indexOf(';', i);
        if (semicolonIndex > i) {
          final String entity = input.substring(i, semicolonIndex + 1);
          String replacement = null;

          // Check for numeric entities
          if (entity.startsWith("&#")) {
            try {
              final String numStr = entity.substring(2, entity.length() - 1);
              final int codePoint;
              if (numStr.startsWith("x") || numStr.startsWith("X")) {
                codePoint = Integer.parseInt(numStr.substring(1), 16);
              } else {
                codePoint = Integer.parseInt(numStr);
              }
              replacement = String.valueOf((char) codePoint);
            } catch (NumberFormatException e) {
              // Not a valid numeric entity
            }
          } else {
            // Check for named entities
            for (Map.Entry<String, String> entry : HTML_ENTITIES.entrySet()) {
              if (entry.getValue().equals(entity)) {
                replacement = entry.getKey();
                break;
              }
            }
          }

          if (replacement != null) {
            result.append(replacement);
            i = semicolonIndex + 1;
          } else {
            result.append(input.charAt(i));
            i++;
          }
        } else {
          result.append(input.charAt(i));
          i++;
        }
      } else {
        result.append(input.charAt(i));
        i++;
      }
    }
    return result.toString();
  }

  /** Escapes the characters in a String using XML entities. */
  public static String escapeXml(final String input) {
    if (input == null) {
      return null;
    }
    return escapeXml10(input);
  }

  /** Escapes the characters in a String using XML 1.0 entities. */
  public static String escapeXml10(final String input) {
    if (input == null) {
      return null;
    }
    final StringBuilder result = new StringBuilder(input.length() + input.length() / 10);
    for (int i = 0; i < input.length(); i++) {
      final char c = input.charAt(i);
      final String entity = XML_ENTITIES.get(String.valueOf(c));
      if (entity != null) {
        result.append(entity);
      } else if (isValidXml10Char(c)) {
        result.append(c);
      } else {
        // Skip invalid XML 1.0 characters
      }
    }
    return result.toString();
  }

  /** Escapes the characters in a String using XML 1.1 entities. */
  public static String escapeXml11(final String input) {
    if (input == null) {
      return null;
    }
    final StringBuilder result = new StringBuilder(input.length() + input.length() / 10);
    for (int i = 0; i < input.length(); i++) {
      final char c = input.charAt(i);
      final String entity = XML_ENTITIES.get(String.valueOf(c));
      if (entity != null) {
        result.append(entity);
      } else if (isValidXml11Char(c)) {
        result.append(c);
      } else {
        // Skip invalid XML 1.1 characters
      }
    }
    return result.toString();
  }

  /**
   * Unescapes a string containing XML entity escapes to a string containing the actual Unicode
   * characters.
   */
  public static String unescapeXml(final String input) {
    if (input == null) {
      return null;
    }
    final StringBuilder result = new StringBuilder(input.length());
    int i = 0;
    while (i < input.length()) {
      if (input.charAt(i) == '&') {
        final int semicolonIndex = input.indexOf(';', i);
        if (semicolonIndex > i) {
          final String entity = input.substring(i, semicolonIndex + 1);
          String replacement = null;

          // Check for numeric entities
          if (entity.startsWith("&#")) {
            try {
              final String numStr = entity.substring(2, entity.length() - 1);
              final int codePoint;
              if (numStr.startsWith("x") || numStr.startsWith("X")) {
                codePoint = Integer.parseInt(numStr.substring(1), 16);
              } else {
                codePoint = Integer.parseInt(numStr);
              }
              replacement = String.valueOf((char) codePoint);
            } catch (NumberFormatException e) {
              // Not a valid numeric entity
            }
          } else {
            // Check for named entities
            for (Map.Entry<String, String> entry : XML_ENTITIES.entrySet()) {
              if (entry.getValue().equals(entity)) {
                replacement = entry.getKey();
                break;
              }
            }
          }

          if (replacement != null) {
            result.append(replacement);
            i = semicolonIndex + 1;
          } else {
            result.append(input.charAt(i));
            i++;
          }
        } else {
          result.append(input.charAt(i));
          i++;
        }
      } else {
        result.append(input.charAt(i));
        i++;
      }
    }
    return result.toString();
  }

  /** Escapes the characters in a String using Java String rules. */
  public static String escapeJava(final String input) {
    if (input == null) {
      return null;
    }
    final StringBuilder result = new StringBuilder(input.length() + input.length() / 10);
    for (int i = 0; i < input.length(); i++) {
      final char c = input.charAt(i);
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
          if (c < 32 || c > 0x7f) {
            result.append("\\u");
            final String hex = Integer.toHexString(c);
            for (int j = 0; j < 4 - hex.length(); j++) {
              result.append('0');
            }
            result.append(hex);
          } else {
            result.append(c);
          }
      }
    }
    return result.toString();
  }

  /** Unescapes any Java literals found in the String. */
  public static String unescapeJava(final String input) {
    if (input == null) {
      return null;
    }
    final StringBuilder result = new StringBuilder(input.length());
    int i = 0;
    while (i < input.length()) {
      if (input.charAt(i) == '\\' && i + 1 < input.length()) {
        final char next = input.charAt(i + 1);
        switch (next) {
          case 'b':
            result.append('\b');
            i += 2;
            break;
          case 'n':
            result.append('\n');
            i += 2;
            break;
          case 't':
            result.append('\t');
            i += 2;
            break;
          case 'f':
            result.append('\f');
            i += 2;
            break;
          case 'r':
            result.append('\r');
            i += 2;
            break;
          case '\"':
            result.append('\"');
            i += 2;
            break;
          case '\\':
            result.append('\\');
            i += 2;
            break;
          case 'u':
            if (i + 5 < input.length()) {
              try {
                final String hex = input.substring(i + 2, i + 6);
                final int codePoint = Integer.parseInt(hex, 16);
                result.append((char) codePoint);
                i += 6;
              } catch (NumberFormatException e) {
                result.append(input.charAt(i));
                i++;
              }
            } else {
              result.append(input.charAt(i));
              i++;
            }
            break;
          default:
            result.append(input.charAt(i));
            i++;
        }
      } else {
        result.append(input.charAt(i));
        i++;
      }
    }
    return result.toString();
  }

  /** Checks if a character is valid in XML 1.0. */
  private static boolean isValidXml10Char(final char c) {
    return c == 0x9
        || c == 0xA
        || c == 0xD
        || (c >= 0x20 && c <= 0xD7FF)
        || (c >= 0xE000 && c <= 0xFFFD);
  }

  /** Checks if a character is valid in XML 1.1. */
  private static boolean isValidXml11Char(final char c) {
    return (c >= 0x1 && c <= 0xD7FF) || (c >= 0xE000 && c <= 0xFFFD);
  }

  /** Escapes the characters in a String using JavaScript String rules. */
  public static String escapeEcmaScript(final String input) {
    return escapeJava(input);
  }

  /** Unescapes any JavaScript literals found in the String. */
  public static String unescapeEcmaScript(final String input) {
    return unescapeJava(input);
  }

  /** Escapes the characters in a String to be suitable to pass to an SQL query. */
  public static String escapeSql(final String input) {
    if (input == null) {
      return null;
    }
    return input.replace("'", "''");
  }

  /** Escapes the characters in a String using CSV rules. */
  public static String escapeCsv(final String input) {
    if (input == null) {
      return null;
    }
    if (input.contains(",")
        || input.contains("\"")
        || input.contains("\n")
        || input.contains("\r")) {
      return "\"" + input.replace("\"", "\"\"") + "\"";
    }
    return input;
  }

  /** Unescapes a string containing CSV entity escapes. */
  public static String unescapeCsv(final String input) {
    if (input == null) {
      return null;
    }
    if (input.startsWith("\"") && input.endsWith("\"")) {
      final String quotesRemoved = input.substring(1, input.length() - 1);
      return quotesRemoved.replace("\"\"", "\"");
    }
    return input;
  }
}
