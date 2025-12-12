package oculus.aperture.common.json;

/**
 * Custom JSONException to replace org.json.JSONException Provides a lightweight alternative without
 * external dependencies
 */
public class JSONException extends Exception {
  private static final long serialVersionUID = 1L;

  public JSONException(String message) {
    super(message);
  }

  public JSONException(String message, Throwable cause) {
    super(message, cause);
  }

  public JSONException(Throwable cause) {
    super(cause);
  }
}
