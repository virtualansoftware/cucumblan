package io.virtualan.cucumblan.props;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

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
  public static boolean isProdMode() {
    return properties.getProperty("prod-mode") != null ?
        properties.getProperty("prod-mode").equalsIgnoreCase("true") : false;
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
   * @return the property
   */
  public static String getMessageTypePackage() {
    return properties.getProperty("message-package") != null ?
        properties.getProperty("message-package") : "io.virtualan.cucumblan.message.typeimpl";
  }

  /**
   * Gets property.
   *
   * @return the property
   */
  public static String getActionPackage() {
    return properties.getProperty("action-package") != null ?
        properties.getProperty("action-package") : "io.virtualan.cucumblan.ui.actionimpl";
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


  public static int getMessageCount() {
    return properties.getProperty("wait-message-count") != null ?
        Integer.parseInt(properties.getProperty("wait-message-count")) : 2;
  }
}
