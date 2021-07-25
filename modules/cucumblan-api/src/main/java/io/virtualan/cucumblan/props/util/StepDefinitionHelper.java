package io.virtualan.cucumblan.props.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.logging.Logger;


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
  public static Object replace(String value) {
    String returnValue = value;
    if (value.contains("[") && value.contains("]")) {
      String key = value.substring(value.indexOf("[") + 1, value.lastIndexOf("]"));
      if (key.contains(",")) {
        StringBuffer keys = new StringBuffer();
        for (String token : key.split(",")) {
          if (!ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())).containsKey(token)) {
            System.out.println("Value missing...");
          }
          keys.append(ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())).get(token)).append(",");
        }
        returnValue = keys.toString().substring(0, keys.toString().length() - 1);

      } else {
        if (!ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())).containsKey(key)) {
          System.out.println("Value missing...");
        }
        returnValue = ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())).get(key);
      }
    }
    return returnValue;
  }

  /**
   * Gets actual value.
   *
   * @param object the object
   * @return the actual value
   */
  public static String getActualValue(Object object) {
    if (object == null) {
      return null;
    }
    //TODO validate
//    if (object instanceof JSONArray) {
//      return object;
//    }

    String returnValue = (String) object;
    String key = "";
    if (returnValue.contains("[") && returnValue.contains("]")) {
      key = returnValue.substring(returnValue.indexOf("[") + 1, returnValue.indexOf("]"));
      if (key.contains(",")) {
        StringBuffer keys = new StringBuffer();
        for (String token : key.split(",")) {
          if (!ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())).containsKey(token)) {
            return object.toString();
          }
          keys.append(ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())).get(token)).append(",");
        }
        returnValue = keys.toString().substring(0, keys.toString().length() - 1);
      }
      else {
        if (!ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())).containsKey(key)) {
          LOGGER.warning(object +" has Value missing... for the key : " + key);
          return object.toString();
        } else {
          returnValue = ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())).get(key);
        }
      }
    }
    String response = ((String) object).replace("[" + key + "]", returnValue);
    return response.indexOf("[") != -1 ? getActualValue(response) : response;
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

  public static Object getJSON(String json) {
    try {
      return new JSONObject(json);
    } catch (JSONException err) {
      try {
        return new JSONArray(json);
      } catch (Exception e) {
        LOGGER.warning("invalid JSON > " + json);
      }
    }
    return null;
  }
}