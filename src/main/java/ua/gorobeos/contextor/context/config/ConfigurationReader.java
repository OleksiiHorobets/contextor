package ua.gorobeos.contextor.context.config;


import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import lombok.experimental.UtilityClass;
import ua.gorobeos.contextor.context.exceptions.PropertiesLoadException;

@UtilityClass
public class ConfigurationReader {

  private static final String CONFIGURATION_FILE = "application.properties";
  private static final Properties PROPERTIES = new Properties();

  static {
    loadProperties();
  }

  private static void loadProperties() {
    try {
      PROPERTIES.load(ConfigurationReader.class.getClassLoader().getResourceAsStream(CONFIGURATION_FILE));
    } catch (Exception e) {
      throw new PropertiesLoadException("Failed to load configuration file: " + CONFIGURATION_FILE, e);
    }
  }


  public static String getOrDefault(String key, String defaultValue) {
    return PROPERTIES.getProperty(key, defaultValue);
  }

  public static <T> T getOrDefault(String propertyName, T defaultValue, Function<String, T> mapper) {
    return Optional.ofNullable(PROPERTIES.getProperty(propertyName))
        .map(mapper)
        .orElse(defaultValue);
  }


}
