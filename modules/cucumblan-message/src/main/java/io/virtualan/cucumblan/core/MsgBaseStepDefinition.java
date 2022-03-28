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

package io.virtualan.cucumblan.core;


import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.virtualan.csvson.Csvson;
import io.virtualan.cucumblan.core.msg.MQClient;
import io.virtualan.cucumblan.core.msg.kafka.KafkaConsumerClient;
import io.virtualan.cucumblan.core.msg.kafka.KafkaProducerClient;
import io.virtualan.cucumblan.core.msg.kafka.MessageContext;
import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.exception.SkipMessageException;
import io.virtualan.cucumblan.message.exception.UnableToProcessException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.props.TopicConfiguration;
import io.virtualan.cucumblan.props.util.EventRequest;
import io.virtualan.cucumblan.props.util.MsgHelper;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import io.virtualan.cucumblan.standard.StandardProcessing;
import io.virtualan.mapson.Mapson;
import io.virtualan.mapson.exception.BadInputDataException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 7 * The type Message base step definition.
 *
 * @author Elan Thangamani
 */
@Slf4j
public class MsgBaseStepDefinition {

    String msgJson = null;
    private boolean skipScenario = false;
    private Scenario scenario;
    private static Map<String, StandardProcessing> stdProcessor = new java.util.HashMap<>();
    private final static java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(MsgBaseStepDefinition.class.getName());

    static {
        try {
            loadStandardProcessors();
        } catch (Exception parserError) {
            LOGGER
                    .warning("Unable to load the process for standard processor for  message " + parserError.getMessage());
        }
    }

    /**
     * Load action processors.
     */
    public static void loadStandardProcessors() {
        org.reflections.Reflections reflections = new org.reflections.Reflections(io.virtualan.cucumblan.props.ApplicationConfiguration.getStandardPackage(),
                new org.reflections.scanners.SubTypesScanner(false));
        java.util.Set<Class<? extends io.virtualan.cucumblan.standard.StandardProcessing>> classes = reflections
                .getSubTypesOf(StandardProcessing.class);
        classes.stream().forEach(x -> {
            StandardProcessing action = null;
            try {
                action = x.newInstance();
                stdProcessor.put(action.getType(), action);
            } catch (InstantiationException e) {
                LOGGER.warning("MSG:Unable to process this action (" + action.getType() + ") class: " + action);
            } catch (IllegalAccessException e) {
                LOGGER.warning("MSG:Unable to process this action (" + action.getType() + ") class: " + action);
            }
        });
    }

