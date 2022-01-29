/*
 *
 *
 *    Copyright (c) 2022.  Virtualan Contributors (https://virtualan.io)
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

package io.virtualan.cucumblan.ui.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * The type Page prop loader.
 * @author Elan Thangamani
 */
public class PagePropLoader {
  private final static Logger LOGGER = Logger.getLogger(PagePropLoader.class.getName());
  /**
   * The Prop.
   */
  static Properties prop;

  /**
   * Read page element map.
   *
   * @param resource the resource
   * @param fileName the file name
   * @return the map
   * @throws IOException the io exception
   */
  public static Map<String, PageElement> readPageElement(String resource, String fileName) throws IOException {
    Map<String, PageElement> pageMap = new TreeMap<>();
    InputStream inputStream = null;
    prop = new Properties();
    String propFileName = "pages/"+resource+"/"+fileName + ".page";
    try {
      inputStream = PagePropLoader.class.getClassLoader().getResourceAsStream(propFileName);
      if(inputStream != null) {
        prop.load(inputStream);
        for (Entry<Object, Object> p : prop.entrySet()) {
          String[] page = ((String) p.getValue()).split("<~~>");
          if(page.length ==5) {
            pageMap.put(p.getKey().toString(),
                new PageElement(page[0], page[1], page[2], page[3], page[4]));
          } else {
            LOGGER.warning( propFileName + " >> page element does not match.. revisit the page definition ");
          }
        }
      } else {
        LOGGER.warning("page is not defined >>> " + propFileName );
      }
    } catch (Exception ioe) {
      LOGGER.warning("page is not defined/loaded >>> " + propFileName );
    } finally {
      if (inputStream != null) {
        inputStream.close();
      }
    }
    return pageMap;
  }

  /**
   * Gets property.
   *
   * @param key the key
   * @return the property
   */
  public String getProperty(String key) {
    return prop.getProperty(key);
  }

}