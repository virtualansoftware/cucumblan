package io.virtualan.cucumblan.props.util;

import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.EndpointConfiguration;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApiHelper {

  /**
   * Gets actual resource.
   *
   * @param resourceKey the resource key
   * @param system      the system
   * @return the actual resource
   */
  public static String getHostName(String resourceKey, String system) {

    if (ApplicationConfiguration.getProperty("service.api." + system) == null) {
      if( ApplicationConfiguration.getProperty("service.api") != null){
          return StepDefinitionHelper.getActualValue(ApplicationConfiguration.getProperty("service.api"));
      } else {
        return StepDefinitionHelper.getActualValue(resourceKey);
      }
    }
    return StepDefinitionHelper.getActualValue(ApplicationConfiguration.getProperty("service.api." + system));
  }

  /**
   * Gets actual resource.
   *
   * @param resourceKey the resource key
   * @param system      the system
   * @return the actual resource
   */
  public static String getActualResource(String resourceKey, String system) throws Exception {
    Properties props = EndpointConfiguration.getInstance().getProperty(system);
    if (ApplicationConfiguration.getProperty("service.api." + system) == null ) {
      if( ApplicationConfiguration.getProperty("service.api") != null){
        String url = ApplicationConfiguration.getProperty("service.api")
            +(props != null && props.getProperty(resourceKey) != null ? props.getProperty(resourceKey) : resourceKey);
        return url;
      } else {
        log.warn("service.api : configuration is missing.. Unable to proceed");
        throw  new Exception("service.api : configuration is missing.. Unable to proceed");
      }
    }
    String url = ApplicationConfiguration.getProperty("service.api." + system)
        + (props != null && props.getProperty(resourceKey) != null ? props.getProperty(resourceKey) : resourceKey);
    return StepDefinitionHelper.getActualValue(url);
  }

}
