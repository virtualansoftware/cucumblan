package io.virtualan.cucumblan.core;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.virtualan.cucumblan.util.ScenarioContext;
import io.virtualan.cucumblan.util.StepDefinitionHelper;


public class BaseStepDefinition {

	private final static Logger LOGGER = Logger.getLogger(BaseStepDefinition.class.getName());

	private Properties resourceEndPointProps = new Properties();

	private Response response;
	private ValidatableResponse json;
	private String jsonBody;
	private int port = 8080;
	private RequestSpecification request = given().port(port);

	private static final MustacheFactory mf = new DefaultMustacheFactory();

	public static MustacheFactory getMustacheFactory() {
		return mf;
	}

	
	@Given("^(.*) with an (.*) of (.*)")
	public void readRequestByPathParam(String dummy, String identifier, String value) {
		request = request.pathParam(identifier, StepDefinitionHelper.getActualValue(value));
	}

	@Given("^(.*) without an identifier")
	public void readRequestByPathParam(String dummy) {
		request = given().port(port);
	}

	
	@Given("^(.*) with an query param (.*) of (.*)")
	public void readRequestByQueryParam(String dummy, String identifier, String value) {
		request = request.queryParam(identifier, StepDefinitionHelper.getActualValue(value));
	}

	
	
	@Given("^Provided all the feature level parameters and endpoints$")
	public void loadGlobalParam(Map<String, String> globalParams) throws IOException {
		ScenarioContext.setContext(globalParams);
	}

	@Then("^Verify all the feature level parameters are present")
	public void validateGlobalParam() {
		assertTrue("Valid Global Parameters are present ", ScenarioContext.hasContextValues());
	}

	@Given("^Store the (.*) value of the key as (.*)")
	public void loadAsGlobalParam(String responseKey, String key) {
		ScenarioContext.setContext(key, json.extract().body().jsonPath().getString(responseKey));
	}

	
	@Given("^Populate (.*) with given input$")
	public void loadRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		jsonBody = StepDefinitionHelper.buildInputRequest(parameterMap);
		request = request.contentType("application/json").body(jsonBody);
	}

	@Given("^Create (.*) with given input$")
	public void createRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		jsonBody = StepDefinitionHelper.buildInputRequest(parameterMap);
		request = request.contentType("application/json").body(jsonBody);
	}

	@Given("^Update (.*) with given input$")
	public void updateRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		jsonBody = StepDefinitionHelper.buildInputRequest( parameterMap);
		request = request.contentType("application/json").body(jsonBody);
	}

	@When("^(.+) CREATE (.*) in (.*) resource")
	public void createRequest(String dummyString, String dummyEnding, String resource) {
		response = request.when().accept("application/json")
				.post(StepDefinitionHelper.getActualResource(resourceEndPointProps, resource));
	}

	@When("^(.*) READ (.*) in (.*) resource")
	public void readRequest(String dummyString, String dummyEnding, String resource) {
		response = request.when().accept("application/json")
				.get(StepDefinitionHelper.getActualResource(resourceEndPointProps, resource));
	}

	@When("^(.*) UPDATE (.*) in (.*) resource")
	public void modifyRequest(String dummyString, String dummyEnding, String resource) {
		response = request.when().accept("application/json")
				.put(StepDefinitionHelper.getActualResource(resourceEndPointProps, resource));
	}

	@When("^(.*) DELETE (.*) in (.*) resource")
	public void deleteById(String dummyString, String dummyEnding, String resource) {
		response = request.when().accept("application/json")
				.delete(StepDefinitionHelper.getActualResource(resourceEndPointProps, resource));
	}

	@Then("^Verify the status code is (\\d+)")
	public void verifyStatusCode(int statusCode) {
		json = response.then().log().ifValidationFails().statusCode(statusCode);
		LOGGER.info(ScenarioContext.getContext().toString());
	}

	@And("^Verify (.*) includes following in the response$")
	public void verfiyResponse(String dummyString, DataTable data) throws Throwable {
		data.asMap(String.class, String.class).forEach((k, v) -> {
			System.out.println(v + " : " + json.extract().body().jsonPath().getString((String) k));
			assertEquals(v, json.extract().body().jsonPath().getString((String) k));
		});
	}