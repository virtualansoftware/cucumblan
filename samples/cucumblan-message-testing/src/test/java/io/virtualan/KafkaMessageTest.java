package io.virtualan;

import io.cucumber.java.After;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;

import static net.mguenther.kafka.junit.EmbeddedKafkaCluster.provisionWith;
import static net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig.defaultClusterConfig;

/**
 * To run cucumber test
 */
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features",
    glue = {"io.virtualan.cucumblan.core"},
    plugin = {"pretty",
        "json:target/cucumber-report.json"})

public class KafkaMessageTest {

  private static EmbeddedKafkaCluster kafka;

  @BeforeClass
  public static void setupKafka() {
    kafka = provisionWith(defaultClusterConfig());
    kafka.start();
  }

  @AfterClass
  public static void tearDownKafka() {
    kafka.stop();
  }

}
