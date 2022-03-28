package io.virtualan.cucumblan.props.util;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Scenario context.
 *
 * @author Elan Thangamani
 */
public class ScenarioContext {


    private static Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();


    /**
     * Gets parent scenario context.
     *
     * @return the parent scenario context
     */
    public static Map<String, Map<String, String>> getParentScenarioContext() {
        return parentScenarioContext;
    }

    /**
     * Sets parent scenario context.
     *
     * @param parentScenarioContext the parent scenario context
     */
    public static void setParentScenarioContext(
            Map<String, Map<String, String>> parentScenarioContext) {
        ScenarioContext.parentScenarioContext = parentScenarioContext;
    }

    /**
     * Gets scenario context.
     *
     * @param id the id
     * @return the scenario context
     */
    public static Map<String, String> getScenarioContext(String id) {
        return parentScenarioContext.get(id);
    }


    /**
     * Has context values boolean.
     *
     * @param id the id
     * @return the boolean
     */
    public static boolean hasContextValues(String id) {
        return getScenarioContext(id) != null && !getScenarioContext(id).isEmpty();
    }

    /**
     * Sets context.
     *
     * @param id           the id
     * @param globalParams the global params
     */
    public static void setContext(String id, Map<String, String> globalParams) {
        if (getScenarioContext(id) != null) {
            getScenarioContext(id).putAll(globalParams);
        } else {
            parentScenarioContext.put(id, globalParams);
        }
    }

    /**
     * Sets context.
     *
     * @param id    the id
     * @param key   the key
     * @param value the value
     */
    public static void setContext(String id, String key, String value) throws Exception {
        if (parentScenarioContext.get(id) != null){
            getScenarioContext(id).put(key, value);
        } else {
            throw new Exception("Context not found");
        }
    }

    /**
     * Gets printable context object.
     *
     * @param id the id
     * @return the printable context object
     * @throws JSONException the json exception
     */
    public static Map<String, String> getPrintableContextObject(String id) throws JSONException {
        Map<String, String> resultValues = getScenarioContext(id).entrySet().stream()
                .collect(
                        Collectors.toMap(entry -> entry.getKey(),
                                entry -> entry.getKey().contains("password") ? "xxxxxxxxxxxx" : entry.getValue()));

        return resultValues;
    }


    /**
     * Gets context.
     *
     * @param id  the id
     * @param key the key
     * @return the context
     */
    public static Object getContext(String id, String key) {
        return getScenarioContext(id).get(key.toString());
    }

    /**
     * Gets context.
     *
     * @param id the id
     * @return the context
     */
    public static Map<String, String> getContext(String id) {
        return getScenarioContext(id);
    }


    /**
     * Gets context.
     *
     * @param id the id
     * @return the context
     */
    public static Map<String, String> remove(String id) {
        return parentScenarioContext.remove(id);
    }


    /**
     * Is contains boolean.
     *
     * @param id  the id
     * @param key the key
     * @return the boolean
     */
    public static Boolean isContains(String id, String key) {
        return getScenarioContext(id).containsKey(key);
    }

}