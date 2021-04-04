package io.virtualan.cucumblan.jdbc.util;
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

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * Streams a ResultSet as JSON.
 */
public class StreamingJsonResultSetExtractor implements ResultSetExtractor<Void> {
 
  private final OutputStream os;

  /**
   * Instantiates a new Streaming json result set extractor.
   *
   * @param os the OutputStream to stream the JSON to
   */
  public StreamingJsonResultSetExtractor(final OutputStream os) {
    this.os = os;
  }
 
  @Override
  public Void extractData(final ResultSet rs) {
    final ObjectMapper objectMapper = new ObjectMapper();
    try (JsonGenerator jg = objectMapper.getFactory().createGenerator(
                  os, JsonEncoding.UTF8)) {
      writeResultSetToJson(rs, jg);
      jg.flush();
    } catch (IOException | SQLException e) {
      throw new RuntimeException(e);
    }
    return null;
  }
 
  private static void writeResultSetToJson(final ResultSet rs,
                            final JsonGenerator jg)
                            throws SQLException, IOException {
    final ResultSetMetaData rsmd = rs.getMetaData();
    final int columnCount = rsmd.getColumnCount();
    jg.writeStartArray();
    while (rs.next()) {
      jg.writeStartObject();
      for (int i = 1; i <= columnCount; i++) {
        jg.writeObjectField(rsmd.getColumnName(i), rs.getObject(i));
      }
      jg.writeEndObject();
    }
    jg.writeEndArray();
  }
}