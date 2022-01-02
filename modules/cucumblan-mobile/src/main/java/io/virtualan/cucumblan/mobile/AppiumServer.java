package io.virtualan.cucumblan.mobile;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import io.virtualan.cucumblan.props.util.MobileHelper;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.DesiredCapabilities;

@Slf4j
public class AppiumServer {

  public AppiumDriverLocalService service;
  public AppiumServiceBuilder builder;
  public DesiredCapabilities cap;
  public DesiredCapabilities dc;
  AppiumDriver<MobileElement> driver;

  public AppiumDriver<MobileElement> startServer(String resource, String type) throws Exception {

    File root = new File(System.getProperty("user.dir"));
    File app = new File(root, MobileHelper.getUrl(resource));
    // apk Capabilities
    dc = new DesiredCapabilities();
    dc.setCapability("BROWSER_NAME", "Android");
    dc.setCapability(MobileCapabilityType.PLATFORM_NAME, Platform.ANDROID);
    dc.setCapability("platformName", "Android");
    dc.setCapability("deviceName", MobileHelper.getAppName(resource));
    dc.setCapability("app", app.getAbsolutePath());
    dc.setCapability("appPackage", MobileHelper.getAppPackage(resource));
    dc.setCapability("appActivity",MobileHelper.getAppActivity(resource));

    // Appium Capabilities
    cap = new DesiredCapabilities();

    // Build the Appium Service
    builder = new AppiumServiceBuilder();
    builder.usingDriverExecutable(new File(MobileHelper.getNode()));
    builder.withAppiumJS(new File(MobileHelper.getAppium()));
    builder.withIPAddress(MobileHelper.getMobileHost());
    builder.usingPort(MobileHelper.getMobilePort());
    builder.withCapabilities(cap);
    builder.withArgument(GeneralServerFlag.SESSION_OVERRIDE);
    builder.withArgument(GeneralServerFlag.LOG_LEVEL, "error");

    // Start the server with the builder
    try {
      service = AppiumDriverLocalService.buildService(builder);
      service.start();
    } catch (Exception e) {
      log.warn(" Unable to start the app " + e.getMessage());
    }
    driver = new AppiumDriver<MobileElement>(service.getUrl(), dc);
    driver.manage().timeouts().implicitlyWait(MobileHelper.getWaitTime(resource), TimeUnit.SECONDS);
    return driver;
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
