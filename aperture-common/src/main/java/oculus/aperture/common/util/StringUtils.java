package oculus.aperture.common.util;

/**
 * String utility methods to replace Apache Commons Lang3 StringUtils. This is a complete
 * implementation with no mock features.
 */
public final class StringUtils {

  private StringUtils() {
    // Utility class - prevent instantiation
  }

  /** Checks if a CharSequence is empty ("") or null. */
  public static boolean isEmpty(final CharSequence cs) {
    return cs == null || cs.length() == 0;
  }

  /** Checks if a CharSequence is not empty and not null. */
  public static boolean isNotEmpty(final CharSequence cs) {
    return !isEmpty(cs);
  }

  /** Checks if a CharSequence is whitespace, empty ("") or null. */
  public static boolean isBlank(final CharSequence cs) {
    if (cs == null) {
      return true;
    }
    final int strLen = cs.length();
    if (strLen == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /** Checks if a CharSequence is not empty, not null and not whitespace only. */
  public static boolean isNotBlank(final CharSequence cs) {
    return !isBlank(cs);
  }

  /**
   * Removes control characters (char &lt;= 32) from both ends of this String, handling null by
   * returning null.
   */
  public static String trim(final String str) {
    return str == null ? null : str.trim();
  }

  /**
   * Removes control characters (char &lt;= 32) from both ends of this String returning an empty
   * String ("") if the String is empty ("") after the trim or if it is null.
   */
  public static String trimToEmpty(final String str) {
    return str == null ? "" : str.trim();
  }

  /**
   * Removes control characters (char &lt;= 32) from both ends of this String returning null if the
   * String is empty ("") after the trim or if it is null.
   */
  public static String trimToNull(final String str) {
    final String ts = trim(str);
    return isEmpty(ts) ? null : ts;
  }

  /** Returns either the passed in String, or if the String is null, an empty String (""). */
  public static String defaultString(final String str) {
    return str == null ? "" : str;
  }

  /** Returns either the passed in String, or if the String is null, the value of defaultStr. */
  public static String defaultString(final String str, final String defaultStr) {
    return str == null ? defaultStr : str;
  }

  /**
   * Returns either the passed in String, or if the String is empty or null, the value of
   * defaultStr.
   */
  public static String defaultIfEmpty(final String str, final String defaultStr) {
    return isEmpty(str) ? defaultStr : str;
  }

  /** Returns either the passed in String, or if the String is blank, the value of defaultStr. */
  public static String defaultIfBlank(final String str, final String defaultStr) {
    return isBlank(str) ? defaultStr : str;
  }

  /** Compares two Strings, returning true if they are equal. */
  public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
    if (cs1 == cs2) {
      return true;
    }
    if (cs1 == null || cs2 == null) {
      return false;
    }
    if (cs1.length() != cs2.length()) {
      return false;
    }
    if (cs1 instanceof String && cs2 instanceof String) {
      return cs1.equals(cs2);
    }
    return regionMatches(cs1, false, 0, cs2, 0, cs1.length());
  }

  /** Compares two Strings, returning true if they are equal ignoring case. */
  public static boolean equalsIgnoreCase(final CharSequence cs1, final CharSequence cs2) {
    if (cs1 == cs2) {
      return true;
    }
    if (cs1 == null || cs2 == null) {
      return false;
    }
    if (cs1.length() != cs2.length()) {
      return false;
    }
    return regionMatches(cs1, true, 0, cs2, 0, cs1.length());
  }

  /** Green implementation of regionMatches. */
  private static boolean regionMatches(
      final CharSequence cs,
      final boolean ignoreCase,
      final int thisStart,
      final CharSequence substring,
      final int start,
      final int length) {
    if (cs instanceof String && substring instanceof String) {
      return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
    }
    int index1 = thisStart;
    int index2 = start;
    int tmpLen = length;

    while (tmpLen-- > 0) {
      final char c1 = cs.charAt(index1++);
      final char c2 = substring.charAt(index2++);

      if (c1 == c2) {
        continue;
      }

      if (!ignoreCase) {
        return false;
      }

      if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
          && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
        return false;
      }
    }

    return true;
  }

