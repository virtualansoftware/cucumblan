package io.virtualan.cucumblan.props.util;

import io.virtualan.cucumblan.props.ApplicationConfiguration;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MobileHelper {

  public static String getUrl(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.url." + resource);
  }


  public static String getFile(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.file." + resource);
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

  public static String getPlatform(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.platform.resource");
  }

  public static String getBrowserName(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.browser_name.resource");
  }

  public static String getDevice(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.device.resource");
  }

  public static String getUDID(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.udid.resource");
  }

  public static String getBundleId(String resource) {
    return ApplicationConfiguration.getProperty("service.mobile.bundle_id.resource");
  }

  public static void getAdditionalConfigResource(String resource, DesiredCapabilities dc) {
    String readAddConf = ApplicationConfiguration.getProperty("service.mobile.additional.resource");
    if(readAddConf != null){
      Map<String,String> addconf = Pattern.compile("\\s*;\\s*")
              .splitAsStream(readAddConf.trim())
              .map(s -> s.split("=", 2))
              .collect(Collectors.toMap(a -> a[0], a -> a.length>1? a[1]: ""));
      addconf.entrySet().forEach(x -> dc.setCapability(x.getKey(), x.getValue()));

    }
  }
}
