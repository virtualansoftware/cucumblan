package io.virtualan.cucumblan.props;

import java.util.Map;
import java.util.Properties;

/**
 * The type Application configuration.
 *
 * @author Elan Thangamani
 */
public class ApplicationConfiguration {
  private static Properties properties = new Properties();
  static {
    try {
      properties.load(ApplicationConfiguration.class.getClassLoader().getResourceAsStream("cucumblan.properties"));
    } catch (Exception e) {

    }
  }

  /**
   * Gets properties.
   *
   * @return the properties
   */
  public static Map<String, String> getProperties() {
    return (Map)properties;
  }

  /**
   * Gets property.
   *
   * @param keyName the key name
   * @return the property
   */
  public static String getProperty(String keyName) {
    return properties.getProperty(keyName);
  }

}
