package settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
  private static final Properties VARIABLES = new Properties();

  static {
    try (InputStream input = Config.class.getResourceAsStream("/config.properties")) {
      if (input == null) {
          throw new RuntimeException("config.properties not found in classpath");
      }
      VARIABLES.load(input);
    } catch (IOException e) {
      throw new RuntimeException("Error while loading config.properties", e);
    }
  }

  public static String get(String key) {
    String result = VARIABLES.getProperty(key.toUpperCase());
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
}
