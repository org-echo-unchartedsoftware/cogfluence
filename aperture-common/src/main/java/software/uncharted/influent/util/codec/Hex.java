package software.uncharted.influent.util.codec;

/** Hex encoding and decoding utility. Replaces org.apache.commons.codec.binary.Hex */
public class Hex {
  private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

  /**
   * Encode bytes to hex string
   *
   * @param bytes the bytes to encode
   * @return hex string
   */
  public static String encodeHexString(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      int v = bytes[i] & 0xFF;
      hexChars[i * 2] = HEX_ARRAY[v >>> 4];
      hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  /**
   * Encode bytes to hex char array
   *
   * @param bytes the bytes to encode
   * @return hex char array
   */
  public static char[] encodeHex(byte[] bytes) {
    return encodeHexString(bytes).toCharArray();
  }

  /**
   * Decode hex string to bytes
   *
   * @param hexString the hex string to decode
   * @return decoded bytes
   */
  public static byte[] decodeHex(String hexString) {
    return decodeHex(hexString.toCharArray());
  }

  /**
   * Decode hex char array to bytes
   *
   * @param hexChars the hex char array to decode
   * @return decoded bytes
   */
  public static byte[] decodeHex(char[] hexChars) {
    int len = hexChars.length;
    if (len % 2 != 0) {
      throw new IllegalArgumentException("Hex string must have even length");
    }

    byte[] bytes = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      int high = Character.digit(hexChars[i], 16);
      int low = Character.digit(hexChars[i + 1], 16);

      if (high == -1 || low == -1) {
        throw new IllegalArgumentException("Invalid hex character");
      }

      bytes[i / 2] = (byte) ((high << 4) + low);
    }
    return bytes;
  }
}
