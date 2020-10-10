package io.virtualan.cucumblan.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * To run cucumber test
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
    glue = {"io.virtualan.cucumblan.core"},
    plugin = {"pretty",
        "json:target/cucumber-report.json"})
public class DemoTest {

}
