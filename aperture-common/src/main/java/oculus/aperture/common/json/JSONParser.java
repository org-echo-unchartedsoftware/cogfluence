package oculus.aperture.common.json;

import java.util.*;

/** Simple JSON parser for parsing JSON strings */
class JSONParser {
  private final String json;
  private int index;
  private char current;

  public JSONParser(String json) {
    this.json = json;
    this.index = 0;
    this.current = json.length() > 0 ? json.charAt(0) : 0;
  }

  public Object parse() throws JSONException {
    skipWhitespace();
    Object result = parseValue();
    skipWhitespace();
    if (index < json.length()) {
      throw new JSONException("Unexpected character at position " + index);
    }
    return result;
  }

  private Object parseValue() throws JSONException {
    skipWhitespace();

    if (current == '{') {
      return parseObject();
    } else if (current == '[') {
      return parseArray();
    } else if (current == '"') {
      return parseString();
    } else if (current == 't' || current == 'f') {
      return parseBoolean();
    } else if (current == 'n') {
      return parseNull();
    } else if (current == '-' || Character.isDigit(current)) {
      return parseNumber();
    } else {
      throw new JSONException("Unexpected character: " + current + " at position " + index);
    }
  }

  private Map<String, Object> parseObject() throws JSONException {
    Map<String, Object> map = new LinkedHashMap<>();
    next(); // skip '{'
    skipWhitespace();

    if (current == '}') {
      next();
      return map;
    }

    while (true) {
      skipWhitespace();
      if (current != '"') {
        throw new JSONException("Expected '\"' at position " + index);
      }
      String key = parseString();

      skipWhitespace();
      if (current != ':') {
        throw new JSONException("Expected ':' at position " + index);
      }
      next();

      Object value = parseValue();
      map.put(key, value);

      skipWhitespace();
      if (current == '}') {
        next();
        return map;
      } else if (current == ',') {
        next();
      } else {
        throw new JSONException("Expected ',' or '}' at position " + index);
      }
    }
  }

  private List<Object> parseArray() throws JSONException {
    List<Object> list = new ArrayList<>();
    next(); // skip '['
    skipWhitespace();

    if (current == ']') {
      next();
      return list;
    }

    while (true) {
      Object value = parseValue();
      list.add(value);

      skipWhitespace();
      if (current == ']') {
        next();
        return list;
      } else if (current == ',') {
        next();
      } else {
        throw new JSONException("Expected ',' or ']' at position " + index);
      }
    }
  }

  private String parseString() throws JSONException {
    StringBuilder sb = new StringBuilder();
    next(); // skip opening '"'

    while (current != '"') {
      if (current == 0) {
        throw new JSONException("Unterminated string at position " + index);
      }
      if (current == '\\') {
        next();
        switch (current) {
          case '"':
          case '\\':
          case '/':
            sb.append(current);
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
            break;
          default:
            throw new JSONException("Invalid escape sequence at position " + index);
        }
      } else {
        sb.append(current);
      }
      next();
    }
    next(); // skip closing '"'
    return sb.toString();
  }

  private char parseUnicode() throws JSONException {
    int value = 0;
    for (int i = 0; i < 4; i++) {
      next();
      int digit;
      if (current >= '0' && current <= '9') {
        digit = current - '0';
      } else if (current >= 'a' && current <= 'f') {
        digit = current - 'a' + 10;
      } else if (current >= 'A' && current <= 'F') {
        digit = current - 'A' + 10;
      } else {
        throw new JSONException("Invalid unicode escape at position " + index);
      }
      value = (value << 4) | digit;
    }
    return (char) value;
  }

  private Number parseNumber() throws JSONException {
    int start = index;

    if (current == '-') {
      next();
    }

    if (current == '0') {
      next();
    } else if (current >= '1' && current <= '9') {
      while (Character.isDigit(current)) {
        next();
      }
    } else {
      throw new JSONException("Invalid number at position " + index);
    }

    boolean isDecimal = false;
    if (current == '.') {
      isDecimal = true;
      next();
      if (!Character.isDigit(current)) {
        throw new JSONException("Invalid number at position " + index);
      }
      while (Character.isDigit(current)) {
        next();
      }
    }

    if (current == 'e' || current == 'E') {
      isDecimal = true;
      next();
      if (current == '+' || current == '-') {
        next();
      }
      if (!Character.isDigit(current)) {
        throw new JSONException("Invalid number at position " + index);
      }
      while (Character.isDigit(current)) {
        next();
      }
    }

    String numberStr = json.substring(start, index);
    try {
      if (isDecimal) {
        return Double.parseDouble(numberStr);
      } else {
        long longValue = Long.parseLong(numberStr);
        if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
          return (int) longValue;
        }
        return longValue;
      }
    } catch (NumberFormatException e) {
      throw new JSONException("Invalid number: " + numberStr);
    }
  }

  private Boolean parseBoolean() throws JSONException {
    if (current == 't') {
      if (match("true")) {
        return Boolean.TRUE;
      }
    } else if (current == 'f') {
      if (match("false")) {
        return Boolean.FALSE;
      }
    }
    throw new JSONException("Invalid boolean at position " + index);
  }

  private Object parseNull() throws JSONException {
    if (match("null")) {
      return null;
    }
    throw new JSONException("Invalid null at position " + index);
  }

  private boolean match(String keyword) {
    int len = keyword.length();
    if (index + len > json.length()) {
      return false;
    }
    for (int i = 0; i < len; i++) {
      if (json.charAt(index + i) != keyword.charAt(i)) {
        return false;
      }
    }
    index += len;
    current = index < json.length() ? json.charAt(index) : 0;
    return true;
  }

  private void next() {
    index++;
    current = index < json.length() ? json.charAt(index) : 0;
  }

  private void skipWhitespace() {
    while (current == ' ' || current == '\t' || current == '\n' || current == '\r') {
      next();
    }
  }
}
