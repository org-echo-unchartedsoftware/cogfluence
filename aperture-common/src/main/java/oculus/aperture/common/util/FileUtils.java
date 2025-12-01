package oculus.aperture.common.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** General file manipulation utilities. Replacement for Apache Commons IO FileUtils. */
public final class FileUtils {

  private static final long ONE_KB = 1024;
  private static final long ONE_MB = ONE_KB * ONE_KB;
  private static final long ONE_GB = ONE_KB * ONE_MB;

  private FileUtils() {
    // Utility class
  }

  /** Deletes a file or directory. */
  public static void deleteDirectory(final File directory) throws IOException {
    if (!directory.exists()) {
      return;
    }

    if (!isSymlink(directory)) {
      cleanDirectory(directory);
    }

    if (!directory.delete()) {
      throw new IOException("Unable to delete directory " + directory);
    }
  }

  /** Cleans a directory without deleting it. */
  public static void cleanDirectory(final File directory) throws IOException {
    if (!directory.exists()) {
      throw new IllegalArgumentException(directory + " does not exist");
    }

    if (!directory.isDirectory()) {
      throw new IllegalArgumentException(directory + " is not a directory");
    }

    final File[] files = directory.listFiles();
    if (files == null) {
      throw new IOException("Failed to list contents of " + directory);
    }

    IOException exception = null;
    for (final File file : files) {
      try {
        forceDelete(file);
      } catch (final IOException ioe) {
        exception = ioe;
      }
    }

    if (exception != null) {
      throw exception;
    }
  }

  /** Deletes a file. If file is a directory, delete it and all sub-directories. */
  public static void forceDelete(final File file) throws IOException {
    if (file.isDirectory()) {
      deleteDirectory(file);
    } else {
      final boolean filePresent = file.exists();
      if (!file.delete()) {
        if (!filePresent) {
          throw new FileNotFoundException("File does not exist: " + file);
        }
        throw new IOException("Unable to delete file: " + file);
      }
    }
  }

  /** Determines whether the specified file is a symbolic link. */
  private static boolean isSymlink(final File file) {
    try {
      return Files.isSymbolicLink(file.toPath());
    } catch (Exception e) {
      return false;
    }
  }

  /** Reads the contents of a file into a String using the default encoding. */
  public static String readFileToString(final File file) throws IOException {
    return readFileToString(file, Charset.defaultCharset());
  }

  /** Reads the contents of a file into a String using the specified encoding. */
  public static String readFileToString(final File file, final Charset charset) throws IOException {
    try (InputStream in = new FileInputStream(file)) {
      return IOUtils.toString(in, charset);
    }
  }

  /** Reads the contents of a file into a String using the specified encoding name. */
  public static String readFileToString(final File file, final String charsetName)
      throws IOException {
    return readFileToString(file, Charset.forName(charsetName));
  }

  /** Reads the contents of a file into a byte array. */
  public static byte[] readFileToByteArray(final File file) throws IOException {
    try (InputStream in = new FileInputStream(file)) {
      return IOUtils.toByteArray(in);
    }
  }

  /** Reads the contents of a file line by line to a List of Strings using the default encoding. */
  public static List<String> readLines(final File file) throws IOException {
    return readLines(file, Charset.defaultCharset());
  }

  /**
   * Reads the contents of a file line by line to a List of Strings using the specified encoding.
   */
  public static List<String> readLines(final File file, final Charset charset) throws IOException {
    try (InputStream in = new FileInputStream(file)) {
      return IOUtils.readLines(in, charset);
    }
  }

  /**
   * Reads the contents of a file line by line to a List of Strings using the specified encoding
   * name.
   */
  public static List<String> readLines(final File file, final String charsetName)
      throws IOException {
    return readLines(file, Charset.forName(charsetName));
  }

  /** Writes a String to a file creating the file if it does not exist. */
  public static void writeStringToFile(final File file, final String data) throws IOException {
    writeStringToFile(file, data, Charset.defaultCharset(), false);
  }

  /** Writes a String to a file creating the file if it does not exist. */
  public static void writeStringToFile(final File file, final String data, final Charset charset)
      throws IOException {
    writeStringToFile(file, data, charset, false);
  }

