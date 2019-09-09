package io.virtualan.test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;

import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

public class BaseStepDefinition extends PetApiTest {

	private final static Logger LOGGER = Logger.getLogger(BaseStepDefinition.class.getName());

	private Properties resourceEndPointProps = new Properties();
	private ScenarioContext scenarioContext = new ScenarioContext();

	private Response response;
	private ValidatableResponse json;
	private String jsonBody;
	private int port = 80;
	private RequestSpecification request = given().port(port);

	private static final MustacheFactory mf = new DefaultMustacheFactory();

	public static MustacheFactory getMustacheFactory() {
		return mf;
	}

	@Before
	public void setUp() throws Exception {
		try {
			resourceEndPointProps.load(this.getClass().getClassLoader().getResourceAsStream("endpoints.properties"));
			port = Integer.parseInt(resourceEndPointProps.getProperty("PORT", "80"));
			request = request.port(port);
		} catch (Exception e) {
			LOGGER.warning(e.getMessage());
		}
	}

	@Given("^Provided all the feature level parameters and endpoints$")
	public void loadGlobalParam(Map<String, String> globalParams) throws IOException {
		scenarioContext.setContext(globalParams);
	}

	@Then("^Verify all the feature level parameters are present")
	public void validateGlobalParam() {
		assertTrue("Valid Global Parameters are present ", scenarioContext.hasContextValues());
	}

	@Given("^Store the (.*) value of the key as (.*)")
	public void loadAsGlobalParam(String responseKey, String key) {
		scenarioContext.setContext(key, json.extract().body().jsonPath().getString(responseKey));
	}

	@Given("^(.*) with an (.*) of (.*)")
	public void readRequestByPathParam(String dummy, String identifier, String value) {
		request = request.pathParam(identifier, StepDefinitionHelper.getActualValue(scenarioContext, value));
	}

	@And("^(.*) with an query param (.*) of (.*)")
	public void readRequestByQueryParam(String dummy, String identifier, String value) {
		request = request.queryParam(identifier, StepDefinitionHelper.getActualValue(scenarioContext, value));
	}

	@Given("^Populate (.*) with given input$")
	public void loadRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		jsonBody = StepDefinitionHelper.buildInputRequest(scenarioContext, parameterMap);
		request = request.contentType("application/json").body(jsonBody);
	}

	@Given("^Create (.*) with given input$")
	public void createRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		jsonBody = StepDefinitionHelper.buildInputRequest(scenarioContext, parameterMap);
		request = request.contentType("application/json").body(jsonBody);
	}

	@Given("^Update (.*) with given input$")
	public void updateRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		jsonBody = StepDefinitionHelper.buildInputRequest(scenarioContext, parameterMap);
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
		json = response.then().statusCode(statusCode);
	}

	@And("^Verify (.*) includes following in the response$")
	public void verfiyResponse(String dummyString, DataTable data) throws Throwable {
		data.asMap(String.class, String.class).forEach((k, v) -> {
			System.out.println(v + " : " + json.extract().body().jsonPath().getString(k));
			assertEquals(v, json.extract().body().jsonPath().getString(k));
		});
	}
}