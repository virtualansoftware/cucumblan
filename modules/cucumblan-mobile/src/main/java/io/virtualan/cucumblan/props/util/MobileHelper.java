package io.virtualan.cucumblan.props.util;

import io.cucumber.java.sl.In;
import io.virtualan.cucumblan.props.ApplicationConfiguration;

public class MobileHelper {

  public static String getUrl(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile." + resource);
  }

  public static String getNode() {
    return ApplicationConfiguration.getProperty("service.mobile.node");
  }

  public static String getAppium() {
    return ApplicationConfiguration.getProperty("service.mobile.appium");
  }


  public static int getWaitTime(String resource) {
    String value = ApplicationConfiguration.getProperty("service.mobile.wait_time." + resource);
    if (value != null) {
      return Integer.parseInt(value);
    }
    return 15;
  }

  public static String getAppName(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.app_name." + resource);
  }

  public static String getAppActivity(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.app_activity." + resource);
  }

  public static String getAppPackage(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.app_package." + resource);
  }

  public static String getMobileHost() {
    String value = ApplicationConfiguration.getProperty("service.mobile.host");
    if (value != null) {
      return value;
    }
    return "0.0.0.0";
  }

  public static int getMobilePort() {
    String value = ApplicationConfiguration.getProperty("service.mobile.port");
    if (value != null) {
      return Integer.parseInt(value);
    }
    return 4732;

  }

}
