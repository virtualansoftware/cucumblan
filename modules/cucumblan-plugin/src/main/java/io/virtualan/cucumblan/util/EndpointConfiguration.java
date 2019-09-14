package io.virtualan.cucumblan.util;

import java.util.Properties;

public class EndpointConfiguration {
	private static Properties resourceEndPointProps = new Properties();
	
	static {
		try {
			resourceEndPointProps.load(EndpointConfiguration.class.getClassLoader().getResourceAsStream("endpoints.properties"));
		} catch (Exception e) {
		}
	}
	
	public static String getProperty(String keyName) {
		return resourceEndPointProps.getProperty(keyName);
	}
	
}

