package io.virtualan.cucumblan.props.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.Scenario;
import io.restassured.response.ValidatableResponse;
import io.virtualan.csvson.Csvson;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.ExcludeConfiguration;
import io.virtualan.jassert.VirtualJSONAssert;
import io.virtualan.mapson.Mapson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * The type Helper util.
 */
@Slf4j
public class HelperApiUtil {


  private static String convertStreamToString(InputStream is) throws IOException {
    if (is != null) {
      StringBuilder sb = new StringBuilder();
      String line;

      try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
      } finally {
        is.close();
      }
      return sb.toString();
    } else {
      return null;
    }
  }

  /**
   * Read file as string string.
   *
   * @param fileBody the file body
   * @return the string
   */
  public static String readFileAsString(String fileBody) {
    String body = null;
    InputStream stream = Thread.currentThread().getContextClassLoader()
        .getResourceAsStream(fileBody);
    if (stream == null) {
      stream = ApplicationConfiguration.class.getClassLoader().getResourceAsStream(fileBody);
    }
    if (stream != null) {
      try {
        body = convertStreamToString(stream);
      } catch (IOException e) {
      }
    }
    return body;
  }

  private static Document getDocument(String xmlData) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    DocumentBuilder builder = factory.newDocumentBuilder();
    InputSource inputSource = new InputSource(new StringReader(xmlData));
    Document doc = builder.parse(inputSource);
    return doc;
  }

  /**
   * Assert xpaths equal.
   *
   * @param xpaths      the xpaths
   * @param expectedXML the expected xml
   * @param actualXML   the actual xml
   * @throws Exception the exception
   */
  public static void assertXpathsEqual(List<String> xpaths, String expectedXML, String actualXML)
      throws Exception {
    Document expectedDoc = getDocument(expectedXML);
    Document actualDoc = getDocument(actualXML);
    for (String xpathStr : xpaths) {
      XpathEngine xpath = XMLUnit.newXpathEngine();
      Assert.assertEquals(xpathStr, xpath.evaluate(xpathStr, expectedDoc), xpath.evaluate(xpathStr, actualDoc));
    }
  }

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

  /**
   * Assert json object.
   *
   * @param resource            the resource
   * @param jsonRequestExpected the json request expected
   * @param jsonRequestActual   the json request actual
   */
  public static  void assertJSONObject(String resource, String jsonRequestExpected, String jsonRequestActual){
    if (jsonRequestExpected != null && jsonRequestActual != null) {
      Map<String, String> mapson = Mapson.buildMAPsonFromJson(jsonRequestExpected);
      Map<String, String> mapsonExpected = Mapson.buildMAPsonFromJson(jsonRequestActual);
      mapsonExpected.forEach((k, v) -> {
        if (!ExcludeConfiguration.shouldSkip(resource, (String) k)) {
          if (v == null) {
            if (mapson.get(k) == null) {
              assertNull(mapson.get(k));
            } else {
              assertEquals(" ", mapson.get(k));
            }
          } else {
            assertEquals("Key: " + k + "  Expected : " + v + " ==> Actual " + mapson.get(k),
                v, mapson.get(k));
          }
        }
      });
    } else {
      assertTrue("JSON missing in the expected or actual  ", false);
    }
  }

  /**
   * Assert jsonpath equal.
   *
   * @param jsonPath     the json path
   * @param expectedjson the expectedjson
   * @param actualjson   the actualjson
   */
  public static void assertJsonpathEqual(List<String> jsonPath, String expectedjson, String actualjson) {
    for (String jpath : jsonPath) {
     Object expected = getJSON(expectedjson, jpath);
     Object actual = getJSON(actualjson, jpath);
     if (expected instanceof  JSONObject){
       Assert.assertTrue(jpath, VirtualJSONAssert.jAssertObject((JSONObject) expected, (JSONObject) actual, JSONCompareMode.LENIENT));
     } else if (expected instanceof  JSONArray){
       Assert.assertTrue(jpath, VirtualJSONAssert.jAssertArray((JSONArray) expected, (JSONArray) actual, JSONCompareMode.LENIENT));
     } else {
       Assert.assertEquals(jpath, expected.toString(), actual.toString());
     }
    }
  }

  /**
   * Assert xml equals.
   *
   * @param expectedXML the expected xml
   * @param actualXML   the actual xml
   * @throws Exception the exception
   */
  public static void assertXMLEquals(String expectedXML, String actualXML) throws Exception {
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setIgnoreAttributeOrder(true);
    DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedXML, actualXML));
    List<?> allDifferences = diff.getAllDifferences();
    Assert.assertEquals("Differences found: " + diff.toString(), 0, allDifferences.size());
  }


  /**
   * Verify csvson.
   *
   * @param validatableResponse the validatable response
   * @param path                the path
   * @param csvson              the csvson
   * @param mode                the mode
   * @param scenario            the scenario
   * @throws Exception the exception
   */
  public static void verifyCSVSON(ValidatableResponse validatableResponse, String path,
      List<String> csvson, JSONCompareMode mode, Scenario scenario)
      throws Exception {
    JSONArray expectedArray = Csvson.buildCSVson(csvson, ScenarioContext
        .getContext(String.valueOf(Thread.currentThread().getId())));
    Object objJson = StepDefinitionHelper.getJSON(validatableResponse.extract().body().asString());
    JSONCompareResult result = null;
    scenario.attach(expectedArray.toString(), "application/json", "Expected json");
    JSONArray notFoundArray = null;
    if (objJson instanceof JSONArray) {
      JSONArray actualArray = new JSONArray(validatableResponse.extract().body().asString());
      scenario.attach(actualArray.toString(), "application/json", "Actual json");
      if(expectedArray.length() == actualArray.length()) {
        result = JSONCompare.compareJSON(expectedArray, actualArray, mode);
      } else {
        notFoundArray = getUnmatchedElement(mode, expectedArray, actualArray);
      }
    } else if (objJson instanceof JSONObject) {
      JSONObject actualArray = new JSONObject(validatableResponse.extract().body().asString());
      if (actualArray.optJSONArray(path) != null && actualArray.optJSONArray(path).length() > 0) {
        if(expectedArray.length() == actualArray.getJSONArray(path).length()) {
          result = JSONCompare.compareJSON(expectedArray, actualArray.getJSONArray(path), mode);
        } else {
          notFoundArray = getUnmatchedElement(mode, expectedArray, actualArray.getJSONArray(path));
        }
      } else {
        result = JSONCompare.compareJSON(expectedArray.getJSONObject(0), actualArray, mode);
      }
      scenario.attach(actualArray.toString(), "application/json", "Actual json");
    }
    if (result == null && notFoundArray == null) {
      Assert.assertTrue("Actual input is not a valid JSON Object", false);
    } else if (result != null && result.failed()) {
      scenario.attach(result.getMessage(), "text/plain", "Unmatched csvson");
      assertTrue("Csvson record does not match", result.passed());
    } else if (notFoundArray != null && notFoundArray.length() > 0) {
      scenario.attach(notFoundArray.toString(2), "application/json", "Unmatched csvson");
      assertTrue("Csvson record does not match and see unmatched records", false);
    } else {
      assertTrue("Csvson record matches", true);
    }
  }

  private static boolean isMatchNotFound(JSONObject actual, JSONArray expectedArray, JSONCompareMode mode){
    for (int j = 0; j < expectedArray.length(); j++) {
      JSONCompareResult result = JSONCompare.compareJSON(actual,expectedArray.getJSONObject(j), mode);
      if(result.passed()){
        return false;
      }
    }
    return true;
  }

  private static JSONArray getUnmatchedElement(JSONCompareMode mode, JSONArray expectedArray,
      JSONArray actualArray) {
    JSONArray notFoundArray = new JSONArray();
    for (int i = 0; i < expectedArray.length(); i++) {
      if(isMatchNotFound(expectedArray.getJSONObject(i), actualArray, mode)){
        notFoundArray.put(expectedArray.getJSONObject(i));
      }
    }
    return notFoundArray;
  }
}
