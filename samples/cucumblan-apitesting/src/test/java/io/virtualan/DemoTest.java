package io.virtualan;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * To run cucumber test
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
        extraGlue = {"io.virtualan.core"},
        plugin = {"pretty",
                "io.virtualan.cucumblan.props.hook.FeatureScope",

                "json:target/cucumber/cucumber-report.json"})

public class DemoTest {

}
