package io.virtualan.cucumblan.core;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.virtualan.cucumblan.exception.ParserError;
import io.virtualan.cucumblan.parser.OpenAPIParser;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.EndpointConfiguration;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import io.virtualan.mapson.Mapson;
import java.io.IOException;
import java.util.Map;import
java.util.logging.Logger;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.virtualan.cucumblan.props.util.ScenarioContext;


public class BaseStepDefinition {

	private final static Logger LOGGER = Logger.getLogger(BaseStepDefinition.class.getName());


	private Response response;
	private ValidatableResponse json;
	private String jsonBody;
	private RequestSpecification request = given();
	static  {
		try {
			OpenAPIParser.loader();
			EndpointConfiguration.getInstance().loadEndpoints();
		} catch (ParserError parserError) {
			LOGGER.warning("Unable to start the process - see if conf folder and endpoints are generated");
			System.exit(1);
		}
	}

	@Given("^(.*) with an path param (.*) of (.*)")
	public void readRequestByPathParam(String dummy, String identifier, String value) {
		request = request.pathParam(identifier, StepDefinitionHelper.getActualValue(value));
	}

	@Given("^(.*) without an identifier")
	public void readRequestByPathParam(String dummy) {
		request = given();
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

  @Given("^Read (.*) with given input$")
  public void readRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
    request = request.contentType("application/json");
    for(Map.Entry<String, String> params : parameterMap.entrySet()) {
      request = request.queryParam(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
    }
  }

  @Given("^Populate (.*) with given input$")
	public void loadRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		request = request.contentType("application/json");
    for(Map.Entry<String, String> params : parameterMap.entrySet()) {
      request = request.queryParam(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
    }
	}

	@Given("^Create (.*) with given input$")
	public void createRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
		request = request.contentType("application/json").body(jsonBody);
	}

	@Given("^Update (.*) with given input$")
	public void updateRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
		request = request.contentType("application/json").body(jsonBody);
	}

	@When("^(.+) post accept (.*) in (.*) resource on (.*)")
	public void createRequest(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.post(StepDefinitionHelper.getActualResource(resource, system));
	}

	@When("^(.*) get (.*) in (.*) resource on (.*)")
	public void readRequest(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.get(StepDefinitionHelper.getActualResource(resource, system));
	}

	@When("^(.*) update (.*) in (.*) resource on (.*)")
	public void modifyRequest(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.put(StepDefinitionHelper.getActualResource( resource, system));
	}

	@When("^(.*) patch (.*) in (.*) resource on (.*)")
	public void pathchRequest(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.patch(StepDefinitionHelper.getActualResource( resource, system));
	}

	@When("^(.*) delete (.*) in (.*) resource on (.*)")
	public void deleteById(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.delete(StepDefinitionHelper.getActualResource(resource, system));
	}



	@Then("^Verify the status code is (\\d+)")
	public void verifyStatusCode(int statusCode) {
		json = response.then().log().ifValidationFails().statusCode(statusCode);
		LOGGER.info(ScenarioContext.getContext().toString());
	}

	@And("^Verify (.*) includes following in the response$")
	public void verifyResponse(String dummyString, DataTable data) throws Throwable {
		data.asMap(String.class, String.class).forEach((k, v) -> {
			System.out.println(v + " : " + json.extract().body().jsonPath().getString((String) k));
			assertEquals(v, json.extract().body().jsonPath().getString((String) k));
		});
	}

}