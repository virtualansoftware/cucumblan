package io.virtualan.cucumblan.util;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

	private static Map<String, String> scenarioContext = new HashMap<>();
	

	public static boolean hasContextValues() {
		return scenarioContext != null && !scenarioContext.isEmpty();
	}
	
	public static void setContext(Map<String, String> globalParams) {
		scenarioContext.putAll(globalParams);
	}
	
	public static void setContext(String key, String value) {
		scenarioContext.put(key, value);
	}

	public static Object getContext(String key) {
		return scenarioContext.get(key.toString());
	}

	public static Map<String, String> getContext() {
		return scenarioContext;
	}
	
	public static Boolean isContains(String key) {
		return scenarioContext.containsKey(key);
	}

}