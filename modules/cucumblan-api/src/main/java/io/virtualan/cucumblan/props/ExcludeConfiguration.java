package io.virtualan.cucumblan.props;


import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Application configuration.
 *
 * @author Elan Thangamani
 */
public class ExcludeConfiguration {

  /**
   * The Exclude properties.
   */
  static Properties excludeProperties = new Properties();
  /**
   * The Exclude list.
   */
  static List<String> excludeList = new ArrayList<>();
  /**
   * The Excludes.
   */
  static String excludes = null;
  private final static Logger LOGGER = Logger.getLogger(ExcludeConfiguration.class.getName());

  static {
    reload();
  }

  public static  void reload() {
    try {
      InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("exclude-response.properties");
      if(stream == null) {
        stream =  ExcludeConfiguration.class.getClassLoader().getResourceAsStream("exclude-response.properties");
      }

      if(stream != null) {
        excludeProperties.load(stream);
      }else {
        LOGGER.warning("exclude-response.properties is not configured yet? Do you need?");
      }
    } catch (Exception e) {
      LOGGER.warning("exclude-response.properties is not loaded");
    }
  }

    private static boolean findMatch(String actual) {
    for (Map.Entry entry : excludeProperties.entrySet()) {
      if (actual.matches(entry.getKey().toString())){
        return entry.getValue().toString().equalsIgnoreCase("IGNORE");
      }
    }
    return false;
  }

  /**
   * Gets property.
   *
   * @param resource the resource
   * @param keyName  the key name
   * @return the property
   */
  public static boolean shouldSkip(String resource,String keyName) {
    String excludes = excludeProperties.getProperty(resource);
    if(excludes != null &&  excludes.equalsIgnoreCase("IGNORE")) {
      LOGGER.info(" Skipping response comparison for resource : "+ resource );
      return true;
    }else if (excludes != null && keyName != null) {
      excludeList = Stream.of(excludes.split(",")).collect(Collectors.toList());
      return excludeList.contains(keyName) || excludeList.stream().anyMatch(x -> keyName.contains(x));
    } else if(excludes == null && findMatch(resource)){
      LOGGER.info(" Skipping comparison for resource based on pattern : " + resource);
      return true;
    }
    return false;
  }

}