    /**
     * Before.
     *
     * @param scenario the scenario
     */
    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
        this.skipScenario = false;
        this.msgJson = null;
    }


    /**
     * given sql.
     *
     * @param dummy the dummy
     * @throws Exception the exception
     */
    @Given("as a user perform message (.*) action$")
    public void dummyGiven(String dummy) throws Exception {
    }

    /**
     * perform the skip scenario
     *
     * @param condition the response value excel based
     * @throws IOException the io exception
     */
    @Given("^perform-message the (.*) condition to skip scenario")
    public void modifyBooleanVariable(String condition) throws Exception {
        skipScenario = (Boolean) io.virtualan.cucumblan.script.ExcelAndMathHelper
                .evaluateWithVariables(Boolean.class, condition, ScenarioContext
                        .getContext(String.valueOf(Thread.currentThread().getId())));
        scenario.log("condition :" + condition + " : is Skipped : " + skipScenario);
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
    @Given("send message (.*) for event (.*) in partition (.*) on (.*) with type (.*)$")
    public void produceMessageWithPartition(String dummy, String eventName, Integer partition, String resource,
                                            String type, Object messages) throws MessageNotDefinedException {
        if (!this.skipScenario) {
            String eventNameInput = StepDefinitionHelper.getActualValue(eventName);
            String typeInput = StepDefinitionHelper.getActualValue(type);
            String topic = StepDefinitionHelper.getActualValue(TopicConfiguration.getProperty(eventNameInput));
            MessageType messageType = MessageContext.getMessageTypes().get(typeInput);
            if (topic != null && messageType != null) {
                MessageType builtMessage = messageType.buildProducerMessage(messages);
                if (builtMessage == null) {
                    java.util.HashMap contextParam = new java.util.HashMap();
                    contextParam.put("EVENT_NAME", eventName);
                    contextParam.put("RESOURCE", resource);
                    contextParam.put("TYPE", type);
                    builtMessage = messageType.buildProducerMessage(messages, contextParam);
                }
                if (builtMessage != null) {
                    scenario.log(builtMessage.toString());

                    if (builtMessage.getMessageAsJson() != null) {
                        this.msgJson = builtMessage.getMessageAsJson().toString();
                    }
                    KafkaProducerClient
                            .sendMessage(resource, topic, builtMessage.getKey(), builtMessage.getMessage(),
                                    partition, builtMessage.getHeaders());
                } else {
                    assertTrue(type + " message was not built properly", false);
                }

            } else {
                assertTrue(eventName + " is not configured for any topic. or " + type + " is not configured", false);
            }
        }
    }

    /**
     * Produce message.
     *
     * @param sleep the sleep
     * @throws InterruptedException the interrupted exception
     */
    @Given("pause message (.*) for process for (.*) milliseconds$")
    public void produceMessage(String dummy, long sleep) throws InterruptedException {
        Thread.sleep(sleep);
    }

    /**
     * eventName to Produce message.
     *
     * @param eventName the eventName
     * @throws InterruptedException the interrupted exception
     */
    @Given("clear the consumed message (.*) for the event (.*)$")
    public void clearMessage(String dummy, String eventName) throws InterruptedException {
        if (!this.skipScenario) {
            String eventNameInput = StepDefinitionHelper.getActualValue(eventName);
            MessageContext.removeEventContextMap(eventNameInput);
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
    @Given("send message (.*) for event (.*) on (.*) with type (.*)$")
    public void produceMessage(String dummy, String eventName, String resource, String type,
                               DataTable messages) throws MessageNotDefinedException {
        if (!this.skipScenario) {
            String eventNameInput = StepDefinitionHelper.getActualValue(eventName);
            String typeInput = StepDefinitionHelper.getActualValue(type);
            String topic = StepDefinitionHelper.getActualValue(TopicConfiguration.getProperty(eventNameInput));
            MessageType messageType = MessageContext.getMessageTypes().get(typeInput);
            if (topic != null && messageType != null) {
                MessageType builtMessage = messageType.buildProducerMessage(messages);
                if (builtMessage == null) {
                    java.util.HashMap contextParam = new java.util.HashMap();
                    contextParam.put("EVENT_NAME", eventName);
                    contextParam.put("RESOURCE", resource);
                    contextParam.put("TYPE", type);
                    builtMessage = messageType.buildProducerMessage(messages, contextParam);
                }
                if (builtMessage != null) {
                    if (builtMessage.getMessageAsJson() != null) {
                        this.msgJson = builtMessage.getMessageAsJson().toString();
                    }
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
                    assertTrue(type + " message was not built properly", false);
                }
            } else {
                assertTrue(eventName + " is not configured for any topic. or " + type + " is not configured", false);
            }
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
    @Given("send inline message (.*) for event (.*) on (.*) with type (.*)$")
    public void produceMessage(String dummy, String eventName, String resource, String type,
                               List<String> messages) throws MessageNotDefinedException {
        if (!this.skipScenario) {
            String eventNameInput = StepDefinitionHelper.getActualValue(eventName);
            String typeInput = StepDefinitionHelper.getActualValue(type);
            String topic = StepDefinitionHelper.getActualValue(TopicConfiguration.getProperty(eventNameInput));
            MessageType messageType = MessageContext.getMessageTypes().get(typeInput);
            if (topic != null && messageType != null) {
                MessageType builtMessage = messageType.buildProducerMessage(messages);
                if (builtMessage == null) {
                    java.util.HashMap contextParam = new java.util.HashMap();
                    contextParam.put("EVENT_NAME", eventName);
                    contextParam.put("RESOURCE", resource);
                    contextParam.put("TYPE", type);
                    builtMessage = messageType.buildProducerMessage(messages, contextParam);
                }
                if (builtMessage != null) {
                    if (builtMessage.getMessageAsJson() != null) {
                        this.msgJson = builtMessage.getMessageAsJson().toString();
                    }
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
                    assertTrue(type + " message was not built properly", false);
                }
            } else {
                assertTrue(eventName + " is not configured for any topic. or " + type + " is not configured", false);
            }
        }
    }


    /**
     * Produce message.
     *
     * @param dummy     the dummy
     * @param queueName the queue name
     * @param resource  the resource
     * @param queueType the queue type
     * @param messages  the messages
     * @throws UnableToProcessException the message not defined exception
     */
    @Given("send inline message (.*) for messageQ (.*) on (.*) with type (.*)$")
    public void produceJMSMessage(String dummy, String queueName, String resource, String queueType, List<String> messages)
            throws UnableToProcessException {
        if (!this.skipScenario) {
            String eventNameInput = StepDefinitionHelper.getActualValue(queueName);
            if (eventNameInput != null) {
                boolean message = false;
                message = MQClient.postMessage(scenario, resource, eventNameInput,
                        StepDefinitionHelper.getActualValue(messages.stream().map(x -> x).collect(Collectors.joining())), queueType);
                assertTrue("message posting status", message);
            } else {
                assertTrue(queueName + " is not configured.", false);
            }
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
    @Given("send mapson message (.*) for event (.*) on (.*) with type (.*)$")
    public void produceMessageMapson(String dummy, String eventName, String resource, String type,
                                     Map<String, String> messages) throws MessageNotDefinedException {
        if (!this.skipScenario) {
            String eventNameInput = StepDefinitionHelper.getActualValue(eventName);
            String typeInput = StepDefinitionHelper.getActualValue(type);
            String topic = StepDefinitionHelper.getActualValue(TopicConfiguration.getProperty(eventNameInput));
            MessageType messageType = MessageContext.getMessageTypes().get(typeInput);
            if (topic != null && messageType != null) {
                MessageType builtMessage = messageType.buildProducerMessage(messages);
                if (builtMessage == null) {
                    java.util.HashMap contextParam = new java.util.HashMap();
                    contextParam.put("EVENT_NAME", eventName);
                    contextParam.put("RESOURCE", resource);
                    contextParam.put("TYPE", type);
                    builtMessage = messageType.buildProducerMessage(messages, contextParam);
                }
                if (builtMessage != null) {
                    scenario.log(builtMessage.toString());
                    if (builtMessage.getMessageAsJson() != null) {
                        this.msgJson = builtMessage.getMessageAsJson().toString();
                    }
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
                    assertTrue(type + " message was not built properly", false);
                }
            } else {
                assertTrue(eventName + " is not configured for any topic. or " + type + " is not configured", false);
            }
        }
    }


    /**
     * Verify consumed json object.
     *
     * @param receiveQ the event name
     * @param id       the id
     * @param resource the resource
     * @param type     the type
     * @param csvson   the csvson
     * @throws InterruptedException       the interrupted exception
     * @throws BadInputDataException      bad input data exception
     * @throws MessageNotDefinedException the message not defined exception
     */
    @Given("verify (.*) for receiveQ (.*) contains (.*) on (.*) with type (.*)$")
    public void verifyConsumedJMSJSONObject(String dummy, String receiveQ, String id, String resource, String type,
                                            List<String> csvson)
            throws Exception {
        if (!this.skipScenario) {
            String eventNameInput = StepDefinitionHelper.getActualValue(receiveQ);
            String idInput = StepDefinitionHelper.getActualValue(id);

            String expectedJson = MQClient.readMessage(scenario, resource, eventNameInput, idInput, type);
            if (expectedJson != null) {
                this.msgJson = expectedJson;
                JSONArray csvobject = Csvson.buildCSVson(csvson, ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())));
                scenario.attach(csvobject.toString(4), "application/json",
                        "ExpectedResponse:");
                Object expectedJsonObj = StepDefinitionHelper.getJSON(expectedJson);
                if (expectedJsonObj instanceof JSONObject) {
                    scenario.attach(((JSONObject) expectedJsonObj).toString(4), "application/json",
                            "ActualResponse:");
                    JSONAssert
                            .assertEquals(csvobject.getJSONObject(0), (JSONObject) expectedJsonObj,
                                    JSONCompareMode.LENIENT);
                } else {
                    scenario.attach(((JSONArray) expectedJsonObj).toString(4), "application/json",
                            "ActualResponse:");
                    JSONAssert.assertEquals(csvobject, (JSONArray) expectedJsonObj, JSONCompareMode.LENIENT);
                }
            } else {
                assertTrue(
                        " Unable to read message name (" + eventNameInput + ") with identifier : " + id, false);
            }
        }
    }

    /**
     * Verify consumed json object.
     *
     * @param receiveQ the event name
     * @param resource the resource
     * @param type     the type
     * @param csvson   the csvson
     * @throws InterruptedException       the interrupted exception
     * @throws BadInputDataException      bad input data exception
     * @throws MessageNotDefinedException the message not defined exception
     */
    @Given("verify (.*) for receiveQ (.*) find (.*) message on (.*) with type (.*)$")
    public void verifyConsumedJMSJSONObjectWithOutId(String dummy, String receiveQ, String jsonpath, String resource, String type,
                                                     List<String> csvson)
            throws InterruptedException, BadInputDataException, MessageNotDefinedException, IOException, JMSException, SkipMessageException {
        if (!this.skipScenario) {
            String eventNameInput = StepDefinitionHelper.getActualValue(receiveQ);

            String expectedJson = MQClient.findMessage(scenario, resource, eventNameInput, jsonpath, type);
            if (expectedJson != null) {
                this.msgJson = expectedJson;
                JSONArray csvobject = Csvson.buildCSVson(csvson, ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())));
                scenario.attach(csvobject.toString(4), "application/json",
                        "ExpectedResponse:");
                Object expectedJsonObj = StepDefinitionHelper.getJSON(expectedJson);
                if (expectedJsonObj instanceof JSONObject) {
                    scenario.attach(((JSONObject) expectedJsonObj).toString(4), "application/json",
                            "ActualResponse:");
                    JSONAssert
                            .assertEquals(csvobject.getJSONObject(0), (JSONObject) expectedJsonObj,
                                    JSONCompareMode.LENIENT);
                } else {
                    scenario.attach(((JSONArray) expectedJsonObj).toString(4), "application/json",
                            "ActualResponse:");
                    JSONAssert.assertEquals(csvobject, (JSONArray) expectedJsonObj, JSONCompareMode.LENIENT);
                }
            } else {
                assertTrue(
                        " Unable to read message name (" + eventNameInput + ") with identifier : " + jsonpath, false);
            }
        }
    }


    /**
     * Verify consumed json object.
     *
     * @param eventName the event name
     * @param stdType   the stdType
     * @param resource  the resource
     * @param type      the type
     * @param csvson    the csvson
     * @throws InterruptedException       the interrupted exception
     * @throws BadInputDataException      bad input data exception
     * @throws MessageNotDefinedException the message not defined exception
     */
    @Given("verify (.*) for event (.*) message-aggregated-std-type (.*) on (.*) with type (.*)$")
    public void verifyConsumedJSONObjectWithStdType(String dummy, String eventName, String stdType, String resource, String type,
                                                    List<String> csvson)
            throws InterruptedException, BadInputDataException, MessageNotDefinedException {
        if (!this.skipScenario) {
            int recheck = 0;
            String eventNameInput = StepDefinitionHelper.getActualValue(eventName);
            String typeInput = StepDefinitionHelper.getActualValue(type);
            String idInput = StepDefinitionHelper.getActualValue("SKIP_BY_ID");
            EventRequest eventRequest = new EventRequest();
            eventRequest.setRecheck(recheck);
            eventRequest.setEventName(eventNameInput);
            eventRequest.setType(typeInput);
            eventRequest.setId(idInput);
            eventRequest.setResource(resource);

            KafkaConsumerClient.getEvent(eventRequest);
            StandardProcessing processing = stdProcessor.get(stdType);
            if (processing != null && processing.responseEvaluator() != null) {
                Object expectedJson = processing.responseEvaluator();
                JSONArray csvobject = Csvson.buildCSVson(csvson, ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())));
                scenario.attach(csvobject.toString(4), "application/json",
                        "ExpectedResponse:");
                if (expectedJson instanceof JSONObject) {
                    scenario.attach(((JSONObject) expectedJson).toString(4), "application/json",
                            "ActualResponse:");
                    JSONAssert
                            .assertEquals(csvobject.getJSONObject(0), (JSONObject) expectedJson,
                                    JSONCompareMode.LENIENT);
                } else {
                    scenario.attach(((JSONArray) expectedJson).toString(4), "application/json",
                            "ActualResponse:");
                    JSONAssert.assertEquals(csvobject, (JSONArray) expectedJson, JSONCompareMode.LENIENT);
                }
            } else {
                assertTrue(
                        " Unable to read event name (" + eventName + ") with std-type : " + stdType, false);
            }
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
    @Given("verify (.*) for event (.*) contains (.*) on (.*) with type (.*)$")
    public void verifyConsumedJSONObject(String dummy, String eventName, String id, String resource, String type,
                                         List<String> csvson)
            throws InterruptedException, BadInputDataException, MessageNotDefinedException {
        if (!this.skipScenario) {
            int recheck = 0;
            String eventNameInput = StepDefinitionHelper.getActualValue(eventName);
            String typeInput = StepDefinitionHelper.getActualValue(type);
            String idInput = StepDefinitionHelper.getActualValue(id);
            EventRequest eventRequest = new EventRequest();
            eventRequest.setRecheck(recheck);
            eventRequest.setEventName(eventNameInput);
            eventRequest.setType(typeInput);
            eventRequest.setId(idInput);
            eventRequest.setResource(resource);

            MessageType expectedJson = KafkaConsumerClient.getEvent(eventRequest);
            if (expectedJson != null) {
                if (expectedJson.getMessageAsJson() != null) {
                    this.msgJson = expectedJson.getMessageAsJson().toString();
                }
                JSONArray csvobject = Csvson.buildCSVson(csvson, ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())));
                scenario.attach(csvobject.toString(4), "application/json",
                        "ExpectedResponse:");
                if (expectedJson.getMessageAsJson() instanceof JSONObject) {
                    scenario.attach(((JSONObject) expectedJson.getMessageAsJson()).toString(4), "application/json",
                            "ActualResponse:");
                    JSONAssert
                            .assertEquals(csvobject.getJSONObject(0), (JSONObject) expectedJson.getMessageAsJson(),
                                    JSONCompareMode.LENIENT);
                } else {
                    scenario.attach(((JSONArray) expectedJson.getMessageAsJson()).toString(4), "application/json",
                            "ActualResponse:");
                    JSONAssert.assertEquals(csvobject, (JSONArray) expectedJson, JSONCompareMode.LENIENT);
                }
            } else {
                assertTrue(
                        " Unable to read event name (" + eventName + ") with identifier : " + idInput, false);
            }
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
    @Given("verify-by-elements (.*) for event (.*) contains (.*) on (.*) with type (.*)$")
    public void consumeMessage(String dummy, String eventName, String id, String resource, String type,
                               Map<String, String> keyValue)
            throws InterruptedException, MessageNotDefinedException {
        if (!this.skipScenario) {
            int recheck = 0;
            String eventNameInput = StepDefinitionHelper.getActualValue(eventName);
            String typeInput = StepDefinitionHelper.getActualValue(type);
            String idInput = StepDefinitionHelper.getActualValue(id);
            EventRequest eventRequest = new EventRequest();
            eventRequest.setRecheck(recheck);
            eventRequest.setEventName(eventNameInput);
            eventRequest.setType(typeInput);
            eventRequest.setId(idInput);
            eventRequest.setResource(resource);

            MessageType expectedJson = KafkaConsumerClient.getEvent(eventRequest);
            if (expectedJson != null) {
                if (expectedJson.getMessageAsJson() != null) {
                    this.msgJson = expectedJson.getMessageAsJson().toString();
                }
                scenario.attach(expectedJson.getMessageAsJson().toString(), "application/json",
                        "ActualResponse");
                MessageType finalExpectedJson = expectedJson;
                keyValue.forEach((k, v) -> {
                    Object value = MsgHelper.getJSON(finalExpectedJson.getMessageAsJson().toString(), k);
                    assertEquals(k + " is not failed.",
                            StepDefinitionHelper.getObjectValue(StepDefinitionHelper.getActualValue((String) v)),
                            value);
                });
            } else {
                assertTrue(
                        " Unable to read event name (" + eventName + ") with identifier : " + id, false);
            }
        }
    }

    @Given("^store (.*) as key and message's (.*) as value")
    public void storeMessageResponseAskeySwap(String key, String responseKey) throws JSONException {
        storeMessageResponseAskey(responseKey, key);
    }

    @Given("^store-message's (.*) value of the key as (.*)")
    public void storeMessageResponseAskey(String responseKey, String key) throws JSONException {
        if (!this.skipScenario) {
            if (msgJson != null) {
                Map<String, String> mapson = Mapson.buildMAPsonFromJson(msgJson);
                if (mapson.get(responseKey) != null) {
                    ScenarioContext
                            .setContext(String.valueOf(Thread.currentThread().getId()), key,
                                    mapson.get(responseKey));
                } else {
                    assertTrue(responseKey + " not found in the read message ", false);
                }
            } else {
                assertTrue(" Message not found for the read message?  ", false);
            }
        }
    }


}