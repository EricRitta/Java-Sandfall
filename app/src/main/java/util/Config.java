package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

public class Config {
  private static final Properties VARIABLES = new Properties();
  private static final Map<String, String> CONSTANTS = new HashMap<String, String>();

  static {
    try (InputStream input = Config.class.getResourceAsStream("/config.properties")) {
      if (input == null) {
          throw new RuntimeException("config.properties not found in classpath");
      }
      VARIABLES.load(input);
    } catch (IOException e) {
      throw new RuntimeException("Error while loading config.properties", e);
    }
    // Constants
    CONSTANTS.put("OUT_OF_WORLD", "-2147483648");
    CONSTANTS.put("CHUNK_FIELDS", "3");
    CONSTANTS.put("CELL_FIELD", "0");
    CONSTANTS.put("CELL_DEADLINE_FIELD", "1");
    CONSTANTS.put("CELL_SKIP_THIS_FRAME_FIELD", "2");
  }

  public static String get(String key) {
    String result = VARIABLES.getProperty(key.toUpperCase());
    if (result == null) { result = CONSTANTS.get(key.toUpperCase()); }
    if (result == null) { throw new IllegalArgumentException(key + " key doesn't exist or is null."); }
    return result;
  }

  public static int getInt(String key) {
    return Integer.parseInt(get(key));
  }

  public static int getId(String key) {
    return getInt(key + "_ID");
  }

  public static boolean isOutOfWorld(int out) {
    return out == getInt("OUT_OF_WORLD") ? true : false;
  }

  public static String outOfBoundString(int out) {
    return isOutOfWorld(out) ? "OUT OF BOUNDS" : "IN BOUNDS";
  }
}