  /** Writes a String to a file creating the file if it does not exist. */
  public static void writeStringToFile(
      final File file, final String data, final Charset charset, final boolean append)
      throws IOException {
    try (OutputStream out = new FileOutputStream(file, append)) {
      IOUtils.write(data, out, charset);
    }
  }

  /** Writes a String to a file creating the file if it does not exist. */
  public static void writeStringToFile(final File file, final String data, final String charsetName)
      throws IOException {
    writeStringToFile(file, data, Charset.forName(charsetName), false);
  }

  /** Writes a String to a file creating the file if it does not exist. */
  public static void writeStringToFile(
      final File file, final String data, final String charsetName, final boolean append)
      throws IOException {
    writeStringToFile(file, data, Charset.forName(charsetName), append);
  }

  /** Writes a byte array to a file creating the file if it does not exist. */
  public static void writeByteArrayToFile(final File file, final byte[] data) throws IOException {
    writeByteArrayToFile(file, data, false);
  }

  /** Writes a byte array to a file creating the file if it does not exist. */
  public static void writeByteArrayToFile(final File file, final byte[] data, final boolean append)
      throws IOException {
    try (OutputStream out = new FileOutputStream(file, append)) {
      out.write(data);
    }
  }

  /** Writes the toString() value of each item in a collection to a file line by line. */
  public static void writeLines(final File file, final Collection<?> lines) throws IOException {
    writeLines(file, null, lines, null, false);
  }

  /** Writes the toString() value of each item in a collection to a file line by line. */
  public static void writeLines(final File file, final Collection<?> lines, final boolean append)
      throws IOException {
    writeLines(file, null, lines, null, append);
  }

  /** Writes the toString() value of each item in a collection to a file line by line. */
  public static void writeLines(
      final File file, final String charsetName, final Collection<?> lines) throws IOException {
    writeLines(file, charsetName, lines, null, false);
  }

  /** Writes the toString() value of each item in a collection to a file line by line. */
  public static void writeLines(
      final File file,
      final String charsetName,
      final Collection<?> lines,
      final String lineEnding,
      final boolean append)
      throws IOException {
    final Charset charset =
        charsetName != null ? Charset.forName(charsetName) : Charset.defaultCharset();
    try (OutputStream out = new FileOutputStream(file, append)) {
      IOUtils.writeLines(lines, lineEnding, out, charset);
    }
  }

  /** Copies a file to a new location. */
  public static void copyFile(final File srcFile, final File destFile) throws IOException {
    copyFile(srcFile, destFile, true);
  }

