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
import org.monte.media.Format;
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
import static org.monte.media.AudioFormatKeys.EncodingKey;
import static org.monte.media.AudioFormatKeys.FrameRateKey;
import static org.monte.media.AudioFormatKeys.KeyFrameIntervalKey;
import static org.monte.media.AudioFormatKeys.MIME_AVI;
import static org.monte.media.AudioFormatKeys.MediaTypeKey;
import static org.monte.media.AudioFormatKeys.MimeTypeKey;
import static org.monte.media.VideoFormatKeys.MediaType;
import static org.monte.media.VideoFormatKeys.*;

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

    org.monte.screenrecorder.ScreenRecorder screenRecorder = null;


    private AppiumServer appiumServer;
    private Scenario scenario;
    private String resourceId;
    private String recordedFile;

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

        //Load dynamic action
        try {
            java.util.Properties prop = new java.util.Properties();
            String propFileName = "actions/page.action";
            java.io.InputStream inputStream =  UIBaseStepDefinition.class.getClassLoader().getResourceAsStream(propFileName);
            if (inputStream != null) {
                prop.load(inputStream);
                for (java.util.Map.Entry p : prop.entrySet()) {
                    String className = UIHelper.toCamel((String) p.getKey());
                    String javaCode = "package " + ApplicationConfiguration.getActionPackage() + ";\n" +
                            "import io.virtualan.cucumblan.props.util.ScenarioContext; import io.virtualan.cucumblan.ui.action.Action; import org.openqa.selenium.WebDriver; import org.openqa.selenium.WebElement;  " +
                            "public class " + className + "Impl implements Action {      @Override     public String getType() {         " +
                            "return " + (String) p.getKey() + ";     }      @Override     public void perform(WebDriver driver, String key, WebElement webelement, Object value, io.virtualan.cucumblan.ui.core.PageElement element)                 " +
                            "throws  Exception{         driver.wait(element.getSleep()); " + (String) p.getValue() + "   return;     } }";
                    Class aClass = net.openhft.compiler.CompilerUtils.CACHED_COMPILER.
                            loadFromJava(ApplicationConfiguration.getActionPackage() + "." + className, javaCode);
                    Action action = (io.virtualan.cucumblan.ui.action.Action) aClass.getDeclaredConstructor().newInstance();
                    actionProcessorMap.put(action.getType(), action);
                }
            }

        } catch (Exception e) {

        }

    }

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
        recordedFile = UUID.randomUUID().toString();
        if (ApplicationConfiguration.isRecorderMode()) {
            java.awt.GraphicsConfiguration gc = java.awt.GraphicsEnvironment//
                    .getLocalGraphicsEnvironment()//
                    .getDefaultScreenDevice()//
                    .getDefaultConfiguration();
            try {
                screenRecorder = new org.monte.screenrecorder.ScreenRecorder(gc, null,
                        new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                                CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                                DepthKey, 24, FrameRateKey, org.monte.media.math.Rational.valueOf(15),
                                QualityKey, 1.0f,
                                KeyFrameIntervalKey, 15 * 60),
                        new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black",
                                FrameRateKey, org.monte.media.math.Rational.valueOf(30)),
                        null, new java.io.File(ApplicationConfiguration.getPath()
                        + java.io.File.separator + recordedFile));
                screenRecorder.start();
            } catch (Exception e) {
                LOGGER.warning(" Error recording" + e.getMessage());
            }
        }
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
        resourceId = resource;
    }

    /**
     * Load driver and url.
     *
     * @param driverName the driver name
     * @param resource   the url
     */
    @Given("Load driver (.*) and url on (.*)$")
    public void loadDriverAndURL(String driverName, String resource) throws Exception {
        resourceId = resource;
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
        resourceId = resource;
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
        UIDriverManager.addDriver(resource, appiumServer.startServer(resource, driverName));
    }

    private void firefoxDriverBuilder(String resource) throws MalformedURLException {
        resourceId = resource;
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
        UIDriverManager.addDriver(resource, webDriver);
        loadUrl(resource);
    }

    private void chromeDriverBuilder(String resource) throws MalformedURLException {
        resourceId = resource;
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
        UIDriverManager.addDriver(resource, webDriver);
        loadUrl(resource);
    }

    private void loadUrl(String resource) {
        resourceId = resource;
        String url = UIHelper.getUrl(resource);
        if (url == null) {
            scenario.log("Url missing for " + resource);
            System.exit(-1);
        } else {
            scenario.log("Url for " + resource + " : " + url);
        }
        UIDriverManager.getDriver(resource).get(url);
    }


    @After
    public void embedScreenshotOnFail() {
        if (scenario.isFailed() && resourceId != null) {
            embedScreenshot("Failed", resourceId);
        }
        if (screenRecorder != null && ApplicationConfiguration.isRecorderMode()) {
            try {
                screenRecorder.stop();
                java.io.File file = getAviFile(ApplicationConfiguration.getPath()
                        + java.io.File.separator + recordedFile);
                if ((file != null && file.exists()) && (scenario.isFailed() || ApplicationConfiguration.isRecordAll())) {
                    byte[] bytes = new byte[(int) file.length()];
                    java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(file));
                    dis.readFully(bytes);
                    scenario.attach(bytes, MIME_AVI, "Recorded :" + UUID.randomUUID().toString());
                    dis.close();
                }
                java.nio.file.Files.delete(getAviFile(ApplicationConfiguration.getPath()
                        + java.io.File.separator + recordedFile).toPath());
                java.nio.file.Files.delete(java.nio.file.Paths.get(io.virtualan.cucumblan.props.ApplicationConfiguration.getPath()
                        + java.io.File.separator + recordedFile));

            } catch (Exception e) {
                LOGGER.warning("Recorder issue " + e.getMessage());
            }
        }
    }

    @Given("capture (.*) screen on (.*)$")
    public void embedScreenshot(String dummy, String resource) {
        try {
            if (UIDriverManager.getDriver(resource) != null) {
                final byte[] screenshot = ((TakesScreenshot) UIDriverManager.getDriver(resource))
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Image :" + UUID.randomUUID().toString());
            } else {
                LOGGER.warning(" Driver not loade for resource : " + resource);
            }
        } catch (ClassCastException cce) {
            LOGGER.warning(" Error Message : " + cce.getMessage());
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
        resourceId = resource;
        Map<Integer, PageElement> pageMap = PagePropLoader.readPageElement(resource, pageName);
        if (pageMap != null && !pageMap.isEmpty()) {
            java.util.List<String> dataElements =
                    pageMap.values().stream()
                            .filter(x -> x.getType().equalsIgnoreCase("DATA"))
                            .map(x -> x.getName()).collect(java.util.stream.Collectors.toList());

            java.util.Set<String> findParams = data.keySet();
            findParams.stream().filter(x -> !dataElements.contains(x)).forEach(x -> {
                io.virtualan.cucumblan.props.util.ScenarioContext.setContext(
                        String.valueOf(Thread.currentThread().getId()), x, data.get(x));
            });
            scenario.attach(io.virtualan.cucumblan.props.util.ScenarioContext.getContext(
                    String.valueOf(Thread.currentThread().getId())).toString(),
                    "plain/text", "ContextObject: " + pageName);
            scenario.attach(data.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8), "plain/text", "PageName: " + pageName);
            pageMap.forEach((k, v) -> {
                String name = StepDefinitionHelper.getActualValue(v.getName());
                String elementValue = data.get(name);
                if ((elementValue != null && "DATA".equalsIgnoreCase(v.getType()))
                        || "NAVIGATION".equalsIgnoreCase(v.getType())) {
                    try {
                        actionProcessor(name, StepDefinitionHelper.getActualValue(elementValue), v, resource, data);
                    } catch (InterruptedException e) {
                        LOGGER.warning("Unable to process this page:(" + e.getMessage() + ") " + pageName);
                        assertTrue(
                                pageName + " Page for resource " + resource + " (" + v.getName() + " : "
                                        + elementValue + ":" + v + "): " + e.getMessage(), false);
                    } catch (Exception e) {
                        LOGGER.warning("Unable to process this page:(" + e.getMessage() + "):" + pageName);
                        assertTrue(
                                pageName + " Page for resource " + resource + " (" + v.getName() + " : "
                                        + elementValue + ":" + v + "): " + e.getMessage(), false);
                    }
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
        resourceId = resource;
        assertEquals(value, StepDefinitionHelper.getActualValue(name));
    }

    /**
     * Verify.
     *
     * @param name the name
     */
    @Then("verify (.*) contains data in the screen on (.*)$")
    public void verify(String name, String resource, Map<String, String> xpathWithValue) {
        resourceId = resource;
        for (Map.Entry<String, String> xpathMaps : xpathWithValue.entrySet()) {
            WebElement webelement = UIDriverManager.getDriver(resource).findElement(By.xpath(xpathMaps.getKey()));
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
    public void actionProcessor(String key, String value, PageElement element, String resource, Map<String, String> dataMap)
            throws Exception {
        resourceId = resource;
        WebElement webelement = UIDriverManager.getDriver(resource).findElement(element.findElement(dataMap));
        Action action = actionProcessorMap.get(element.getAction());
        action.perform(UIDriverManager.getDriver(resource), key, webelement, value, element);
    }

    /**
     * Produce message.
     *
     * @param sleep the sleep
     * @throws InterruptedException the interrupted exception
     */
    @Given("wait ui (.*) for process for (.*) milliseconds$")
    public void pauseUI(String dummy, long sleep) throws InterruptedException {
        Thread.sleep(sleep);
    }


    /**
     * Clean up.
     */

    @Given("close the driver for (.*)$")
    public void cleanUp(String resource) {
        resourceId = resource;
        if (UIDriverManager.isDriverExists(resource) != null) {
            UIDriverManager.getDriver(resource).close();
            UIDriverManager.removeDriver(resource);
        } else {
            LOGGER.warning(" Driver not loaded/Closed : ");
        }
        if (appiumServer != null) {
            appiumServer.stopServer();
        }
    }

    public static java.io.File getAviFile(String dir) {
        java.io.File file = new java.io.File(dir);
        if (file != null && file.exists()) {
            for (java.io.File file1 : file.listFiles()) {
                if (file1.isFile()) {
                    return file1;
                }
            }
        }
        return null;
    }

    @Given("^add variable as (.*) and (.*) as value$")
    public void addVariable(String key, String responseValue) throws Exception {
        io.virtualan.cucumblan.props.util.ScenarioContext
                .setContext(String.valueOf(Thread.currentThread().getId()), key, StepDefinitionHelper.getActualValue(responseValue));
    }


    @Given("^add variable as (.*) and (.*) as function value$")
    public void modifyFunctionVariable(String key, String responseValue) throws Exception {
        io.virtualan.cucumblan.props.util.ScenarioContext
                .setContext(String.valueOf(Thread.currentThread().getId()), key, io.virtualan.cucumblan.script.ExcelAndMathHelper.evaluateWithVariableType(
                        responseValue, io.virtualan.cucumblan.props.util.ScenarioContext
                                .getContext(String.valueOf(Thread.currentThread().getId()))).toString());
    }


}   