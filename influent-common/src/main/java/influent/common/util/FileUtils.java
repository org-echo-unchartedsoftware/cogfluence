package influent.common.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File utility methods to replace Apache Commons IO FileUtils. Provides basic file operations using
 * Java NIO.
 */
public class FileUtils {

  /**
   * Reads the contents of a file into a String using UTF-8 encoding.
   *
   * @param file the file to read
   * @return the file contents
   * @throws IOException if an I/O error occurs
   */
  public static String readFileToString(File file) throws IOException {
    return readFileToString(file, StandardCharsets.UTF_8);
  }

  /**
   * Reads the contents of a file into a String using the specified encoding.
   *
   * @param file the file to read
   * @param encoding the encoding to use
   * @return the file contents
   * @throws IOException if an I/O error occurs
   */
  public static String readFileToString(File file, Charset encoding) throws IOException {
    return new String(Files.readAllBytes(file.toPath()), encoding);
  }

  /**
   * Writes a String to a file using UTF-8 encoding.
   *
   * @param file the file to write
   * @param data the content to write
   * @throws IOException if an I/O error occurs
   */
  public static void writeStringToFile(File file, String data) throws IOException {
    writeStringToFile(file, data, StandardCharsets.UTF_8);
  }

  /**
   * Writes a String to a file using the specified encoding.
   *
   * @param file the file to write
   * @param data the content to write
   * @param encoding the encoding to use
   * @throws IOException if an I/O error occurs
   */
  public static void writeStringToFile(File file, String data, Charset encoding)
      throws IOException {
    Files.write(file.toPath(), data.getBytes(encoding));
  }

  /**
   * Copies a file to a new location.
   *
   * @param srcFile the source file
   * @param destFile the destination file
   * @throws IOException if an I/O error occurs
   */
  public static void copyFile(File srcFile, File destFile) throws IOException {
    Files.copy(srcFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * Deletes a file or directory recursively.
   *
   * @param file the file or directory to delete
   * @throws IOException if an I/O error occurs
   */
  public static void deleteDirectory(File file) throws IOException {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      if (files != null) {
        for (File child : files) {
          deleteDirectory(child);
        }
      }
    }
    Files.deleteIfExists(file.toPath());
  }

  /**
   * Lists files in a directory matching a wildcard pattern.
   *
   * @param directory the directory to search
   * @param wildcardPattern the wildcard pattern (e.g., "*.txt")
   * @return list of matching files
   */
  public static List<File> listFiles(File directory, String wildcardPattern) {
    List<File> result = new ArrayList<>();
    if (!directory.isDirectory()) {
      return result;
    }

    String regex = wildcardPattern.replace(".", "\\.").replace("*", ".*").replace("?", ".");
    File[] files = directory.listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().matches(regex)) {
          result.add(file);
        }
      }
    }
    return result;
  }

  /**
   * Creates a directory and all necessary parent directories.
   *
   * @param directory the directory to create
   * @throws IOException if an I/O error occurs
   */
  public static void forceMkdir(File directory) throws IOException {
    Files.createDirectories(directory.toPath());
  }

  /**
   * Reads the contents of a file line by line.
   *
   * @param file the file to read
   * @return list of lines
   * @throws IOException if an I/O error occurs
   */
  public static List<String> readLines(File file) throws IOException {
    return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
  }
}
