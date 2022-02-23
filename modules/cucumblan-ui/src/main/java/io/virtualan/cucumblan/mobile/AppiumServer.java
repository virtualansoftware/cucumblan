package io.virtualan.cucumblan.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import io.virtualan.cucumblan.props.util.MobileHelper;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class AppiumServer {
    private final static Logger LOGGER = Logger.getLogger(AppiumServer.class.getName());

    public AppiumDriverLocalService service;
    WebDriver driver;

    public WebDriver startServer(String resource, String platform) throws Exception {
        AppiumServiceBuilder builder = null;
        try {
            if (Platform.ANDROID.name().equalsIgnoreCase(platform)) {
                builder = buildAndroid(resource);
            } else if (Platform.IOS.name().equalsIgnoreCase(platform)) {
                builder = buildIOS(resource);
            }
            service = AppiumDriverLocalService.buildService(builder);
            service.start();
        } catch (Exception e) {
            LOGGER.warning(" Unable to start the app " + e.getMessage());
        }
        if (Platform.ANDROID.name().equalsIgnoreCase(platform)) {
            driver = buildAppiumDriverForAndroid(resource);
        } else if (Platform.IOS.name().equalsIgnoreCase(platform)) {
            driver = buildAppiumDriverForIOS(resource);
        }
        driver.manage().timeouts().implicitlyWait(MobileHelper.getWaitTime(resource), TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(MobileHelper.getPageLoadWaitTime(resource), TimeUnit.SECONDS);
        return driver;
    }

    private AppiumDriver buildAppiumDriverForAndroid(String resource) throws MalformedURLException {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability(MobileCapabilityType.BROWSER_NAME, MobileHelper.getBrowserName(resource));
        dc.setCapability(MobileCapabilityType.PLATFORM_NAME, MobileHelper.getPlatform(resource));
        dc.setCapability(MobileCapabilityType.DEVICE_NAME, MobileHelper.getAppName(resource));
        dc.setCapability("appPackage", MobileHelper.getAppPackage(resource));
        dc.setCapability("appActivity", MobileHelper.getAppActivity(resource));
        MobileHelper.additionalConfigResource(resource, dc);
        if (MobileHelper.getUrl(resource) == null) {
            File root = new File(System.getProperty("user.dir"));
            File app = new File(root, MobileHelper.getFile(resource));
            dc.setCapability("app", app.getAbsolutePath());
            return new AppiumDriver<MobileElement>(service.getUrl(), dc);
        } else {
            return new AppiumDriver<MobileElement>(new URL(MobileHelper.getServerUrl(resource)), dc);
        }
    }

    private AppiumDriver buildAppiumDriverForIOS(String resource) throws MalformedURLException {
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability(MobileCapabilityType.BROWSER_NAME, MobileHelper.getBrowserName(resource));
        dc.setCapability(MobileCapabilityType.PLATFORM_NAME, MobileHelper.getPlatform(resource));
        dc.setCapability(MobileCapabilityType.DEVICE_NAME, MobileHelper.getAppName(resource));
        dc.setCapability("device", MobileHelper.getDevice(resource));
        dc.setCapability("udid", MobileHelper.getUDID(resource));
        dc.setCapability("bundleId", MobileHelper.getBundleId(resource));
        MobileHelper.additionalConfigResource(resource, dc);
        if (MobileHelper.getUrl(resource) == null) {
            File root = new File(System.getProperty("user.dir"));
            File app = new File(root, MobileHelper.getFile(resource));
            dc.setCapability("app", app.getAbsolutePath());
            return new AppiumDriver<MobileElement>(service.getUrl(), dc);
        } else {
            return new AppiumDriver<MobileElement>(new URL(MobileHelper.getServerUrl(resource)), dc);
        }
    }


    private AppiumServiceBuilder buildIOS(String resource) {
        DesiredCapabilities cap = new DesiredCapabilities();
        // Build the Appium Service
        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.usingDriverExecutable(new File(MobileHelper.getNode()));
        builder.withAppiumJS(new File(MobileHelper.getAppium()));
        builder.withIPAddress(MobileHelper.getMobileHost());
        builder.usingPort(MobileHelper.getMobilePort());
        builder.withCapabilities(cap);
        builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "error");
        return builder;
    }

    private AppiumServiceBuilder buildAndroid(String resource) {

        DesiredCapabilities cap = new DesiredCapabilities();
        // Build the Appium Service
        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.usingDriverExecutable(new File(MobileHelper.getNode()));
        builder.withAppiumJS(new File(MobileHelper.getAppium()));
        builder.withIPAddress(MobileHelper.getMobileHost());
        builder.usingPort(MobileHelper.getMobilePort());
        builder.withCapabilities(cap);
        builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
        builder.withArgument(GeneralServerFlag.LOG_LEVEL, "error");
        return builder;
    }


    public void stopServer() {
        service.stop();
    }

    public boolean checkIfServerIsRunnning(int port) {

        boolean isServerRunning = false;
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.close();
        } catch (IOException e) {
            // If control comes here, then it means that the port is in use
            isServerRunning = true;
        } finally {
            serverSocket = null;
        }
        return isServerRunning;
    }
}
