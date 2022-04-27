package io.virtualan.cucumblan.props.util;


import io.virtualan.cucumblan.props.ApplicationConfiguration;

import java.util.Arrays;
import java.util.Properties;

public class UtilHelper {

    public static String getObject(String value) {
        String[] arrayValue = value.split("~");
        if (arrayValue.length == 2) {
            return arrayValue[1];
        } else {
            return value;
        }
    }

    /**
     * Gets actual resource.
     *
     * @param contentType      the system
     * @return the actual resource
     */
    public static boolean isBinaryFile(String contentType) throws Exception {
        boolean isBinaryContent = false;
        String value =ApplicationConfiguration.getProperty("scenario.attachment");
        if (value != null) {
            String[] values = value.split(",");
            isBinaryContent  = Arrays.stream(values).anyMatch(x  -> contentType.contains(x));

        }
        return isBinaryContent;
    }

}
