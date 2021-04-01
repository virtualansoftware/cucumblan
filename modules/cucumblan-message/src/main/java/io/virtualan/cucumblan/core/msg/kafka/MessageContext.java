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

package io.virtualan.cucumblan.core.msg.kafka;

import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.message.type.MessageTypeFactory;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * The Message context.
 *
 * @author Elan Thangamani
 */

@Slf4j
public class MessageContext {


  private static final Map<String, MessageType> messageTypes = new HashMap<>();
  private static final List<MessageTypeFactory> messageTypeFactories = new ArrayList<>();
  private static Map<String, Map<String, Object>> messageContext = new HashMap<>();

  public static List<MessageTypeFactory> getMessageTypeFactories() {
    return messageTypeFactories;
  }

  public static Map<String, MessageType> getMessageTypes() {
    return messageTypes;
  }

  static {
    loadMessageTypeFactories();
    loadMessageTypes();
  }

  private MessageContext() {
  }

  /**
   * Load MessageType processors.
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
   * Load MessageType processors.
   */
  private static void loadMessageTypeFactories() {
    Reflections reflections = new Reflections(ApplicationConfiguration.getMessageTypePackage(),
        new SubTypesScanner(false));
    Set<Class<? extends MessageTypeFactory>> buildInClasses = reflections
        .getSubTypesOf(MessageTypeFactory.class);
    buildInClasses.forEach(x -> {
      MessageTypeFactory messageType = null;
      try {
        messageType = x.newInstance();
        messageTypeFactories.add(messageType);
      } catch (InstantiationException | IllegalAccessException e) {
        log.warn("Unable to process this Message Type Factories (" + x.getName() + ") class: " + messageType);
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

  public static Object getEventContextMap(String eventName, String id) {
    Map<String, Object> events = new HashMap<>();
    if (isContains(eventName)) {
      events = messageContext.get(eventName);
      if (events.containsKey(id)) {
        return events.get(id);
      }
    }
    return null;
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

  public static void setEventContextMap(String eventName, String id, Object jsonobject) {
    Map<String, Object> events = new HashMap<>();
    if (isContains(eventName)) {
      events = messageContext.get(eventName);
    }
    events.put(id, jsonobject);
    messageContext.put(eventName, events);
  }
}