package io.virtualan.cucumblan.props;

import io.virtualan.cucumblan.core.UIBaseStepDefinition;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Application configuration.
 *
 * @author Elan Thangamani
 */
public class ApplicationConfiguration {
  private final static Logger LOGGER = Logger.getLogger(ApplicationConfiguration.class.getName());

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
        LOGGER.warning("unable to load cucumblan.properties");
      }
    } catch (Exception e) {
      LOGGER.warning("cucumblan.properties not found");
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
   * Gets property.
   *
   * @return the property
   */
  public static boolean getInline() {
    return properties.getProperty("data-inline") != null ?
        properties.getProperty("data-inline").equalsIgnoreCase("true") : true;
  }

  /**
   * Gets property.
   *
   * @return the property
   */
  public static String getStandardPackage() {
    return properties.getProperty("standard-package") != null ?
        properties.getProperty("standard-package") : "io.virtualan.cucumblan.standard";
  }
  /**


   /**
   * Gets property.
   *
   * @param keyName the key name
   * @return the property
   */
  public static boolean getBoolean(String keyName) {
    return properties.getProperty(keyName) != null ?
        properties.getProperty(keyName).equalsIgnoreCase("true") : false;
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
