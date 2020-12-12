package io.virtualan.cucumblan.props.util;

import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.EndpointConfiguration;
import java.util.Properties;

import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


/**
 * The type Step definition helper.
 *
 * @author Elan Thangamani
 */
public class StepDefinitionHelper {
	private final static Logger LOGGER = Logger.getLogger(StepDefinitionHelper.class.getName());


  /**
   * Gets actual value.
   *
   * @param value the value
   * @return the actual value
   */
  public static Object getActualValue( String value) {
		String returnValue = value;
		if (value.contains("[") && value.contains("]")) {
			String key = value.substring(value.indexOf("[") + 1, value.lastIndexOf("]"));
			if (key.contains(",")) {
				StringBuffer keys = new StringBuffer();
				for (String token : key.split(",")) {
					if (!ScenarioContext.getContext().containsKey(token)) {
						System.out.println("Value missing...");
					}
					keys.append(ScenarioContext.getContext().get(token)).append(",");
				}
				returnValue = keys.toString().substring(0, keys.toString().length() - 1);

			} else {
				if (!ScenarioContext.getContext().containsKey(key)) {
					System.out.println("Value missing...");
				}
				returnValue = ScenarioContext.getContext().get(key);
			}
		}
		return returnValue;
	}

	/**
	 * Gets actual resource.
	 *
	 * @param resourceKey the resource key
	 * @param system      the system
	 * @return the actual resource
	 */
	public static String getHostName(String resourceKey, String system) {
		Properties props = EndpointConfiguration.getInstance().getProperty(system);
		if (ApplicationConfiguration.getProperty("service.api." + system) == null) {
			if( ApplicationConfiguration.getProperty("service.api") != null){
				String url = ApplicationConfiguration.getProperty("service.api");
				return url;
			} else {
				LOGGER.severe("service.api : configuration is missing.. Unable to proceed");
				System.exit(-1);
			}
		}
		String url = ApplicationConfiguration.getProperty("service.api." + system);
		return url;
	}

  /**
   * Gets actual resource.
   *
   * @param resourceKey the resource key
   * @param system      the system
   * @return the actual resource
   */
  public static String getActualResource(String resourceKey, String system) {
		Properties props = EndpointConfiguration.getInstance().getProperty(system);
		if (ApplicationConfiguration.getProperty("service.api." + system) == null ) {
			if( ApplicationConfiguration.getProperty("service.api") != null){
			String url = ApplicationConfiguration.getProperty("service.api")
				+(props != null && props.getProperty(resourceKey) != null ? props.getProperty(resourceKey) : resourceKey);
				return url;
			} else {
				LOGGER.severe("service.api : configuration is missing.. Unable to proceed");
				System.exit(-1);
			}
		}
		String url = ApplicationConfiguration.getProperty("service.api." + system)
					+ (props != null && props.getProperty(resourceKey) != null ? props.getProperty(resourceKey) : resourceKey);
		return url;
	}

  /**
   * Build json string string.
   *
   * @param fileName  the file name
   * @param jsonInput the json input
   * @return the string
   * @throws Exception the exception
   */
  public static String buildJsonString(String fileName, String jsonInput) throws Exception {
		try {
			JSONTokener tokener = new JSONTokener(jsonInput);
			JSONObject object = new JSONObject(tokener);
			return object.toString();
		} catch (JSONException e) {
			// Update Later
			throw new Exception("Validate " + fileName + " has correct Json format?? " + e.getMessage());
		}
	}

}