package io.virtualan.cucumblan.props;

import com.google.common.io.CharStreams;

import java.io.InputStreamReader;
import java.util.*;
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
      excludeList = Stream.of(excludes.split(",")).collect(Collectors.toList());
    } catch (Exception e) {

    }
  }
  /**
   * Gets property.
   *
   * @param keyName the key name
   * @return the property
   */
  public static boolean shouldSkip(String keyName) {
    return excludeList.contains(keyName) || excludes != null && excludeList.stream().anyMatch( x -> keyName.contains(x));
  }

}
