package oculus.aperture.common.json;

/**
 * Simple XML to JSON converter to replace org.json.XML Provides basic XML to JSONObject conversion
 * functionality
 */
public class XML {

  /** Convert an XML string to a JSONObject */
  public static JSONObject toJSONObject(String string) throws JSONException {
    if (string == null || string.trim().isEmpty()) {
      throw new JSONException("Empty XML string");
    }

    try {
      return parseXML(string.trim());
    } catch (Exception e) {
      throw new JSONException("Failed to parse XML: " + e.getMessage(), e);
    }
  }

  /** Convert a JSONObject to an XML string */
  public static String toString(Object object) throws JSONException {
    return toString(object, null);
  }

  /** Convert a JSONObject to an XML string with a tag name */
  public static String toString(Object object, String tagName) throws JSONException {
    if (object == null) {
      return "";
    }

    StringBuilder sb = new StringBuilder();

    if (object instanceof JSONObject) {
      JSONObject jsonObject = (JSONObject) object;

      if (tagName != null) {
        sb.append("<").append(tagName).append(">");
      }

      for (String key : jsonObject.keySet()) {
        Object value = jsonObject.opt(key);
        if (value == null) {
          sb.append("<").append(key).append("/>");
        } else if (value instanceof JSONObject || value instanceof JSONArray) {
          sb.append(toString(value, key));
        } else {
          sb.append("<").append(key).append(">");
          sb.append(escape(value.toString()));
          sb.append("</").append(key).append(">");
        }
      }

      if (tagName != null) {
        sb.append("</").append(tagName).append(">");
      }
    } else if (object instanceof JSONArray) {
      JSONArray jsonArray = (JSONArray) object;

      for (int i = 0; i < jsonArray.length(); i++) {
        Object value = jsonArray.opt(i);
        sb.append(toString(value, tagName != null ? tagName : "item"));
      }
    } else {
      if (tagName != null) {
        sb.append("<").append(tagName).append(">");
        sb.append(escape(object.toString()));
        sb.append("</").append(tagName).append(">");
      } else {
        sb.append(escape(object.toString()));
      }
    }

    return sb.toString();
  }

  private static JSONObject parseXML(String xml) throws JSONException {
    JSONObject result = new JSONObject();
    XMLTokenizer tokenizer = new XMLTokenizer(xml);

    while (tokenizer.hasNext()) {
      XMLToken token = tokenizer.next();
      if (token.type == XMLToken.Type.START_TAG) {
        String tagName = token.value;
        Object content = parseContent(tokenizer, tagName);

        if (result.has(tagName)) {
          Object existing = result.opt(tagName);
          if (existing instanceof JSONArray) {
            ((JSONArray) existing).put(content);
          } else {
            JSONArray array = new JSONArray();
            array.put(existing);
            array.put(content);
            result.put(tagName, array);
          }
        } else {
          result.put(tagName, content);
        }
      }
    }

    return result;
  }

  private static Object parseContent(XMLTokenizer tokenizer, String tagName) throws JSONException {
    StringBuilder textContent = new StringBuilder();
    JSONObject attributes = new JSONObject();
    JSONObject children = new JSONObject();

    while (tokenizer.hasNext()) {
      XMLToken token = tokenizer.next();

      if (token.type == XMLToken.Type.END_TAG && token.value.equals(tagName)) {
        String text = textContent.toString().trim();
        if (!text.isEmpty() && children.length() == 0) {
          return text;
        } else if (children.length() > 0) {
          return children;
        } else if (!text.isEmpty()) {
          return text;
        } else {
          return "";
        }
      } else if (token.type == XMLToken.Type.TEXT) {
        textContent.append(token.value);
      } else if (token.type == XMLToken.Type.START_TAG) {
        String childTag = token.value;
        Object childContent = parseContent(tokenizer, childTag);

        if (children.has(childTag)) {
          Object existing = children.opt(childTag);
          if (existing instanceof JSONArray) {
            ((JSONArray) existing).put(childContent);
          } else {
            JSONArray array = new JSONArray();
            array.put(existing);
            array.put(childContent);
            children.put(childTag, array);
          }
        } else {
          children.put(childTag, childContent);
        }
      } else if (token.type == XMLToken.Type.SELF_CLOSING) {
        return "";
      }
    }

    return children.length() > 0 ? children : textContent.toString().trim();
  }

  public static String escape(String string) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < string.length(); i++) {
      char c = string.charAt(i);
      switch (c) {
        case '&':
          sb.append("&amp;");
          break;
        case '<':
          sb.append("&lt;");
          break;
        case '>':
          sb.append("&gt;");
          break;
        case '"':
          sb.append("&quot;");
          break;
        case '\'':
          sb.append("&apos;");
          break;
        default:
          sb.append(c);
      }
    }
    return sb.toString();
  }

  private static class XMLToken {
    enum Type {
      START_TAG,
      END_TAG,
      TEXT,
      SELF_CLOSING
    }

    Type type;
    String value;

    XMLToken(Type type, String value) {
      this.type = type;
      this.value = value;
    }
  }

  private static class XMLTokenizer {
    private String xml;
    private int pos;

    XMLTokenizer(String xml) {
      this.xml = xml;
      this.pos = 0;
    }

    boolean hasNext() {
      skipWhitespace();
      return pos < xml.length();
    }

    XMLToken next() throws JSONException {
      skipWhitespace();

      if (pos >= xml.length()) {
        return null;
      }

      if (xml.charAt(pos) == '<') {
        pos++;

        if (pos < xml.length() && xml.charAt(pos) == '/') {
          pos++;
          String tagName = readUntil('>');
          pos++;
          return new XMLToken(XMLToken.Type.END_TAG, tagName.trim());
        } else {
          String tagName = readUntil('>');

          if (tagName.endsWith("/")) {
            pos++;
            return new XMLToken(
                XMLToken.Type.SELF_CLOSING, tagName.substring(0, tagName.length() - 1).trim());
          } else {
            pos++;
            return new XMLToken(XMLToken.Type.START_TAG, tagName.trim());
          }
        }
      } else {
        String text = readUntil('<');
        return new XMLToken(XMLToken.Type.TEXT, unescape(text));
      }
    }

    private String readUntil(char delimiter) {
      int start = pos;
      while (pos < xml.length() && xml.charAt(pos) != delimiter) {
        pos++;
      }
      return xml.substring(start, pos);
    }

    private void skipWhitespace() {
      while (pos < xml.length() && Character.isWhitespace(xml.charAt(pos))) {
        pos++;
      }
    }

    private String unescape(String string) {
      return string
          .replace("&amp;", "&")
          .replace("&lt;", "<")
          .replace("&gt;", ">")
          .replace("&quot;", "\"")
          .replace("&apos;", "'");
    }
  }
}
