package oculus.aperture.common.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Custom JSONObject implementation to replace org.json.JSONObject Provides a lightweight
 * alternative without external dependencies
 */
public class JSONObject {
  /** Sentinel value to explicitly represent null in JSONObject */
  public static final Object NULL =
      new Object() {
        @Override
        public String toString() {
          return "null";
        }
      };

  private final Map<String, Object> map;

  public JSONObject() {
    this.map = new LinkedHashMap<>();
  }

  public JSONObject(String source) throws JSONException {
    this();
    if (source == null || source.trim().isEmpty()) {
      throw new JSONException("Empty JSON string");
    }
    JSONParser parser = new JSONParser(source);
    Object parsed = parser.parse();
    if (parsed instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> parsedMap = (Map<String, Object>) parsed;
      this.map.putAll(parsedMap);
    } else {
      throw new JSONException("JSON string does not represent an object");
    }
  }

  public JSONObject(Map<String, Object> map) {
    this.map = new LinkedHashMap<>(map);
  }

  /** Constructor that accepts any object and converts it to JSON using reflection */
  public JSONObject(Object bean) throws JSONException {
    this();
    if (bean == null) {
      return;
    }

    if (bean instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> mapBean = (Map<String, Object>) bean;
      this.map.putAll(mapBean);
      return;
    }

    // Use reflection to extract properties from the bean
    Class<?> clazz = bean.getClass();

    // Try getter methods first
    Method[] methods = clazz.getMethods();
    for (Method method : methods) {
      String name = method.getName();
      if (method.getParameterCount() == 0 && !method.getReturnType().equals(Void.TYPE)) {
        String key = null;
        if (name.startsWith("get") && name.length() > 3) {
          key = Character.toLowerCase(name.charAt(3)) + name.substring(4);
        } else if (name.startsWith("is") && name.length() > 2) {
          key = Character.toLowerCase(name.charAt(2)) + name.substring(3);
        }

        if (key != null && !key.equals("class")) {
          try {
            Object value = method.invoke(bean);
            if (value != null) {
              this.map.put(key, value);
            }
          } catch (Exception e) {
            // Skip this property
          }
        }
      }
    }

    // If no getters found, try fields
    if (this.map.isEmpty()) {
      Field[] fields = clazz.getDeclaredFields();
      for (Field field : fields) {
        try {
          field.setAccessible(true);
          Object value = field.get(bean);
          if (value != null) {
            this.map.put(field.getName(), value);
          }
        } catch (Exception e) {
          // Skip this field
        }
      }
    }
  }

  public JSONObject put(String key, Object value) throws JSONException {
    if (key == null) {
      throw new JSONException("Null key");
    }
    map.put(key, value);
    return this;
  }

  public JSONObject put(String key, boolean value) throws JSONException {
    return put(key, Boolean.valueOf(value));
  }

  public JSONObject put(String key, double value) throws JSONException {
    return put(key, Double.valueOf(value));
  }

  public JSONObject put(String key, int value) throws JSONException {
    return put(key, Integer.valueOf(value));
  }

  public JSONObject put(String key, long value) throws JSONException {
    return put(key, Long.valueOf(value));
  }

  /**
   * Append values to the array under a key. If the key does not exist, create a new JSONArray and
   * put it in the JSONObject. If the key is already associated with a JSONArray, append the value.
   * Otherwise, throw an exception.
   */
  public JSONObject append(String key, Object value) throws JSONException {
    if (key == null) {
      throw new JSONException("Null key");
    }
    Object object = opt(key);
    if (object == null) {
      put(key, new JSONArray().put(value));
    } else if (object instanceof JSONArray) {
      put(key, ((JSONArray) object).put(value));
    } else {
      throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
    }
    return this;
  }

  public Object get(String key) throws JSONException {
    if (!map.containsKey(key)) {
      throw new JSONException("Key not found: " + key);
    }
    return map.get(key);
  }

  public Object opt(String key) {
    return map.get(key);
  }

  public String getString(String key) throws JSONException {
    Object value = get(key);
    if (value == null) {
      throw new JSONException("Null value for key: " + key);
    }
    return value.toString();
  }

  public String optString(String key) {
    return optString(key, "");
  }

