package io.virtualan.cucumblan.props;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ExcludeConfigurationTest {
    @Test
    public void reload_should_reload_properties_from_exclude_response() {
        // Arrange
        final String key = "firstName";
        final String expected = "IGNORE";

        // Act
        ExcludeConfiguration.reload();
        Map<String, String> actual = ExcludeConfiguration.getProperties();

        // Assert
        Assert.assertEquals(expected, actual.get(key));
    }

    @Test
    public void shouldSkip_should_return_true_if_exclude_resource_match() {
        // Arrange
        final String resource = "firstName";
        final boolean expected = true;

        // Act
        boolean actual = ExcludeConfiguration.shouldSkip(resource, null);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldSkip_should_return_true_if_exclude_resource_not_match() {
        // Arrange
        final String resource = "id";
        final boolean expected = false;

        // Act
        boolean actual = ExcludeConfiguration.shouldSkip(resource, null);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldSkip_should_return_true_if_exclude_resource_and_key_match() {
        // Arrange
        final String resource = "pet_id";
        final String key = "101";
        final boolean expected = true;

        // Act
        boolean actual = ExcludeConfiguration.shouldSkip(resource, key);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldSkip_should_return_true_if_exclude_resource_pattern_match() {
        // Arrange
        final String resource = "/bin/3f64e65d-c657-42d5-bcc9-5b13e71ca493";
        final boolean expected = true;

        // Act
        boolean actual = ExcludeConfiguration.shouldSkip(resource, null);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldSkip_should_return_true_if_exclude_resource_match_with_exclude_properties() {
        // Arrange
        Map<String, String> excludeProperties = new HashMap<>();
        excludeProperties.put("/bin/(.*)", "IGNORE");
        excludeProperties.put("firstName", "IGNORE");
        excludeProperties.put("pet_id", "101,102");
        final String resource = "firstName";
        final boolean expected = true;

        // Act
        boolean actual = ExcludeConfiguration.shouldSkip(excludeProperties, resource, null);

        // Assert
        Assert.assertEquals(expected, actual);
    }
    
    @Test
    public void shouldSkip_should_return_true_if_exclude_resource_not_match_with_exclude_properties() {
        // Arrange
        Map<String, String> excludeProperties = new HashMap<>();
        excludeProperties.put("/bin/(.*)", "IGNORE");
        excludeProperties.put("firstName", "IGNORE");
        excludeProperties.put("pet_id", "101,102");
        final String resource = "id";
        final boolean expected = false;

        // Act
        boolean actual = ExcludeConfiguration.shouldSkip(excludeProperties, resource, null);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldSkip_should_return_true_if_exclude_resource_and_key_match_with_exclude_properties() {
        // Arrange
        Map<String, String> excludeProperties = new HashMap<>();
        excludeProperties.put("/bin/(.*)", "IGNORE");
        excludeProperties.put("firstName", "IGNORE");
        excludeProperties.put("pet_id", "101,    102");
        final String resource = "pet_id";
        final String key = "102";
        final boolean expected = true;

        // Act
        boolean actual = ExcludeConfiguration.shouldSkip(excludeProperties, resource, key);

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldSkip_should_return_true_if_exclude_resource_pattern_match_with_exclude_properties() {
        // Arrange
        Map<String, String> excludeProperties = new HashMap<>();
        excludeProperties.put("/bin/(.*)", "IGNORE");
        excludeProperties.put("firstName", "IGNORE");
        excludeProperties.put("pet_id", "101,102");
        final String resource = "/bin/3f64e65d-c657-42d5-bcc9-5b13e71ca493";
        final boolean expected = true;

        // Act
        boolean actual = ExcludeConfiguration.shouldSkip(excludeProperties, resource, null);

        // Assert
        Assert.assertEquals(expected, actual);

    }
}
