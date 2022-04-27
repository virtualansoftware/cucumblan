package io.virtualan.cucumblan.props.util;

import org.junit.Test;
import org.junit.Assert;

public class HelperApiUtilTest {
    
    @Test
    public void readFileAsString_should_return_file_string() {
        // Arrange
        final String expected = "cucumber.publish.quiet=true";

        // Act
        String actual = HelperApiUtil.readFileAsString("cucumber.properties");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void readFileAsString_should_return_null_if_file_is_not_found() {
        // Arrange
        final String expected = null;

        // Act
        String actual = HelperApiUtil.readFileAsString("cucumber1.properties");

        // Assert
        Assert.assertEquals(expected, actual);
    }
}
