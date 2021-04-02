package io.virtualan.cucumblan.props.util;

import io.virtualan.cucumblan.props.ApplicationConfiguration;

public class UIHelper {

  public static String getUrl(String resource) {
    return ApplicationConfiguration.getProperty("service.ui."+resource);
  }
}