  /** Copies a file to a new location. */
  public static void copyFile(
      final File srcFile, final File destFile, final boolean preserveFileDate) throws IOException {
    if (srcFile == null) {
      throw new NullPointerException("Source must not be null");
    }
    if (destFile == null) {
      throw new NullPointerException("Destination must not be null");
    }
    if (!srcFile.exists()) {
      throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
    }
    if (srcFile.isDirectory()) {
      throw new IOException("Source '" + srcFile + "' is a directory");
    }
    if (destFile.exists() && destFile.isDirectory()) {
      throw new IOException("Destination '" + destFile + "' is a directory");
    }

    final File parentFile = destFile.getParentFile();
    if (parentFile != null && !parentFile.exists()) {
      if (!parentFile.mkdirs()) {
        throw new IOException("Failed to create directory '" + parentFile + "'");
      }
    }

    try (FileInputStream fis = new FileInputStream(srcFile);
        FileOutputStream fos = new FileOutputStream(destFile)) {
      IOUtils.copy(fis, fos);
    }

    if (srcFile.length() != destFile.length()) {
      throw new IOException(
          "Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
    }

    if (preserveFileDate) {
      destFile.setLastModified(srcFile.lastModified());
    }
  }

  /** Copies a directory to within another directory. */
  public static void copyDirectory(final File srcDir, final File destDir) throws IOException {
    copyDirectory(srcDir, destDir, true);
  }

  /** Copies a directory to within another directory. */
  public static void copyDirectory(
      final File srcDir, final File destDir, final boolean preserveFileDate) throws IOException {
    if (srcDir == null) {
      throw new NullPointerException("Source must not be null");
    }
    if (destDir == null) {
      throw new NullPointerException("Destination must not be null");
    }
    if (!srcDir.exists()) {
      throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
    }
    if (!srcDir.isDirectory()) {
      throw new IOException("Source '" + srcDir + "' is not a directory");
    }

    if (destDir.exists()) {
      if (!destDir.isDirectory()) {
        throw new IOException("Destination '" + destDir + "' is not a directory");
      }
    } else {
      if (!destDir.mkdirs()) {
        throw new IOException("Failed to create directory '" + destDir + "'");
      }
      if (preserveFileDate) {
        destDir.setLastModified(srcDir.lastModified());
      }
    }

    final File[] files = srcDir.listFiles();
    if (files == null) {
      throw new IOException("Failed to list contents of " + srcDir);
    }

    for (final File file : files) {
      final File destFile = new File(destDir, file.getName());
      if (file.isDirectory()) {
        copyDirectory(file, destFile, preserveFileDate);
      } else {
        copyFile(file, destFile, preserveFileDate);
      }
    }
  }

  /** Moves a file. */
  public static void moveFile(final File srcFile, final File destFile) throws IOException {
    if (srcFile == null) {
      throw new NullPointerException("Source must not be null");
    }
    if (destFile == null) {
      throw new NullPointerException("Destination must not be null");
    }
    if (!srcFile.exists()) {
      throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
    }
    if (srcFile.isDirectory()) {
      throw new IOException("Source '" + srcFile + "' is a directory");
    }
    if (destFile.exists()) {
      throw new IOException("Destination '" + destFile + "' already exists");
    }
    if (destFile.isDirectory()) {
      throw new IOException("Destination '" + destFile + "' is a directory");
    }

    final boolean rename = srcFile.renameTo(destFile);
    if (!rename) {
      copyFile(srcFile, destFile);
      if (!srcFile.delete()) {
        deleteQuietly(destFile);
        throw new IOException(
            "Failed to delete original file '" + srcFile + "' after copy to '" + destFile + "'");
      }
    }
  }

  /** Moves a directory. */
  public static void moveDirectory(final File srcDir, final File destDir) throws IOException {
    if (srcDir == null) {
      throw new NullPointerException("Source must not be null");
    }
    if (destDir == null) {
      throw new NullPointerException("Destination must not be null");
    }
    if (!srcDir.exists()) {
      throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
    }
    if (!srcDir.isDirectory()) {
      throw new IOException("Source '" + srcDir + "' is not a directory");
    }
    if (destDir.exists()) {
      throw new IOException("Destination '" + destDir + "' already exists");
    }

    final boolean rename = srcDir.renameTo(destDir);
    if (!rename) {
      copyDirectory(srcDir, destDir);
      deleteDirectory(srcDir);
    }
  }

  /** Deletes a file, never throwing an exception. */
  public static boolean deleteQuietly(final File file) {
    if (file == null) {
      return false;
    }
    try {
      if (file.isDirectory()) {
        cleanDirectory(file);
      }
    } catch (final Exception ignored) {
      // ignore
    }

    try {
      return file.delete();
    } catch (final Exception ignored) {
      return false;
    }
  }

  /** Makes a directory, including any necessary but nonexistent parent directories. */
  public static void forceMkdir(final File directory) throws IOException {
    if (directory.exists()) {
      if (!directory.isDirectory()) {
        throw new IOException("File '" + directory + "' exists and is not a directory");
      }
    } else {
      if (!directory.mkdirs()) {
        throw new IOException("Failed to create directory '" + directory + "'");
      }
    }
  }

  /** Returns the size of the specified file or directory. */
  public static long sizeOf(final File file) {
    if (!file.exists()) {
      throw new IllegalArgumentException(file + " does not exist");
    }

    if (file.isDirectory()) {
      return sizeOfDirectory(file);
    } else {
      return file.length();
    }
  }

  /** Counts the size of a directory recursively. */
  public static long sizeOfDirectory(final File directory) {
    if (!directory.exists()) {
      throw new IllegalArgumentException(directory + " does not exist");
    }

    if (!directory.isDirectory()) {
      throw new IllegalArgumentException(directory + " is not a directory");
    }

    long size = 0;

    final File[] files = directory.listFiles();
    if (files == null) {
      return 0;
    }

    for (final File file : files) {
      if (file.isDirectory()) {
        size += sizeOfDirectory(file);
      } else {
        size += file.length();
      }
    }

    return size;
  }

  /** Returns a human-readable version of the file size. */
  public static String byteCountToDisplaySize(final long size) {
    if (size / ONE_GB > 0) {
      return String.format("%.2f GB", (double) size / ONE_GB);
    } else if (size / ONE_MB > 0) {
      return String.format("%.2f MB", (double) size / ONE_MB);
    } else if (size / ONE_KB > 0) {
      return String.format("%.2f KB", (double) size / ONE_KB);
    } else {
      return size + " bytes";
    }
  }

  /** Tests if the specified File is newer than the reference File. */
  public static boolean isFileNewer(final File file, final File reference) {
    if (reference == null) {
      throw new IllegalArgumentException("No specified reference file");
    }
    if (!reference.exists()) {
      throw new IllegalArgumentException("The reference file '" + reference + "' doesn't exist");
    }
    return isFileNewer(file, reference.lastModified());
  }

  /** Tests if the specified File is newer than the specified time reference. */
  public static boolean isFileNewer(final File file, final long timeMillis) {
    if (file == null) {
      throw new IllegalArgumentException("No specified file");
    }
    if (!file.exists()) {
      return false;
    }
    return file.lastModified() > timeMillis;
  }

  /** Tests if the specified File is older than the reference File. */
  public static boolean isFileOlder(final File file, final File reference) {
    if (reference == null) {
      throw new IllegalArgumentException("No specified reference file");
    }
    if (!reference.exists()) {
      throw new IllegalArgumentException("The reference file '" + reference + "' doesn't exist");
    }
    return isFileOlder(file, reference.lastModified());
  }

  /** Tests if the specified File is older than the specified time reference. */
  public static boolean isFileOlder(final File file, final long timeMillis) {
    if (file == null) {
      throw new IllegalArgumentException("No specified file");
    }
    if (!file.exists()) {
      return false;
    }
    return file.lastModified() < timeMillis;
  }

  /** Finds files within a given directory (and optionally its subdirectories). */
  public static Collection<File> listFiles(
      final File directory, final String[] extensions, final boolean recursive) {
    final List<File> files = new ArrayList<>();
    if (!directory.isDirectory()) {
      return files;
    }
    innerListFiles(files, directory, extensions, recursive);
    return files;
  }

  /** Internal method to list files. */
  private static void innerListFiles(
      final Collection<File> files,
      final File directory,
      final String[] extensions,
      final boolean recursive) {
    final File[] found = directory.listFiles();
    if (found != null) {
      for (final File file : found) {
        if (file.isDirectory()) {
          if (recursive) {
            innerListFiles(files, file, extensions, true);
          }
        } else {
          if (extensions == null) {
            files.add(file);
          } else {
            final String name = file.getName();
            for (final String extension : extensions) {
              if (name.endsWith("." + extension)) {
                files.add(file);
                break;
              }
            }
          }
        }
      }
    }
  }

  /** Compares the contents of two files to determine if they are equal or not. */
  public static boolean contentEquals(final File file1, final File file2) throws IOException {
    final boolean file1Exists = file1.exists();
    if (file1Exists != file2.exists()) {
      return false;
    }

    if (!file1Exists) {
      return true;
    }

    if (file1.isDirectory() || file2.isDirectory()) {
      throw new IOException("Can't compare directories");
    }

    if (file1.length() != file2.length()) {
      return false;
    }

    if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
      return true;
    }

    try (InputStream input1 = new FileInputStream(file1);
        InputStream input2 = new FileInputStream(file2)) {
      return IOUtils.contentEquals(input1, input2);
    }
  }

  /** Gets the extension of a filename. */
  public static String getExtension(final String filename) {
    if (filename == null) {
      return null;
    }
    final int index = filename.lastIndexOf('.');
    if (index == -1) {
      return "";
    } else {
      return filename.substring(index + 1);
    }
  }

  /** Removes the extension from a filename. */
  public static String removeExtension(final String filename) {
    if (filename == null) {
      return null;
    }
    final int index = filename.lastIndexOf('.');
    if (index == -1) {
      return filename;
    } else {
      return filename.substring(0, index);
    }
  }
}
