package io.virtualan.cucumblan.props.util;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.jassert.VirtualJSONAssert;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

@Slf4j
public class HelperUtil {


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

  public static void assertXpathsEqual(List<String> xpaths, String expectedXML, String actualXML)
      throws Exception {
    Document expectedDoc = getDocument(expectedXML);
    Document actualDoc = getDocument(actualXML);
    for (String xpath : xpaths) {
      XMLAssert.assertXpathValuesEqual(xpath, expectedDoc, xpath, actualDoc);
    }
  }

  private static Object getJSON(String jsonString, String path){
    DocumentContext docCtx = JsonPath.parse(jsonString);
    JsonPath jsonPath = JsonPath.compile(path);
    Object value =docCtx.read(jsonPath);
    return value;
  }

  public static void assertJsonpathEqual(List<String> jsonPath, String expectedjson, String actualjson) {
    for (String jpath : jsonPath) {
     Object expected = getJSON(expectedjson, jpath);
     Object actual = getJSON(actualjson, jpath);
     if (expected instanceof  JSONObject){
       Assert.assertTrue(VirtualJSONAssert.jAssertObject((JSONObject) expected, (JSONObject) actual, JSONCompareMode.LENIENT));
     } else if (expected instanceof  JSONArray){
       Assert.assertTrue(VirtualJSONAssert.jAssertArray((JSONArray) expected, (JSONArray) actual, JSONCompareMode.LENIENT));
     } else {
       Assert.assertEquals(expected.toString(), actual.toString());
     }
    }
  }

  public static void assertXMLEquals(String expectedXML, String actualXML) throws Exception {
    XMLUnit.setIgnoreWhitespace(true);
    XMLUnit.setIgnoreAttributeOrder(true);
    DetailedDiff diff = new DetailedDiff(XMLUnit.compareXML(expectedXML, actualXML));
    List<?> allDifferences = diff.getAllDifferences();
    Assert.assertEquals("Differences found: " + diff.toString(), 0, allDifferences.size());
  }
}
