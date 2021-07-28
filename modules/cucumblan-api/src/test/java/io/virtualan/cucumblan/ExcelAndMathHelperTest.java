package io.virtualan.cucumblan;

import io.virtualan.cucumblan.script.ExcelAndMathHelper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class ExcelAndMathHelperTest {

  @Test
  public void add() throws IOException {
    Assert.assertEquals(11, ExcelAndMathHelper.evaluate(Integer.class,"SUM(10,1)"));
  }

  @Test
  public void calculate() throws IOException {
    Map<String, String> contextObject = new HashMap<>();
    contextObject.put("I", "629.01");
    contextObject.put("P", "999.20");
    Assert.assertEquals(1628.21, ExcelAndMathHelper.evaluateWithVariables(Double.class,"SUM([P],[I])", contextObject));
  }



  @Test
  public void evaluate() throws IOException {
    Map<String, String> contextObject = new HashMap<>();
    contextObject.put("condition", "100");
    Assert.assertEquals(false,
        ExcelAndMathHelper.evaluateWithVariables(Boolean.class,"[condition]< 100", contextObject));
  }

  @Test
  public void evaluateDate() throws IOException {
    Map<String, String> contextObject = new HashMap<>();
    contextObject.put("cdate", "05/15/2021");
    Assert.assertEquals("15/05/2021",
        ExcelAndMathHelper.evaluateWithVariables(String.class,"TEXT(\"[cdate]\", \"dd/mm/yyyy\")", contextObject));
  }



}
