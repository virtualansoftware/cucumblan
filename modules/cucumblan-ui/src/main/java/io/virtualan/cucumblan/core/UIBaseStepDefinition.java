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

import io.appium.java_client.AppiumDriver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.virtualan.cucumblan.mobile.AppiumServer;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.util.MobileHelper;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import io.virtualan.cucumblan.props.util.UIHelper;
import io.virtualan.cucumblan.ui.action.Action;
import io.virtualan.cucumblan.ui.core.PageElement;
import io.virtualan.cucumblan.ui.core.PagePropLoader;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 * The type Ui base step definition.
 *
 * @author Elan Thangamani
 */
public class UIBaseStepDefinition {

  private final static Logger LOGGER = Logger.getLogger(UIBaseStepDefinition.class.getName());
  private static Map<String, Action> actionProcessorMap = new HashMap<>();
  private AppiumServer appiumServer;

  static {
    loadActionProcessors();
  }

  private WebDriver webDriver = null;
  private Scenario scenario;

  /**
   * Load action processors.
   */
  public static void loadActionProcessors() {
    Reflections reflections = new Reflections("io.virtualan.cucumblan.ui.actionimpl",
        new SubTypesScanner(false));
    Set<Class<? extends Action>> buildInclasses = reflections.getSubTypesOf(Action.class);
    reflections = new Reflections(ApplicationConfiguration.getActionPackage(),
        new SubTypesScanner(false));
    Set<Class<? extends Action>> customclasses = reflections.getSubTypesOf(Action.class);
    if (customclasses != null) {
      buildInclasses.addAll(customclasses);
    }
    buildInclasses.stream().forEach(x -> {
      Action action = null;
      try {
        action = x.getDeclaredConstructor().newInstance();
        actionProcessorMap.put(action.getType(), action);
      } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        LOGGER.warning("Unable to process this action (" + action.getType() + ") class: " + action);
      }
    });
  }

  @Before
  public void before(Scenario scenario) {
    this.scenario = scenario;
  }

  /**
   * Load driver and url.
   *
   * @param driverName the driver name
   * @param resource   the url
   */
  @Given("user wants to create (.*) on (.*)$")
  public void givenStatement(String driverName, String resource) {

  }

    /**
     * Load driver and url.
     *
     * @param driverName the driver name
     * @param resource   the url
     */
  @Given("Load driver (.*) and url on (.*)$")
  public void loadDriverAndURL(String driverName, String resource) throws Exception {
    switch (driverName) {
      case "CHROME":
        System.setProperty("webdriver.chrome.driver", "conf/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("start-maximized");
        //chromeOptions.addArguments("--headless", "--window-size=1920,1200");
        webDriver = new ChromeDriver(chromeOptions);
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        loadUrl(resource);
        break;
      case "FIREFOX":
        webDriver = new FirefoxDriver();
        webDriver.manage().window().maximize();
        loadUrl(resource);
        break;
      case "ANDROID":
        try {
          appiumServer  = new AppiumServer();
          if (!appiumServer.checkIfServerIsRunnning(MobileHelper.getMobilePort())) {
            appiumServer.startServer( resource, "ANDROID");
            appiumServer.stopServer();
          } else {
            LOGGER.warning("Appium Server already running on Port - " + MobileHelper.getMobilePort());
          }
        } catch (Exception e) {
        }
        webDriver = appiumServer.startServer( resource, "ANDROID");
        break;
      case "IOS":
        try {
          appiumServer  = new AppiumServer();
          if (!appiumServer.checkIfServerIsRunnning(MobileHelper.getMobilePort())) {
            appiumServer.startServer( resource, "ANDROID");
            appiumServer.stopServer();
          } else {
            LOGGER.warning("Appium Server already running on Port - " + MobileHelper.getMobilePort());
          }
        } catch (Exception e) {
        }
        webDriver = appiumServer.startServer(resource, "IOS");
        break;
      default:
        throw new IllegalArgumentException("Browser \"" + driverName + "\" isn't supported.");
    }
    LOGGER.info(" Device connection established");
  }

  private void loadUrl(String resource) {
    String url = UIHelper.getUrl(resource);
    if (url == null) {
      scenario.log("Url missing for " + resource);
      System.exit(-1);
    } else {
      scenario.log("Url for " + resource + " : " + url);
    }
    webDriver.get(url);
  }

  @After
  public void embedScreenshotOnFail(Scenario s) {
    if (s.isFailed()) {
      try {
        final byte[] screenshot = ((TakesScreenshot) webDriver)
            .getScreenshotAs(OutputType.BYTES);
        s.attach(screenshot, "image/png", "Failed-Image :" + UUID.randomUUID().toString());
      } catch (ClassCastException cce) {
        LOGGER.warning(" Error Message : " + cce.getMessage());
      }
    }
  }

  /**
   * Load page.
   *
   * @param pageName the page name
   * @param resource the resource
   * @param data       the data
   * @throws Exception the exception
   */
  @Given("(.*) the (.*) page on (.*)$")
  public void loadPage(String dummy, String pageName, String resource, Map<String, String> data) throws Exception {
    Map<String, PageElement> pageMap = PagePropLoader.readPageElement(resource, pageName);
    if (pageMap != null && !pageMap.isEmpty()) {

      pageMap.forEach((k, v) -> {
        String elementValue = data.get(v.getName());
        if (elementValue != null && "DATA".equalsIgnoreCase(v.getType())|| "NAVIGATION".equalsIgnoreCase(v.getType()))  {
          try {
            actionProcessor(v.getName(), elementValue, v);
          } catch (InterruptedException e) {
            LOGGER.warning("Unable to process this page: " + pageName);
            Assertions.assertTrue(false,
                pageName + " Page for resource " + resource + " (" + v.getName() + " : "
                    + elementValue + ":" + v + "): " + e.getMessage());
          } catch (Exception e) {
            LOGGER.warning("Unable to process this page: " + pageName);
            Assertions.assertTrue(false,
                pageName + " Page for resource " + resource + " (" + v.getName() + " : "
                    + elementValue + ":" + v + "): " + e.getMessage());
          }
        } else {
          Assertions.assertTrue(false,
              pageName + " Page for resource " + resource + " (" + v.getName() + " : "
                  + elementValue + ":" + v + "): incorrect field name");
        }

      });
    } else {
      Assertions.assertTrue(false, pageName + " Page is not found for resource " + resource);
    }
  }

  /**
   * Verify.
   *
   * @param name  the name
   * @param value the value
   */
  @Then("verify (.*) has (.*) data in the page$")
  public void verify(String name, String value) {
    Assertions.assertEquals(value, StepDefinitionHelper.getActualValue(name));
  }

  /**
   * Verify.
   *
   * @param name  the name
   */
  @Then("verify (.*) contains data in the page$")
  public void verify(String name, Map<String, String> xpathWithValue) {
    for(Map.Entry<String, String> xpathMaps : xpathWithValue.entrySet()) {
      WebElement webelement = webDriver.findElement(By.xpath(xpathMaps.getKey()));
      Assertions.assertEquals(StepDefinitionHelper.getActualValue(xpathMaps.getValue()), webelement.getText() , xpathMaps.getKey() + " is not Matched.");
    }
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
    WebElement webelement = webDriver.findElement(element.findElement());
    Action action = actionProcessorMap.get(element.getAction());
    action.perform(webDriver, key, webelement, value);
  }

  /**
   * Clean up.
   */
  @After
  public void cleanUp() {
    if (webDriver != null && ApplicationConfiguration.isProdMode()) {
      webDriver.close();
    } else {
      LOGGER.warning(" Driver not loaded/Closed : ");
    }
    if (appiumServer != null) {
      appiumServer.stopServer();
    }
  }

}   