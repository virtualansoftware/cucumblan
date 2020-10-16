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


/**
 * The type Base step definition.
 *
 * @author Elan Thangamani
 */
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

  /**
   * Read request by path param.
   *
   * @param dummy      the dummy
   * @param identifier the identifier
   * @param value      the value
   */
  @Given("^(.*) with an path param (.*) of (.*)")
	public void readRequestByPathParam(String dummy, String identifier, String value) {
		request = request.pathParam(identifier, StepDefinitionHelper.getActualValue(value));
	}

  /**
   * Read request by path param.
   *
   * @param dummy the dummy
   */
  @Given("^(.*) without an identifier")
	public void readRequestByPathParam(String dummy) {
		request = given();
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
		request = request.queryParam(identifier, StepDefinitionHelper.getActualValue(value));
	}

  /**
   * Load global param.
   *
   * @param globalParams the global params
   * @throws IOException the io exception
   */
  @Given("^Provided all the feature level parameters$")
	public void loadGlobalParam(Map<String, String> globalParams) throws IOException {
		ScenarioContext.setContext(globalParams);
	}

  /**
   * Validate global param.
   */
  @Then("^Verify all the feature level parameters exists")
	public void validateGlobalParam() {
		assertTrue("Valid Global Parameters are present ", ScenarioContext.hasContextValues());
	}


  /**
   * Add variable.
   *
   * @param responseValue the response value
   * @param key           the key
   */
  @Given("^Add the (.*) value of the key as (.*)")
	public void addVariable(String responseValue, String key) {
		ScenarioContext.setContext(key, responseValue);
	}

  /**
   * Modify variable.
   *
   * @param responseValue the response value
   * @param key           the key
   */
  @Given("^Modify the (.*) value of the key as (.*)")
	public void modifyVariable(String responseValue, String key) {
		ScenarioContext.setContext(key, responseValue);
	}

  /**
   * Load as global param.
   *
   * @param responseKey the response key
   * @param key         the key
   */
  @Given("^Store the (.*) value of the key as (.*)")
	public void loadAsGlobalParam(String responseKey, String key) {
		ScenarioContext.setContext(key, json.extract().body().jsonPath().getString(responseKey));
	}

  /**
   * Read request.
   *
   * @param nameIgnore   the name ignore
   * @param parameterMap the parameter map
   * @throws Exception the exception
   */
  @Given("^Read (.*) with given input$")
  public void readRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
    request = request.contentType("application/json");
    for(Map.Entry<String, String> params : parameterMap.entrySet()) {
      request = request.queryParam(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
    }
  }

  /**
   * Load request.
   *
   * @param nameIgnore   the name ignore
   * @param parameterMap the parameter map
   * @throws Exception the exception
   */
  @Given("^Populate (.*) with given input$")
	public void loadRequest(String nameIgnore, Map<String, String> parameterMap) throws Exception {
		request = request.contentType("application/json");
    for(Map.Entry<String, String> params : parameterMap.entrySet()) {
      request = request.queryParam(params.getKey(), StepDefinitionHelper.getActualValue(params.getValue()));
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
		jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
		request = request.contentType("application/json").body(jsonBody);
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
		jsonBody = Mapson.buildMAPsonAsJson(parameterMap, ScenarioContext.getContext());
		request = request.contentType("application/json").body(jsonBody);
	}

  /**
   * Create request.
   *
   * @param dummyString       the dummy string
   * @param acceptContentType the accept content type
   * @param resource          the resource
   * @param system            the system
   */
  @When("^(.+) post accept (.*) in (.*) resource on (.*)")
	public void createRequest(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.post(StepDefinitionHelper.getActualResource(resource, system));
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
	public void readRequest(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.get(StepDefinitionHelper.getActualResource(resource, system));
	}

  /**
   * Modify request.
   *
   * @param dummyString       the dummy string
   * @param acceptContentType the accept content type
   * @param resource          the resource
   * @param system            the system
   */
  @When("^(.*) update (.*) in (.*) resource on (.*)")
	public void modifyRequest(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.put(StepDefinitionHelper.getActualResource( resource, system));
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
	public void pathchRequest(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.patch(StepDefinitionHelper.getActualResource( resource, system));
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
	public void deleteById(String dummyString, String acceptContentType, String resource, String system) {
		response = request.baseUri(ApplicationConfiguration.getProperty("service.api."+system)).when().accept(acceptContentType)
				.delete(StepDefinitionHelper.getActualResource(resource, system));
	}


  /**
   * Verify status code.
   *
   * @param statusCode the status code
   */
  @Then("^Verify the status code is (\\d+)")
	public void verifyStatusCode(int statusCode) {
		json = response.then().log().ifValidationFails().statusCode(statusCode);
		LOGGER.info(ScenarioContext.getContext().toString());
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
		data.asMap(String.class, String.class).forEach((k, v) -> {
			System.out.println(v + " : " + json.extract().body().jsonPath().getString((String) k));
			assertEquals(StepDefinitionHelper.getActualValue(v.toString()), json.extract().body().jsonPath().getString((String) k));
		});
	}

}