package io.virtualan.cucumblan.core;

import io.cucumber.datatable.DataTable;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.ui.action.Action;
import io.virtualan.cucumblan.ui.core.PageElement;
import io.virtualan.cucumblan.ui.core.PagePropLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.reflections.Reflections;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.After;
import java.util.concurrent.TimeUnit;


public class UIBaseStepDefinition {

  private final static Logger LOGGER = Logger.getLogger(UIBaseStepDefinition.class.getName());
  private static PagePropLoader pagePropLoader;
  private static Map<String, Action> actionProcessorMap = new HashMap<>();

  static {
    loadActionProcessors();
  }

  private WebDriver driver = null;
  private PageElement pageElement;

  public static void loadActionProcessors() {
    Reflections reflections = new Reflections("io.virtualan.cucumblan.ui.actionimpl");
    Set<Class<? extends Action>> classes = reflections.getSubTypesOf(Action.class);
    classes.stream().forEach(x -> {
      Action action = null;
      try {
        action = x.newInstance();
        actionProcessorMap.put(action.getType(), action);
      } catch (InstantiationException e) {
        LOGGER.warning("Unable to process this action (" + action.getType() + ") class: " + action);
      } catch (IllegalAccessException e) {
        LOGGER.warning("Unable to process this action (" + action.getType() + ") class: " + action);
      }
    });
  }

  @Given("Load Driver (.*) And URL (.*)$")
  public void loadDriverAndURL(String driverName, String url) {
    switch (driverName) {
      case "CHROME":
        System.setProperty("webdriver.chrome.driver", "conf/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("start-maximized");
        //chromeOptions.addArguments("--headless", "--window-size=1920,1200");
        driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        break;
      case "FIREFOX":
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        break;
      default:
        throw new IllegalArgumentException("Browser \"" + driverName + "\" isn't supported.");
    }
    driver.get(url);
  }

  @Given("perform the (.*) page action on (.*)$")
  public void loadPage(String pageName, String resource, DataTable dt) throws Exception {
    List<Map<String, String>> data = dt.asMaps();
    Map<String, PageElement> pageMap = PagePropLoader.readPageElement(resource, pageName);

    pageMap.forEach((k, v) -> {
      String elementValue = data.get(0).get(v.getPageElementName());
      try {
        actionProcessor(v.getPageElementName(), elementValue, v);
      } catch (InterruptedException e) {
        LOGGER.warning("Unable to process this page: " + pageName);
      }
    });

  }

  @Then("verify (.*) has (.*) data in the page$")
  public void verify(String name, String value) {
     Assertions.assertEquals(value, ScenarioContext.getContext(name));
  }

  public void actionProcessor(String key, String value, PageElement element)
      throws InterruptedException {
    WebElement webelement = driver.findElement(By.xpath(element.getPageElementXPath()));
    Action action = actionProcessorMap.get(element.getPageElementAction());
    action.perform(key, webelement, value);
  }

  @After
  public void cleanUp() {
    if(driver != null) {
      driver.close();
    }else {
      LOGGER.warning(" Driver not loaded : ");
    }
  }

}   