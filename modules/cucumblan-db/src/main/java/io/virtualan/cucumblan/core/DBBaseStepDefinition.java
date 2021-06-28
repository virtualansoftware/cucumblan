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


/**
 * The type JDBC base step definition.
 *
 * @author Elan Thangamani
 */
public class DBBaseStepDefinition {

  private final static Logger LOGGER = Logger.getLogger(DBBaseStepDefinition.class.getName());
  /**
   * The Jdbc template map.
   */
  static Map<String, JdbcTemplate> jdbcTemplateMap = new HashMap<String, JdbcTemplate>();


  /**
   * The Scenario.
   */
  Scenario scenario;

  public static void loadAllDataSource() {
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

  /**
   * Before.
   *
   * @param scenario the scenario
   */
  @Before
  public void before(Scenario scenario) {
    this.scenario = scenario;
    if(jdbcTemplateMap.isEmpty()){
      loadAllDataSource();
    }
  }

  /**
   * given sql.
   *
   * @param dummy    the dummy
   * @throws Exception the exception
   */
  @Given("As a user perform sql (.*) action$")
  public void dummyGiven(String dummy) throws Exception {
  }

    /**
     * Insert sql.
     *
     * @param dummy    the dummy
     * @param resource the resource
     * @param sqls     the sqls
     * @throws Exception the exception
     */
  @Given("Execute DDL for the given sql (.*) on (.*)$")
  @Given("Execute UPDATE for the given sql (.*) on (.*)$")
  @Given("Execute DELETE for the given sql (.*) on (.*)$")
  @Given("Execute INSERT for the given sql (.*) on (.*)$")
  public void insertSql(String dummy, String resource, List<String> sqls) throws Exception {
    JdbcTemplate jdbcTemplate = getJdbcTemplate(resource);
    for (String sql : sqls) {
      try {
        jdbcTemplate.execute(StepDefinitionHelper.getActualValue(sql).toString());
      } catch (Exception e) {
        LOGGER.warning("Unable to load " + dummy +" this sqls " + sql + " : " + e.getMessage());
        scenario.log("Unable to load " + dummy +" this sqls " + sql + " : " + e.getMessage());
        Assert.assertTrue(dummy+"  sqls are not inserted : (" + e.getMessage() + ")", false);
      }
    }
    Assert.assertTrue("All sqls are executed successfully", true);
  }

  private JdbcTemplate getJdbcTemplate(String resource) throws Exception {
    if (jdbcTemplateMap.containsKey(resource)) {
      return jdbcTemplateMap.get(resource);
    } else {
      Assert.assertTrue("Jdbc sources are not defined in configuration for : " + resource, false);
      throw new Exception("Jdbc sources are not defined in configuration : ");
    }
  }


  /**
   * Verify.
   *
   * @param dummy1    the dummy 1
   * @param dummy     the dummy
   * @param resource  the resource
   * @param selectSql the select sql
   * @throws Exception the exception
   */
  @Given("Verify (.*) with the given sql (.*) on (.*)$")
  public void verify(String dummy1, String dummy, String resource, List<String> selectSql)
      throws Exception {
    JdbcTemplate jdbcTemplate = getJdbcTemplate(resource);
    String json = null;
    if (selectSql.size() >= 1) {
      try {
        json = getJson(resource,
            StepDefinitionHelper.getActualValue(selectSql.get(0)).toString());
      }catch (Exception e){
        Assert.assertTrue(" Invalid sqls?? " + e.getMessage(), false);
      }
    } else {
      Assert.assertTrue(" select sqls missing ", false);
    }
    scenario.attach(json, "application/json", "ActualSqlResponse");
    if (selectSql.size() == 1) {
      Assert.assertNull(json);
    } else {
      List<String> csvons = selectSql.subList(1, selectSql.size());
      JSONArray expectedArray = Csvson.buildCSVson(csvons, ScenarioContext.getContext(String.valueOf(Thread.currentThread().getId())));
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