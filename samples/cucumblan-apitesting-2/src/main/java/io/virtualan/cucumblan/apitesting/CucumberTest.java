package io.virtualan.cucumblan.apitesting;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		plugin = {
				"pretty", "html:target/cucumberHTML-report", "json:target/cucumber.json"
				},	
		features = { "classpath:features/book/book.feature" },
		  glue = {"io.virtualan.cucumblan.apitesting" },
		  tags  ="@book",
		  dryRun = false
		  )
public class CucumberTest {
}