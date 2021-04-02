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


import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.virtualan.csvson.Csvson;
import io.virtualan.cucumblan.core.msg.kafka.KafkaConsumerClient;
import io.virtualan.cucumblan.core.msg.kafka.KafkaProducerClient;
import io.virtualan.cucumblan.core.msg.kafka.MessageContext;
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

  @Before
  public void before(Scenario scenario) {
    this.scenario = scenario;
  }


  @Given("send message event (.*) in partition (.*) on the (.*) with type (.*)$")
  public void produceMessageWithPartition(String eventName, Integer partition, String resource,
      String type, List<String> messages) {
    String topic = TopicConfiguration.getProperty(eventName);
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    MessageType builtMessage = messageType.build(messages);
    scenario.log(builtMessage.toString());
    KafkaProducerClient
        .sendMessage(resource, topic, builtMessage.getId(), builtMessage.getMessage(),
            partition);
  }

  @Given("send message event (.*) on the (.*) with type (.*)$")
  public void produceMessage(String eventName, String resource, String type,
      List<String> messages) {
    String topic = TopicConfiguration.getProperty(eventName);
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    MessageType builtMessage = messageType.build(messages);
    scenario.log(builtMessage.toString());
    if (builtMessage.getId() != null) {
      KafkaProducerClient
          .sendMessage(resource, topic, builtMessage.getId(), builtMessage.getMessage(),
              null);
    } else {
      KafkaProducerClient
          .sendMessage(resource, topic, null, builtMessage.getMessage(),
              null);
    }
  }

  @Given("verify (.*) contains (.*) on the (.*)$")
  public void verifyConsumedJSONObject(String eventName, String id, String resource,
      List<String> csvson)
      throws InterruptedException, BadInputDataException {
    KafkaConsumerClient client = new KafkaConsumerClient(eventName, resource);
    client.run();
    int recheck = 5;
    Object expectedJson = MessageContext.getEventContextMap(eventName, id);
    if (expectedJson == null) {
      expectedJson = KafkaConsumerClient.getEvent(eventName, id, recheck);
    }
    if (expectedJson != null) {
      scenario.attach(expectedJson.toString(), "application/json", "verifyConsumedJSONObject");
      JSONArray csvobject = Csvson.buildCSVson(csvson, ScenarioContext.getContext());
      if (expectedJson instanceof JSONObject) {
        JSONAssert.assertEquals(csvobject.getJSONObject(0), (JSONObject) expectedJson,
            JSONCompareMode.LENIENT);
      } else {
        JSONAssert.assertEquals(csvobject, (JSONArray) expectedJson, JSONCompareMode.LENIENT);
      }
    } else {
      Assertions.assertTrue(false,
          " Unable to read event name (" + eventName + ") with identifier : " + id);
    }
  }

  @Given("verify-by-elements (.*) contains (.*) on the (.*)$")
  public void consumeMessage(String eventName, String id, String resource,
      Map<String, String> keyValue)
      throws InterruptedException {
    KafkaConsumerClient client = new KafkaConsumerClient(eventName, resource);
    client.run();
    int recheck = 5;
    MessageType expectedJson = (MessageType) MessageContext.getEventContextMap(eventName, id);
    if (expectedJson == null) {
      expectedJson = (MessageType) KafkaConsumerClient.getEvent(eventName, id, recheck);
    }
    if (expectedJson != null) {
      scenario.attach(expectedJson.getMessage().toString(), "application/json", "verifyConsumedJSONObject");
      MessageType finalExpectedJson = expectedJson;
      keyValue.forEach((k, v) -> {
        Object value = MsgHelper.getJSON(finalExpectedJson.getMessage().toString(), k);
        Assertions.assertEquals(StepDefinitionHelper.getActualValue((String) v),
            value, k + " is not failed.");
      });
    } else {
      Assertions.assertTrue(false,
          " Unable to read event name (" + eventName + ") with identifier : " + id);
    }
  }

}