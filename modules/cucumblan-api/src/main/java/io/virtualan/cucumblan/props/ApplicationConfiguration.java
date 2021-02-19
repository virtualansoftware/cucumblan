package io.virtualan.cucumblan.props;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Application configuration.
 *
 * @author Elan Thangamani
 */
@Slf4j
public class ApplicationConfiguration {
  private static Properties properties = new Properties();
  static {
    reload();
  }

  public static  void reload(){
    try {
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("cucumblan.properties");
      if(stream == null) {
        stream = ApplicationConfiguration.class.getClassLoader().getResourceAsStream("cucumblan.properties");
      }
      if(stream != null) {
        properties.load(stream);
      } else {
        log.warn("unable to load cucumblan.properties");
      }
    } catch (Exception e) {
      log.warn("cucumblan.properties not found");
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
