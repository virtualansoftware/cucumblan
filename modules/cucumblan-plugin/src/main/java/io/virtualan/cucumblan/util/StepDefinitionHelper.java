package io.virtualan.cucumblan.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class StepDefinitionHelper {
	
	private static final MustacheFactory mf = new DefaultMustacheFactory();

	public static MustacheFactory getMustacheFactory() {
		return mf;
	}
	
	public static Object getActualValue(ScenarioContext scenarioContext, String value) {
		if (value.contains("[") && value.contains("]")) {
			return scenarioContext.getContext(value.substring(value.indexOf("[")+1, value.lastIndexOf("]")-1));
		} else {
			return value;
		}
	}
	
	public static String getActualResource(Properties props, String resourceKey) {
		return props.getProperty("BASE_URL", "http://localhost/") + (props.getProperty(resourceKey) != null ? props.getProperty(resourceKey) : resourceKey);
	}
	
	public static String buildInputRequest(ScenarioContext scenarioContext, Map<String, String> parameterMap) throws Exception {
		String inputFileName = parameterMap.get("inputJson");
		try {
			StringBuffer sb = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					StepDefinitionHelper.class.getResourceAsStream(inputFileName), "UTF-8"));
			  for (int c = br.read(); c != -1; c = br.read()) sb.append((char) c);
			  Map<String, String> context = new HashMap<String, String>();
			  context.putAll(parameterMap);
			  context.putAll(scenarioContext.getContext());
			  return buildJsonString(inputFileName, populateVariables(sb.toString(), context));
		} catch (Exception e) {
			// Update Later
			e.printStackTrace();
			throw new Exception("Validate " + inputFileName + " is missing or has some issue(s) with " +  e.getMessage());
		}
	}


	public static String populateVariables(String body, Map<String, String> context) throws IOException {
        Reader reader = new StringReader(body);
		Mustache m = getMustacheFactory().compile(reader , "JSON_Mustache_Template");
		return executeTemplate(m, context);
	}

	public static String executeTemplate(Mustache m, Map<String, String> context) throws IOException {
		StringWriter writer = new StringWriter();
		m.execute(writer, context).flush();
		return writer.toString();
	}

	public static String buildJsonString(String fileName, String jsonInput) throws Exception {
		try {
			JSONTokener tokener = new JSONTokener(jsonInput);
			JSONObject object = new JSONObject(tokener);
			return object.toString();
		} catch (JSONException e) {
			// Update Later
			throw new Exception("Validate " + fileName + " has correct Json format?? "+ e.getMessage());
		}
	}

}