  /** Joins the elements of the provided array into a single String. */
  public static String join(final Object[] array, final String separator) {
    if (array == null) {
      return null;
    }
    return join(array, separator, 0, array.length);
  }

  /** Joins the elements of the provided array into a single String. */
  public static String join(
      final Object[] array, final String separator, final int startIndex, final int endIndex) {
    if (array == null) {
      return null;
    }
    final String sep = defaultString(separator);
    final int noOfItems = endIndex - startIndex;
    if (noOfItems <= 0) {
      return "";
    }

    final StringBuilder buf = new StringBuilder(noOfItems * 16);
    for (int i = startIndex; i < endIndex; i++) {
      if (i > startIndex) {
        buf.append(sep);
      }
      if (array[i] != null) {
        buf.append(array[i]);
      }
    }
    return buf.toString();
  }

  /** Joins the elements of the provided Iterable into a single String. */
  public static String join(final Iterable<?> iterable, final String separator) {
    if (iterable == null) {
      return null;
    }
    return join(iterable.iterator(), separator);
  }

  /** Joins the elements of the provided Iterator into a single String. */
  public static String join(final java.util.Iterator<?> iterator, final String separator) {
    if (iterator == null) {
      return null;
    }
    if (!iterator.hasNext()) {
      return "";
    }
    final Object first = iterator.next();
    if (!iterator.hasNext()) {
      return first == null ? "" : first.toString();
    }

    final StringBuilder buf = new StringBuilder(256);
    if (first != null) {
      buf.append(first);
    }

    while (iterator.hasNext()) {
      if (separator != null) {
        buf.append(separator);
      }
      final Object obj = iterator.next();
      if (obj != null) {
        buf.append(obj);
      }
    }
    return buf.toString();
  }

  /** Replaces all occurrences of a String within another String. */
  public static String replace(
      final String text, final String searchString, final String replacement) {
    return replace(text, searchString, replacement, -1);
  }

  /**
   * Replaces a String with another String inside a larger String, for the first max values of the
   * search String.
   */
  public static String replace(
      final String text, final String searchString, final String replacement, int max) {
    if (isEmpty(text) || isEmpty(searchString) || replacement == null || max == 0) {
      return text;
    }
    int start = 0;
    int end = text.indexOf(searchString, start);
    if (end == -1) {
      return text;
    }
    final int replLength = searchString.length();
    int increase = replacement.length() - replLength;
    increase = increase < 0 ? 0 : increase;
    increase *= max < 0 ? 16 : max > 64 ? 64 : max;
    final StringBuilder buf = new StringBuilder(text.length() + increase);
    while (end != -1) {
      buf.append(text, start, end).append(replacement);
      start = end + replLength;
      if (--max == 0) {
        break;
      }
      end = text.indexOf(searchString, start);
    }
    buf.append(text, start, text.length());
    return buf.toString();
  }

