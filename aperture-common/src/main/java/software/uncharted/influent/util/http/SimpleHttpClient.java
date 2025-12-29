package software.uncharted.influent.util.http;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Simple HTTP client using Java's built-in HttpURLConnection. Replaces Apache HttpClient for basic
 * HTTP operations.
 */
public class SimpleHttpClient {

  private int connectTimeout = 30000; // 30 seconds
  private int readTimeout = 30000; // 30 seconds
  private Map<String, String> defaultHeaders = new HashMap<>();

  public SimpleHttpClient() {
    defaultHeaders.put("User-Agent", "SimpleHttpClient/1.0");
  }

  public void setConnectTimeout(int timeout) {
    this.connectTimeout = timeout;
  }

  public void setReadTimeout(int timeout) {
    this.readTimeout = timeout;
  }

  public void setDefaultHeader(String name, String value) {
    defaultHeaders.put(name, value);
  }

  /**
   * Execute a GET request
   *
   * @param url the URL
   * @return the response
   * @throws IOException if an error occurs
   */
  public HttpResponse get(String url) throws IOException {
    return execute("GET", url, null, null);
  }

  /**
   * Execute a GET request with headers
   *
   * @param url the URL
   * @param headers the headers
   * @return the response
   * @throws IOException if an error occurs
   */
  public HttpResponse get(String url, Map<String, String> headers) throws IOException {
    return execute("GET", url, headers, null);
  }

  /**
   * Execute a POST request
   *
   * @param url the URL
   * @param body the request body
   * @return the response
   * @throws IOException if an error occurs
   */
  public HttpResponse post(String url, String body) throws IOException {
    return execute("POST", url, null, body);
  }

  /**
   * Execute a POST request with headers
   *
   * @param url the URL
   * @param headers the headers
   * @param body the request body
   * @return the response
   * @throws IOException if an error occurs
   */
  public HttpResponse post(String url, Map<String, String> headers, String body)
      throws IOException {
    return execute("POST", url, headers, body);
  }

  /**
   * Execute a PUT request
   *
   * @param url the URL
   * @param body the request body
   * @return the response
   * @throws IOException if an error occurs
   */
  public HttpResponse put(String url, String body) throws IOException {
    return execute("PUT", url, null, body);
  }

  /**
   * Execute a DELETE request
   *
   * @param url the URL
   * @return the response
   * @throws IOException if an error occurs
   */
  public HttpResponse delete(String url) throws IOException {
    return execute("DELETE", url, null, null);
  }

  /**
   * Execute an HTTP request
   *
   * @param method the HTTP method
   * @param urlString the URL
   * @param headers the headers (can be null)
   * @param body the request body (can be null)
   * @return the response
   * @throws IOException if an error occurs
   */
  private HttpResponse execute(
      String method, String urlString, Map<String, String> headers, String body)
      throws IOException {
    URL url = new URL(urlString);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    try {
      conn.setRequestMethod(method);
      conn.setConnectTimeout(connectTimeout);
      conn.setReadTimeout(readTimeout);

      // Set default headers
      for (Map.Entry<String, String> entry : defaultHeaders.entrySet()) {
        conn.setRequestProperty(entry.getKey(), entry.getValue());
      }

      // Set custom headers
      if (headers != null) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
          conn.setRequestProperty(entry.getKey(), entry.getValue());
        }
      }

      // Send body if present
      if (body != null && !body.isEmpty()) {
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
          byte[] input = body.getBytes(StandardCharsets.UTF_8);
          os.write(input, 0, input.length);
        }
      }

      // Read response
      int statusCode = conn.getResponseCode();
      String responseBody;

      try {
        InputStream is =
            (statusCode >= 200 && statusCode < 300) ? conn.getInputStream() : conn.getErrorStream();
        responseBody = readStream(is);
      } catch (IOException e) {
        responseBody = "";
      }

      Map<String, String> responseHeaders = new HashMap<>();
      for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
        if (entry.getKey() != null && entry.getValue() != null && !entry.getValue().isEmpty()) {
          responseHeaders.put(entry.getKey(), entry.getValue().get(0));
        }
      }

      return new HttpResponse(statusCode, responseBody, responseHeaders);
    } finally {
      conn.disconnect();
    }
  }

  private String readStream(InputStream is) throws IOException {
    if (is == null) {
      return "";
    }
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line).append('\n');
      }
      return response.toString();
    }
  }

  /** HTTP Response */
  public static class HttpResponse {
    private final int statusCode;
    private final String body;
    private final Map<String, String> headers;

    public HttpResponse(int statusCode, String body, Map<String, String> headers) {
      this.statusCode = statusCode;
      this.body = body;
      this.headers = headers;
    }

    public int getStatusCode() {
      return statusCode;
    }

    public String getBody() {
      return body;
    }

    public Map<String, String> getHeaders() {
      return headers;
    }

    public String getHeader(String name) {
      return headers.get(name);
    }

    public boolean isSuccess() {
      return statusCode >= 200 && statusCode < 300;
    }
  }
}
