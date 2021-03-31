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

import java.util.HashMap;
import java.util.Map;

/**
 * The Message context.
 *
 * @author Elan Thangamani
 */
public class MessageContext {

  private static Map<String, Map<String, Object>> messageContext = new HashMap<>();

  private MessageContext() {
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