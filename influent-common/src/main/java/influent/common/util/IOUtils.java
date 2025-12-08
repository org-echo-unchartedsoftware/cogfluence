package influent.common.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/** IO utility methods to replace Apache Commons IO IOUtils. Provides basic stream operations. */
public class IOUtils {

  private static final int DEFAULT_BUFFER_SIZE = 8192;

  /**
   * Closes a closeable quietly without throwing exceptions.
   *
   * @param closeable the object to close
   */
  public static void closeQuietly(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException e) {
        // Ignore
      }
    }
  }

  /**
   * Reads the contents of an InputStream into a String using UTF-8 encoding.
   *
   * @param input the InputStream to read
   * @return the contents as a String
   * @throws IOException if an I/O error occurs
   */
  public static String toString(InputStream input) throws IOException {
    return toString(input, StandardCharsets.UTF_8);
  }

  /**
   * Reads the contents of an InputStream into a String using the specified encoding.
   *
   * @param input the InputStream to read
   * @param encoding the encoding to use
   * @return the contents as a String
   * @throws IOException if an I/O error occurs
   */
  public static String toString(InputStream input, Charset encoding) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    copy(input, result);
    return result.toString(encoding.name());
  }

  /**
   * Copies bytes from an InputStream to an OutputStream.
   *
   * @param input the InputStream to read from
   * @param output the OutputStream to write to
   * @return the number of bytes copied
   * @throws IOException if an I/O error occurs
   */
  public static long copy(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    long count = 0;
    int n;
    while ((n = input.read(buffer)) != -1) {
      output.write(buffer, 0, n);
      count += n;
    }
    return count;
  }

  /**
   * Reads the contents of an InputStream into a byte array.
   *
   * @param input the InputStream to read
   * @return the contents as a byte array
   * @throws IOException if an I/O error occurs
   */
  public static byte[] toByteArray(InputStream input) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    copy(input, output);
    return output.toByteArray();
  }

  /**
   * Writes bytes to an OutputStream.
   *
   * @param data the byte array to write
   * @param output the OutputStream to write to
   * @throws IOException if an I/O error occurs
   */
  public static void write(byte[] data, OutputStream output) throws IOException {
    if (data != null) {
      output.write(data);
    }
  }

  /**
   * Writes a String to an OutputStream using UTF-8 encoding.
   *
   * @param data the String to write
   * @param output the OutputStream to write to
   * @throws IOException if an I/O error occurs
   */
  public static void write(String data, OutputStream output) throws IOException {
    write(data, output, StandardCharsets.UTF_8);
  }

  /**
   * Writes a String to an OutputStream using the specified encoding.
   *
   * @param data the String to write
   * @param output the OutputStream to write to
   * @param encoding the encoding to use
   * @throws IOException if an I/O error occurs
   */
  public static void write(String data, OutputStream output, Charset encoding) throws IOException {
    if (data != null) {
      output.write(data.getBytes(encoding));
    }
  }
}
