package io.virtualan.cucumblan.core;

public class UIDriverManager {

    static java.util.Map<String, org.openqa.selenium.WebDriver> webDrivers = new java.util.HashMap();

    public static java.util.Map<String, org.openqa.selenium.WebDriver> getWebDrivers() {
        return webDrivers;
    }


    public static org.openqa.selenium.WebDriver getDriver(String resource) {
        return webDrivers.get(resource);
    }

    public static void addDriver(String resource, org.openqa.selenium.WebDriver webDriver) {
        webDrivers.put(resource, webDriver);
    }

    public static  void removeDriver(String resource) {
        webDrivers.remove(resource);
    }

    public static Object isDriverExists(String resource) {
        return webDrivers.containsKey(resource);
    }
}
