package io.virtualan.cucumblan.props.util;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

public class MsgHelper {

  public static Object getJSON(String jsonString, String path){
    DocumentContext docCtx = JsonPath.parse(jsonString);
    JsonPath jsonPath = JsonPath.compile(path);
    Object value =docCtx.read(jsonPath);
    return value;
  }

}
