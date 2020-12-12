package io.virtualan.cucumblan.props;

import com.google.common.io.CharStreams;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.Charsets;

/**
 * The type Application configuration.
 *
 * @author Elan Thangamani
 */
public class ExcludeConfiguration {
  static List<String> excludeList = new ArrayList<>();
  static String excludes = null;

  static {
    try {
      excludes = CharStreams.toString(new InputStreamReader(
          ExcludeConfiguration.class.getClassLoader().getResourceAsStream("compare_exclude.list"), Charsets.UTF_8));
      excludeList = Stream.of(excludes).collect(Collectors.toList());
    } catch (Exception e) {

    }
  }
  /**
   * Gets property.
   *
   * @param keyName the key name
   * @return the property
   */
  public static boolean isExists(String keyName) {
    return excludeList.contains(keyName) || excludes != null && excludes.contains(keyName);
  }

}
