package software.uncharted.influent.util.compress;

import java.io.*;
import java.util.zip.*;

/**
 * Compression utilities for GZIP, ZIP, and BZIP2. Replaces org.apache.commons.compress
 * functionality with Java built-in compression.
 */
public class CompressionUtils {

  /** GZIP Compression */
  public static class GzipUtils {
    public static InputStream createInputStream(InputStream in) throws IOException {
      return new GZIPInputStream(in);
    }

    public static OutputStream createOutputStream(OutputStream out) throws IOException {
      return new GZIPOutputStream(out);
    }

    public static byte[] compress(byte[] data) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
        gzos.write(data);
      }
      return baos.toByteArray();
    }

    public static byte[] decompress(byte[] data) throws IOException {
      ByteArrayInputStream bais = new ByteArrayInputStream(data);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try (GZIPInputStream gzis = new GZIPInputStream(bais)) {
        byte[] buffer = new byte[8192];
        int len;
        while ((len = gzis.read(buffer)) > 0) {
          baos.write(buffer, 0, len);
        }
      }
      return baos.toByteArray();
    }
  }

  /** ZIP Compression */
  public static class ZipUtils {
    public static ZipInputStream createInputStream(InputStream in) {
      return new ZipInputStream(in);
    }

    public static ZipOutputStream createOutputStream(OutputStream out) {
      return new ZipOutputStream(out);
    }

    public static void addEntry(ZipOutputStream zos, String name, byte[] data) throws IOException {
      ZipEntry entry = new ZipEntry(name);
      zos.putNextEntry(entry);
      zos.write(data);
      zos.closeEntry();
    }

    public static byte[] readEntry(ZipInputStream zis) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int len;
      while ((len = zis.read(buffer)) > 0) {
        baos.write(buffer, 0, len);
      }
      return baos.toByteArray();
    }
  }

  /**
   * BZIP2 Compression Note: Java doesn't have built-in BZIP2 support. This is a minimal
   * implementation that wraps the data with BZIP2 header. For full BZIP2 support, consider keeping
   * the Apache Commons Compress dependency or implementing a full BZIP2 codec.
   */
  public static class BZip2Utils {
    // BZIP2 magic header
    private static final byte[] BZIP2_HEADER = {'B', 'Z', 'h', '9'};

    public static InputStream createInputStream(InputStream in) throws IOException {
      // This is a placeholder - full BZIP2 decompression requires a complete implementation
      // For now, we'll throw an exception to indicate this needs the external library
      throw new UnsupportedOperationException(
          "BZIP2 decompression requires Apache Commons Compress or a full BZIP2 implementation");
    }

    public static OutputStream createOutputStream(OutputStream out) throws IOException {
      // This is a placeholder - full BZIP2 compression requires a complete implementation
      throw new UnsupportedOperationException(
          "BZIP2 compression requires Apache Commons Compress or a full BZIP2 implementation");
    }
  }

  /** Deflate Compression (used by ZIP) */
  public static class DeflateUtils {
    public static byte[] compress(byte[] data) throws IOException {
      Deflater deflater = new Deflater();
      deflater.setInput(data);
      deflater.finish();

      ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
      byte[] buffer = new byte[8192];
      while (!deflater.finished()) {
        int count = deflater.deflate(buffer);
        baos.write(buffer, 0, count);
      }
      deflater.end();
      return baos.toByteArray();
    }

    public static byte[] decompress(byte[] data) throws IOException, DataFormatException {
      Inflater inflater = new Inflater();
      inflater.setInput(data);

      ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
      byte[] buffer = new byte[8192];
      while (!inflater.finished()) {
        int count = inflater.inflate(buffer);
        baos.write(buffer, 0, count);
      }
      inflater.end();
      return baos.toByteArray();
    }
  }
}
