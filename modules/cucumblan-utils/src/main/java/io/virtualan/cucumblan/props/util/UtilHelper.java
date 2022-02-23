package io.virtualan.cucumblan.props.util;


public class UtilHelper {

    public static String getObject(String value) {
        String[] arrayValue = value.split("~");
        if (arrayValue.length == 2) {
            return arrayValue[1];
        } else {
            return value;
        }
    }
}
