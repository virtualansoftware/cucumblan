/*
 *
 *
 *    Copyright (c) 2021.  Virtualan Contributors (https://virtualan.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software distributed under the License
 *     is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing permissions and limitations under
 *     the License.
 *
 *
 *
 */
package io.virtualan.cucumblan.core;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.virtualan.csvson.Csvson;
import io.virtualan.cucumblan.jdbc.util.StreamingJsonResultSetExtractor;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.props.util.StepDefinitionHelper;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.BasicDataSource;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * The type JDBC base step definition.
 *
 * @author Elan Thangamani
 */

public class DBBaseStepDefinition {

  private final static Logger LOGGER = Logger.getLogger(DBBaseStepDefinition.class.getName());
  static Map<String, JdbcTemplate> jdbcTemplateMap = new HashMap<String, JdbcTemplate>();

  static {
    loadAllDataSource();
  }

  Scenario scenario;

  private static void loadAllDataSource() {
    try {
      for (String key : ApplicationConfiguration.getProperties().keySet()) {
        if (key.contains(".cucumblan.jdbc.driver-class-name")) {
          String source = key.replaceAll(".cucumblan.jdbc.driver-class-name", "");
          if (!jdbcTemplateMap.containsKey(source)) {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(
                ApplicationConfiguration.getProperty(source + ".cucumblan.jdbc.driver-class-name"));
            dataSource
                .setUsername(
                    ApplicationConfiguration.getProperty(source + ".cucumblan.jdbc.username"));
            dataSource
                .setPassword(
                    ApplicationConfiguration.getProperty(source + ".cucumblan.jdbc.password"));
            dataSource.setUrl(ApplicationConfiguration.getProperty(source + ".cucumblan.jdbc.url"));
            dataSource.setMaxIdle(5);
            dataSource.setInitialSize(5);
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplateMap.put(source, jdbcTemplate);
          }
        }
      }
    } catch (Exception e) {
      LOGGER.severe("Unable to load properties :" + e.getMessage());
    }
  }

  @Before
  public void before(Scenario scenario) {
    this.scenario = scenario;
  }

  @Given("As a sql (.*) user on (.*)$")
  public void startSql(String dummy, String resource) {
    //ignore

  }


  @Given("insert the given sql for (.*) on (.*)$")
  public void insertSql(String dummy, String resource, List<String> sqls) throws Exception {
    JdbcTemplate jdbcTemplate = getJdbcTemplate(resource);
    for (String sql : sqls) {
      try {
        jdbcTemplate.execute(sql);
      } catch (Exception e) {
        LOGGER.warning("Unable to load this sqls " + sql + " : " + e.getMessage());
        scenario.log("Unable to load this sqls " + sql + " : " + e.getMessage());
        Assert.assertTrue("  sqls are not inserted : (" + e.getMessage() + ")", false);
      }
    }
    Assert.assertTrue(" All sqls are inserted successfully ", true);
  }

  private JdbcTemplate getJdbcTemplate(String resource) throws Exception {
    if (jdbcTemplateMap.containsKey(resource)) {
      return jdbcTemplateMap.get(resource);
    } else {
      Assert.assertTrue("Jdbc sources are not defined in configuration for : " + resource, false);
      throw new Exception("Jdbc sources are not defined in configuration : ");
    }
  }

  @Given("update the given sql for (.*) on (.*)$")
  public void updateSql(String dummy, String resource, List<String> sqls) throws Exception {
    JdbcTemplate jdbcTemplate = getJdbcTemplate(resource);
    for (String sql : sqls) {
      try {
        jdbcTemplate.execute(sql);
      } catch (Exception e) {
        LOGGER.warning("Unable to load this sqls " + sql + " : " + e.getMessage());
        scenario.log("Unable to load this sqls " + sql + " : " + e.getMessage());
        Assert.assertTrue("  sqls are not updated : (" + e.getMessage() + ")", false);
      }
    }
    Assert.assertTrue(" All sqls are updated successfully ", true);
  }


  @Given("delete the given sql for (.*) on (.*)$")
  public void deleteSql(String dummy, String resource, List<String> sqls) throws Exception {
    JdbcTemplate jdbcTemplate = getJdbcTemplate(resource);
    for (String sql : sqls) {
      try {
        jdbcTemplate.execute(sql);
      } catch (Exception e) {
        LOGGER.warning("Unable to load this sqls " + sql + " : " + e.getMessage());
        scenario.log("Unable to load this sqls " + sql + " : " + e.getMessage());
        Assert.assertTrue("  sqls are not deleted : (" + e.getMessage() + ")", false);
      }
    }
    Assert.assertTrue(" All sqls are deleted successfully ", true);
  }

  @Given("select the given sql for (.*) on (.*)$")
  public void verify(String dummy, String resource, List<String> selectSql)
      throws Exception {
    JdbcTemplate jdbcTemplate = getJdbcTemplate(resource);
    String json = null;
    if (selectSql.size() >= 1) {
      json = getJson(resource,
          StepDefinitionHelper.getActualValue(selectSql.get(0)).toString());
    } else {
      Assert.assertTrue(" select sqls missing ", false);
    }

    if (selectSql.size() == 1) {
      Assert.assertNull(json);
    } else {
      List<String> csvons = selectSql.subList(1, selectSql.size());
      JSONArray expectedArray = Csvson.buildCSVson(csvons, ScenarioContext.getContext());
      JSONArray actualArray = new JSONArray(json);
      JSONCompareResult result = JSONCompare.compareJSON(actualArray, expectedArray, JSONCompareMode.LENIENT);
      if(result.failed()){
        scenario.log(result.getMessage());
      }
      Assertions.assertTrue(result.passed(), " select sql and cvson record matches");
    }
  }

  private String getJson(String resource, String sql) throws Exception {
    JdbcTemplate jdbcTemplate = getJdbcTemplate(resource);
    OutputStream os = new ByteArrayOutputStream();
    jdbcTemplate.query(sql, new StreamingJsonResultSetExtractor(os));
    return os.toString();
  }

}   