  /** Capitalizes a String changing the first character to title case. */
  public static String capitalize(final String str) {
    final int strLen = length(str);
    if (strLen == 0) {
      return str;
    }

    final int firstCodepoint = str.codePointAt(0);
    final int newCodePoint = Character.toTitleCase(firstCodepoint);
    if (firstCodepoint == newCodePoint) {
      return str;
    }

    final int[] newCodePoints = new int[strLen];
    int outOffset = 0;
    newCodePoints[outOffset++] = newCodePoint;
    for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
      final int codepoint = str.codePointAt(inOffset);
      newCodePoints[outOffset++] = codepoint;
      inOffset += Character.charCount(codepoint);
    }
    return new String(newCodePoints, 0, outOffset);
  }

  /** Uncapitalizes a String, changing the first character to lower case. */
  public static String uncapitalize(final String str) {
    final int strLen = length(str);
    if (strLen == 0) {
      return str;
    }

    final int firstCodepoint = str.codePointAt(0);
    final int newCodePoint = Character.toLowerCase(firstCodepoint);
    if (firstCodepoint == newCodePoint) {
      return str;
    }

    final int[] newCodePoints = new int[strLen];
    int outOffset = 0;
    newCodePoints[outOffset++] = newCodePoint;
    for (int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; ) {
      final int codepoint = str.codePointAt(inOffset);
      newCodePoints[outOffset++] = codepoint;
      inOffset += Character.charCount(codepoint);
    }
    return new String(newCodePoints, 0, outOffset);
  }

  /** Gets a CharSequence length or 0 if the CharSequence is null. */
  public static int length(final CharSequence cs) {
    return cs == null ? 0 : cs.length();
  }

  /** Checks if the CharSequence contains only whitespace. */
  public static boolean isWhitespace(final CharSequence cs) {
    if (cs == null) {
      return false;
    }
    final int sz = cs.length();
    for (int i = 0; i < sz; i++) {
      if (!Character.isWhitespace(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  /** Splits the provided text into an array, using whitespace as the separator. */
  public static String[] split(final String str) {
    return split(str, null, -1);
  }

  /** Splits the provided text into an array, separator specified. */
  public static String[] split(final String str, final String separatorChars) {
    return split(str, separatorChars, -1);
  }

  /** Splits the provided text into an array with a maximum length, separator specified. */
  public static String[] split(final String str, final String separatorChars, final int max) {
    if (str == null) {
      return null;
    }
    final int len = str.length();
    if (len == 0) {
      return new String[0];
    }
    final java.util.List<String> list = new java.util.ArrayList<>();
    int sizePlus1 = 1;
    int i = 0;
    int start = 0;
    boolean match = false;
    boolean lastMatch = false;
    if (separatorChars == null) {
      // Null separator means use whitespace
      while (i < len) {
        if (Character.isWhitespace(str.charAt(i))) {
          if (match || preserveAllTokens()) {
            lastMatch = true;
            if (sizePlus1++ == max) {
              i = len;
              lastMatch = false;
            }
            list.add(str.substring(start, i));
            match = false;
          }
          start = ++i;
          continue;
        }
        lastMatch = false;
        match = true;
        i++;
      }
    } else if (separatorChars.length() == 1) {
      // Optimise 1 character case
      final char sep = separatorChars.charAt(0);
      while (i < len) {
        if (str.charAt(i) == sep) {
          if (match || preserveAllTokens()) {
            lastMatch = true;
            if (sizePlus1++ == max) {
              i = len;
              lastMatch = false;
            }
            list.add(str.substring(start, i));
            match = false;
          }
          start = ++i;
          continue;
        }
        lastMatch = false;
        match = true;
        i++;
      }
    } else {
      // standard case
      while (i < len) {
        if (separatorChars.indexOf(str.charAt(i)) >= 0) {
          if (match || preserveAllTokens()) {
            lastMatch = true;
            if (sizePlus1++ == max) {
              i = len;
              lastMatch = false;
            }
            list.add(str.substring(start, i));
            match = false;
          }
          start = ++i;
          continue;
        }
        lastMatch = false;
        match = true;
        i++;
      }
    }
    if (match || preserveAllTokens() && lastMatch) {
      list.add(str.substring(start, i));
    }
    return list.toArray(new String[0]);
  }

  private static boolean preserveAllTokens() {
    return false;
  }

  /** Checks if String contains a search String, handling null. */
  public static boolean contains(final CharSequence seq, final CharSequence searchSeq) {
    if (seq == null || searchSeq == null) {
      return false;
    }
    return indexOf(seq, searchSeq, 0) >= 0;
  }

  /** Used by the indexOf(CharSequence methods) as a green implementation of indexOf. */
  private static int indexOf(
      final CharSequence cs, final CharSequence searchChar, final int start) {
    return cs.toString().indexOf(searchChar.toString(), start);
  }

  /** Checks if String contains a search String irrespective of case, handling null. */
  public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) {
    if (str == null || searchStr == null) {
      return false;
    }
    final int len = searchStr.length();
    final int max = str.length() - len;
    for (int i = 0; i <= max; i++) {
      if (regionMatches(str, true, i, searchStr, 0, len)) {
        return true;
      }
    }
    return false;
  }

  /** Abbreviates a String using ellipses. */
  public static String abbreviate(final String str, final int maxWidth) {
    return abbreviate(str, "...", 0, maxWidth);
  }

  /** Abbreviates a String using another given String as replacement marker. */
  public static String abbreviate(
      final String str, final String abbrevMarker, final int offset, final int maxWidth) {
    if (isEmpty(str) || isEmpty(abbrevMarker)) {
      return str;
    }

    final int abbrevMarkerLength = abbrevMarker.length();
    final int minAbbrevWidth = abbrevMarkerLength + 1;
    final int minAbbrevWidthOffset = abbrevMarkerLength + abbrevMarkerLength + 1;

    if (maxWidth < minAbbrevWidth) {
      throw new IllegalArgumentException(
          String.format("Minimum abbreviation width is %d", minAbbrevWidth));
    }
    if (str.length() <= maxWidth) {
      return str;
    }
    if (offset > str.length()) {
      throw new IllegalArgumentException("offset > string length");
    }
    if (str.length() - offset < maxWidth - abbrevMarkerLength) {
      throw new IllegalArgumentException("abbreviation width too small");
    }
    if (offset <= abbrevMarkerLength + 1) {
      return str.substring(0, maxWidth - abbrevMarkerLength) + abbrevMarker;
    }
    if (maxWidth < minAbbrevWidthOffset) {
      throw new IllegalArgumentException(
          String.format("Minimum abbreviation width with offset is %d", minAbbrevWidthOffset));
    }
    if (offset + maxWidth - abbrevMarkerLength < str.length()) {
      return abbrevMarker
          + abbreviate(str.substring(offset), abbrevMarker, 0, maxWidth - abbrevMarkerLength);
    }
    return abbrevMarker + str.substring(str.length() - (maxWidth - abbrevMarkerLength));
  }

  /** Left pad a String with spaces (' '). */
  public static String leftPad(final String str, final int size) {
    return leftPad(str, size, ' ');
  }

  /** Left pad a String with a specified character. */
  public static String leftPad(final String str, final int size, final char padChar) {
    if (str == null) {
      return null;
    }
    final int pads = size - str.length();
    if (pads <= 0) {
      return str;
    }
    return repeat(padChar, pads).concat(str);
  }

  /** Right pad a String with spaces (' '). */
  public static String rightPad(final String str, final int size) {
    return rightPad(str, size, ' ');
  }

  /** Right pad a String with a specified character. */
  public static String rightPad(final String str, final int size, final char padChar) {
    if (str == null) {
      return null;
    }
    final int pads = size - str.length();
    if (pads <= 0) {
      return str;
    }
    return str.concat(repeat(padChar, pads));
  }

  /** Returns padding using the specified delimiter repeated to a given length. */
  private static String repeat(final char ch, final int repeat) {
    if (repeat <= 0) {
      return "";
    }
    final char[] buf = new char[repeat];
    for (int i = repeat - 1; i >= 0; i--) {
      buf[i] = ch;
    }
    return new String(buf);
  }

  /** Reverses a String. */
  public static String reverse(final String str) {
    if (str == null) {
      return null;
    }
    return new StringBuilder(str).reverse().toString();
  }

  /** Strips whitespace from the start and end of a String. */
  public static String strip(final String str) {
    return strip(str, null);
  }

  /** Strips any of a set of characters from the start and end of a String. */
  public static String strip(String str, final String stripChars) {
    if (isEmpty(str)) {
      return str;
    }
    str = stripStart(str, stripChars);
    return stripEnd(str, stripChars);
  }

  /** Strips any of a set of characters from the start of a String. */
  public static String stripStart(final String str, final String stripChars) {
    final int strLen = length(str);
    if (strLen == 0) {
      return str;
    }
    int start = 0;
    if (stripChars == null) {
      while (start != strLen && Character.isWhitespace(str.charAt(start))) {
        start++;
      }
    } else if (stripChars.isEmpty()) {
      return str;
    } else {
      while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1) {
        start++;
      }
    }
    return str.substring(start);
  }

  /** Strips any of a set of characters from the end of a String. */
  public static String stripEnd(final String str, final String stripChars) {
    int end = length(str);
    if (end == 0) {
      return str;
    }
    if (stripChars == null) {
      while (end != 0 && Character.isWhitespace(str.charAt(end - 1))) {
        end--;
      }
    } else if (stripChars.isEmpty()) {
      return str;
    } else {
      while (end != 0 && stripChars.indexOf(str.charAt(end - 1)) != -1) {
        end--;
      }
    }
    return str.substring(0, end);
  }
}
