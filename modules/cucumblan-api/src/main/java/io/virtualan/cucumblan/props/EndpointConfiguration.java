package io.virtualan.cucumblan.props;

import io.cucumber.java.af.En;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The type Endpoint configuration.
 *
 * @author Elan Thangamani
 */
public class EndpointConfiguration {

	private EndpointConfiguration(){

	}

	private static EndpointConfiguration endpointConfiguration = null;

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static EndpointConfiguration getInstance() {
		if (endpointConfiguration == null) {
			endpointConfiguration  = new EndpointConfiguration();
		}
		return endpointConfiguration;
	}

	private static Map<String, Properties> propertiesMap = new HashMap<String, Properties>();

  /**
   * Load endpoints.
   */
  public  void loadEndpoints() {
		File directory = new File("conf/");
		String[] myFiles = directory.list(new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
				return fileName.matches("endpoint.*.properties");
			}
		});
		int i = 0;
		for(String file: myFiles) {
			Properties resourceEndPoint  = new Properties();
			try {
				resourceEndPoint.load(new InputStreamReader(new FileInputStream("conf/"+file)));
				propertiesMap.put(file.substring(file.indexOf(".")+1, file.lastIndexOf(".")), resourceEndPoint);
			} catch (IOException e) {
			}
		}
	}

  /**
   * Gets property.
   *
   * @param keyName the key name
   * @return the property
   */
  public Properties getProperty(String keyName) {
		return propertiesMap.get(keyName);
	}
	
}

