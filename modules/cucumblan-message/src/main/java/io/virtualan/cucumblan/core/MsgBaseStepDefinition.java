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

package io.virtualan.cucumblan.core;


import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.virtualan.csvson.Csvson;
import io.virtualan.cucumblan.core.msg.kafka.KafkaConsumerClient;
import io.virtualan.cucumblan.core.msg.kafka.KafkaProducerClient;
import io.virtualan.cucumblan.core.msg.kafka.MessageContext;
import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.props.TopicConfiguration;
import io.virtualan.cucumblan.props.util.MsgHelper;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import io.virtualan.mapson.exception.BadInputDataException;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;


/**
 * The type Message base step definition.
 *
 * @author Elan Thangamani
 */
@Slf4j
public class MsgBaseStepDefinition {

  private Scenario scenario;

  /**
   * Before.
   *
   * @param scenario the scenario
   */
  @Before
  public void before(Scenario scenario) {
    this.scenario = scenario;
  }


  /**
   * Produce message with partition.
   *
   * @param eventName the event name
   * @param partition partition
   * @param resource  the resource
   * @param type      the type
   * @param messages  the messages
   * @throws MessageNotDefinedException the message not defined exception
   */
  @Given("send message event (.*) in partition (.*) on (.*) with type (.*)$")
  public void produceMessageWithPartition(String eventName, Integer partition, String resource,
      String type, Object messages) throws MessageNotDefinedException {
    String topic = TopicConfiguration.getProperty(eventName);
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    if (topic != null && messageType != null) {
      MessageType builtMessage = messageType.buildProducerMessage(messages);
      scenario.log(builtMessage.toString());

      KafkaProducerClient
          .sendMessage(resource, topic, builtMessage.getKey(), builtMessage.getMessage(),
              partition, builtMessage.getHeaders());
    } else {
      Assertions.assertTrue(false, eventName + " is not configured for any topic. or " + type +" is not configured");
    }
  }

  /**
   * Produce message.
   *
   * @param sleep the sleep
   * @throws InterruptedException the interrupted exception
   */
  @Given("pause message process for (.*) milliseconds$")
  public void produceMessage(long sleep) throws InterruptedException {
    Thread.sleep(sleep);
  }

  /**
   * eventName to Produce message.
   *
   * @param eventName the eventName
   * @throws InterruptedException the interrupted exception
   */
  @Given("clear the consumed message for the event (.*)$")
  public void clearMessage(String eventName) throws InterruptedException {
    MessageContext.removeEventContextMap(eventName);
  }

  /**
   * Produce message.
   *
   * @param eventName the event name
   * @param resource  the resource
   * @param type      the type
   * @param messages  the messages
   * @throws MessageNotDefinedException the message not defined exception
   */
  @Given("send message event (.*) on (.*) with type (.*)$")
  public void produceMessage(String eventName, String resource, String type,
      DataTable messages) throws MessageNotDefinedException {
    String topic = TopicConfiguration.getProperty(eventName);
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    if (topic != null && messageType != null) {
      MessageType builtMessage = messageType.buildProducerMessage(messages);
      scenario.log(builtMessage.toString());
      if (builtMessage.getKey() != null) {
        KafkaProducerClient
            .sendMessage(resource, topic, builtMessage.getKey(), builtMessage.getMessage(),
                null, builtMessage.getHeaders());
      } else {
        KafkaProducerClient
            .sendMessage(resource, topic, null, builtMessage.getMessage(),
                null, builtMessage.getHeaders());
      }
    } else {
      Assertions.assertTrue(false, eventName + " is not configured for any topic. or " + type +" is not configured");
    }
  }

  /**
   * Produce message.
   *
   * @param eventName the event name
   * @param resource  the resource
   * @param type      the type
   * @param messages  the messages
   * @throws MessageNotDefinedException the message not defined exception
   */
  @Given("send inline message event (.*) on (.*) with type (.*)$")
  public void produceMessage(String eventName, String resource, String type,
      List<String> messages) throws MessageNotDefinedException {
    String topic = TopicConfiguration.getProperty(eventName);
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    if (topic != null && messageType != null) {
      MessageType builtMessage = messageType.buildProducerMessage(messages);
      scenario.log(builtMessage.toString());
      if (builtMessage.getKey() != null) {
        KafkaProducerClient
            .sendMessage(resource, topic, builtMessage.getKey(), builtMessage.getMessage(),
                null, builtMessage.getHeaders());
      } else {
        KafkaProducerClient
            .sendMessage(resource, topic, null, builtMessage.getMessage(),
                null, builtMessage.getHeaders());
      }
    } else {
      Assertions.assertTrue(false, eventName + " is not configured for any topic. or " + type +" is not configured");
    }
  }

