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

package io.virtualan.cucumblan.core.msg.kafka;

import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Message context.
 *
 * @author Elan Thangamani
 */
@Slf4j
public class MessageContext {


    private static final Map<String, MessageType> messageTypes = new HashMap<>();
    private static Map<String, Map<String, Object>> messageContext = new HashMap<>();

    static {
        loadMessageTypes();
    }

    private MessageContext() {
    }


    /**
     * Gets message types.
     *
     * @return the message types
     */
    public static Map<String, MessageType> getMessageTypes() {
        return messageTypes;
    }

    /**
     * Produce message to Load MessageType processors.
     */
    private static void loadMessageTypes() {
        Reflections reflections = new Reflections(ApplicationConfiguration.getMessageTypePackage(),
                new SubTypesScanner(false));
        Set<Class<? extends MessageType>> buildInClasses = reflections
                .getSubTypesOf(MessageType.class);
        buildInClasses.forEach(x -> {
            MessageType messageType = null;
            try {
                messageType = x.newInstance();
                messageTypes.put(messageType.getType(), messageType);
            } catch (InstantiationException | IllegalAccessException e) {
                log.warn("Unable to process this messageType (" + x.getName() + ") class: " + messageType);
            }
        });
    }


    /**
     * Has context values boolean.
     *
     * @return the boolean
     */
    public static boolean hasContextValues() {
        return messageContext != null && !messageContext.isEmpty();
    }

    /**
     * Gets event context map.
     *
     * @param eventName the event name
     * @param id        the id
     * @return the event context map
     */
    public static Object getEventContextMap(String eventName, String id) {
        if (isContains(eventName)) {
            Map<String, Object> events = messageContext.get(eventName);
            if (events != null && events.containsKey(id)) {
                return events.get(id);
            }
        }
        return null;
    }

    /**
     * remove event context map.
     *
     * @param eventName the event name
     * @return the event context map
     */
    public static Object removeEventContextMap(String eventName) {
        if ("ALL".equalsIgnoreCase(eventName)) {
            messageContext.clear();
        } else if (isContains(eventName)) {
            messageContext.remove(eventName);
        }
        return null;
    }

    /**
     * Gets event context map.
     *
     * @param eventName the event name
     * @param id        the id
     * @return the event context map
     */
    public static boolean isEventContextMap(String eventName, String id) {
        if (isContains(eventName)) {
            Map<String, Object> events = messageContext.get(eventName);
            if (events != null && events.containsKey(id)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets context.
     *
     * @param key the key
     * @return the context
     */
    public static Map<String, Object> getContext(String key) {
        return messageContext.get(key);
    }

    /**
     * Is contains boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public static Boolean isContains(String key) {
        return messageContext.containsKey(key);
    }

    /**
     * Sets event context map.
     *
     * @param eventName  the event name
     * @param id         the id
     * @param jsonobject the jsonobject
     */
    public static void setEventContextMap(String eventName, String id, Object jsonobject) {
        Map<String, Object> events = new HashMap<>();
        if (isContains(eventName)) {
            events = messageContext.get(eventName);
        }
        events.put(id, jsonobject);
        messageContext.put(eventName, events);
    }
}