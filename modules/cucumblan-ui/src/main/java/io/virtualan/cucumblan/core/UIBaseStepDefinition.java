/*
 *
 *
 *    Copyright (c) 2021.  Virtualan Contributors (https://virtualan.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software distributed under the License
 *     is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing permissions and limitations under
 *     the License.
 *
 *
 *
 */


package io.virtualan.cucumblan.core;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Scenario;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.ui.action.Action;
import io.virtualan.cucumblan.ui.core.PageElement;
import io.virtualan.cucumblan.ui.core.PagePropLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.reflections.Reflections;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.After;
import java.util.concurrent.TimeUnit;
import org.reflections.scanners.SubTypesScanner;


/**
 * The type Ui base step definition.
 * @author Elan Thangamani
 */
public class UIBaseStepDefinition {

  private final static Logger LOGGER = Logger.getLogger(UIBaseStepDefinition.class.getName());
  private static PagePropLoader pagePropLoader;
  private static Map<String, Action> actionProcessorMap = new HashMap<>();

  static {
    loadActionProcessors();
  }

  private WebDriver driver = null;
  private PageElement pageElement;

  /**
   * Load action processors.
   */
  public static void loadActionProcessors() {
    Reflections reflections = new Reflections(ApplicationConfiguration.getStandardPackage(),
        new SubTypesScanner(false));
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

  /**
   * Load driver and url.
   *
   * @param driverName the driver name
   * @param url        the url
   */
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

  @After
  public void embedScreenshotOnFail(Scenario s) {
    if (s.isFailed()) try {
      final byte[] screenshot = ((TakesScreenshot) driver)
          .getScreenshotAs(OutputType.BYTES);
      s.attach(screenshot , "image/png" , "Failed-Image :" + UUID.randomUUID().toString());
    } catch (ClassCastException cce) {
      LOGGER.warning(" Error Message : " + cce.getMessage());
    }
  }

  /**
   * Load page.
   *
   * @param pageName the page name
   * @param resource the resource
   * @param dt       the dt
   * @throws Exception the exception
   */
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

  /**
   * Verify.
   *
   * @param name  the name
   * @param value the value
   */
  @Then("verify (.*) has (.*) data in the page$")
  public void verify(String name, String value) {
     Assertions.assertEquals(value, ScenarioContext.getContext(name));
  }

  /**
   * Action processor.
   *
   * @param key     the key
   * @param value   the value
   * @param element the element
   * @throws InterruptedException the interrupted exception
   */
  public void actionProcessor(String key, String value, PageElement element)
      throws InterruptedException {
    WebElement webelement = driver.findElement(By.xpath(element.getPageElementXPath()));
    Action action = actionProcessorMap.get(element.getPageElementAction());
    action.perform(key, webelement, value);
  }

  /**
   * Clean up.
   */
  @After
  public void cleanUp() {
    if(driver != null) {
      driver.close();
    }else {
      LOGGER.warning(" Driver not loaded : ");
    }
  }

}   