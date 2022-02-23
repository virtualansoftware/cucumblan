package io.virtualan.cucumblan.props;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
/*
 *
 *
 *    Copyright (c) 2022.  Virtualan Contributors (https://virtualan.io)
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

/**
 * The type Topic configuration.
 *
 * @author Elan Thangamani
 */
public class TopicConfiguration {
    private final static Logger LOGGER = Logger.getLogger(TopicConfiguration.class.getName());

    private static Properties properties = new Properties();

    static {
        reload();
    }

    /**
     * Reload.
     */
    public static void reload() {
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("topic.properties");
            if (stream == null) {
                stream = TopicConfiguration.class.getClassLoader().getResourceAsStream("topic.properties");
            }
            if (stream != null) {
                properties.load(stream);
            } else {
                LOGGER.warning("unable to load topic.properties");
            }
        } catch (Exception e) {
            LOGGER.warning("topic.properties not found");
        }
    }

    /**
     * Gets properties.
     *
     * @param key   the key
     * @param value the value
     */
    public static void setProperty(String key, String value) {
        properties.put(key, value);
    }


    /**
     * Gets properties.
     *
     * @return the properties
     */
    public static Map<String, String> getProperties() {
        return (Map) properties;
    }

    /**
     * Gets property.
     *
     * @param keyName the key name
     * @return the property
     */
    public static String getProperty(String keyName) {
        return properties.getProperty(keyName);
    }

}
