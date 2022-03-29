package io.virtualan.cucumblan.parser;

import org.junit.Assert;
import org.junit.Test;

public class OpenAPIParserTest {

    @Test
    public void init_should_throw_error_if_system_and_url_is_empty() {
        // Arrange
        final String expected = "Unable to create endpoint mapping url mapping : null";
        String actual = "";

        // Act
        try {
            OpenAPIParser.init("", "");
        } catch (Exception e) {
            // System.out.println(e.getMessage() + "------------");
            actual = e.getMessage();
        }

        // Assert
        Assert.assertEquals(expected, actual);
    }
}
