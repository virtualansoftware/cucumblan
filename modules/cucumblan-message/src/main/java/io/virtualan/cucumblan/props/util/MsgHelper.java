package io.virtualan.cucumblan.props.util;


import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

/**
 * The type Msg helper.
 */
public class MsgHelper {

  /**
   * Get json object.
   *
   * @param jsonString the json string
   * @param path       the path
   * @return the object
   */
  public static Object getJSON(String jsonString, String path){
    DocumentContext docCtx = JsonPath.parse(jsonString);
    JsonPath jsonPath = JsonPath.compile(path);
    Object value =docCtx.read(jsonPath);
    return value;
  }

}
