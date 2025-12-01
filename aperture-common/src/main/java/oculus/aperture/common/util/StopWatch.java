package oculus.aperture.common.util;

import java.util.concurrent.TimeUnit;

/** A simple stopwatch for timing operations. Replacement for Apache Commons Lang3 StopWatch. */
public class StopWatch {

  private static final long NANO_2_MILLIS = 1000000L;

  private long startTime;
  private long stopTime;
  private boolean running;
  private long splitTime;
  private boolean suspended;
  private long suspendTime;
  private long totalSuspendedTime;

  /** Constructor. */
  public StopWatch() {
    this.running = false;
    this.suspended = false;
    this.totalSuspendedTime = 0;
  }

  /** Creates and starts a new stopwatch. */
  public static StopWatch createStarted() {
    final StopWatch sw = new StopWatch();
    sw.start();
    return sw;
  }

  /** Starts the stopwatch. */
  public void start() {
    if (this.running) {
      throw new IllegalStateException("Stopwatch is already running");
    }
    this.startTime = System.nanoTime();
    this.running = true;
    this.suspended = false;
    this.totalSuspendedTime = 0;
  }

  /** Stops the stopwatch. */
  public void stop() {
    if (!this.running) {
      throw new IllegalStateException("Stopwatch is not running");
    }
    if (this.suspended) {
      this.suspended = false;
    }
    this.stopTime = System.nanoTime();
    this.running = false;
  }

  /** Resets the stopwatch. */
  public void reset() {
    this.running = false;
    this.suspended = false;
    this.startTime = 0;
    this.stopTime = 0;
    this.splitTime = 0;
    this.totalSuspendedTime = 0;
  }

  /** Splits the time. */
  public void split() {
    if (!this.running) {
      throw new IllegalStateException("Stopwatch is not running");
    }
    this.splitTime = System.nanoTime();
  }

  /** Unsplits the stopwatch. */
  public void unsplit() {
    if (!this.running) {
      throw new IllegalStateException("Stopwatch is not running");
    }
    this.splitTime = 0;
  }

  /** Suspends the stopwatch. */
  public void suspend() {
    if (!this.running) {
      throw new IllegalStateException("Stopwatch is not running");
    }
    if (this.suspended) {
      throw new IllegalStateException("Stopwatch is already suspended");
    }
    this.suspendTime = System.nanoTime();
    this.suspended = true;
  }

  /** Resumes the stopwatch. */
  public void resume() {
    if (!this.suspended) {
      throw new IllegalStateException("Stopwatch is not suspended");
    }
    this.totalSuspendedTime += System.nanoTime() - this.suspendTime;
    this.suspended = false;
  }

  /** Gets the time on the stopwatch in nanoseconds. */
  public long getNanoTime() {
    if (this.running) {
      if (this.suspended) {
        return this.suspendTime - this.startTime - this.totalSuspendedTime;
      }
      return System.nanoTime() - this.startTime - this.totalSuspendedTime;
    }
    return this.stopTime - this.startTime - this.totalSuspendedTime;
  }

  /** Gets the time on the stopwatch in milliseconds. */
  public long getTime() {
    return getNanoTime() / NANO_2_MILLIS;
  }

  /** Gets the time on the stopwatch in the specified time unit. */
  public long getTime(final TimeUnit timeUnit) {
    return timeUnit.convert(getNanoTime(), TimeUnit.NANOSECONDS);
  }

  /** Gets the split time on the stopwatch in nanoseconds. */
  public long getSplitNanoTime() {
    if (this.splitTime == 0) {
      throw new IllegalStateException("Stopwatch has not been split");
    }
    return this.splitTime - this.startTime - this.totalSuspendedTime;
  }

  /** Gets the split time on the stopwatch in milliseconds. */
  public long getSplitTime() {
    return getSplitNanoTime() / NANO_2_MILLIS;
  }

  /** Gets the started time. */
  public long getStartTime() {
    if (!this.running && this.startTime == 0) {
      throw new IllegalStateException("Stopwatch has not been started");
    }
    return this.startTime / NANO_2_MILLIS;
  }

  /** Checks if the stopwatch is started. */
  public boolean isStarted() {
    return this.running || this.stopTime > 0;
  }

  /** Checks if the stopwatch is suspended. */
  public boolean isSuspended() {
    return this.suspended;
  }

  /** Checks if the stopwatch is stopped. */
  public boolean isStopped() {
    return !this.running;
  }

  /** Returns a string representation of the time. */
  @Override
  public String toString() {
    return formatTime(getTime());
  }

  /** Formats the time in a human-readable format. */
  private String formatTime(final long millis) {
    final long hours = TimeUnit.MILLISECONDS.toHours(millis);
    final long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
    final long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
    final long ms = millis % 1000;

    if (hours > 0) {
      return String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, ms);
    } else if (minutes > 0) {
      return String.format("%d:%02d.%03d", minutes, seconds, ms);
    } else {
      return String.format("%d.%03d", seconds, ms);
    }
  }

  /** Returns a string representation of the split time. */
  public String toSplitString() {
    return formatTime(getSplitTime());
  }
}
