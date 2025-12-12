package oculus.aperture.common.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;

/**
 * Custom JSONArray implementation to replace org.json.JSONArray Provides a lightweight alternative
 * without external dependencies
 */
public class JSONArray implements Iterable<Object> {
  private final List<Object> list;

  public JSONArray() {
    this.list = new ArrayList<>();
  }

  public JSONArray(String source) throws JSONException {
    this();
    if (source == null || source.trim().isEmpty()) {
      throw new JSONException("Empty JSON string");
    }
    JSONParser parser = new JSONParser(source);
    Object parsed = parser.parse();
    if (parsed instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> parsedList = (List<Object>) parsed;
      this.list.addAll(parsedList);
    } else {
      throw new JSONException("JSON string does not represent an array");
    }
  }

  public JSONArray(Collection<?> collection) {
    this.list = new ArrayList<>(collection);
  }

  public JSONArray(Object array) throws JSONException {
    this();
    if (array == null) {
      throw new JSONException("Null array");
    }
    if (array.getClass().isArray()) {
      int length = java.lang.reflect.Array.getLength(array);
      for (int i = 0; i < length; i++) {
        this.put(java.lang.reflect.Array.get(array, i));
      }
    } else {
      throw new JSONException("Not an array");
    }
  }

  public JSONArray put(Object value) {
    list.add(value);
    return this;
  }

  public JSONArray put(boolean value) {
    return put(Boolean.valueOf(value));
  }

  public JSONArray put(double value) throws JSONException {
    Double d = Double.valueOf(value);
    if (d.isInfinite() || d.isNaN()) {
      throw new JSONException("Invalid double value");
    }
    return put(d);
  }

  public JSONArray put(int value) {
    return put(Integer.valueOf(value));
  }

  public JSONArray put(long value) {
    return put(Long.valueOf(value));
  }

  public JSONArray put(int index, Object value) throws JSONException {
    if (index < 0) {
      throw new JSONException("Negative index");
    }
    if (index < list.size()) {
      list.set(index, value);
    } else {
      while (index > list.size()) {
        list.add(null);
      }
      list.add(value);
    }
    return this;
  }

  public JSONArray put(int index, boolean value) throws JSONException {
    return put(index, Boolean.valueOf(value));
  }

  public JSONArray put(int index, double value) throws JSONException {
    return put(index, Double.valueOf(value));
  }

  public JSONArray put(int index, int value) throws JSONException {
    return put(index, Integer.valueOf(value));
  }

  public JSONArray put(int index, long value) throws JSONException {
    return put(index, Long.valueOf(value));
  }

  public Object get(int index) throws JSONException {
    if (index < 0 || index >= list.size()) {
      throw new JSONException("Index out of bounds: " + index);
    }
    return list.get(index);
  }

  public Object opt(int index) {
    if (index < 0 || index >= list.size()) {
      return null;
    }
    return list.get(index);
  }

  public String getString(int index) throws JSONException {
    Object value = get(index);
    if (value == null) {
      throw new JSONException("Null value at index: " + index);
    }
    return value.toString();
  }

  public String optString(int index) {
    return optString(index, "");
  }

  public String optString(int index, String defaultValue) {
    Object value = opt(index);
    return value == null ? defaultValue : value.toString();
  }

  public int getInt(int index) throws JSONException {
    Object value = get(index);
    if (value instanceof Number) {
      return ((Number) value).intValue();
    }
    try {
      return Integer.parseInt(value.toString());
    } catch (NumberFormatException e) {
      throw new JSONException("Value is not an integer at index: " + index);
    }
  }

  public int optInt(int index) {
    return optInt(index, 0);
  }

  public int optInt(int index, int defaultValue) {
    Object value = opt(index);
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

  public long getLong(int index) throws JSONException {
    Object value = get(index);
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    try {
      return Long.parseLong(value.toString());
    } catch (NumberFormatException e) {
      throw new JSONException("Value is not a long at index: " + index);
    }
  }

  public long optLong(int index) {
    return optLong(index, 0L);
  }

  public long optLong(int index, long defaultValue) {
    Object value = opt(index);
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

  public double getDouble(int index) throws JSONException {
    Object value = get(index);
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    try {
      return Double.parseDouble(value.toString());
    } catch (NumberFormatException e) {
      throw new JSONException("Value is not a double at index: " + index);
    }
  }

  public double optDouble(int index) {
    return optDouble(index, Double.NaN);
  }

  public double optDouble(int index, double defaultValue) {
    Object value = opt(index);
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

  public boolean getBoolean(int index) throws JSONException {
    Object value = get(index);
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
    throw new JSONException("Value is not a boolean at index: " + index);
  }

  public boolean optBoolean(int index) {
    return optBoolean(index, false);
  }

  public boolean optBoolean(int index, boolean defaultValue) {
    Object value = opt(index);
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

  public JSONObject getJSONObject(int index) throws JSONException {
    Object value = get(index);
    if (value instanceof JSONObject) {
      return (JSONObject) value;
    }
    if (value instanceof Map) {
      @SuppressWarnings("unchecked")
      Map<String, Object> mapValue = (Map<String, Object>) value;
      return new JSONObject(mapValue);
    }
    throw new JSONException("Value is not a JSONObject at index: " + index);
  }

  public JSONObject optJSONObject(int index) {
    Object value = opt(index);
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

  public JSONArray getJSONArray(int index) throws JSONException {
    Object value = get(index);
    if (value instanceof JSONArray) {
      return (JSONArray) value;
    }
    if (value instanceof List) {
      @SuppressWarnings("unchecked")
      List<Object> listValue = (List<Object>) value;
      return new JSONArray(listValue);
    }
    throw new JSONException("Value is not a JSONArray at index: " + index);
  }

  public JSONArray optJSONArray(int index) {
    Object value = opt(index);
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

  public boolean isNull(int index) {
    return opt(index) == null;
  }

  public Object remove(int index) {
    if (index >= 0 && index < list.size()) {
      return list.remove(index);
    }
    return null;
  }

  public int length() {
    return list.size();
  }

  /** Returns an iterator over the elements in this array. */
  @Override
  public Iterator<Object> iterator() {
    return list.iterator();
  }

  public List<Object> toList() {
    return new ArrayList<>(list);
  }

  public JSONObject toJSONObject(JSONArray names) throws JSONException {
    if (names == null || names.length() == 0 || this.length() == 0) {
      return null;
    }
    JSONObject jo = new JSONObject();
    for (int i = 0; i < names.length(); i++) {
      jo.put(names.getString(i), this.opt(i));
    }
    return jo;
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
    writer.write('[');

    if (length == 1) {
      JSONObject.writeValue(writer, list.get(0), indentFactor, indent);
    } else if (length != 0) {
      int newIndent = indent + indentFactor;
      for (int i = 0; i < length; i++) {
        if (needsComma) {
          writer.write(',');
        }
        if (indentFactor > 0) {
          writer.write('\n');
        }
        JSONObject.indent(writer, newIndent);
        JSONObject.writeValue(writer, list.get(i), indentFactor, newIndent);
        needsComma = true;
      }
      if (indentFactor > 0) {
        writer.write('\n');
      }
      JSONObject.indent(writer, indent);
    }
    writer.write(']');
    return writer;
  }

  public String join(String separator) throws JSONException {
    int len = this.length();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < len; i++) {
      if (i > 0) {
        sb.append(separator);
      }
      sb.append(JSONObject.quote(this.get(i).toString()));
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof JSONArray)) {
      return false;
    }
    return list.equals(((JSONArray) obj).list);
  }

  @Override
  public int hashCode() {
    return list.hashCode();
  }
}
