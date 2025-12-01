package oculus.aperture.common.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/** General IO stream manipulation utilities. Replacement for Apache Commons IO IOUtils. */
public final class IOUtils {

  private static final int DEFAULT_BUFFER_SIZE = 8192;
  private static final int EOF = -1;

  private IOUtils() {
    // Utility class
  }

  /** Closes a Closeable unconditionally. */
  public static void closeQuietly(final Closeable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (final IOException ioe) {
      // ignore
    }
  }

  /** Closes a Closeable unconditionally. */
  public static void closeQuietly(final AutoCloseable closeable) {
    try {
      if (closeable != null) {
        closeable.close();
      }
    } catch (final Exception e) {
      // ignore
    }
  }

  /** Copies bytes from an InputStream to an OutputStream. */
  public static int copy(final InputStream input, final OutputStream output) throws IOException {
    final long count = copyLarge(input, output);
    if (count > Integer.MAX_VALUE) {
      return -1;
    }
    return (int) count;
  }

  /** Copies bytes from a large (over 2GB) InputStream to an OutputStream. */
  public static long copyLarge(final InputStream input, final OutputStream output)
      throws IOException {
    return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
  }

  /** Copies bytes from a large (over 2GB) InputStream to an OutputStream using a buffer. */
  public static long copyLarge(
      final InputStream input, final OutputStream output, final byte[] buffer) throws IOException {
    long count = 0;
    int n;
    while (EOF != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  /** Copies chars from a Reader to a Writer. */
  public static int copy(final Reader input, final Writer output) throws IOException {
    final long count = copyLarge(input, output);
    if (count > Integer.MAX_VALUE) {
      return -1;
    }
    return (int) count;
  }

  /** Copies chars from a large (over 2GB) Reader to a Writer. */
  public static long copyLarge(final Reader input, final Writer output) throws IOException {
    return copyLarge(input, output, new char[DEFAULT_BUFFER_SIZE]);
  }

  /** Copies chars from a large (over 2GB) Reader to a Writer using a buffer. */
  public static long copyLarge(final Reader input, final Writer output, final char[] buffer)
      throws IOException {
    long count = 0;
    int n;
    while (EOF != (n = input.read(buffer))) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  /** Gets the contents of an InputStream as a byte array. */
  public static byte[] toByteArray(final InputStream input) throws IOException {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    copy(input, output);
    return output.toByteArray();
  }

  /** Gets the contents of a Reader as a byte array using the default character encoding. */
  public static byte[] toByteArray(final Reader input) throws IOException {
    return toByteArray(input, Charset.defaultCharset());
  }

  /** Gets the contents of a Reader as a byte array using the specified character encoding. */
  public static byte[] toByteArray(final Reader input, final Charset charset) throws IOException {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    copy(input, new OutputStreamWriter(output, charset));
    return output.toByteArray();
  }

  /** Gets the contents of an InputStream as a String using the default character encoding. */
  public static String toString(final InputStream input) throws IOException {
    return toString(input, Charset.defaultCharset());
  }

  /** Gets the contents of an InputStream as a String using the specified character encoding. */
  public static String toString(final InputStream input, final Charset charset) throws IOException {
    final StringBuilder sb = new StringBuilder();
    final InputStreamReader reader = new InputStreamReader(input, charset);
    final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
    int n;
    while (EOF != (n = reader.read(buffer))) {
      sb.append(buffer, 0, n);
    }
    return sb.toString();
  }

  /**
   * Gets the contents of an InputStream as a String using the specified character encoding name.
   */
  public static String toString(final InputStream input, final String charsetName)
      throws IOException {
    return toString(input, Charset.forName(charsetName));
  }

  /** Gets the contents of a Reader as a String. */
  public static String toString(final Reader input) throws IOException {
    final StringBuilder sb = new StringBuilder();
    final char[] buffer = new char[DEFAULT_BUFFER_SIZE];
    int n;
    while (EOF != (n = input.read(buffer))) {
      sb.append(buffer, 0, n);
    }
    return sb.toString();
  }

  /** Gets the contents of a byte array as a String using the default character encoding. */
  public static String toString(final byte[] input) {
    return new String(input, Charset.defaultCharset());
  }

  /** Gets the contents of a byte array as a String using the specified character encoding. */
  public static String toString(final byte[] input, final String charsetName) {
    return new String(input, Charset.forName(charsetName));
  }

  /** Gets the contents of an InputStream as a list of Strings, one entry per line. */
  public static List<String> readLines(final InputStream input) throws IOException {
    return readLines(input, Charset.defaultCharset());
  }

  /** Gets the contents of an InputStream as a list of Strings, one entry per line. */
  public static List<String> readLines(final InputStream input, final Charset charset)
      throws IOException {
    final InputStreamReader reader = new InputStreamReader(input, charset);
    return readLines(reader);
  }

  /** Gets the contents of an InputStream as a list of Strings, one entry per line. */
  public static List<String> readLines(final InputStream input, final String charsetName)
      throws IOException {
    return readLines(input, Charset.forName(charsetName));
  }

  /** Gets the contents of a Reader as a list of Strings, one entry per line. */
  public static List<String> readLines(final Reader input) throws IOException {
    final BufferedReader reader = toBufferedReader(input);
    final List<String> list = new ArrayList<>();
    String line;
    while ((line = reader.readLine()) != null) {
      list.add(line);
    }
    return list;
  }

  /**
   * Returns the given reader if it is a BufferedReader, otherwise creates a BufferedReader from the
   * given reader.
   */
  private static BufferedReader toBufferedReader(final Reader reader) {
    return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
  }

  /** Writes bytes from a byte array to an OutputStream. */
  public static void write(final byte[] data, final OutputStream output) throws IOException {
    if (data != null) {
      output.write(data);
    }
  }

  /** Writes chars from a String to a Writer. */
  public static void write(final String data, final Writer output) throws IOException {
    if (data != null) {
      output.write(data);
    }
  }

  /** Writes chars from a String to an OutputStream using the default character encoding. */
  public static void write(final String data, final OutputStream output) throws IOException {
    write(data, output, Charset.defaultCharset());
  }

  /** Writes chars from a String to an OutputStream using the specified character encoding. */
  public static void write(final String data, final OutputStream output, final Charset charset)
      throws IOException {
    if (data != null) {
      output.write(data.getBytes(charset));
    }
  }

  /** Writes chars from a String to an OutputStream using the specified character encoding name. */
  public static void write(final String data, final OutputStream output, final String charsetName)
      throws IOException {
    write(data, output, Charset.forName(charsetName));
  }

  /** Writes chars from a CharSequence to a Writer. */
  public static void write(final CharSequence data, final Writer output) throws IOException {
    if (data != null) {
      write(data.toString(), output);
    }
  }

  /** Writes chars from a CharSequence to an OutputStream using the default character encoding. */
  public static void write(final CharSequence data, final OutputStream output) throws IOException {
    write(data, output, Charset.defaultCharset());
  }

  /** Writes chars from a CharSequence to an OutputStream using the specified character encoding. */
  public static void write(
      final CharSequence data, final OutputStream output, final Charset charset)
      throws IOException {
    if (data != null) {
      write(data.toString(), output, charset);
    }
  }

  /**
   * Writes chars from a CharSequence to an OutputStream using the specified character encoding
   * name.
   */
  public static void write(
      final CharSequence data, final OutputStream output, final String charsetName)
      throws IOException {
    write(data, output, Charset.forName(charsetName));
  }

  /** Writes lines from a collection to an OutputStream using the default character encoding. */
  public static void writeLines(
      final Iterable<?> lines, final String lineEnding, final OutputStream output)
      throws IOException {
    writeLines(lines, lineEnding, output, Charset.defaultCharset());
  }

  /** Writes lines from a collection to an OutputStream using the specified character encoding. */
  public static void writeLines(
      final Iterable<?> lines,
      final String lineEnding,
      final OutputStream output,
      final Charset charset)
      throws IOException {
    if (lines != null) {
      final String ending = lineEnding == null ? System.lineSeparator() : lineEnding;
      for (final Object line : lines) {
        if (line != null) {
          write(line.toString(), output, charset);
        }
        write(ending, output, charset);
      }
    }
  }

  /** Writes lines from a collection to a Writer. */
  public static void writeLines(
      final Iterable<?> lines, final String lineEnding, final Writer writer) throws IOException {
    if (lines != null) {
      final String ending = lineEnding == null ? System.lineSeparator() : lineEnding;
      for (final Object line : lines) {
        if (line != null) {
          writer.write(line.toString());
        }
        writer.write(ending);
      }
    }
  }

  /**
   * Converts the specified string to an input stream, encoded as bytes using the default character
   * encoding.
   */
  public static InputStream toInputStream(final String input) {
    return toInputStream(input, Charset.defaultCharset());
  }

  /**
   * Converts the specified string to an input stream, encoded as bytes using the specified
   * character encoding.
   */
  public static InputStream toInputStream(final String input, final Charset charset) {
    return new ByteArrayInputStream(input.getBytes(charset));
  }

  /**
   * Converts the specified string to an input stream, encoded as bytes using the specified
   * character encoding name.
   */
  public static InputStream toInputStream(final String input, final String charsetName) {
    return toInputStream(input, Charset.forName(charsetName));
  }

  /** Compares the contents of two Streams to determine if they are equal or not. */
  public static boolean contentEquals(final InputStream input1, final InputStream input2)
      throws IOException {
    if (input1 == input2) {
      return true;
    }
    if (input1 == null || input2 == null) {
      return false;
    }

    final byte[] buffer1 = new byte[DEFAULT_BUFFER_SIZE];
    final byte[] buffer2 = new byte[DEFAULT_BUFFER_SIZE];
    int n1;
    int n2;
    while (true) {
      n1 = input1.read(buffer1);
      n2 = input2.read(buffer2);
      if (n1 != n2) {
        return false;
      }
      if (n1 == EOF) {
        return true;
      }
      for (int i = 0; i < n1; i++) {
        if (buffer1[i] != buffer2[i]) {
          return false;
        }
      }
    }
  }

  /** Compares the contents of two Readers to determine if they are equal or not. */
  public static boolean contentEquals(final Reader input1, final Reader input2) throws IOException {
    if (input1 == input2) {
      return true;
    }
    if (input1 == null || input2 == null) {
      return false;
    }

    final char[] buffer1 = new char[DEFAULT_BUFFER_SIZE];
    final char[] buffer2 = new char[DEFAULT_BUFFER_SIZE];
    int n1;
    int n2;
    while (true) {
      n1 = input1.read(buffer1);
      n2 = input2.read(buffer2);
      if (n1 != n2) {
        return false;
      }
      if (n1 == EOF) {
        return true;
      }
      for (int i = 0; i < n1; i++) {
        if (buffer1[i] != buffer2[i]) {
          return false;
        }
      }
    }
  }

  /** Skips bytes from an InputStream. */
  public static long skip(final InputStream input, final long toSkip) throws IOException {
    if (toSkip < 0) {
      throw new IllegalArgumentException("Skip count must be non-negative");
    }
    long remain = toSkip;
    while (remain > 0) {
      final long n = input.skip(remain);
      if (n == 0) {
        // Check if we've reached EOF
        if (input.read() == EOF) {
          break;
        }
        remain--;
      } else {
        remain -= n;
      }
    }
    return toSkip - remain;
  }

  /** Skips characters from a Reader. */
  public static long skip(final Reader input, final long toSkip) throws IOException {
    if (toSkip < 0) {
      throw new IllegalArgumentException("Skip count must be non-negative");
    }
    long remain = toSkip;
    while (remain > 0) {
      final long n = input.skip(remain);
      if (n == 0) {
        // Check if we've reached EOF
        if (input.read() == EOF) {
          break;
        }
        remain--;
      } else {
        remain -= n;
      }
    }
    return toSkip - remain;
  }

  /** Reads bytes from an input stream. */
  public static int read(final InputStream input, final byte[] buffer) throws IOException {
    return read(input, buffer, 0, buffer.length);
  }

  /** Reads bytes from an input stream. */
  public static int read(
      final InputStream input, final byte[] buffer, final int offset, final int length)
      throws IOException {
    if (length < 0) {
      throw new IllegalArgumentException("Length must not be negative");
    }
    int remaining = length;
    while (remaining > 0) {
      final int location = length - remaining;
      final int count = input.read(buffer, offset + location, remaining);
      if (EOF == count) {
        break;
      }
      remaining -= count;
    }
    return length - remaining;
  }

  /** Reads characters from a Reader. */
  public static int read(final Reader input, final char[] buffer) throws IOException {
    return read(input, buffer, 0, buffer.length);
  }

  /** Reads characters from a Reader. */
  public static int read(
      final Reader input, final char[] buffer, final int offset, final int length)
      throws IOException {
    if (length < 0) {
      throw new IllegalArgumentException("Length must not be negative");
    }
    int remaining = length;
    while (remaining > 0) {
      final int location = length - remaining;
      final int count = input.read(buffer, offset + location, remaining);
      if (EOF == count) {
        break;
      }
      remaining -= count;
    }
    return length - remaining;
  }
}