  public String optString(String key, String defaultValue) {
    Object value = opt(key);
    return value == null ? defaultValue : value.toString();
  }

  public int getInt(String key) throws JSONException {
    Object value = get(key);
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    try {
      return Integer.parseInt(value.toString());
    } catch (NumberFormatException e) {
      throw new JSONException("Value is not an integer: " + key);
    }
  }

  public int optInt(String key) {
    return optInt(key, 0);
  }

  public int optInt(String key, int defaultValue) {
    Object value = opt(key);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    try {
      return Integer.parseInt(value.toString());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public long getLong(String key) throws JSONException {
    Object value = get(key);
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    try {
      return Long.parseLong(value.toString());
    } catch (NumberFormatException e) {
      throw new JSONException("Value is not a long: " + key);
    }
  }

  public long optLong(String key) {
    return optLong(key, 0L);
  }

  public long optLong(String key, long defaultValue) {
    Object value = opt(key);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    try {
      return Long.parseLong(value.toString());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public double getDouble(String key) throws JSONException {
    Object value = get(key);
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    try {
      return Double.parseDouble(value.toString());
    } catch (NumberFormatException e) {
      throw new JSONException("Value is not a double: " + key);
    }
  }

  public double optDouble(String key) {
    return optDouble(key, Double.NaN);
  }

  public double optDouble(String key, double defaultValue) {
    Object value = opt(key);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    try {
      return Double.parseDouble(value.toString());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  public boolean getBoolean(String key) throws JSONException {
    Object value = get(key);
    if (value instanceof Boolean) {
      return ((Boolean) value).booleanValue();
    }
    String str = value.toString().toLowerCase();
    if ("true".equals(str)) {
      return true;
    }
    if ("false".equals(str)) {
      return false;
    }
    throw new JSONException("Value is not a boolean: " + key);
  }

  public boolean optBoolean(String key) {
    return optBoolean(key, false);
  }

  public boolean optBoolean(String key, boolean defaultValue) {
    Object value = opt(key);
    if (value == null) {
      return defaultValue;
    }
    if (value instanceof Boolean) {
      return ((Boolean) value).booleanValue();
    }
    String str = value.toString().toLowerCase();
    if ("true".equals(str)) {
      return true;
    }
    if ("false".equals(str)) {
      return false;
    }
    return defaultValue;
  }

  public JSONObject getJSONObject(String key) throws JSONException {
    Object value = get(key);
    if (value instanceof JSONObject) {
      return (JSONObject) value;
    }
    if (value instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> mapValue = (Map<String, Object>) value;
      return new JSONObject(mapValue);
    }
    throw new JSONException("Value is not a JSONObject: " + key);
  }

  public JSONObject optJSONObject(String key) {
    Object value = opt(key);
    if (value instanceof JSONObject) {
      return (JSONObject) value;
    }
    if (value instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> mapValue = (Map<String, Object>) value;
      return new JSONObject(mapValue);
    }
    return null;
  }

  public JSONArray getJSONArray(String key) throws JSONException {
    Object value = get(key);
    if (value instanceof JSONArray) {
      return (JSONArray) value;
    }
    if (value instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> listValue = (List<Object>) value;
      return new JSONArray(listValue);
    }
    throw new JSONException("Value is not a JSONArray: " + key);
  }

  public JSONArray optJSONArray(String key) {
    Object value = opt(key);
    if (value instanceof JSONArray) {
      return (JSONArray) value;
    }
    if (value instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> listValue = (List<Object>) value;
      return new JSONArray(listValue);
    }
    return null;
  }

  public boolean has(String key) {
    return map.containsKey(key);
  }

  public boolean isNull(String key) {
    return map.get(key) == null;
  }

  public JSONObject remove(String key) {
    map.remove(key);
    return this;
  }

  public int length() {
    return map.size();
  }

  public Iterator<String> keys() {
    return map.keySet().iterator();
  }

  public Set<String> keySet() {
    return map.keySet();
  }

  public JSONArray names() {
    if (map.isEmpty()) {
      return null;
    }
    return new JSONArray(new ArrayList<>(map.keySet()));
  }

  /** Get an array of field names from a JSONObject. */
  public static JSONArray getNames(JSONObject jo) {
    return jo.names();
  }

  public Map<String, Object> toMap() {
    return new LinkedHashMap<>(map);
  }

  @Override
  public String toString() {
    try {
      return toString(0);
    } catch (JSONException e) {
      return null;
    }
  }

  public String toString(int indentFactor) throws JSONException {
    StringWriter writer = new StringWriter();
    try {
      write(writer, indentFactor, 0);
    } catch (IOException e) {
      throw new JSONException("IO Error: " + e.getMessage());
    }
    return writer.toString();
  }

  Writer write(Writer writer, int indentFactor, int indent) throws IOException, JSONException {
    boolean needsComma = false;
    int length = this.length();
    writer.write('{');

    if (length == 1) {
      Map.Entry<String, Object> entry = map.entrySet().iterator().next();
      writer.write(quote(entry.getKey()));
      writer.write(':');
      if (indentFactor > 0) {
        writer.write(' ');
      }
      writeValue(writer, entry.getValue(), indentFactor, indent);
    } else if (length != 0) {
      int newIndent = indent + indentFactor;
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        if (needsComma) {
          writer.write(',');
        }
        if (indentFactor > 0) {
          writer.write('\n');
        }
        indent(writer, newIndent);
        writer.write(quote(entry.getKey()));
        writer.write(':');
        if (indentFactor > 0) {
          writer.write(' ');
        }
        writeValue(writer, entry.getValue(), indentFactor, newIndent);
        needsComma = true;
      }
      if (indentFactor > 0) {
        writer.write('\n');
      }
      indent(writer, indent);
    }
    writer.write('}');
    return writer;
  }

  static void writeValue(Writer writer, Object value, int indentFactor, int indent)
      throws IOException, JSONException {
    if (value == null || value.equals(null)) {
      writer.write("null");
    } else if (value instanceof JSONObject) {
      ((JSONObject) value).write(writer, indentFactor, indent);
    } else if (value instanceof JSONArray) {
      ((JSONArray) value).write(writer, indentFactor, indent);
    } else if (value instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> mapValue = (Map<String, Object>) value;
      new JSONObject(mapValue).write(writer, indentFactor, indent);
    } else if (value instanceof Collection) {
      new JSONArray((Collection<?>) value).write(writer, indentFactor, indent);
    } else if (value instanceof Number) {
      writer.write(numberToString((Number) value));
    } else if (value instanceof Boolean) {
      writer.write(value.toString());
    } else {
      writer.write(quote(value.toString()));
    }
  }

  static void indent(Writer writer, int indent) throws IOException {
    for (int i = 0; i < indent; i++) {
      writer.write(' ');
    }
  }

  static String quote(String string) {
    if (string == null || string.isEmpty()) {
      return "\"\"";
    }

    char c;
    int len = string.length();
    StringBuilder sb = new StringBuilder(len + 4);
    sb.append('"');
    for (int i = 0; i < len; i++) {
      c = string.charAt(i);
      switch (c) {
        case '\\':
        case '"':
          sb.append('\\');
          sb.append(c);
          break;
        case '/':
          sb.append('\\');
          sb.append(c);
          break;
        case '\b':
          sb.append("\\b");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\f':
          sb.append("\\f");
          break;
        case '\r':
          sb.append("\\r");
          break;
        default:
          if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
            sb.append("\\u");
            String hex = Integer.toHexString(c);
            sb.append("0000", 0, 4 - hex.length());
            sb.append(hex);
          } else {
            sb.append(c);
          }
      }
    }
    sb.append('"');
    return sb.toString();
  }

  static String numberToString(Number number) throws JSONException {
    if (number == null) {
      throw new JSONException("Null number");
    }
    String string = number.toString();
    if (string.indexOf('.') > 0 && string.indexOf('e') < 0 && string.indexOf('E') < 0) {
      while (string.endsWith("0")) {
        string = string.substring(0, string.length() - 1);
      }
      if (string.endsWith(".")) {
        string = string.substring(0, string.length() - 1);
      }
    }
    return string;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof JSONObject)) {
      return false;
    }
    return map.equals(((JSONObject) obj).map);
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }
}
