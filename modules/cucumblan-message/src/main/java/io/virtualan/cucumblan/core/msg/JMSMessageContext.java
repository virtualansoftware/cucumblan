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

package io.virtualan.cucumblan.core.msg;

import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSException;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * The Message context.
 *
 * @author Elan Thangamani
 */
public class JMSMessageContext {

  private static Logger logger = Logger.getLogger(JMSMessageContext.class.getName());
  private static final Map<String, ConnectionFactory> jmsConnectionFactory = new HashMap<>();
  private static Map<String, Map<String, Object>> messageContext = new HashMap<>();

  private JMSMessageContext() {

  }


  /**
   * Gets message types.
   *
   * @return the jms connection factory
   */
  public static Map<String, ConnectionFactory> getJmsConnectionFactory() {
    return jmsConnectionFactory;
  }

  private static ConnectionFactory buildJMSsConnectionFactory(String resource) throws IOException, JMSException {
    Properties props = new Properties();
    InputStream stream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream("consumer-amq-" + resource + ".properties");
    if (stream != null) {
      props.load(stream);
    } else {
      logger.warning("consumer-amq-" + resource + ".properties is not found");
      System.exit(1);
    }
    if(props.get("username") != null && props.get("password") != null){
      ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
          props.getProperty("username"), props.getProperty("password"), props.getProperty("brokerUrl"));
      return connectionFactory;
    } else {
      ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(props.getProperty("brokerUrl"));
      return connectionFactory;
    }
  }


  /**
   * Produce message to Load MessageType processors.
   */
  public static ConnectionFactory loadConnectionFactory(String resource)
      throws IOException, JMSException {
    if(!jmsConnectionFactory.containsKey(resource)){
      jmsConnectionFactory.put(resource, buildJMSsConnectionFactory(resource));
    }
    return jmsConnectionFactory.get(resource);
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
    } else if(isContains(eventName)){
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