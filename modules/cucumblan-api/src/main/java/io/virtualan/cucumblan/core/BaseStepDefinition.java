//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.virtualan.cucumblan.core;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.virtualan.cucumblan.exception.ParserError;
import io.virtualan.cucumblan.parser.OpenAPIParser;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.EndpointConfiguration;
import io.virtualan.cucumblan.props.ExcludeConfiguration;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import io.virtualan.cucumblan.script.ExcelAndMathHelper;
import io.virtualan.mapson.Mapson;
import io.virtualan.util.Helper;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.apache.xmlbeans.impl.util.Base64;
import org.junit.Assert;

public class BaseStepDefinition {
    private static final Logger LOGGER = Logger.getLogger(BaseStepDefinition.class.getName());
    private Response response;
    private ValidatableResponse json;
    private String jsonBody;
    private RequestSpecification request = RestAssured.given();

    public BaseStepDefinition() {
    }

    @Given("^(.*) with an path param (.*) of (.*)")
    public void readRequestByPathParam(String dummy, String identifier, String value) {
        this.request = RestAssured.given().pathParam(identifier, StepDefinitionHelper.getActualValue(value));
    }

    @Given("^enable cert for (.*) of (.*)")
    public void cert(String identifier, String value) {
        RestAssured.authentication = RestAssured.certificate(identifier, value);
    }

    @Given("^basic authentication with (.*) and (.*)")
    public void auth(String username, String password) {
        byte[] authBasic = Base64.encode(String.format("%s:%s", StepDefinitionHelper.getActualValue(username), StepDefinitionHelper.getActualValue(password)).getBytes());
        this.request.header("Authorization", String.format("Basic %s", new String(authBasic)), new Object[0]);
    }

    @Given("^(.*) auth with (.*) token$")
    public void bearer(String auth, String token) {
        this.request.header("Authorization", String.format("%s %s", auth, Helper.getActualValueForAll(token, ScenarioContext.getContext())), new Object[0]);
    }

    @Given("^(.*) perform a api action")
    public void readRequestByPathParam(String dummy) {
        this.request = RestAssured.given();
    }

    @Given("^(.*) with an header param (.*) of (.*)")
    public void readRequestByHeaderParam(String dummy, String identifier, String value) {
        this.request = this.request.header(identifier, StepDefinitionHelper.getActualValue(value), new Object[0]);
    }

    @Given("add (.*) with given header params$")
    public void readAllHeaderParams(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        Entry params;
        for(Iterator var3 = parameterMap.entrySet().iterator(); var3.hasNext(); this.request = this.request.header((String)params.getKey(), StepDefinitionHelper.getActualValue((String)params.getValue()), new Object[0])) {
            params = (Entry)var3.next();
        }

    }

    @Given("^(.*) with an query param (.*) of (.*)")
    public void readRequestByQueryParam(String dummy, String identifier, String value) {
        this.request = RestAssured.given().queryParam(identifier, new Object[]{StepDefinitionHelper.getActualValue(value)});
    }

    @Given("^Provided all the feature level parameters$")
    public void loadGlobalParam(Map<String, String> globalParams) throws IOException {
        ScenarioContext.setContext(globalParams);
    }

    @Given("^Provided all the feature level parameters from file$")
    public void loadGlobalParamFromFile() throws IOException {
        Properties properties = new Properties();
        properties.load(ApplicationConfiguration.class.getClassLoader().getResourceAsStream("cucumblan-env.properties"));
        ScenarioContext.setContext(properties);
    }

    @Then("^Verify all the feature level parameters exists")
    public void validateGlobalParam() {
        Assert.assertTrue("Valid Global Parameters are present ", ScenarioContext.hasContextValues());
    }

    @Given("^Add the (.*) value of the key as (.*)")
    public void addVariable(String responseValue, String key) {
        ScenarioContext.setContext(key, Helper.getActualValueForAll(responseValue, ScenarioContext.getContext()).toString());
    }

    @Given("^evaluate the (.*) decimal value of the key as (.*)")
    public void modifyDecimalVariable(String responseValue, String key) throws IOException {
        ScenarioContext.setContext(key, ExcelAndMathHelper.evaluateWithVariables(Double.class, responseValue, ScenarioContext.getContext()).toString());
    }

    @Given("^evaluate the (.*) integer value of the key as (.*)")
    public void modifyIntVariable(String responseValue, String key) throws IOException {
        ScenarioContext.setContext(key, ExcelAndMathHelper.evaluateWithVariables(Integer.class, responseValue, ScenarioContext.getContext()).toString());
    }

    @Given("^evaluate the (.*) boolean value of the key as (.*)")
    public void modifyBooleanVariable(String responseValue, String key) throws IOException {
        ScenarioContext.setContext(key, ExcelAndMathHelper.evaluateWithVariables(Boolean.class, responseValue, ScenarioContext.getContext()).toString());
    }

    @Given("^Modify the (.*) value of the key as (.*)")
    public void modifyStringVariable(String responseValue, String key) throws IOException {
        ScenarioContext.setContext(key, Helper.getActualValueForAll(responseValue, ScenarioContext.getContext()).toString());
    }

