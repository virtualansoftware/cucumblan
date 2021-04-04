package io.virtualan.cucumblan.props;

/*
 *
 *
 *    Copyright (c) 2021.  Virtualan Contributors (https://virtualan.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software distributed under the License
 *     is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing permissions and limitations under
 *     the License.
 *
 *
 *
 */

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

  /**
   * Reload.
   */
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
   * @param key   the key
   * @param value the value
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