  /**
   * Produce message.
   *
   * @param eventName the event name
   * @param resource  the resource
   * @param type      the type
   * @param messages  the messages
   * @throws MessageNotDefinedException the message not defined exception
   */
  @Given("send mapson message event (.*) on (.*) with type (.*)$")
  public void produceMessageMapson(String eventName, String resource, String type,
      Map<String, String> messages) throws MessageNotDefinedException {
    String topic = TopicConfiguration.getProperty(eventName);
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    if (topic != null && messageType != null) {
      MessageType builtMessage = messageType.buildProducerMessage(messages);
      scenario.log(builtMessage.toString());
      if (builtMessage.getKey() != null) {
        KafkaProducerClient
            .sendMessage(resource, topic, builtMessage.getKey(), builtMessage.getMessage(),
                null, builtMessage.getHeaders());
      } else {
        KafkaProducerClient
            .sendMessage(resource, topic, null, builtMessage.getMessage(),
                null, builtMessage.getHeaders());
      }
    } else {
      Assertions.assertTrue(false, eventName + " is not configured for any topic. or " + type +" is not configured");
    }

  }


  /**
   * Verify consumed json object.
   *
   * @param eventName the event name
   * @param id        the id
   * @param resource  the resource
   * @param type      the type
   * @param csvson    the csvson
   * @throws InterruptedException       the interrupted exception
   * @throws BadInputDataException      bad input data exception
   * @throws MessageNotDefinedException the message not defined exception
   */
  @Given("verify (.*) contains (.*) on (.*) with type (.*)$")
  public void verifyConsumedJSONObject(String eventName, String id, String resource, String type,
      List<String> csvson)
      throws InterruptedException, BadInputDataException, MessageNotDefinedException {
    int recheck = 0;
    MessageType expectedJson = KafkaConsumerClient.getEvent(eventName, type, id, resource, recheck);
    if (expectedJson != null) {
      scenario.attach(expectedJson.getMessageAsJson().toString(), "application/json",
          "verifyConsumedJSONObject");
      JSONArray csvobject = Csvson.buildCSVson(csvson, ScenarioContext.getContext());
      if (expectedJson.getMessageAsJson() instanceof JSONObject) {
        JSONAssert
            .assertEquals(csvobject.getJSONObject(0), (JSONObject) expectedJson.getMessageAsJson(),
                JSONCompareMode.LENIENT);
      } else {
        JSONAssert.assertEquals(csvobject, (JSONArray) expectedJson, JSONCompareMode.LENIENT);
      }
    } else {
      Assertions.assertTrue(false,
          " Unable to read event name (" + eventName + ") with identifier : " + id);
    }
  }

  /**
   * Consume message.
   *
   * @param eventName the event name
   * @param id        the id
   * @param resource  the resource
   * @param type      the type
   * @param keyValue  the key value
   * @throws InterruptedException       interrupted exception
   * @throws MessageNotDefinedException the message not defined exception
   */
  @Given("verify-by-elements (.*) contains (.*) on (.*) with type (.*)$")
  public void consumeMessage(String eventName, String id, String resource, String type,
      Map<String, String> keyValue)
      throws InterruptedException, MessageNotDefinedException {
    int recheck = 0;
    MessageType expectedJson = KafkaConsumerClient.getEvent(eventName, type, id, resource, recheck);
    if (expectedJson != null) {
      scenario.attach(expectedJson.getMessage().toString(), "application/json",
          "verifyConsumedJSONObject");
      MessageType finalExpectedJson = expectedJson;
      keyValue.forEach((k, v) -> {
        Object value = MsgHelper.getJSON(finalExpectedJson.getMessageAsJson().toString(), k);
        Assertions.assertEquals(
            StepDefinitionHelper.getObjectValue(StepDefinitionHelper.getActualValue((String) v)),
            value, k + " is not failed.");
      });
    } else {
      Assertions.assertTrue(false,
          " Unable to read event name (" + eventName + ") with identifier : " + id);
    }
  }


}