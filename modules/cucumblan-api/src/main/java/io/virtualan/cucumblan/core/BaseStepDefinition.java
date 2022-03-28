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

import com.jayway.jsonpath.JsonPath;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Cookie;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.virtualan.cucumblan.exception.ParserError;
import io.virtualan.cucumblan.parser.OpenAPIParser;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.EndpointConfiguration;
import io.virtualan.cucumblan.props.ExcludeConfiguration;
import io.virtualan.cucumblan.props.util.ApiHelper;
import io.virtualan.cucumblan.props.util.HelperApiUtil;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import io.virtualan.cucumblan.script.ExcelAndMathHelper;
import io.virtualan.cucumblan.standard.StandardProcessing;
import io.virtualan.mapson.Mapson;
import io.virtualan.util.Helper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidatorSettings.settings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * The type Base step definition for api.
 *
 * @author Elan Thangamani
 */
public class BaseStepDefinition {

    private final static Logger LOGGER = Logger.getLogger(BaseStepDefinition.class.getName());
    private static Map<String, StandardProcessing> stdProcessorMap = new HashMap<>();

    static {
        try {
            OpenAPIParser.loader();
            EndpointConfiguration.getInstance().loadEndpoints();
            loadStandardProcessors();
        } catch (ParserError parserError) {
            LOGGER
                    .warning("Unable to start the process - see if conf folder and endpoints are generated");
            System.exit(-1);
        }
    }

    private Response response;
    private ValidatableResponse validatableResponse;
    private String jsonBody;
    private RequestSpecification request = given();
    private Scenario scenario;
    private String acceptContentType;
    private boolean skipScenario = false;

    /**
     * Load action processors.
     */
    public static void loadStandardProcessors() {
        Reflections reflections = new Reflections(ApplicationConfiguration.getStandardPackage(),
                new SubTypesScanner(false));
        Set<Class<? extends StandardProcessing>> classes = reflections
                .getSubTypesOf(StandardProcessing.class);
        classes.stream().forEach(x -> {
            StandardProcessing action = null;
            try {
                action = x.newInstance();
                stdProcessorMap.put(action.getType(), action);
            } catch (InstantiationException e) {
                LOGGER.warning("Unable to process this action (" + action.getType() + ") class: " + action);
            } catch (IllegalAccessException e) {
                LOGGER.warning("Unable to process this action (" + action.getType() + ") class: " + action);
            }
        });
    }

    /**
     * Read request by path param.
     *
     * @param dummy      the dummy
     * @param identifier the identifier
     * @param value      the value
     */
    @Given("^(.*) with an path param (.*) of (.*)")
    public void readRequestByPathParam(String dummy, String identifier, String value) {
        if (!this.skipScenario) {
            request = given().pathParam(identifier, StepDefinitionHelper.getActualValue(value));
        }
    }


    /**
     * Read request by path param.
     *
     * @param identifier the identifier
     * @param value      the value
     */
    @Given("^enable cert for (.*) of (.*)")
    public void cert(String identifier, String value) {
        if (!this.skipScenario) {
            RestAssured.authentication = RestAssured.certificate(identifier, value);
        }
    }

    /**
     * Read request by path param.
     *
     * @param username the identifier
     * @param password the value
     */
    @Given("^basic authentication with (.*) and (.*)")
    public void auth(String username, String password) {
        if (!this.skipScenario) {
            byte[] authBasic = Base64.getEncoder().encode(String
                    .format("%s:%s", StepDefinitionHelper.getActualValue(username),
                            StepDefinitionHelper.getActualValue(password)).getBytes());
            request.header("Authorization", String.format("Basic %s", new String(authBasic)));
        }
    }

    /**
     * Read request by path param.
     *
     * @param auth  the identifier
     * @param token the value
     */
    @Given("^(.*) auth with (.*) token$")
    public void bearer(String auth, String token) {
        if (!this.skipScenario) {
            request.header("Authorization", String
                    .format("%s %s", auth, Helper.getActualValueForAll(token, ScenarioContext
                            .getContext(String.valueOf(Thread.currentThread().getId())))));
        }
    }


    /**
     * Read request by path param.
     *
     * @param dummy the dummy
     */
    @Given("^(.*) perform (.*) api action")
    public void readRequestByPathParam(String dummy, String dummy1) {
        if (!this.skipScenario) {
            request = given();
        }
    }

    /**
     * Read request by header param.
     *
     * @param dummy      the dummy
     * @param identifier the identifier
     * @param value      the value
     */
    @Given("^(.*) with an header param (.*) of (.*)")
    public void readRequestByHeaderParam(String dummy, String identifier, String value) {
        if (!this.skipScenario) {
            if ("Accept".equalsIgnoreCase(identifier)) {
                acceptContentType = value;
            }
            request = request.header(identifier, StepDefinitionHelper.getActualValue(value));
        }
    }


    /**
     * Read request.
     *
     * @param nameIgnore   the name ignore
     * @param parameterMap the parameter map
     */
    @Given("add (.*) with given header params$")
    public void readAllHeaderParams(String nameIgnore, Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            for (Map.Entry<String, String> params : parameterMap.entrySet()) {
                if ("Accept".equalsIgnoreCase(params.getKey())) {
                    acceptContentType = StepDefinitionHelper.getActualValue(params.getValue());
                }
                if ("contentType".equalsIgnoreCase(params.getKey()) &&
                        StepDefinitionHelper.getActualValue(params.getValue()).contains("multipart/form-data")) {
                    String boundary = Long.toHexString(System.currentTimeMillis());
                    request = request.contentType(StepDefinitionHelper.getActualValue(params.getValue()) + "; boundary=" + boundary);
                } else if ("contentType".equalsIgnoreCase(params.getKey())) {
                    request = request.contentType(StepDefinitionHelper.getActualValue(params.getValue()));
                }
                request = request
                        .header(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
            }
        }
    }

