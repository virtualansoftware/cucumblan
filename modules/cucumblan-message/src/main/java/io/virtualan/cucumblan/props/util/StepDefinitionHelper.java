package io.virtualan.cucumblan.props.util;

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
	 * Gets object value.
	 *
	 * @param value the value
	 * @return the object value
	 */
	public static Object getObjectValue(Object value) {
		String[] arrayValue = value.toString().split("~");
		if ("i".equals(arrayValue[0])) {
			return Integer.parseInt(arrayValue[1]);
		} else if ("b".equals(arrayValue[0])) {
			return Boolean.parseBoolean(arrayValue[1]);
		} else if ("d".equals(arrayValue[0])) {
			return Double.parseDouble(arrayValue[1]);
		} else if ("l".equals(arrayValue[0])) {
			return Long.parseLong(arrayValue[1]);
		} else if ("f".equals(arrayValue[0])) {
			return Float.parseFloat(arrayValue[1]);
		} else {
			return value.toString();
		}
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