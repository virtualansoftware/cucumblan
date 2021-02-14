package io.virtualan.cucumblan.ui.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

public class PagePropLoader {
  private final static Logger LOGGER = Logger.getLogger(PagePropLoader.class.getName());
  static Properties prop;

  public static Map<String, PageElement> readPageElement(String resource, String fileName) throws IOException {
    Map<String, PageElement> pageMap = new LinkedHashMap<String, PageElement>();
    InputStream inputStream = null;
    prop = new Properties();
    String propFileName = "pages/"+resource+"/"+fileName + ".page";
    try {
      inputStream = PagePropLoader.class.getClassLoader().getResourceAsStream(propFileName);
      if(inputStream != null) {
        prop.load(inputStream);
        for (Entry<Object, Object> p : prop.entrySet()) {
          String[] page = ((String) p.getValue()).split(",");
          pageMap.put(p.getKey().toString(), new PageElement(page[0], page[1], page[2]));
        }
      } else {
        LOGGER.warning("propFileName is not found >>> " + propFileName );
      }
    } catch (Exception ioe) {
      LOGGER.warning("propFileName is not loaded >>> " + propFileName );
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
    return pageMap;
  }

  public String getProperty(String key) {
    return prop.getProperty(key);
  }

}