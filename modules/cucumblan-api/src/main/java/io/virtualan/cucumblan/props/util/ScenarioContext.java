package io.virtualan.cucumblan.props.util;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Scenario context.
 * @author Elan Thangamani
 */
public class ScenarioContext {

	private static Map<String, String> scenarioContext = new HashMap<>();


  /**
   * Has context values boolean.
   *
   * @return the boolean
   */
  public static boolean hasContextValues() {
		return scenarioContext != null && !scenarioContext.isEmpty();
	}

  /**
   * Sets context.
   *
   * @param globalParams the global params
   */
  public static void setContext(Map<String, String> globalParams) {
		scenarioContext.putAll(globalParams);
	}

  /**
   * Sets context.
   *
   * @param key   the key
   * @param value the value
   */
  public static void setContext(String key, String value) {
		scenarioContext.put(key, value);
	}

  /**
   * Gets context.
   *
   * @param key the key
   * @return the context
   */
  public static Object getContext(String key) {
		return scenarioContext.get(key.toString());
	}

  /**
   * Gets context.
   *
   * @return the context
   */
  public static Map<String, String> getContext() {
		return scenarioContext;
	}

  /**
   * Is contains boolean.
   *
   * @param key the key
   * @return the boolean
   */
  public static Boolean isContains(String key) {
		return scenarioContext.containsKey(key);
	}

}