/*
 *
 *
 *    Copyright (c) 2022.  Virtualan Contributors (https://virtualan.io)
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
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The type Ui base step definition.
 *
 * @author Elan Thangamani
 */
public class UIBaseStepDefinition {

    private final static Logger LOGGER = Logger.getLogger(UIBaseStepDefinition.class.getName());
    private static Map<String, Action> actionProcessorMap = new HashMap<>();

    static {
        loadActionProcessors();
    }

    Map<String, WebDriver> webDrivers = new HashMap();
    private AppiumServer appiumServer;
    //private WebDriver webDriver = null;
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
     * @param dummy1   the driver name
     * @param dummy2   the driver name
     * @param resource the url
     */
    @Given("(.*) want to validate (.*) on (.*)$")
    public void givenStatement(String dummy1, String dummy2, String resource) {

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
                chromeDriverBuilder(resource);
                break;
            case "FIREFOX":
                firefoxDriverBuilder(resource);
                break;
            case "ANDROID":
                mobilebuilder(driverName, resource);
                break;
            case "IOS":
                mobilebuilder(driverName, resource);
                break;
            default:
                throw new IllegalArgumentException("Browser \"" + driverName + "\" isn't supported.");
        }
        LOGGER.info(" Device connection established");
    }

    private void mobilebuilder(String driverName, String resource) throws Exception {
        try {
            appiumServer = new AppiumServer();
            if (!appiumServer.checkIfServerIsRunnning(MobileHelper.getMobilePort())) {
                appiumServer.startServer(resource, driverName);
                appiumServer.stopServer();
            } else {
                LOGGER.warning("Appium Server already running on Port - " + MobileHelper.getMobilePort());
            }
        } catch (Exception e) {
        }
        webDrivers.put(resource, appiumServer.startServer(resource, driverName));
    }

    private void firefoxDriverBuilder(String resource) throws MalformedURLException {
        WebDriver webDriver = null;
        DesiredCapabilities capabilities = DesiredCapabilities.firefox();
        UIHelper.additionalConfigResource(resource, capabilities);
        FirefoxOptions firefoxOptions = new FirefoxOptions(capabilities);
        capabilities.setCapability("marionette", true);
        if (UIHelper.getServerUrl(resource) == null) {
            System.setProperty("webdriver.gecko.driver", UIHelper.getFireboxDriverPath());
            webDriver = new FirefoxDriver(firefoxOptions);
        } else {
            webDriver = new RemoteWebDriver(new URL(UIHelper.getServerUrl(resource)), firefoxOptions);
        }
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(MobileHelper.getWaitTime(resource), TimeUnit.SECONDS);
        webDrivers.put(resource, webDriver);
        loadUrl(resource);
    }

    private void chromeDriverBuilder(String resource) throws MalformedURLException {
        WebDriver webDriver = null;
        ChromeOptions options = new ChromeOptions();
        UIHelper.additionalConfigArguments(resource, options);
        if (UIHelper.getServerUrl(resource) == null) {
            System.setProperty("webdriver.chrome.driver", UIHelper.getChromeDriverPath());
            webDriver = new ChromeDriver(options);
        } else {
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability(ChromeOptions.CAPABILITY, options);
            UIHelper.additionalConfigResource(resource, caps);
            webDriver = new RemoteWebDriver(new URL(UIHelper.getServerUrl(resource)), caps);
        }
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(UIHelper.getWaitTime(resource), TimeUnit.SECONDS);
        webDriver.manage().timeouts().pageLoadTimeout(UIHelper.getPageLoadWaitTime(resource), TimeUnit.SECONDS);
        webDrivers.put(resource, webDriver);
        loadUrl(resource);
    }

    private void loadUrl(String resource) {
        String url = UIHelper.getUrl(resource);
        if (url == null) {
            scenario.log("Url missing for " + resource);
            System.exit(-1);
        } else {
            scenario.log("Url for " + resource + " : " + url);
        }
        webDrivers.get(resource).get(url);
    }


    @Given("Capture (.*) screen on (.*)$")
    public void embedScreenshotOnFail(String dummy, String resource) {
        if (scenario.isFailed()) {
            try {
                final byte[] screenshot = ((TakesScreenshot) webDrivers.get(resource))
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failed-Image :" + UUID.randomUUID().toString());
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
     * @param data     the data
     * @throws Exception the exception
     */
    @Given("(.*) the (.*) page on (.*)$")
    public void loadPage(String dummy, String pageName, String resource, Map<String, String> data) throws Exception {
        Map<String, PageElement> pageMap = PagePropLoader.readPageElement(resource, pageName);
        if (pageMap != null && !pageMap.isEmpty()) {

            pageMap.forEach((k, v) -> {
                String elementValue = data.get(v.getName());
                if ("DATA".equalsIgnoreCase(v.getType()) && elementValue == null && v.getName() != null) {
                    //Skip the parameter.. its optional
                } else if (elementValue != null && "DATA".equalsIgnoreCase(v.getType()) || "NAVIGATION".equalsIgnoreCase(v.getType())) {
                    try {
                        actionProcessor(v.getName(), elementValue, v, resource);
                    } catch (InterruptedException e) {
                        LOGGER.warning("Unable to process this page: " + pageName);
                        assertTrue(
                                pageName + " Page for resource " + resource + " (" + v.getName() + " : "
                                        + elementValue + ":" + v + "): " + e.getMessage(), false);
                    } catch (Exception e) {
                        LOGGER.warning("Unable to process this page: " + pageName);
                        assertTrue(
                                pageName + " Page for resource " + resource + " (" + v.getName() + " : "
                                        + elementValue + ":" + v + "): " + e.getMessage(), false);
                    }
                } else {
                    assertTrue(
                            pageName + " Page for resource " + resource + " (" + v.getName() + " : "
                                    + elementValue + ":" + v + "): incorrect field name", false);
                }

            });
        } else {
            assertTrue(pageName + " Page is not found for resource " + resource, false);
        }
    }

    /**
     * Verify.
     *
     * @param name  the name
     * @param value the value
     */
    @Then("verify (.*) has (.*) data in the screen on (.*)$")
    public void verify(String name, String value, String resource) {
        assertEquals(value, StepDefinitionHelper.getActualValue(name));
    }

    /**
     * Verify.
     *
     * @param name the name
     */
    @Then("verify (.*) contains data in the screen on (.*)$")
    public void verify(String name, String resource, Map<String, String> xpathWithValue) {
        for (Map.Entry<String, String> xpathMaps : xpathWithValue.entrySet()) {
            WebElement webelement = webDrivers.get(resource).findElement(By.xpath(xpathMaps.getKey()));
            assertEquals(xpathMaps.getKey() + " is not Matched.", StepDefinitionHelper.getActualValue(xpathMaps.getValue()), webelement.getText());
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
    public void actionProcessor(String key, String value, PageElement element, String resource)
            throws InterruptedException {
        WebElement webelement = webDrivers.get(resource).findElement(element.findElement());
        Action action = actionProcessorMap.get(element.getAction());
        action.perform(webDrivers.get(resource), key, webelement, value);
    }

    /**
     * Produce message.
     *
     * @param sleep the sleep
     * @throws InterruptedException the interrupted exception
     */
    @Given("pause ui (.*) for process for (.*) milliseconds$")
    public void pauseUI(String dummy, long sleep) throws InterruptedException {
        Thread.sleep(sleep);
    }


    /**
     * Clean up.
     */

    @Given("Close the driver on (.*)$")
    public void cleanUp(String resource) {
        if (webDrivers.get(resource) != null && ApplicationConfiguration.isProdMode()) {
            webDrivers.get(resource).close();
        } else {
            LOGGER.warning(" Driver not loaded/Closed : ");
        }
        if (appiumServer != null) {
            appiumServer.stopServer();
        }
    }

}   