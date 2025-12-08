package influent.common.util;

/**
 * Simple stopwatch utility to replace Apache Commons Lang StopWatch. Provides basic timing
 * functionality.
 */
public class StopWatch {

  private long startTime = 0;
  private long stopTime = 0;
  private boolean running = false;

  /** Starts the stopwatch. */
  public void start() {
    this.startTime = System.currentTimeMillis();
    this.running = true;
  }

  /** Stops the stopwatch. */
  public void stop() {
    this.stopTime = System.currentTimeMillis();
    this.running = false;
  }

  /** Resets the stopwatch. */
  public void reset() {
    this.startTime = 0;
    this.stopTime = 0;
    this.running = false;
  }

  /**
   * Gets the elapsed time in milliseconds.
   *
   * @return the elapsed time
   */
  public long getTime() {
    if (running) {
      return System.currentTimeMillis() - startTime;
    } else {
      return stopTime - startTime;
    }
  }

  /**
   * Checks if the stopwatch is running.
   *
   * @return true if running
   */
  public boolean isRunning() {
    return running;
  }

  /** Suspends the stopwatch. */
  public void suspend() {
    if (running) {
      stopTime = System.currentTimeMillis();
      running = false;
    }
  }

  /** Resumes the stopwatch. */
  public void resume() {
    if (!running) {
      startTime += (System.currentTimeMillis() - stopTime);
      running = true;
    }
  }

  @Override
  public String toString() {
    return String.valueOf(getTime());
  }
}
