package io.virtualan.cucumblan.props.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ScenarioContextTest {

    @Before
    public void init() {
        Map<String, Map<String, String>> parentScenarioContext = new HashMap<>();
        ScenarioContext.setParentScenarioContext(parentScenarioContext);
    }

    @Test
    public void getParentScenarioContext_should_return_parent_scenario_context() {
        // Arrange
        final String id = "1";
        final String key = "STATUS_CODE";
        final String value = "200";

        Map<String, Map<String, String>> expected = new HashMap<>();
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        expected.put(id, params);


        // Act
        ScenarioContext.setParentScenarioContext(expected);
        Map<String, Map<String, String>> actual = ScenarioContext.getParentScenarioContext();
        
        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void setParentScenarioContext_should_set_parent_scenario_context() {
        // Arrange
        Map<String, Map<String, String>> expected = new HashMap<>();

        // Act
        ScenarioContext.setParentScenarioContext(expected);
        Map<String, Map<String, String>> actual = ScenarioContext.getParentScenarioContext();
        
        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getScenarioContext_should_return_scenario_context() {
        // Arrange
        final String id = "1";
        final String key = "STATUS_CODE";
        final String value = "200";

        Map<String, String> expected = new HashMap<>();
        expected.put(key, value);

        // Act
        ScenarioContext.setContext(id, expected);
        Map<String, String> actual = ScenarioContext.getContext(id);
        
        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void hasContextValues_should_return_true_if_context_is_available() {
        // Arrange
        final String id = "1";
        final String key = "STATUS_CODE";
        final String value = "200";
        final boolean expected = true;

        Map<String, String> params = new HashMap<>();
        params.put(key, value);

        // Act
        ScenarioContext.setContext(id, params);
        boolean actual = ScenarioContext.hasContextValues(id);
        
        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void hasContextValues_should_return_false_if_context_is_not_available() {
        // Arrange
        final String id = "1";
        final boolean expected = false;

        Map<String, String> params = new HashMap<>();

        // Act
        ScenarioContext.setContext(id, params);
        boolean actual = ScenarioContext.hasContextValues(id);
        
        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void hasContextValues_should_return_false_if_parent_context_is_not_available() {
        // Arrange
        final String id = "1";
        final boolean expected = false;

        // Act
        boolean actual = ScenarioContext.hasContextValues(id);
        
        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void setContext_should_set_context() {

        // Arrange
        final String id = "1";
        final String key = "STATUS_CODE";
        final String value = "200";
        final String expected = "200";

        // Act
        ScenarioContext.setContext(id, new HashMap<>());
        ScenarioContext.setContext(id, key, value);
        final Object actual = ScenarioContext.getContext(id, key);

        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void setContext_should_set_context_by_params() {

        // Arrange
        final String id = "1";
        final String key = "STATUS_CODE";
        final String value = "200";
        final String expected = "200";

        // Act
        ScenarioContext.setContext(id, new HashMap<>());
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        ScenarioContext.setContext(id, params);
        final Object actual = ScenarioContext.getContext(id, key);

        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void getPrintableContextObject_should_return_printable_context() {

        // Arrange
        final String id = "1";
        final String key = "STATUS_CODE";
        final String value = "200";
        // final String expected = "200";

        // Act
        Map<String, String> expected = new HashMap<>();
        expected.put(key, value);
        ScenarioContext.setContext(id, expected);
        final Map<String, String> actual = ScenarioContext.getPrintableContextObject(id);

        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void getPrintableContextObject_should_not_return_the_actual_password() {

        // Arrange
        final String id = "1";
        final String key = "password";
        final String value = "test123";
        final String expected = "xxxxxxxxxxxx";

        // Act
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        ScenarioContext.setContext(id, params);
        final Map<String, String> actual = ScenarioContext.getPrintableContextObject(id);

        // Assert
        Assert.assertEquals(expected, actual.get(key));

    }

    @Test
    public void remove_should_remove_the_context() {

        // Arrange
        final String id = "1";
        final String key = "STATUS_CODE";
        final String value = "200";
        final Object expected = null;

        // Act
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        ScenarioContext.setContext(id, params);
        ScenarioContext.remove(id);
        Map<String, String> actual = ScenarioContext.getContext(id);

        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void isContains_should_return_true_if_the_context_and_key_is_available() {

        // Arrange
        final String id = "1";
        final String key = "STATUS_CODE";
        final String value = "200";
        final Object expected = true;

        // Act
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        ScenarioContext.setContext(id, params);
        Boolean actual = ScenarioContext.isContains(id, key);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void isContains_should_return_false_if_the_context_and_key_is_not_available() {

        // Arrange
        final String id = "1";
        final String key = "STATUS_CODE";
        final String value = "200";
        final Object expected = false;

        // Act
        Map<String, String> params = new HashMap<>();
        params.put(key, value);
        ScenarioContext.setContext(id, params);
        Boolean actual = ScenarioContext.isContains(id, "TEST");

        // Assert
        Assert.assertEquals(expected, actual);
    }
}
