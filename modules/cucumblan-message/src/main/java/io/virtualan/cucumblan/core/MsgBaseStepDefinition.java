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
import io.virtualan.cucumblan.core.msg.kafka.KafkaClient;
import io.virtualan.cucumblan.core.msg.kafka.MessageContext;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.jassert.VirtualJSONAssert;
import io.virtualan.mapson.exception.BadInputDataException;
import java.util.List;
import java.util.logging.Logger;
import org.apache.kafka.common.protocol.types.Field.Str;
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
public class MsgBaseStepDefinition {

  private final static Logger LOGGER = Logger.getLogger(MsgBaseStepDefinition.class.getName());
  private Scenario scenario;


  @Before
  public void before(Scenario scenario) {
    this.scenario = scenario;
  }


  @Given("send message (.*) on the (.*)")
  public void produceMessage(String eventName, String resource, List<String> message) {

  }

  @Given("verify (.*) contains (.*) on the (.*)")
  public void consumeMessage(String eventName, String id, String resource, List<String> csvson)
      throws InterruptedException, BadInputDataException {
    KafkaClient client = new KafkaClient(eventName, resource);
    client.run();
    int recheck = 5;
    Object expectedJson = MessageContext.getEventContextMap(eventName, id);
    if (expectedJson == null) {
      expectedJson = KafkaClient.getEvent(eventName, id, recheck);
    }
    if (expectedJson == null) {
      JSONArray csvobject = Csvson.buildCSVson(csvson, ScenarioContext.getContext());
      if (expectedJson instanceof JSONObject) {
        JSONAssert.assertEquals(csvobject.getJSONObject(0), (JSONObject) expectedJson,
            JSONCompareMode.LENIENT);
      } else {
        JSONAssert.assertEquals(csvobject, (JSONArray) expectedJson, JSONCompareMode.LENIENT);
      }
    } else {
      Assertions.assertTrue(false, " Unable to read event name ("+ eventName+") with identifier : "+ id);
    }
  }


}