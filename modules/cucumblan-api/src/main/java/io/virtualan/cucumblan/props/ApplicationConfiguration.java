package io.virtualan.cucumblan.props;

import java.util.Map;
import java.util.Properties;

public class ApplicationConfiguration {
  private static Properties properties = new Properties();
  static {
    try {
      properties.load(ApplicationConfiguration.class.getClassLoader().getResourceAsStream("cucumblan.properties"));
    } catch (Exception e) {

    }
  }

  public static Map<String, String> getProperties() {
    return (Map)properties;
  }

  public static String getProperty(String keyName) {
    return properties.getProperty(keyName);
  }

}
