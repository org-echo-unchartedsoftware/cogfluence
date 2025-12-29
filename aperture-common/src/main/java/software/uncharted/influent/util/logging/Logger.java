package software.uncharted.influent.util.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

/** Simple logging facade to replace SLF4J. Uses java.util.logging as the backend. */
public class Logger {
  private static final ConcurrentHashMap<String, Logger> loggers = new ConcurrentHashMap<>();
  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  private final String name;
  private final java.util.logging.Logger julLogger;

  private Logger(String name) {
    this.name = name;
    this.julLogger = java.util.logging.Logger.getLogger(name);
  }

  /**
   * Get a logger for the specified class
   *
   * @param clazz the class
   * @return the logger
   */
  public static Logger getLogger(Class<?> clazz) {
    return getLogger(clazz.getName());
  }

  /**
   * Get a logger for the specified name
   *
   * @param name the logger name
   * @return the logger
   */
  public static Logger getLogger(String name) {
    return loggers.computeIfAbsent(name, Logger::new);
  }

  private String formatMessage(String level, String message) {
    return String.format(
        "[%s] [%s] [%s] %s",
        formatter.format(LocalDateTime.now()), level, Thread.currentThread().getName(), message);
  }

  private String formatMessage(String level, String message, Throwable throwable) {
    StringBuilder sb = new StringBuilder();
    sb.append(formatMessage(level, message));
    sb.append("\n");
    sb.append(throwable.getClass().getName());
    sb.append(": ");
    sb.append(throwable.getMessage());
    for (StackTraceElement element : throwable.getStackTrace()) {
      sb.append("\n\tat ");
      sb.append(element.toString());
    }
    if (throwable.getCause() != null) {
      sb.append("\nCaused by: ");
      sb.append(formatMessage(level, "", throwable.getCause()));
    }
    return sb.toString();
  }

  public void trace(String message) {
    julLogger.finest(formatMessage("TRACE", message));
  }

  public void trace(String message, Object... args) {
    julLogger.finest(formatMessage("TRACE", String.format(message, args)));
  }

  public void debug(String message) {
    julLogger.fine(formatMessage("DEBUG", message));
  }

  public void debug(String message, Object... args) {
    julLogger.fine(formatMessage("DEBUG", String.format(message, args)));
  }

  public void info(String message) {
    julLogger.info(formatMessage("INFO", message));
  }

  public void info(String message, Object... args) {
    julLogger.info(formatMessage("INFO", String.format(message, args)));
  }

  public void warn(String message) {
    julLogger.warning(formatMessage("WARN", message));
  }

  public void warn(String message, Object... args) {
    julLogger.warning(formatMessage("WARN", String.format(message, args)));
  }

  public void warn(String message, Throwable throwable) {
    julLogger.warning(formatMessage("WARN", message, throwable));
  }

  public void error(String message) {
    julLogger.severe(formatMessage("ERROR", message));
  }

  public void error(String message, Object... args) {
    julLogger.severe(formatMessage("ERROR", String.format(message, args)));
  }

  public void error(String message, Throwable throwable) {
    julLogger.severe(formatMessage("ERROR", message, throwable));
  }

  public boolean isTraceEnabled() {
    return julLogger.isLoggable(java.util.logging.Level.FINEST);
  }

  public boolean isDebugEnabled() {
    return julLogger.isLoggable(java.util.logging.Level.FINE);
  }

  public boolean isInfoEnabled() {
    return julLogger.isLoggable(java.util.logging.Level.INFO);
  }

  public boolean isWarnEnabled() {
    return julLogger.isLoggable(java.util.logging.Level.WARNING);
  }

  public boolean isErrorEnabled() {
    return julLogger.isLoggable(java.util.logging.Level.SEVERE);
  }
}
