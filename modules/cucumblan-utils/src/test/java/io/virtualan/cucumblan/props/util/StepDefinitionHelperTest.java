package io.virtualan.cucumblan.props.util;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StepDefinitionHelperTest {

    @Before
    public void init() {
        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
    }

    @Test
    public void replace_should_return_value_if_its_available_in_scenario_context() {
        // Arrange
        final String id = "1";
        final String key = "petId_post";
        final String value = "100";
        final String expected = "100";

        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        parentScenarioContext.put(id, params);

        // Act
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
        Object actual = StepDefinitionHelper.replace("[petId_post]");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void replace_should_return_value_for_multiple_keys() {
        // Arrange
        final String id = "1";
        final String expected = "100,11";

        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("petId_post", "100");
        params.put("petId_post_mock", "11");
        parentScenarioContext.put(id, params);

        // Act
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
        Object actual = StepDefinitionHelper.replace("[petId_post,petId_post_mock]");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void replace_should_return_actual_value_if_one_of_key_is_not_available() {
        // Arrange
        final String id = "1";
        final String expected = "100,null";

        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("petId_post", "100");
        params.put("petId_post_mock", "11");
        parentScenarioContext.put(id, params);

        // Act
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
        Object actual = StepDefinitionHelper.replace("[petId_post,STATUS_CODE]");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void replace_should_return_null_if_key_is_not_available_in_scenario_context() {
        // Arrange
        final String id = "1";
        final String key = "[petId_post]";
        final String expected = null;

        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        parentScenarioContext.put(id, params);

        // Act
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
        Object actual = StepDefinitionHelper.replace(key);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void replace_should_return_actuel_value_if_its_not_enclosed_with_brackets() {
        // Arrange
        final String key = "petId_post";
        final String expected = "petId_post";

        // Act
        Object actual = StepDefinitionHelper.replace(key);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void replace_should_return_actuel_value_if_its_enclosed_with_left_bracket() {
        // Arrange
        final String key = "[petId_post";
        final String expected = "[petId_post";

        // Act
        Object actual = StepDefinitionHelper.replace(key);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getActualValue_should_return_value_if_its_available_in_scenario_context() {
        // Arrange
        final String id = "1";
        final String key = "petId_post";
        final String value = "100";
        final String expected = "100";

        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        parentScenarioContext.put(id, params);

        // Act
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
        String actual = StepDefinitionHelper.getActualValue("[petId_post]");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getActualValue_should_return_value_for_multiple_keys() {
        // Arrange
        final String id = "1";
        final String expected = "100,11";

        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("petId_post", "100");
        params.put("petId_post_mock", "11");
        parentScenarioContext.put(id, params);

        // Act
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
        String actual = StepDefinitionHelper.getActualValue("[petId_post,petId_post_mock]");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getActualValue_should_return_actual_value_if_one_of_key_is_not_available() {
        // Arrange
        final String id = "1";
        final String expected = "[petId_post,STATUS_CODE]";

        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put("petId_post", "100");
        params.put("petId_post_mock", "11");
        parentScenarioContext.put(id, params);

        // Act
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
        String actual = StepDefinitionHelper.getActualValue("[petId_post,STATUS_CODE]");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getActualValue_should_return_actual_value_if_its_not_available_in_scenario_context() {
        // Arrange
        final String id = "1";
        final String key = "[petId_post]";
        final String expected = "[petId_post]";

        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        parentScenarioContext.put(id, params);

        // Act
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
        String actual = StepDefinitionHelper.getActualValue(key);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getActualValue_should_return_actual_value_if_its_not_have_brackets() {
        // Arrange
        final String id = "1";
        final String key = "petId_post";
        final String expected = "petId_post";

        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        parentScenarioContext.put(id, params);

        // Act
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
        String actual = StepDefinitionHelper.getActualValue(key);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getActualValue_should_return_null_if_key_is_null() {
        // Arrange
        final String expected = null;

        // Act
        String actual = StepDefinitionHelper.getActualValue(null);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void buildJsonString_should_build_valid_json() {
        // Arrange
        final String fileName = "sample.json";
        final String jsonInput = "{\"parameterType\":\"PATH_PARAM\",\"value\":\"101\",\"key\":\"petId\"}";
        final String expected = jsonInput;

        // Act
        String actual;
        try {
            actual = StepDefinitionHelper.buildJsonString(fileName, jsonInput);
        } catch (Exception e) {
            actual = e.getMessage();
        }

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void buildJsonString_should_throw_exception_for_invalid_json() {
        // Arrange
        final String fileName = "sample.json";
        final String jsonInput = "{\"parameterType\":\"PATH_PARAM\",\"value\":\"101\",\"key\":\"petId\"";
        final String expected = "Validate sample.json has correct Json format?? Expected a ',' or '}' at 57 [character 58 line 1]";

        // Act
        String actual;
        try {
            actual = StepDefinitionHelper.buildJsonString(fileName, jsonInput);
        } catch (Exception e) {
            actual = e.getMessage();
        }

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getObjectValue_should_return_actual_value_if_it_does_not_contain_tilde_symbol() {
        // Arrange
        final String value = "GoldFish-POST";
        final String expected = value;

        // Act
        Object actual = StepDefinitionHelper.getObjectValue(value);
        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getObjectValue_should_return_integer_if_its_starts_with_i() {
        // Arrange
        final String value = "i~100";
        final Integer expected = 100;

        // Act
        Object actual = StepDefinitionHelper.getObjectValue(value);
        // Assert
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void getObjectValue_should_return_boolean_if_its_starts_with_b() {
        // Arrange
        final String value = "b~true";
        final Boolean expected = true;

        // Act
        Object actual = StepDefinitionHelper.getObjectValue(value);
        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getObjectValue_should_return_double_if_its_starts_with_d() {
        // Arrange
        final String value = "d~5.5";
        final Double expected = 5.5;

        // Act
        Object actual = StepDefinitionHelper.getObjectValue(value);
        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getObjectValue_should_return_long_if_its_starts_with_l() {
        // Arrange
        final String value = "l~10";
        final Long expected = 10L;

        // Act
        Object actual = StepDefinitionHelper.getObjectValue(value);
        // Assert
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void getObjectValue_should_return_float_if_its_starts_with_f() {
        // Arrange
        final String value = "f~10";
        final Float expected = 10f;

        // Act
        Object actual = StepDefinitionHelper.getObjectValue(value);
        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getJSON_should_return_valid_json() {
        // Arrange
        final String json = "{\"parameterType\":\"PATH_PARAM\",\"value\":\"101\",\"key\":\"petId\"}";
        final JSONObject expected = new JSONObject(json);

        // Act
        Object actual;
        try {
            actual = StepDefinitionHelper.getJSON(json);
        } catch (Exception e) {
            actual = e.getMessage();
        }

        // Assert
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void getJSON_should_return_valid_json_array() {
        // Arrange
        final String json = "[{\"parameterType\":\"PATH_PARAM\",\"value\":\"101\",\"key\":\"petId\"}]";
        final JSONArray expected = new JSONArray(json);

        // Act
        Object actual;
        try {
            actual = StepDefinitionHelper.getJSON(json);
        } catch (Exception e) {
            actual = e.getMessage();
        }

        // Assert
        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void getJSON_should_exception_for_invalid_json() {
        // Arrange
        final String json = "{\"parameterType\":\"PATH_PARAM\",\"value\":\"101\",\"key\":\"petId\"";
        final String expected = null;

        // Act
        Object actual;
        try {
            actual = StepDefinitionHelper.getJSON(json);
        } catch (Exception e) {
            actual = e.getMessage();
        }

        // Assert
        Assert.assertEquals(expected, actual);
    }
}
