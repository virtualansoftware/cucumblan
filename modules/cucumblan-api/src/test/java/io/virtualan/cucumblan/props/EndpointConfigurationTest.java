package io.virtualan.cucumblan.props;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import java.util.logging.Logger;

public class EndpointConfigurationTest {

    private static final String CONF_FOLDER_NAME = "conf/";
    private static final String RESOURCE_FOLDER_NAME = "src/test/resources/conf";
    private final static Logger LOGGER = Logger.getLogger(EndpointConfigurationTest.class.getName());

    @BeforeClass
    public static void init() {
        if (!new File(CONF_FOLDER_NAME).exists()) {
            new File(CONF_FOLDER_NAME).mkdirs();
        }

        File src = new File(RESOURCE_FOLDER_NAME);
        File dest = new File(CONF_FOLDER_NAME);

        try {
            FileUtils.copyDirectory(src, dest);
        } catch (IOException e) {
            LOGGER.warning("Not able to create output folder " + e.getMessage());
        }
    }

    @AfterClass
    public static void cleanUp() {
        try {
            FileUtils.deleteDirectory(new File(CONF_FOLDER_NAME));
        } catch (IOException e) {
            LOGGER.warning("Not able to cleanup output folder " + e.getMessage());
        }
    }

    @Test
    public void getInstance_should_retrun_singletone_class() {
        // Arrange
        EndpointConfiguration instance1;
        EndpointConfiguration instance2;

        // Act
        instance1 = EndpointConfiguration.getInstance();
        instance2 = EndpointConfiguration.getInstance();

        // Assert
        assertSame(instance1, instance2);
    }

    @Test
    public void getProperty_should_return_property_if_key_exist() {
        // Arrange
        EndpointConfiguration configuration = EndpointConfiguration.getInstance();
        configuration.loadEndpoints();

        // Act
        final Properties expected = configuration.getProperty("pet");

        // Assert
        assertNotNull(expected);
    }

    @Test
    public void getProperty_should_return_null_if_key_not_exist() {
        // Arrange
        EndpointConfiguration configuration = EndpointConfiguration.getInstance();
        configuration.loadEndpoints();

        // Act
        final Properties expected = configuration.getProperty("test");

        // Assert
        assertNull(expected);
    }

}
