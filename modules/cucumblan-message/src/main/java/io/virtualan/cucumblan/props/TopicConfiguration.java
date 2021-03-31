package io.virtualan.cucumblan.props;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The type Topic configuration.
 *
 * @author Elan Thangamani
 */
public class TopicConfiguration {
  private final static Logger LOGGER = Logger.getLogger(TopicConfiguration.class.getName());

  private static Properties properties = new Properties();
  static {
    reload();
  }

  public static  void reload(){
    try {
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("topic.properties");
      if(stream == null) {
        stream = TopicConfiguration.class.getClassLoader().getResourceAsStream("topic.properties");
      }
      if(stream != null) {
        properties.load(stream);
      } else {
        LOGGER.warning("unable to load topic.properties");
      }
    } catch (Exception e) {
      LOGGER.warning("topic.properties not found");
    }
  }

  /**
   * Gets properties.
   *
   */
  public static void setProperty(String key, String value) {
    properties.put(key, value);
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
