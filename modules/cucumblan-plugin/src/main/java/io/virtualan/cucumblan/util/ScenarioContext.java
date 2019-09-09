package io.virtualan.cucumblan.util;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {

	private Map<String, String> scenarioContext;

	public ScenarioContext() {
		scenarioContext = new HashMap<>();
	}

	public boolean hasContextValues() {
		System.out.println(" Size :" + scenarioContext);
		return scenarioContext != null && !scenarioContext.isEmpty();
	}
	
	public void setContext(Map<String, String> globalParams) {
		scenarioContext.putAll(globalParams);
	}
	
	public void setContext(String key, String value) {
		scenarioContext.put(key, value);
	}

	public Object getContext(String key) {
		return scenarioContext.get(key.toString());
	}

	public Map<String, String> getContext() {
		return scenarioContext;
	}
	
	public Boolean isContains(String key) {
		return scenarioContext.containsKey(key);
	}

}