    /**
     * Read request.
     *
     * @param nameIgnore   the name ignore
     * @param parameterMap the parameter map
     */
    @Given("add (.*) with given cookie params$")
    public void readAllCookieParams(String nameIgnore, Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            for (Map.Entry<String, String> params : parameterMap.entrySet()) {
                request = request.cookie(new
                        Cookie.Builder(params.getKey(),
                        StepDefinitionHelper.getActualValue(params.getValue())).build());
            }
        }
    }

    /**
     * Read request by query param.
     *
     * @param dummy      the dummy
     * @param identifier the identifier
     * @param value      the value
     */
    @Given("^(.*) with an query param (.*) of (.*)")
    public void readRequestByQueryParam(String dummy, String identifier, String value) {
        if (!this.skipScenario) {
            request = given().queryParam(identifier, StepDefinitionHelper.getActualValue(value));
        }
    }

    /**
     * Load global param.
     *
     * @param globalParams the global params
     * @throws IOException the io exception
     */
    @Given("^provided all the feature level parameters$")
    public void loadGlobalParam(Map<String, String> globalParams) throws IOException {
        if (!this.skipScenario) {
            ScenarioContext
                    .setContext(String.valueOf(Thread.currentThread().getId()), globalParams);
            scenario.attach(new JSONObject(ScenarioContext
                            .getPrintableContextObject(String.valueOf(Thread.currentThread().getId()))).toString(4),
                    "application/json", "ContextId:" + String.valueOf(Thread.currentThread().getId()));
        }
    }

    /**
     * Load global param.
     *
     * @throws IOException the io exception
     */
    @Given("^provided all the feature level parameters from file$")
    public void loadGlobalParamFromFile() throws IOException {
        Map<String, String> env = System.getenv();
        if (env != null && !env.isEmpty()) {
            for (String envName : env.keySet()) {
                if (envName.startsWith("cucumblan.")) {
                    ScenarioContext
                            .setContext(String.valueOf(Thread.currentThread().getId()),
                                    envName.replace("cucumblan.", ""),
                                    env.get(envName));
                }
            }
        }
        Properties properties = new Properties();
        InputStream stream = ApplicationConfiguration.class.getClassLoader()
                .getResourceAsStream("cucumblan-env.properties");
        if (stream == null) {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("cucumblan-env.properties");
        }
        if (stream != null) {
            properties.load(stream);
            ScenarioContext
                    .setContext(String.valueOf(Thread.currentThread().getId()), (Map) properties);
            scenario.attach(new JSONObject(ScenarioContext
                            .getPrintableContextObject(String.valueOf(Thread.currentThread().getId()))).toString(4),
                    "application/json", "ContextId:" + String.valueOf(Thread.currentThread().getId()));
        } else {
            LOGGER.warning(
                    "cucumblan-env.properties is not configured. Need to add if default data loaded");
        }
    }

    /**
     * Validate global param.
     */
    @Then("^verify all the feature level parameters exists$")
    public void validateGlobalParam() {
        if (!this.skipScenario) {
            assertTrue("Valid Global Parameters are present ", ScenarioContext
                    .hasContextValues(String.valueOf(Thread.currentThread().getId())));
        }
    }

    @Given("^create variable (.*) as key and (.*) as value$")
    public void addVariableSwap(String key, String responseValue) throws  Exception{
        addVariable(responseValue, key);
    }

    /**
     * Add variable.
     *
     * @param responseValue the response value
     * @param key           the key
     */
    @Given("^add the (.*) value of the key as (.*)$")
    public void addVariable(String responseValue, String key) throws Exception {
        if (!this.skipScenario) {

            Object value = ExcelAndMathHelper.evaluateWithVariableType(
                    responseValue, ScenarioContext
                            .getContext(String.valueOf(Thread.currentThread().getId())));
            if(value  != null) {
                ScenarioContext
                        .setContext(String.valueOf(Thread.currentThread().getId()), key,value.toString());
            } else {
                ScenarioContext
                        .setContext(String.valueOf(Thread.currentThread().getId()), key, responseValue);
            }
        }
    }

    @Given("^evaluate key as (.*) and (.*) as decimal value$")
    public void modifyDecimalVariableSwap(String key, String responseValue) throws Exception {
        modifyDecimalVariable(responseValue, key);
    }

    /**
     * Modify variable.
     *
     * @param responseValue the response value
     * @param key           the key
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) decimal value of the key as (.*)$")
    public void modifyDecimalVariable(String responseValue, String key) throws Exception {
        if (!this.skipScenario) {
            ScenarioContext
                    .setContext(String.valueOf(Thread.currentThread().getId()), key, ExcelAndMathHelper.evaluateWithVariables(Double.class,
                            responseValue, ScenarioContext
                                    .getContext(String.valueOf(Thread.currentThread().getId()))).toString());
        }
    }

    @Given("^evaluate key as (.*) and (.*) as integer value$")
    public void modifyIntVariableSwap(String key, String responseValue) throws Exception {
        modifyIntVariable(responseValue, key);
    }


    /**
     * Modify variable.
     *
     * @param responseValue the response value
     * @param key           the key
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) integer value of the key as (.*)$")
    public void modifyIntVariable(String responseValue, String key) throws Exception {
        if (!this.skipScenario) {
            ScenarioContext
                    .setContext(String.valueOf(Thread.currentThread().getId()), key, ExcelAndMathHelper.evaluateWithVariables(Integer.class,
                            responseValue, ScenarioContext
                                    .getContext(String.valueOf(Thread.currentThread().getId()))).toString());
        }
    }

    @Given("^evaluate key as (.*) and (.*) as function value$")
    public void modifyfunctionVariableSwap(String key, String responseValue) throws Exception {
        modifyfunctionVariable(responseValue, key);
    }


    /**
     * Modify variable.
     *
     * @param responseValue the response value
     * @param key           the key
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) function value of the key as (.*)$")
    public void modifyfunctionVariable(String responseValue, String key) throws Exception {
        if (!this.skipScenario) {
            ScenarioContext
                    .setContext(String.valueOf(Thread.currentThread().getId()), key, ExcelAndMathHelper.evaluateWithVariables(String.class,
                            responseValue, ScenarioContext
                                    .getContext(String.valueOf(Thread.currentThread().getId()))).toString());
        }
    }


    /**
     * perform the skip scenario
     *
     * @param condition the response value excel based
     * @throws IOException the io exception
     */
    @Given("^perform the (.*) condition to skip scenario$")
    public void modifyBooleanVariable(String condition) throws Exception {
        skipScenario = (Boolean) ExcelAndMathHelper
                .evaluateWithVariables(Boolean.class, condition, ScenarioContext
                        .getContext(String.valueOf(Thread.currentThread().getId())));
        scenario.log("condition :" + condition + " : is Skipped : " + skipScenario);
    }

    /**
     * perform the success met
     *
     * @param condition the response value excel based
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) condition success$")
    public void evaluateVariable(String condition) throws Exception {
        if (!this.skipScenario) {
            boolean flag = (Boolean) ExcelAndMathHelper
                    .evaluateWithVariables(Boolean.class, condition, ScenarioContext
                            .getContext(String.valueOf(Thread.currentThread().getId())));
            scenario.log("Success condition :" + condition + " >>> status " + flag);
            assertTrue("Valid success" + condition + " is met ", flag);
        }

    }

    /**
     * perform the failure met
     *
     * @param condition the response value excel based
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) condition fail$")
    public void evaluateVariableFail(String condition) throws Exception {
        if (!this.skipScenario) {
            boolean flag = (Boolean) ExcelAndMathHelper
                    .evaluateWithVariables(Boolean.class, condition, ScenarioContext
                            .getContext(String.valueOf(Thread.currentThread().getId())));
            scenario.log("Failure condition :" + condition + " >>> status " + flag);
            assertTrue("Valid Failure" + condition + " is met ", flag);
        }
    }

    @Given("^evaluate key as (.*) and (.*) as boolean value$")
    public void modifyBooleanVariableSwap(String key, String responseValue) throws Exception {
        modifyBooleanVariable(responseValue, key);
    }


    /**
     * Modify variable.
     *
     * @param responseValue the response value
     * @param key           the key
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) boolean value of the key as (.*)$")
    public void modifyBooleanVariable(String responseValue, String key) throws Exception {
        if (!this.skipScenario) {
            ScenarioContext
                    .setContext(String.valueOf(Thread.currentThread().getId()), key, ExcelAndMathHelper.evaluateWithVariables(Boolean.class,
                            responseValue, ScenarioContext
                                    .getContext(String.valueOf(Thread.currentThread().getId()))).toString());
        }
    }

    @Given("^modify key as (.*) and (.*) as value$")
    public void modifyStringVariableSwap(String responseValue, String key) throws IOException {
        modifyStringVariable(responseValue, key);
    }

    /**
     * Modify variable.
     *
     * @param responseValue the response value
     * @param key           the key
     * @throws IOException the io exception
     */
    @Given("^modify the (.*) value of the key as (.*)$")
    public void modifyStringVariable(String responseValue, String key) throws IOException {
        if (!this.skipScenario) {
            ScenarioContext
                    .setContext(String.valueOf(Thread.currentThread().getId()), key,
                            Helper.getActualValueForAll(responseValue, ScenarioContext
                                    .getContext(String.valueOf(Thread.currentThread().getId()))).toString());
        }
    }

    @Given("^store (.*) as key and api's (.*) as value$")
    public void storeResponseAskeySwap(String key, String responseKey) {
        storeResponseAskey(null, responseKey, key);
    }

    @Given("^store-standard type's (.*) with (.*) as key and api's (.*) as value$")
    public void storeStandarReposne(String type, String key, String responseKey) {
        storeResponseAskey(type, responseKey, key);
    }


    /**
     * Load as global param.
     *
     * @param responseKey the response key
     * @param key         the key
     */
    @Given("^store the (.*) value of the key as (.*)$")
    public void storeResponseAskey(String responseKey, String key) {
        storeResponseAskey(null, responseKey, key);
    }

    public void storeResponseAskey(String type, String responseKey, String key) {
        if (!this.skipScenario) {
            if (".".equalsIgnoreCase(responseKey)) {
                ScenarioContext
                        .setContext(String.valueOf(Thread.currentThread().getId()), key,
                                validatableResponse.extract().body().asString());
            } else {
                String value = null;
                if (type != null) {
                    StandardProcessing processing = stdProcessorMap.get(type);
                    if (processing != null) {
                        if (validatableResponse != null
                                && validatableResponse.extract().body().asString() != null) {
                            String jsonRequestActual = processing
                                    .postResponseProcessing(validatableResponse.extract().body().asString());
                            if (jsonRequestActual != null) {
                                try {
                                    value = JsonPath.read(jsonRequestActual, responseKey);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                } else {
                    value = validatableResponse.extract().body().jsonPath()
                            .getString(responseKey);
                }
                if (value != null) {
                    ScenarioContext

                            .setContext(String.valueOf(Thread.currentThread().getId()), key,
                                    validatableResponse.extract().body().jsonPath().getString(responseKey));
                } else if (response.getCookie(responseKey) != null) {
                    ScenarioContext

                            .setContext(String.valueOf(Thread.currentThread().getId()), key,
                                    response.getCookie(responseKey));
                } else if (response.getHeader(responseKey) != null) {
                    ScenarioContext

                            .setContext(String.valueOf(Thread.currentThread().getId()), key,
                                    response.getHeader(responseKey));
                } else {
                    LOGGER.warning(responseKey + " :  for " + key + " not found");
                    scenario.log(responseKey + " :  for " + key + " not found");
                }
            }
        }
    }

    /**
     * Read request.
     *
     * @param nameIgnore   the name ignore
     * @param parameterMap the parameter map
     */
    @Given("^add (.*) with given path params$")
    public void readParamsRequest(String nameIgnore, Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            //request = request.contentType("application/json");
            for (Map.Entry<String, String> params : parameterMap.entrySet()) {
                request = request
                        .pathParam(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
            }
        }
    }

    /**
     * Read request.
     *
     * @param nameIgnore   the name ignore
     * @param contentType  the content type
     * @param parameterMap the parameter map
     */
    @Given("^add (.*) with (.*) given form params$")
    public void readMultiParamsRequest(String nameIgnore, String contentType,
                                       Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            request = request.config(RestAssured.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(contentType,
                                    ContentType.fromContentType(contentType))));
            for (Map.Entry<String, String> params : parameterMap.entrySet()) {
                request = request
                        .param(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
            }
        }
    }

    /**
     * Read request.
     *
     * @param nameIgnore   the name ignore
     * @param contentType  the content type
     * @param parameterMap the parameter map
     */
    @Given("^add (.*) with (.*) given multipart-form params$")
    public void readPathParamsRequest(String nameIgnore, String contentType,
                                      Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            request = request.config(RestAssured.config()
                    .encoderConfig(EncoderConfig.encoderConfig()
                            .encodeContentTypeAs(contentType,
                                    ContentType.fromContentType(contentType))));
            for (Map.Entry<String, String> params : parameterMap.entrySet()) {
                String fileAndType = StepDefinitionHelper.getActualValue(params.getValue());
                if (BaseStepDefinition.class.getClassLoader()
                        .getResource(fileAndType) != null &&
                        new File(BaseStepDefinition.class.getClassLoader()
                                .getResource(fileAndType).getFile()).exists()) {
                    request = request
                            .multiPart(params.getKey(),
                                    new File(BaseStepDefinition.class.getClassLoader()
                                            .getResource(fileAndType).getFile()));
                } else {
                    request = request
                            .multiPart(params.getKey(), fileAndType);
                }
            }
        }
    }

    /**
     * Read request.
     *
     * @param nameIgnore   the name ignore
     * @param parameterMap the parameter map
     */
    @Given("add (.*) with given query params$")
    public void readRequest(String nameIgnore, Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            //request = request.contentType("application/json");
            for (Map.Entry<String, String> params : parameterMap.entrySet()) {
                request = request
                        .queryParam(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
            }
        }
    }

    /**
     * Read request.
     *
     * @param nameIgnore   the name ignore
     * @param contentType  the content type
     * @param parameterMap the parameter map
     */
    @Given("add (.*) with contentType (.*) given query params$")
    public void readRequest(String nameIgnore, String contentType, Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            request = request.contentType(contentType);
            for (Map.Entry<String, String> params : parameterMap.entrySet()) {
                request = request
                        .queryParam(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
            }
        }
    }

    /**
     * Load request.
     *
     * @param nameIgnore   the name ignore
     * @param contentType  the content type
     * @param parameterMap the parameter map
     */
    @Given("^populate (.*) with contentType(.*) given input$")
    public void loadRequest(String nameIgnore, String contentType, Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            request = request.contentType(contentType);
            for (Map.Entry<String, String> params : parameterMap.entrySet()) {
                request = request
                        .queryParam(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
            }
        }
    }

    /**
     * Load request.
     *
     * @param nameIgnore   the name ignore
     * @param parameterMap the parameter map
     */
    @Given("^populate (.*) with given input$")
    public void loadRequest(String nameIgnore, Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            //request = request.contentType("application/json");
            for (Map.Entry<String, String> params : parameterMap.entrySet()) {
                request = request
                        .queryParam(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
            }
        }
    }

    /**
     * Create request.
     *
     * @param body        the body
     * @param contentType the content type
     */
    @Given("^add (.*) data with (.*) given input$")
    public void createRequest(String body, String contentType) {
        if (!this.skipScenario) {
            request = request.contentType(contentType).body(body);
        }
    }


    /**
     * Create request.
     *
     * @param fileBody    the body
     * @param contentType the content type
     * @throws IOException the io exception
     */
    @Given("add (.*) data file with (.*) given input$")
    public void createFileRequest(String fileBody, String contentType) throws IOException {
        if (!this.skipScenario) {
            String body = HelperApiUtil.readFileAsString(fileBody);
            if (body != null) {
                Map<String, String> mapHeader = new HashMap();
                mapHeader.put("content-type", contentType);
                request = request.headers(mapHeader).contentType(contentType).body(body);
            } else {
                Assert.assertTrue(fileBody + " input file is missing ", false);
            }
        }
    }

    /**
     * Create request.
     *
     * @param fileBody    the body
     * @param contentType the content type
     * @param input       the input
     * @throws IOException the io exception
     */
    @Given("add (.*) data inline with (.*) given input$")
    public void createInlineRequest(String fileBody, String contentType, List<String> input)
            throws IOException {
        if (!this.skipScenario) {
            if (input != null && !input.isEmpty()) {
                Map<String, String> mapHeader = new HashMap();
                mapHeader.put("content-type", contentType);
                String listString = input.stream().map(Object::toString)
                        .collect(Collectors.joining("\n"));
                request = request.headers(mapHeader).contentType(contentType).body(listString);
            } else {
                Assert.assertTrue(fileBody + " input inline is missing ", false);
            }
        }
    }

    /**
     * Create request.
     *
     * @param nameIgnore   the name ignore
     * @param contentType  the content type
     * @param parameterMap the parameter map
     * @throws Exception the exception
     */
    @Given("^create (.*) with contentType (.*) given input$")
    public void createRequest(String nameIgnore, String contentType, Map<String, String> parameterMap)
            throws Exception {
        if (!this.skipScenario) {
            jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext
                    .getContext(String.valueOf(Thread.currentThread().getId())));
            scenario.attach(jsonBody
                    , contentType, "Request:");
            request = request.contentType(contentType).body(jsonBody);
        }
    }


    /**
     * Create request.
     *
     * @param nameIgnore   the name ignore
     * @param parameterMap the parameter map
     * @throws Exception the exception
     */
    @Given("^create (.*) with given input$")
    public void createRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        if (!this.skipScenario) {
            jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext
                    .getContext(String.valueOf(Thread.currentThread().getId())));

            if (StepDefinitionHelper.getJSON(jsonBody) instanceof JSONArray) {
                scenario.attach(new JSONArray(jsonBody).toString(4)
                        , "application/json", "RequestData:");
            } else {
                scenario.attach(new JSONObject(jsonBody).toString(4)
                        , "application/json", "RequestData:");
            }
            request = request.body(jsonBody);
        }
    }

    /**
     * Update request.
     *
     * @param nameIgnore   the name ignore
     * @param parameterMap the parameter map
     * @throws Exception the exception
     */
    @Given("^update (.*) with given input$")
    public void updateRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        if (!this.skipScenario) {
            jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext
                    .getContext(String.valueOf(Thread.currentThread().getId())));
            scenario.attach(jsonBody
                    , "application/json", "RequestData:");
            request = request.contentType("application/json").body(jsonBody);
        }
    }

    /**
     * Delete request.
     *
     * @param nameIgnore   the name ignore
     * @param parameterMap the parameter map
     * @throws Exception the exception
     */
    @Given("^delete (.*) with given input$")
    public void deleteRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        if (!this.skipScenario) {
            jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext
                    .getContext(String.valueOf(Thread.currentThread().getId())));
            scenario.attach(jsonBody
                    , "application/json", "RequestData:");
            request = request.contentType("application/json").body(jsonBody);
        }
    }

    /**
     * Update request.
     *
     * @param nameIgnore   the name ignore
     * @param contentType  the content type
     * @param parameterMap the parameter map
     * @throws Exception the exception
     */
    @Given("^update (.*) with contentType (.*) given input$")
    public void updateRequest(String nameIgnore, String contentType, Map<String, String> parameterMap)
            throws Exception {
        if (!this.skipScenario) {
            jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext
                    .getContext(String.valueOf(Thread.currentThread().getId())));
            scenario.attach(jsonBody
                    , "application/json", "RequestData:");
            request = request.contentType(contentType).body(jsonBody);
        }
    }


    /**
     * Create request.
     *
     * @param dummyString       the dummy string
     * @param acceptContentType the accept content type
     * @param resource          the resource
     * @param system            the system
     */
    @When("^(.*) post (.*) in (.*) resource on (.*)")
    public void createRequest(String dummyString, String acceptContentType, String resource,
                              String system) throws Exception {
        resource = StepDefinitionHelper.getActualValue(resource);
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            acceptContentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", acceptContentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext
                    .getPrintableContextObject(String.valueOf(Thread.currentThread().getId()))));

            scenario.attach(object.toString(4)
                    , "application/json", "RequestData:");

            response = request.baseUri(url).when()
                    .log().all()
                    .accept(acceptContentType)
                    .post(resourceDetails);
        }
    }

    /**
     * Read request.
     *
     * @param dummyString       the dummy string
     * @param acceptContentType the accept content type
     * @param resource          the resource
     * @param system            the system
     */
    @When("^(.*) get (.*) in (.*) resource on (.*)")
    public void readRequest(String dummyString, String acceptContentType, String resource,
                            String system) throws Exception {
        resource = StepDefinitionHelper.getActualValue(resource);
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            String contentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", contentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext
                    .getPrintableContextObject(String.valueOf(Thread.currentThread().getId()))));

            scenario.attach(object.toString(4)
                    , "application/json", "RequestData:");
            response = request.baseUri(ApiHelper.getHostName(resource, system)).when()
                    .log().all().accept(acceptContentType)
                    .get(ApiHelper.getActualResource(resource, system));
        }
    }

    /**
     * Modify request.
     *
     * @param dummyString       the dummy string
     * @param acceptContentType the accept content type
     * @param resource          the resource
     * @param system            the system
     */
    @When("^(.*) put (.*) in (.*) resource on (.*)")
    public void modifyRequest(String dummyString, String acceptContentType, String resource,
                              String system) throws Exception {
        resource = StepDefinitionHelper.getActualValue(resource);
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            acceptContentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", acceptContentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext
                    .getPrintableContextObject(String.valueOf(Thread.currentThread().getId()))));

            scenario.attach(object.toString(4)
                    , "application/json", "RequestData:");
            response = request.baseUri(ApiHelper.getHostName(resource, system)).when()
                    .log().all().accept(acceptContentType)
                    .put(ApiHelper.getActualResource(resource, system));
        }
    }

    /**
     * Pathch request.
     *
     * @param dummyString       the dummy string
     * @param acceptContentType the accept content type
     * @param resource          the resource
     * @param system            the system
     */
    @When("^(.*) patch (.*) in (.*) resource on (.*)")
    public void patchRequest(String dummyString, String acceptContentType, String resource,
                             String system) throws Exception {
        resource = StepDefinitionHelper.getActualValue(resource);
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            acceptContentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", acceptContentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext
                    .getPrintableContextObject(String.valueOf(Thread.currentThread().getId()))));

            scenario.attach(object.toString(4)
                    , "application/json", "RequestData:");
            response = request.baseUri(ApiHelper.getHostName(resource, system)).when()
                    .log().all().accept(acceptContentType)
                    .patch(ApiHelper.getActualResource(resource, system));
        }
    }

    /**
     * Delete by id.
     *
     * @param dummyString       the dummy string
     * @param acceptContentType the accept content type
     * @param resource          the resource
     * @param system            the system
     */
    @When("^(.*) delete (.*) in (.*) resource on (.*)")
    public void deleteById(String dummyString, String acceptContentType, String resource,
                           String system) throws Exception {
        resource = StepDefinitionHelper.getActualValue(resource);
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            acceptContentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", acceptContentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext
                    .getPrintableContextObject(String.valueOf(Thread.currentThread().getId()))));
            scenario.attach(object.toString(4)
                    , "application/json", "RequestData:");
            response = request.baseUri(ApiHelper.getHostName(resource, system)).when()
                    .log().all().accept(acceptContentType)
                    .delete(ApiHelper.getActualResource(resource, system));
        }
    }

    /**
     * Before.
     *
     * @param scenario the scenario
     */
    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
        LOGGER.info("scenario ID:: " + scenario.getId());
        this.acceptContentType = null;
        this.skipScenario = false;
    }

    /**
     * Verify status code
     *
     * @param statusCode the status code
     */
    @Then("^verify the status code is (\\d+)")
    @Then("^the status code is (\\d+)")
    public void verifyStatusCode(int statusCode) {
        if (!this.skipScenario) {
            ScenarioContext
                    .setContext(String.valueOf(Thread.currentThread().getId()), "STATUS_CODE", String.valueOf(response.getStatusCode()));
            if (response.getStatusCode() != statusCode) {
                scenario.log(response.asPrettyString());
            }
            validatableResponse = response.then().log().ifValidationFails().statusCode(statusCode);
            LOGGER.info(ScenarioContext
                    .getContext(String.valueOf(Thread.currentThread().getId())).toString());
            LOGGER.info(validatableResponse.extract().body().asString());
            scenario.attach(
                    new JSONObject(ScenarioContext.getPrintableContextObject(
                            String.valueOf(Thread.currentThread().getId()))).toString(4), "application/json",
                    "Contextual-Dataset:");
            attachResponse(validatableResponse);
        }
    }

    @Given("^verify (.*) schema validation for resource on (.*)")
    public void assertToSchema(String dummy, String resource, DataTable schemaMap) {
        schemaMap.asMap(String.class, String.class).forEach((k, v) -> {
            String body = HelperApiUtil.readFileAsString(k);
            scenario.attach(
                    new JSONObject(body).toString(4), "application/json", "Schema:");
            if (response != null && response.body().asString() != null) {
                response.then().assertThat()
                        .body(matchesJsonSchemaInClasspath(k).using(
                                settings().with().checkedValidation(Boolean.valueOf(v))));
            } else {
                assertTrue("Empty Response returned", false);
            }
        });
    }


    private void attachResponse(ValidatableResponse validatableResponse) {
        if (validatableResponse != null && validatableResponse.extract().body() != null) {
            attachResponse(validatableResponse.extract().body().asString(), "Actual-Response:");
        }
    }

    private void attachActualResponse(String actual) {
        attachResponse(actual, "Expected-Response:");
    }

    private void attachResponse(String actual, String category) {
        try {
            String xmlType =
                    response.getContentType().contains("xml") ? "text/xml" : response.getContentType();
            if (io.restassured.http.ContentType.JSON.matches(response.getContentType())) {
                if (StepDefinitionHelper.getJSON(actual) instanceof JSONArray) {
                    scenario
                            .attach(new JSONArray(actual).toString(4), xmlType, category);
                } else if (StepDefinitionHelper.getJSON(actual) instanceof JSONObject) {
                    scenario
                            .attach(new JSONObject(actual).toString(4), xmlType, category);
                } else {
                    scenario
                            .attach(actual, "text/plain", category);
                }
            } else {
                scenario
                        .attach(actual, xmlType, category);
            }
        } catch (Exception e) {
            scenario.attach(actual, "text/plain", category);
        }
    }

    /**
     * Verify response.
     *
     * @param type     the data
     * @param resource the resource
     * @param readData the data
     * @throws Throwable the throwable
     */
    @And("^verify-standard (.*) all inline (.*) api includes following in the response$")
    public void verifyFormatedMapson(String type, String resource, List<String> readData)
            throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            StandardProcessing processing = stdProcessorMap.get(type);
            if (processing != null) {
                if (validatableResponse != null
                        && validatableResponse.extract().body().asString() != null) {
                    String readXML = readData.stream().map(Object::toString)
                            .collect(Collectors.joining());
                    String jsonRequestActual = processing
                            .postResponseProcessing(validatableResponse.extract().body().asString());
                    String jsonRequestExpected = processing.postResponseProcessing(readXML);

                    if (jsonRequestExpected != null && jsonRequestActual != null) {
                        Map<String, String> mapson = Mapson.buildMAPsonFromJson(jsonRequestExpected);
                        Map<String, String> mapsonActual = Mapson.buildMAPsonFromJson(jsonRequestActual);
                        if (areEqualKeyValues(resource, mapsonActual, mapson, true)) {
                            Assert.assertTrue("Comparison success", true);
                        } else {
                            Assert.assertTrue("Comparison failed refer Comparison Failure", false);
                        }

                    } else {
                        assertTrue("Standard " + type + " has no response validation ", false);
                    }
                } else {
                    assertTrue("Api Response was not received ", false);
                }
            } else {
                assertTrue("Standard " + type + " is not implemented for response ", false);
            }
        }
    }

    /**
     * Verify response.
     *
     * @param dummy    the desc
     * @param type     the data
     * @param resource the resource
     * @param csvson the csvson
     * @throws Throwable the throwable
     */
    @Given("verify (.*) for api-aggregated-std-type (.*) on (.*)$")
    public void verifyAggregatorResponse(String dummy, String type, String resource, List<String> csvson)
            throws Throwable {
        if (!this.skipScenario) {
            StandardProcessing processing = stdProcessorMap.get(type);
            if (processing != null && processing.responseEvaluator() != null) {
                Object aggregatedObject = processing.responseEvaluator();
                JSONArray expectedArray = io.virtualan.csvson.Csvson.buildCSVson(csvson, ScenarioContext
                        .getContext(String.valueOf(Thread.currentThread().getId())));

                scenario.attach(expectedArray.toString(4), "application/json",
                        "ExpectedResponse:");
                if (aggregatedObject instanceof JSONObject) {
                    scenario.attach(((JSONObject) aggregatedObject).toString(4), "application/json",
                            "ActualResponse:");
                    org.skyscreamer.jsonassert.JSONAssert
                            .assertEquals(expectedArray.getJSONObject(0), (JSONObject) aggregatedObject,
                                    JSONCompareMode.LENIENT);
                } else {
                    scenario.attach(((JSONArray) expectedArray).toString(4), "application/json",
                            "ActualResponse:");
                    org.skyscreamer.jsonassert.JSONAssert.assertEquals(expectedArray, (JSONArray) aggregatedObject, JSONCompareMode.LENIENT);
                }

            } else {
                assertTrue("Aggregated Standard " + type + " is not implemented for response ", false);
            }
        }
    }


    /**
     * Verify response.
     *
     * @param type     the data
     * @param file     the data
     * @param resource the resource
     * @throws Throwable the throwable
     */
    @Given("^verify-standard (.*) all (.*) file (.*) api includes following in the response$")
    public void verifyFormatedMapson(String type, String file, String resource)
            throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            StandardProcessing processing = stdProcessorMap.get(type);
            if (processing != null) {
                if (validatableResponse != null
                        && validatableResponse.extract().body().asString() != null) {
                    String body = HelperApiUtil.readFileAsString(file);
                    String jsonRequestActual = processing
                            .postResponseProcessing(validatableResponse.extract().body().asString());
                    String jsonRequestExpected = processing.postResponseProcessing(body);
                    if (jsonRequestExpected != null && jsonRequestActual != null) {
                        Map<String, String> mapson = Mapson.buildMAPsonFromJson(jsonRequestExpected);
                        Map<String, String> mapsonExpected = Mapson.buildMAPsonFromJson(jsonRequestActual);
                        if (areEqualKeyValues(resource, mapson, mapsonExpected, true)) {
                            Assert.assertTrue("Comparison success", true);
                        } else {
                            Assert.assertTrue("Comparison failed refer Comparison Failure", false);
                        }
                    } else {
                        assertTrue("Standard " + type + " has no response validation ", false);
                    }
                } else {
                    assertTrue("Api Response was not received ", false);
                }
            } else {
                assertTrue("Standard " + type + " is not implemented for response ", false);
            }
        }
    }

    /**
     * Verify response.
     *
     * @param resource the resource
     * @param data     the data
     * @throws Throwable the throwable
     */
    @And("^verify-all (.*) api includes following in the response$")
    public void verifyResponseMapson(String resource, DataTable data) throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            Map<String, String> mapson = Mapson.buildMAPsonFromJson(
                    validatableResponse.extract().body().asString());
            if (areEqualKeyValues(resource,
                    data.asMap(String.class, String.class), mapson, false)) {
                Assert.assertTrue("Comparison success", true);
            } else {
                Assert.assertTrue("Comparison failed refer Comparison Failure", false);
            }
        }
    }

    private boolean areEqualKeyValues(
            String resource, Map<String, String> first,
            Map<String, String> second, boolean isActual) {
        String actual = isActual ? "expected" : "actual";
        String expected = isActual ? "actual" : "expected";
        Map<String, JSONObject> result = first.entrySet().stream()
                .filter(y -> (!ExcludeConfiguration.shouldSkip(resource, y.getKey())))
                .filter(e ->
                        !((e.getValue() != null && second.get(e.getKey()) != null && StepDefinitionHelper.getActualValue(e.getValue().trim())
                                .equals(second.get(e.getKey()).trim()))
                                || (e.getValue() == null && (e.getValue() == second.get(e.getKey()) || ""
                                .equals(second.get(e.getKey()))))))
                .collect(Collectors.toMap(e -> e.getKey(),
                        e -> {
                            JSONObject object = new JSONObject();
                            object.put(
                                    expected, e.getValue() != null ? StepDefinitionHelper
                                            .getActualValue(e.getValue().trim()) : null);
                            object.put(actual, second
                                    .get(e.getKey()));
                            return object;
                        }));
        if (!result.isEmpty()) {
            List<JSONObject> values = result.values().stream()
                    .filter(x -> !(x.optString("expected").replace("\\r", "\r")
                            .equals(x.optString("actual")))).collect(Collectors.toList());
            if (!values.isEmpty()) {
                JSONArray array = new JSONArray();
                result.entrySet().forEach(x -> {
                    JSONObject object = new JSONObject();
                    object.put(x.getKey(), x.getValue());
                    array.put(object);
                });
                scenario.attach(array.toString(4), "application/json", "Comparison Failure:");

            } else {
                return values.isEmpty();
            }
        }
        return result.isEmpty();
    }


    /**
     * Mock single response.
     *
     * @param resource  the resource
     * @param xmlString the xml string
     * @throws Throwable the throwable
     */
    @And("^verify (.*) response inline includes in the response$")
    public void verifyFileResponse(String resource, List<String> xmlString) throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            String listString = xmlString.stream().map(Object::toString)
                    .collect(Collectors.joining());
            HelperApiUtil.assertXMLEquals(listString, response.asString());
        }
    }

    /**
     * Mock single response.
     *
     * @param resource the resource
     * @param fileBody the file body
     * @throws Throwable the throwable
     */
    @And("^verify (.*) response XML File (.*) includes in the response$")
    public void verifyXMLResponse(String resource, String fileBody)
            throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            String body = HelperApiUtil.readFileAsString(fileBody);
            if (body != null) {
                HelperApiUtil.assertXMLEquals(body, response.asString());
            } else {
                Assert.assertTrue(fileBody + "  file is missing :", false);
            }
        }
    }


    /**
     * Mock single response.
     *
     * @param resource    the resource
     * @param contentType the content type
     * @param fileBody    the file body
     * @param xpaths      the xpaths
     * @throws Exception the exception
     */
    @And("^verify (.*) response (.*) include byPath (.*) includes in the response$")
    public void verifyXMLByPathResponse(String resource, String contentType,
                                        String fileBody, List<String> xpaths) throws Exception {
        if (!this.skipScenario) {
            String body = HelperApiUtil.readFileAsString(fileBody);
            attachActualResponse(body);
            attachResponse(validatableResponse);
            if (body != null) {
                if (contentType.contains("xml")) {
                    HelperApiUtil.assertXpathsEqual(xpaths, body, response.asString());
                } else {
                    HelperApiUtil.assertJsonpathEqual(xpaths, body, response.asString());
                }
            } else {
                Assert.assertTrue(fileBody + "  file is missing :", false);
            }
        }
    }

    /**
     * Mock single response.
     *
     * @param resource the resource
     * @param context  the context
     */
    @And("^verify (.*) response with (.*) includes in the response$")
    public void verifySingleResponse(String resource, String context) {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            String output =
                    validatableResponse.extract().body().asString() != null ? validatableResponse.extract()
                            .body().asString().trim() : null;
            assertEquals(context.trim(), output.trim());
        }
    }

    /**
     * Verify.
     *
     * @param path   the path
     * @param csvson the csvson
     * @throws Exception the exception
     */
    @And("^verify (.*) response csvson includes in the response$")
    public void verify(String path, List<String> csvson)
            throws Exception {
        HelperApiUtil
                .verifyCSVSON(validatableResponse, path, csvson, JSONCompareMode.LENIENT, scenario);
    }

    /**
     * Verify exact order match.
     *
     * @param path   the path
     * @param csvson the csvson
     * @throws Exception the exception
     */
    @And("^verify (.*) response csvson includes exact-order-match in the response$")
    public void verifyExactOrderMatch(String path, List<String> csvson)
            throws Exception {
        HelperApiUtil
                .verifyCSVSON(validatableResponse, path, csvson, JSONCompareMode.STRICT_ORDER, scenario);
    }

    /**
     * Verify exact match.
     *
     * @param path   the path
     * @param csvson the csvson
     * @throws Exception the exception
     */
    @And("^verify (.*) response csvson includes exact-match in the response$")
    public void verifyExactMatch(String path, List<String> csvson) throws Exception {
        HelperApiUtil
                .verifyCSVSON(validatableResponse, path, csvson, JSONCompareMode.STRICT, scenario);
    }


    /**
     * Verify response.
     *
     * @param dummyString the dummy string
     * @param data        the data
     * @throws Throwable the throwable
     */
    @And("^verify (.*) includes following in the response$")
    public void verifyResponse(String dummyString, DataTable data) throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            data.asMap(String.class, String.class).forEach((k, v) -> {
                LOGGER
                        .info(
                                v + " : " + validatableResponse.extract().body().jsonPath().getString(StepDefinitionHelper.getActualValue((String) k)));
                assertEquals(StepDefinitionHelper.getActualValue((String) v),
                        validatableResponse.extract().body().jsonPath().getString(StepDefinitionHelper.getActualValue((String) k)));
            });
        }
    }
}

