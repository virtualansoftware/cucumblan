package io.virtualan.cucumblan.props;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class ApplicationConfigurationTest {

    
    @Test
    public void reload_should_load_application_configuration() {

        // Arrange
        final String expected = "http://localhost:8800/api";

        // Act
        ApplicationConfiguration.reload();
        Map<String, String> actual = ApplicationConfiguration.getProperties();

        // Assert
        Assert.assertEquals(expected, actual.get("service.api.pet"));

    }

    @Test
    public void setProperty_should_set_properties() {

        // Arrange
        final String key = "endpoint";
        final String value = "http://localhost:8800/api";
        final String expected = value;

        // Act
        ApplicationConfiguration.setProperty(key, value);
        Map<String, String> actual = ApplicationConfiguration.getProperties();

        // Assert
        Assert.assertEquals(expected, actual.get(key));

    }


    @Test
    public void getStandardPackage_should_return_default_package() {

        // Arrange
        final String expected = "io.virtualan.cucumblan.standard";

        // Act
        String actual = ApplicationConfiguration.getStandardPackage();

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void getMessageTypePackage_should_return_default_message_type_package() {

        // Arrange
        final String expected = "io.virtualan.cucumblan.message.typeimpl";

        // Act
        String actual = ApplicationConfiguration.getMessageTypePackage();

        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void getActionPackage_should_return_default_action_package() {

        // Arrange
        final String expected = "io.virtualan.cucumblan.ui.actionimpl";

        // Act
        String actual = ApplicationConfiguration.getActionPackage();

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void getMobileActionPackage_should_return_default_mobile_action_package() {

        // Arrange
        final String expected = "io.virtualan.cucumblan.mobile.actionimpl";

        // Act
        String actual = ApplicationConfiguration.getMobileActionPackage();

        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void getBoolean_should_return_the_boolean_value() {

        // Arrange
        final boolean expected = true;

        // Act
        boolean actual = ApplicationConfiguration.getBoolean("service.test");

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void getBoolean_should_return_default_boolean() {

        // Arrange
        final boolean expected = false;

        // Act
        boolean actual = ApplicationConfiguration.getBoolean("");

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void getProperty_should_return_the_property() {

        // Arrange
        final String key = "endpoint";
        final String value = "http://localhost:8800/api";
        final String expected = value;

        // Act
        String actual = ApplicationConfiguration.getProperty(key);

        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void getProperty_should_return_null_if_property_does_not_exist() {

        // Arrange
        final String key = "";
        final String expected = null;

        // Act
        String actual = ApplicationConfiguration.getProperty(key);

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void getMessageCount_should_return_default_message_count() {

        // Arrange
        final int expected = 2;

        // Act
        int actual = ApplicationConfiguration.getMessageCount();

        // Assert
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void getInline_should_return_inline_value() {

        // Arrange
        final boolean expected = true;

        // Act
        boolean actual = ApplicationConfiguration.getInline();

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void isProdMode_should_return_prod_mode() {

        // Arrange
        final boolean expected = true;

        // Act
        boolean actual = ApplicationConfiguration.isProdMode();

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void isRecorderMode_should_return_recorder_mode() {

        // Arrange
        final boolean expected = true;

        // Act
        boolean actual = ApplicationConfiguration.isRecorderMode();

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void isRecordAll_should_return_record_all_is_enable() {

        // Arrange
        final boolean expected = true;

        // Act
        boolean actual = ApplicationConfiguration.isRecordAll();

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void getPath_should_return_base_path() {

        // Arrange
        final String expected = "src";

        // Act
        String actual = ApplicationConfiguration.getPath();

        // Assert
        Assert.assertEquals(expected, actual);

    }
    
    @Test
    public void getBuildPath_should_return_build_path() {

        // Arrange
        final String expected = "build";

        // Act
        String actual = ApplicationConfiguration.getBuildPath();

        // Assert
        Assert.assertEquals(expected, actual);

    }
}
