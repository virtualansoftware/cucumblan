package io.virtualan.cucumblan.props.util;

import io.virtualan.cucumblan.props.ApplicationConfiguration;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UIHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(UIHelper.class.getName());

    public static void missingLog(String key) {
        if (ApplicationConfiguration.getProperty(key) == null) {
            LOGGER.warn(key + " configuration is missing ");
        }
    }

    public static String toCamel(String str)
    {
        // Capitalize first letter of string
        str = str.substring(0, 1).toUpperCase()
                + str.substring(1);

        while (str.contains("_")) {

            str = str
                    .replaceFirst(
                            "_[a-z]",
                            String.valueOf(
                                    Character.toUpperCase(
                                            str.charAt(
                                                    str.indexOf("_") + 1))));
        }

        return str;
    }

    public static String getUrl(String resource) {
        missingLog("service.ui." + resource);
        return ApplicationConfiguration.getProperty("service.ui." + resource);
    }

    public static String getServerUrl(String resource) {
        return ApplicationConfiguration.getProperty("service.ui.server_url." + resource);
    }

    public static long getPageLoadWaitTime(String resource) {
        String value = ApplicationConfiguration.getProperty("service.ui.page_load.timeout." + resource);
        if (value != null) {
            return Long.parseLong(value);
        }
        return 300;
    }

    public static long getWaitTime(String resource) {
        String value = ApplicationConfiguration.getProperty("service.ui.wait_time." + resource);
        if (value != null) {
            return Long.parseLong(value);
        }
        return 30;
    }

    public static void additionalConfigResource(String resource, DesiredCapabilities dc) {
        String readAddConf = ApplicationConfiguration.getProperty("service.ui.additional." + resource);
        if (readAddConf != null) {
            Map<String, String> addconf = Pattern.compile("\\s*;\\s*")
                    .splitAsStream(readAddConf.trim())
                    .map(s -> s.split("=", 2))
                    .collect(Collectors.toMap(a -> a[0], a -> a.length > 1 ? a[1] : ""));
            addconf.entrySet().forEach(x -> dc.setCapability(x.getKey(), x.getValue()));

        }
    }


    public static void additionalConfigArguments(String resource, ChromeOptions options) {
        String readAddConf = ApplicationConfiguration.getProperty("service.ui.arguments." + resource);
        if (readAddConf != null) {
            List<String> addconf = Pattern.compile("\\s*;\\s*")
                    .splitAsStream(readAddConf.trim()).collect(Collectors.toList());
            addconf.forEach(x -> options.addArguments(x));

        }
    }

    public static String getChromeDriverPath() {
        String value = ApplicationConfiguration.getProperty("chrome.driver.path");
        if (value != null) {
            return value;
        }
        return "conf/chromedriver.exe";
    }


    /**
     * Gets actual value.
     *
     * @param object the object
     * @return the actual value
     */
    public static String getUIActualValue(String object, Map<String, String> currentContext) {
        if (object == null) {
            return null;
        }

        String returnValue =  object;
        String key = "";
        if (returnValue.contains("{") && returnValue.contains("}")) {
            key = returnValue.substring(returnValue.indexOf("{") + 1, returnValue.indexOf("}"));
            if (key.contains(",")) {
                StringBuffer keys = new StringBuffer();
                for (String token : key.split(",")) {
                    if (!currentContext.containsKey(token)) {
                        return object;
                    }
                    keys.append(currentContext.get(token)).append(",");
                }
                returnValue = keys.toString().substring(0, keys.toString().length() - 1);
            } else {
                if (!currentContext.containsKey(key)) {
                    LOGGER.warn(object + " has Value missing... for the key : " + key);
                    return object.toString();
                } else {
                    returnValue = currentContext.get(key);
                }
            }
        }
        String response = object.replace("{" + key + "}", returnValue);
        return response.indexOf("{") != -1 ? getUIActualValue(response, currentContext) : response;
    }


    public static String getFireboxDriverPath() {
        String value = ApplicationConfiguration.getProperty("firefox.driver.path");
        if (value != null) {
            return value;
        }
        return "conf/geckodriver.exe";
    }
}