    @Given("^Store the (.*) value of the key as (.*)")
    public void loadAsGlobalParam(String responseKey, String key) {
        ScenarioContext.setContext(key, this.json.extract().body().jsonPath().getString(responseKey));
    }

    @Given("^add (.*) with given path params$")
    public void readPathParamsRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        this.request = this.request.contentType("application/json");

        Entry params;
        for(Iterator var3 = parameterMap.entrySet().iterator(); var3.hasNext(); this.request = this.request.pathParam((String)params.getKey(), StepDefinitionHelper.getActualValue((String)params.getValue()))) {
            params = (Entry)var3.next();
        }

    }

    @Given("add (.*) with given query params$")
    public void readRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        this.request = this.request.contentType("application/json");

        Entry params;
        for(Iterator var3 = parameterMap.entrySet().iterator(); var3.hasNext(); this.request = this.request.queryParam((String)params.getKey(), new Object[]{StepDefinitionHelper.getActualValue((String)params.getValue())})) {
            params = (Entry)var3.next();
        }

    }

    @Given("^Populate (.*) with given input$")
    public void loadRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        this.request = this.request.contentType("application/json");

        Entry params;
        for(Iterator var3 = parameterMap.entrySet().iterator(); var3.hasNext(); this.request = this.request.queryParam((String)params.getKey(), new Object[]{StepDefinitionHelper.getActualValue((String)params.getValue())})) {
            params = (Entry)var3.next();
        }

    }

    @Given("^Create (.*) with given input$")
    public void createRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        this.jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
        this.request = this.request.contentType("application/json").body(this.jsonBody);
    }

    @Given("^Update (.*) with given input$")
    public void updateRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
        this.jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
        this.request = this.request.contentType("application/json").body(this.jsonBody);
    }

    @When("^(.*) post (.*) in (.*) resource on (.*)")
    public void createRequest(String dummyString, String acceptContentType, String resource, String system) {
        this.response = (Response)((RequestSpecification)this.request.baseUri(StepDefinitionHelper.getHostName(resource, system)).when().log().all()).accept(acceptContentType).post(StepDefinitionHelper.getActualResource(resource, system), new Object[0]);
    }

    @When("^(.*) get (.*) in (.*) resource on (.*)")
    public void readRequest(String dummyString, String acceptContentType, String resource, String system) {
        this.response = (Response)((RequestSpecification)this.request.baseUri(StepDefinitionHelper.getHostName(resource, system)).when().log().all()).accept(acceptContentType).get(StepDefinitionHelper.getActualResource(resource, system), new Object[0]);
    }

    @When("^(.*) put (.*) in (.*) resource on (.*)")
    public void modifyRequest(String dummyString, String acceptContentType, String resource, String system) {
        this.response = (Response)((RequestSpecification)this.request.baseUri(StepDefinitionHelper.getHostName(resource, system)).when().log().all()).accept(acceptContentType).put(StepDefinitionHelper.getActualResource(resource, system), new Object[0]);
    }

    @When("^(.*) patch (.*) in (.*) resource on (.*)")
    public void patchRequest(String dummyString, String acceptContentType, String resource, String system) {
        this.response = (Response)((RequestSpecification)this.request.baseUri(StepDefinitionHelper.getHostName(resource, system)).when().log().all()).accept(acceptContentType).patch(StepDefinitionHelper.getActualResource(resource, system), new Object[0]);
    }

    @When("^(.*) delete (.*) in (.*) resource on (.*)")
    public void deleteById(String dummyString, String acceptContentType, String resource, String system) {
        this.response = (Response)((RequestSpecification)this.request.baseUri(StepDefinitionHelper.getHostName(resource, system)).when().log().all()).accept(acceptContentType).delete(StepDefinitionHelper.getActualResource(resource, system), new Object[0]);
    }

    @Then("^Verify the status code is (\\d+)")
    public void verifyStatusCode(int statusCode) {
        this.json = (ValidatableResponse)((ValidatableResponse)((ValidatableResponse)this.response.then()).log().ifValidationFails()).statusCode(statusCode);
        LOGGER.info(ScenarioContext.getContext().toString());
        LOGGER.info(this.json.extract().body().asString());
    }

    @And("^Verify (.*) includes following in the response$")
    public void verifyResponse(String dummyString, DataTable data) throws Throwable {
        data.asMap(String.class, String.class).forEach((k, v) -> {
            if (!ExcludeConfiguration.shouldSkip((String)k)) {
                Map<String, String> mapson = Mapson.buildMAPsonFromJson(this.json.extract().body().asString());
                if (v == null) {
                    if (mapson.get(k) == null) {
                        Assert.assertNull(mapson.get(k));
                    } else {
                        Assert.assertEquals(" ", mapson.get(k));
                    }
                } else {
                    LOGGER.info("Key: " + k + "  Expected : " + v + " ==> Actual " + (String)mapson.get(k));
                    Assert.assertEquals(v, mapson.get(k));
                }
            }

        });
    }

    static {
        try {
            OpenAPIParser.loader();
            EndpointConfiguration.getInstance().loadEndpoints();
        } catch (ParserError var1) {
            LOGGER.warning("Unable to start the process - see if conf folder and endpoints are generated");
            System.exit(-1);
        }

    }
}
