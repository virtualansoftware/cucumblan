/*
 *
 *
 *    Copyright (c) 2021.  Virtualan Contributors (https://virtualan.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software distributed under the License
 *     is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing permissions and limitations under
 *     the License.
 *
 *
 *
 */

package io.virtualan.cucumblan.props.util;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Scenario context.
 *
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
        return scenarioContext.get(key);
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