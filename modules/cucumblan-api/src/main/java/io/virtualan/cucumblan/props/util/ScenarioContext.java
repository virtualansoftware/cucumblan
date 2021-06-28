package io.virtualan.cucumblan.props.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.JSONException;

/**
 * The type Scenario context.
 * @author Elan Thangamani
 */
public class ScenarioContext {



    private static Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();

    public static Map<String, String> getScenarioContext(String id) {
        return parentScenarioContext.get(id);
    }


    /**
     * Has context values boolean.
     *
     * @return the boolean
     */
    public static boolean hasContextValues(String id) {
        return getScenarioContext(id) != null && !getScenarioContext(id).isEmpty();
    }

    /**
     * Sets context.
     *
     * @param globalParams the global params
     */
    public static void setContext(String id, Map<String, String> globalParams) {
        if(getScenarioContext(id) != null) {
            getScenarioContext(id).putAll(globalParams);
        }else {
            parentScenarioContext.put(id, globalParams);
        }
    }

    /**
     * Sets context.
     *
     * @param key   the key
     * @param value the value
     */
    public static void setContext(String id, String key, String value) {
        getScenarioContext(id).put(key, value);
    }

    public static Map<String, String> getPrintableContextObject(String id) throws JSONException {
        Map<String, String> resultValues = getScenarioContext(id).entrySet().stream()
            .collect(
                Collectors.toMap( entry -> entry.getKey(),
                    entry -> entry.getKey().contains("password") ? "xxxxxxxxxxxx" : entry.getValue()));

        return resultValues;
    }


    /**
     * Gets context.
     *
     * @param key the key
     * @return the context
     */
    public static Object getContext(String id, String key) {
        return getScenarioContext(id).get(key.toString());
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public static Map<String, String> getContext(String id) {
        return getScenarioContext(id);
    }


    /**
     * Gets context.
     *
     * @return the context
     */
    public static Map<String, String> remove(String id) {
        return parentScenarioContext.remove(id);
    }



    /**
     * Is contains boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public static Boolean isContains(String id, String key) {
        return getScenarioContext(id).containsKey(key);
    }

}