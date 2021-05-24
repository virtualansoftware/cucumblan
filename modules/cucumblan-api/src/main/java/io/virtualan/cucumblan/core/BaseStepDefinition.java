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

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import io.virtualan.csvson.Csvson;
import io.virtualan.cucumblan.exception.ParserError;
import io.virtualan.cucumblan.parser.OpenAPIParser;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.EndpointConfiguration;
import io.virtualan.cucumblan.props.ExcludeConfiguration;
import io.virtualan.cucumblan.props.util.ApiHelper;
import io.virtualan.cucumblan.props.util.HelperUtil;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import io.virtualan.cucumblan.script.ExcelAndMathHelper;
import io.virtualan.cucumblan.standard.StandardProcessing;
import io.virtualan.mapson.Mapson;
import io.virtualan.util.Helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.xmlbeans.impl.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;


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
    private int sequence;
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
            byte[] authBasic = Base64.encode(String
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
                    .format("%s %s", auth, Helper.getActualValueForAll(token, ScenarioContext.getContext())));
        }
    }

    /**
     * Read request by path param.
     *
     * @param dummy the dummy
     */
    @Given("^(.*) perform a api action")
    public void readRequestByPathParam(String dummy) {
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
                    acceptContentType = StepDefinitionHelper.getActualValue(params.getValue()).toString();
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
                        StepDefinitionHelper.getActualValue(params.getValue()).toString()).build());
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
    @Given("^Provided all the feature level parameters$")
    public void loadGlobalParam(Map<String, String> globalParams) throws IOException {
        if (!this.skipScenario) {
            ScenarioContext.setContext(globalParams);
            scenario.attach(new JSONObject(ScenarioContext.getPrintableContextObject()).toString(),
                    "application/json", "requestData :  " + scenario.getName() + " : " + (sequence++));
        }
    }

    /**
     * Load global param.
     *
     * @throws IOException the io exception
     */
    @Given("^Provided all the feature level parameters from file$")
    public void loadGlobalParamFromFile() throws IOException {
        if (!this.skipScenario) {
            Properties properties = new Properties();
            InputStream stream = ApplicationConfiguration.class.getClassLoader()
                    .getResourceAsStream("cucumblan-env.properties");
            if (stream != null) {
                properties.load(stream);
                ScenarioContext.setContext((Map) properties);
            } else {
                LOGGER.warning(
                        "cucumblan-env.properties is not configured. Need to add if default data loaded");
            }
        }
    }

    /**
     * Validate global param.
     */
    @Then("^Verify all the feature level parameters exists")
    public void validateGlobalParam() {
        if (!this.skipScenario) {
            assertTrue("Valid Global Parameters are present ", ScenarioContext.hasContextValues());
        }
    }

    /**
     * Add variable.
     *
     * @param responseValue the response value
     * @param key           the key
     */
    @Given("^Add the (.*) value of the key as (.*)")
    public void addVariable(String responseValue, String key) {
        if (!this.skipScenario) {
            if (responseValue.startsWith("[") && responseValue.endsWith("]")) {
                ScenarioContext.setContext(key,
                        Helper.getActualValueForAll(responseValue, ScenarioContext.getContext()).toString());
            } else {
                ScenarioContext.setContext(key, responseValue);

            }
        }
    }

    /**
     * Modify variable.
     *
     * @param responseValue the response value
     * @param key           the key
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) decimal value of the key as (.*)")
    public void modifyDecimalVariable(String responseValue, String key) throws IOException {
        if (!this.skipScenario) {
            ScenarioContext.setContext(key, ExcelAndMathHelper.evaluateWithVariables(Double.class,
                    responseValue, ScenarioContext.getContext()).toString());
        }
    }


    /**
     * Modify variable.
     *
     * @param responseValue the response value
     * @param key           the key
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) integer value of the key as (.*)")
    public void modifyIntVariable(String responseValue, String key) throws IOException {
        if (!this.skipScenario) {
            ScenarioContext.setContext(key, ExcelAndMathHelper.evaluateWithVariables(Integer.class,
                    responseValue, ScenarioContext.getContext()).toString());
        }
    }

    /**
     * perform the skip scenario
     *
     * @param condition the response value excel based
     * @throws IOException the io exception
     */
    @Given("^perform the (.*) condition to skip scenario")
    public void modifyBooleanVariable(String condition) throws IOException {
        skipScenario = (Boolean) ExcelAndMathHelper
                .evaluateWithVariables(Boolean.class, condition, ScenarioContext.getContext());
        scenario.log("condition :" + condition + " : is Skipped : " + skipScenario);
    }

    /**
     * perform the success met
     *
     * @param condition the response value excel based
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) condition success$")
    public void evaluateVariable(String condition) throws IOException {
        if (!this.skipScenario) {
            boolean flag = (Boolean) ExcelAndMathHelper
                .evaluateWithVariables(Boolean.class, condition, ScenarioContext.getContext());
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
    @Given("^evaluate the (.*) condition fails$")
    public void evaluateVariableFail(String condition) throws IOException {
        if (!this.skipScenario) {
            boolean flag = (Boolean) ExcelAndMathHelper
                .evaluateWithVariables(Boolean.class, condition, ScenarioContext.getContext());
            scenario.log("Failure condition :" + condition + " >>> status " + flag);
            assertTrue("Valid Failure" + condition + " is met ", flag);
        }
    }

    /**
     * Modify variable.
     *
     * @param responseValue the response value
     * @param key           the key
     * @throws IOException the io exception
     */
    @Given("^evaluate the (.*) boolean value of the key as (.*)")
    public void modifyBooleanVariable(String responseValue, String key) throws IOException {
        if (!this.skipScenario) {
            ScenarioContext.setContext(key, ExcelAndMathHelper.evaluateWithVariables(Boolean.class,
                    responseValue, ScenarioContext.getContext()).toString());
        }
    }


    /**
     * Modify variable.
     *
     * @param responseValue the response value
     * @param key           the key
     * @throws IOException the io exception
     */
    @Given("^Modify the (.*) value of the key as (.*)")
    public void modifyStringVariable(String responseValue, String key) throws IOException {
        if (!this.skipScenario) {
            ScenarioContext.setContext(key,
                    Helper.getActualValueForAll(responseValue, ScenarioContext.getContext()).toString());
        }
    }

    /**
     * Load as global param.
     *
     * @param responseKey the response key
     * @param key         the key
     */
    @Given("^Store the (.*) value of the key as (.*)")
    public void loadAsGlobalParam(String responseKey, String key) {
        if (!this.skipScenario) {
            String value = validatableResponse.extract().body().jsonPath().getString(responseKey);
            if (value != null) {
                ScenarioContext
                        .setContext(key,
                                validatableResponse.extract().body().jsonPath().getString(responseKey));
            } else if (response.getCookie(responseKey) != null) {
                ScenarioContext
                        .setContext(key, response.getCookie(responseKey));
            } else if (response.getHeader(responseKey) != null) {
                ScenarioContext
                        .setContext(key, response.getCookie(responseKey));
            } else {
                LOGGER.warning(responseKey + " :  for " + key + " not found");
                scenario.log(responseKey + " :  for " + key + " not found");
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
            request = request.contentType("application/json");
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
                if (params.getKey().contains("MULTI-PART")) {
                    if (params.getValue() != null) {
                        String fileAndType = StepDefinitionHelper.getActualValue(params.getValue()).toString();
                        if (params.getKey().split("=").length == 2 && fileAndType.split("=").length == 2) {
                            request = request
                                    .multiPart(params.getKey().split("=")[1],
                                            new File(BaseStepDefinition.class.getClassLoader()
                                                    .getResource(fileAndType.split("=")[0]).getFile()),
                                            fileAndType.split("=")[1]);
                        } else {
                            scenario.log(
                                    "MULTI-PART was not set up correctly. should be like key => MULTI-PART => MULTI-PART=uploadtext.txt  value => filename.txt=plain/txt");
                            LOGGER.warning(
                                    "MULTI-PART was not set up correctly. should be like key => MULTI-PART => MULTI-PART=uploadtext.txt  value => filename.txt=plain/txt");
                        }
                    }
                } else {
                    request = request
                            .param(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
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
            request = request.contentType("application/json");
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
    @Given("^Populate (.*) with contentType(.*) given input$")
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
    @Given("^Populate (.*) with given input$")
    public void loadRequest(String nameIgnore, Map<String, String> parameterMap) {
        if (!this.skipScenario) {
            request = request.contentType("application/json");
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
            String body = HelperUtil.readFileAsString(fileBody);
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
                        .collect(Collectors.joining());
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
    @Given("^Create (.*) with contentType (.*) given input$")
    public void createRequest(String nameIgnore, String contentType, Map<String, String> parameterMap)
            throws Exception {
        if (!this.skipScenario) {
            jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
            scenario.attach(jsonBody
                    , contentType, "requestData :  " + scenario.getName() + " : " + (sequence++));
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
    @Given("^Create (.*) with given input$")
    public void createRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        if (!this.skipScenario) {
            jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
            scenario.attach(jsonBody
                    , "application/json", "requestData :  " + scenario.getName() + " : " + (sequence++));
            request = request.contentType("application/json").body(jsonBody);
        }
    }

    /**
     * Update request.
     *
     * @param nameIgnore   the name ignore
     * @param parameterMap the parameter map
     * @throws Exception the exception
     */
    @Given("^Update (.*) with given input$")
    public void updateRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        if (!this.skipScenario) {
            jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
            scenario.attach(jsonBody
                    , "application/json", "requestData :  " + scenario.getName() + " : " + (sequence++));
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
    @Given("^Update (.*) with contentType (.*) given input$")
    public void updateRequest(String nameIgnore, String contentType, Map<String, String> parameterMap)
            throws Exception {
        if (!this.skipScenario) {
            jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
            scenario.attach(jsonBody
                    , "application/json", "requestData :  " + scenario.getName() + " : " + (sequence++));
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
                              String system) {
        resource = StepDefinitionHelper.getActualValue(resource).toString();
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            acceptContentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", acceptContentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext.getPrintableContextObject()));

            scenario.attach(object.toString()
                    , "application/json", "requestData :  " + scenario.getName() + " : " + (sequence++));

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
                            String system) {
        resource = StepDefinitionHelper.getActualValue(resource).toString();
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            String contentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", contentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext.getPrintableContextObject()));

            scenario.attach(object.toString()
                    , "application/json", "requestData :  " + scenario.getName() + " : " + (sequence++));
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
                              String system) {
        resource = StepDefinitionHelper.getActualValue(resource).toString();
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            acceptContentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", acceptContentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext.getPrintableContextObject()));

            scenario.attach(object.toString()
                    , "application/json", "requestData :  " + scenario.getName() + " : " + (sequence++));
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
                             String system) {
        resource = StepDefinitionHelper.getActualValue(resource).toString();
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            acceptContentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", acceptContentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext.getPrintableContextObject()));

            scenario.attach(object.toString()
                    , "application/json", "requestData :  " + scenario.getName() + " : " + (sequence++));
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
                           String system) {
        resource = StepDefinitionHelper.getActualValue(resource).toString();
        if (!this.skipScenario) {
            String url = ApiHelper.getHostName(resource, system);
            acceptContentType =
                    this.acceptContentType != null ? this.acceptContentType : acceptContentType;
            String resourceDetails = ApiHelper.getActualResource(resource, system);
            JSONObject object = new JSONObject();
            object.put("url", url);
            object.put("AcceptContentType", acceptContentType);
            object.put("resource", resourceDetails);
            object.put("context", new JSONObject(ScenarioContext.getPrintableContextObject()));
            scenario.attach(object.toString()
                    , "application/json", "requestData :  " + scenario.getName() + " : " + (sequence++));
            response = request.baseUri(ApiHelper.getHostName(resource, system)).when()
                    .log().all().accept(acceptContentType)
                    .delete(ApiHelper.getActualResource(resource, system));
        }
    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
        this.sequence = 1;
        this.acceptContentType = null;
        this.skipScenario = false;
    }

    /**
     * Verify status code
     *
     * @param statusCode the status code
     */
    @Then("^Verify the status code is (\\d+)")
    public void verifyStatusCode(int statusCode) {
        if (!this.skipScenario) {
            ScenarioContext.setContext("STATUS_CODE", String.valueOf(response.getStatusCode()));
            validatableResponse = response.then().log().ifValidationFails().statusCode(statusCode);
            LOGGER.info(ScenarioContext.getContext().toString());
            LOGGER.info(validatableResponse.extract().body().asString());
            scenario.attach(ScenarioContext.getPrintableContextObject().toString(), "text/plain",
                    "PreDefinedDataSet :  " + scenario.getName() + " : " + (sequence++));
        }
    }

    private void attachResponse(ValidatableResponse validatableResponse) {
        if (validatableResponse != null && validatableResponse.extract().body() != null) {
            String xmlType =
                    response.getContentType().contains("xml") ? "text/xml" : response.getContentType();
            scenario.attach(validatableResponse.extract().body().asString(), xmlType,
                    "actual-response " + scenario.getName() + " : " + (sequence++));
        }
    }

    private void attachActualResponse(String actual) {
        String xmlType =
                response.getContentType().contains("xml") ? "text/xml" : response.getContentType();
        scenario
                .attach(actual, xmlType, "expected-response " + scenario.getName() + " : " + (sequence++));
    }

    /**
     * Verify response.
     *
     * @param resource the resource
     * @param type     the data
     * @param readData the data
     * @throws Throwable the throwable
     */
    @And("^Verify-standard (.*) all inline (.*) api includes following in the response$")
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
                        if(areEqualKeyValues(resource, mapsonActual, mapson, true)){
                            Assert.assertTrue("Comparison success", true );
                        } else {
                            Assert.assertTrue("Comparison failed refer Comparison Failure", false );
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
     * @param type     the data
     * @param file     the data
     * @param resource the resource
     * @throws Throwable the throwable
     */
    @Given("^Verify-standard (.*) all (.*) file (.*) api includes following in the response$")
    public void verifyFormatedMapson(String type, String file, String resource)
            throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            StandardProcessing processing = stdProcessorMap.get(type);
            if (processing != null) {
                if (validatableResponse != null
                        && validatableResponse.extract().body().asString() != null) {
                    String body = HelperUtil.readFileAsString(file);
                    String jsonRequestActual = processing
                            .postResponseProcessing(validatableResponse.extract().body().asString());
                    String jsonRequestExpected = processing.postResponseProcessing(body);
                    if (jsonRequestExpected != null && jsonRequestActual != null) {
                        Map<String, String> mapson = Mapson.buildMAPsonFromJson(jsonRequestExpected);
                        Map<String, String> mapsonExpected = Mapson.buildMAPsonFromJson(jsonRequestActual);
                        if(areEqualKeyValues(resource, mapson, mapsonExpected, true)){
                            Assert.assertTrue("Comparison success", true );
                        } else {
                            Assert.assertTrue("Comparison failed refer Comparison Failure", false );
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
    @And("^Verify-all (.*) api includes following in the response$")
    public void verifyResponseMapson(String resource, DataTable data) throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            Map<String, String> mapson = Mapson.buildMAPsonFromJson(
                    validatableResponse.extract().body().asString());
            if(areEqualKeyValues(resource,
                data.asMap(String.class, String.class), mapson, false)) {
                Assert.assertTrue("Comparison success", true );
            } else {
                Assert.assertTrue("Comparison failed refer Comparison Failure", false );
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
        if (!result.isEmpty() ) {
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
                scenario.attach(array.toString(4), "application/json", "Comparison Failure");

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
    @And("^Verify (.*) response inline includes in the response$")
    public void verifyFileResponse(String resource, List<String> xmlString) throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            String listString = xmlString.stream().map(Object::toString)
                    .collect(Collectors.joining());
            HelperUtil.assertXMLEquals(listString, response.asString());
        }
    }

    /**
     * Mock single response.
     *
     * @param resource the resource
     * @param fileBody the file body
     * @throws Throwable the throwable
     */
    @And("^Verify (.*) response XML File (.*) includes in the response$")
    public void verifyXMLResponse(String resource, String fileBody)
            throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            String body = HelperUtil.readFileAsString(fileBody);
            if (body != null) {
                HelperUtil.assertXMLEquals(body, response.asString());
            } else {
                Assert.assertTrue(fileBody + "  file is missing :", false);
            }
        }
    }


    /**
     * Mock single response.
     *
     * @param resource the resource
     * @param fileBody the file body
     */
    @And("^Verify (.*) response (.*) include byPath (.*) includes in the response$")
    public void verifyXMLByPathResponse(String resource, String contentType,
                                        String fileBody, List<String> xpaths) throws Exception {
        if (!this.skipScenario) {
            String body = HelperUtil.readFileAsString(fileBody);
            attachActualResponse(body);
            attachResponse(validatableResponse);
            if (body != null) {
                if (contentType.contains("xml")) {
                    HelperUtil.assertXpathsEqual(xpaths, body, response.asString());
                } else {
                    HelperUtil.assertJsonpathEqual(xpaths, body, response.asString());
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
    @And("^Verify (.*) response with (.*) includes in the response$")
    public void verifySingleResponse(String resource, String context) {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            String output =
                    validatableResponse.extract().body().asString() != null ? validatableResponse.extract()
                            .body().asString().trim() : null;
            assertEquals(context.trim(), output.trim());
        }
    }

    @And("^Verify (.*) response csvson includes in the response$")
    public void verify(String dummy, List<String> csvson)
            throws Exception {
        JSONArray expectedArray = Csvson.buildCSVson(csvson, ScenarioContext.getContext());
        Object objJson = StepDefinitionHelper.getJSON(validatableResponse.extract().body().asString());
        JSONCompareResult result = null;
        scenario.attach(expectedArray.toString(), "application/json", "Expected json");
        if(objJson instanceof JSONArray){
            JSONArray actualArray = new JSONArray(validatableResponse.extract().body().asString());
            result = JSONCompare.compareJSON(expectedArray, actualArray,  JSONCompareMode.LENIENT);
            scenario.attach(actualArray.toString(), "application/json", "Actual json");
        } else if(objJson instanceof JSONObject){
            JSONObject actualArray = new JSONObject(validatableResponse.extract().body().asString());
            result = JSONCompare.compareJSON( expectedArray.getJSONObject(0), actualArray, JSONCompareMode.LENIENT);
            scenario.attach(actualArray.toString(), "application/json", "Actual json");
        }

        if (result == null){
            Assert.assertTrue("Actual input is not a valid JSON Object", false);
        } else if (result.failed()) {
            scenario.attach(result.getMessage(), "text/plain", "Unmatched csvson");
            assertTrue("Csvson record does not match", result.passed());
        } else {
            assertTrue("Csvson record matches", result.passed());
        }
    }


    /**
     * Verify response.
     *
     * @param dummyString the dummy string
     * @param data        the data
     * @throws Throwable the throwable
     */
    @And("^Verify (.*) includes following in the response$")
    public void verifyResponse(String dummyString, DataTable data) throws Throwable {
        if (!this.skipScenario) {
            attachResponse(validatableResponse);
            data.asMap(String.class, String.class).forEach((k, v) -> {
                LOGGER
                        .info(
                                v + " : " + validatableResponse.extract().body().jsonPath().getString((String) k));
                assertEquals(StepDefinitionHelper.getActualValue((String) v),
                        validatableResponse.extract().body().jsonPath().getString((String) k));
            });
        }
    }
}

