package io.virtualan.cucumblan.props.util;

import org.junit.Assert;
import org.junit.Test;

public class UtilHelperTest {

    @Test
    public void getObject_should_spilt_and_return_the_second_string() {

        // Arrange
        final String value = "i~[petId_post]";
        final String expected = "[petId_post]";

        // Act
        String actual = UtilHelper.getObject(value);

        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void getObject_should_return_actual_string_if_there_is_no_tilde() {

        // Arrange
        final String value = "GoldFish-POST";
        final String expected = "GoldFish-POST";

        // Act
        String actual = UtilHelper.getObject(value);

        // Assert
        Assert.assertEquals(expected, actual);

    }

}
