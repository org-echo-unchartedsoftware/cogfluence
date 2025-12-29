package software.uncharted.influent.util.json;

import java.util.*;

/**
 * Simple JSON utilities for basic JSON parsing and generation. Replaces Jackson for simple use
 * cases. For complex JSON operations, consider keeping Jackson or using a full JSON library.
 */
public class JsonUtils {

  /**
   * Parse a JSON string into a Map or List
   *
   * @param json the JSON string
   * @return the parsed object (Map, List, String, Number, Boolean, or null)
   */
  public static Object parse(String json) {
    if (json == null || json.trim().isEmpty()) {
      return null;
    }
    return new JsonParser(json).parse();
  }

  /**
   * Convert an object to JSON string
   *
   * @param obj the object to convert
   * @return the JSON string
   */
  public static String toJson(Object obj) {
    if (obj == null) {
      return "null";
    }
    return new JsonWriter().write(obj);
  }

  /** Simple JSON parser */
  private static class JsonParser {
    private final String json;
    private int index;

    JsonParser(String json) {
      this.json = json.trim();
      this.index = 0;
    }

    Object parse() {
      skipWhitespace();
      return parseValue();
    }

    private Object parseValue() {
      skipWhitespace();
      char c = current();

      if (c == '{') {
        return parseObject();
      } else if (c == '[') {
        return parseArray();
      } else if (c == '"') {
        return parseString();
      } else if (c == 't' || c == 'f') {
        return parseBoolean();
      } else if (c == 'n') {
        return parseNull();
      } else {
        return parseNumber();
      }
    }

    private Map<String, Object> parseObject() {
      Map<String, Object> map = new LinkedHashMap<>();
      consume('{');
      skipWhitespace();

      if (current() == '}') {
        consume('}');
        return map;
      }

      while (true) {
        skipWhitespace();
        String key = parseString();
        skipWhitespace();
        consume(':');
        skipWhitespace();
        Object value = parseValue();
        map.put(key, value);
        skipWhitespace();

        if (current() == '}') {
          consume('}');
          break;
        }
        consume(',');
      }

      return map;
    }

    private List<Object> parseArray() {
      List<Object> list = new ArrayList<>();
      consume('[');
      skipWhitespace();

      if (current() == ']') {
        consume(']');
        return list;
      }

      while (true) {
        skipWhitespace();
        list.add(parseValue());
        skipWhitespace();

        if (current() == ']') {
          consume(']');
          break;
        }
        consume(',');
      }

      return list;
    }

    private String parseString() {
      consume('"');
      StringBuilder sb = new StringBuilder();

      while (current() != '"') {
        if (current() == '\\') {
          index++;
          char escaped = current();
          switch (escaped) {
            case '"':
            case '\\':
            case '/':
              sb.append(escaped);
              break;
            case 'b':
              sb.append('\b');
              break;
            case 'f':
              sb.append('\f');
              break;
            case 'n':
              sb.append('\n');
              break;
            case 'r':
              sb.append('\r');
              break;
            case 't':
              sb.append('\t');
              break;
            case 'u':
              sb.append(parseUnicode());
              continue;
            default:
              sb.append(escaped);
          }
        } else {
          sb.append(current());
        }
        index++;
      }

      consume('"');
      return sb.toString();
    }

    private char parseUnicode() {
      index++;
      String hex = json.substring(index, index + 4);
      index += 3;
      return (char) Integer.parseInt(hex, 16);
    }

    private Number parseNumber() {
      int start = index;
      if (current() == '-') {
        index++;
      }

      while (index < json.length() && (Character.isDigit(current()) || current() == '.')) {
        index++;
      }

      String numStr = json.substring(start, index);
      if (numStr.contains(".")) {
        return Double.parseDouble(numStr);
      } else {
        try {
          return Integer.parseInt(numStr);
        } catch (NumberFormatException e) {
          return Long.parseLong(numStr);
        }
      }
    }

    private Boolean parseBoolean() {
      if (json.startsWith("true", index)) {
        index += 4;
        return true;
      } else if (json.startsWith("false", index)) {
        index += 5;
        return false;
      }
      throw new IllegalArgumentException("Invalid boolean at position " + index);
    }

    private Object parseNull() {
      if (json.startsWith("null", index)) {
        index += 4;
        return null;
      }
      throw new IllegalArgumentException("Invalid null at position " + index);
    }

    private char current() {
      if (index >= json.length()) {
        throw new IllegalArgumentException("Unexpected end of JSON");
      }
      return json.charAt(index);
    }

    private void consume(char expected) {
      if (current() != expected) {
        throw new IllegalArgumentException(
            "Expected '" + expected + "' at position " + index + " but found '" + current() + "'");
      }
      index++;
    }

    private void skipWhitespace() {
      while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
        index++;
      }
    }
  }

  /** Simple JSON writer */
  private static class JsonWriter {
    String write(Object obj) {
      if (obj == null) {
        return "null";
      } else if (obj instanceof String) {
        return writeString((String) obj);
      } else if (obj instanceof Number) {
        return obj.toString();
      } else if (obj instanceof Boolean) {
        return obj.toString();
      } else if (obj instanceof Map) {
        return writeObject((Map<?, ?>) obj);
      } else if (obj instanceof List) {
        return writeArray((List<?>) obj);
      } else {
        return writeString(obj.toString());
      }
    }

    private String writeString(String str) {
      StringBuilder sb = new StringBuilder();
      sb.append('"');
      for (char c : str.toCharArray()) {
        switch (c) {
          case '"':
            sb.append("\\\"");
            break;
          case '\\':
            sb.append("\\\\");
            break;
          case '\b':
            sb.append("\\b");
            break;
          case '\f':
            sb.append("\\f");
            break;
          case '\n':
            sb.append("\\n");
            break;
          case '\r':
            sb.append("\\r");
            break;
          case '\t':
            sb.append("\\t");
            break;
          default:
            if (c < 32 || c > 126) {
              sb.append(String.format("\\u%04x", (int) c));
            } else {
              sb.append(c);
            }
        }
      }
      sb.append('"');
      return sb.toString();
    }

    private String writeObject(Map<?, ?> map) {
      StringBuilder sb = new StringBuilder();
      sb.append('{');
      boolean first = true;
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        if (!first) {
          sb.append(',');
        }
        first = false;
        sb.append(writeString(entry.getKey().toString()));
        sb.append(':');
        sb.append(write(entry.getValue()));
      }
      sb.append('}');
      return sb.toString();
    }

    private String writeArray(List<?> list) {
      StringBuilder sb = new StringBuilder();
      sb.append('[');
      boolean first = true;
      for (Object item : list) {
        if (!first) {
          sb.append(',');
        }
        first = false;
        sb.append(write(item));
      }
      sb.append(']');
      return sb.toString();
    }
  }
}
