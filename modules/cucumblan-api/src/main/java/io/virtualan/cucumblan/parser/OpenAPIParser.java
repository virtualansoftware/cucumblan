package io.virtualan.cucumblan.parser;

import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.parser.Swagger20Parser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.virtualan.cucumblan.props.ApplicationConfiguration;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

public class OpenAPIParser {

  public static void loader(){
    Map<String,String> properties = ApplicationConfiguration.getProperties();
      for(Map.Entry<String, String> property : properties.entrySet()) {
        if (property.getKey().indexOf(".api.spec.") != -1) {
          init(property.getKey().substring(property.getKey().indexOf(".api.spec.") + 10),
              property.getValue());
        }
      }
  }

  @lombok.SneakyThrows
  public static void init(String system, String url) {
    if(!new File("conf/endpoint."+system+".properties").exists()) {

      OpenAPI openAPI = new OpenAPIV3Parser().read(url);
      if(openAPI == null) {
        Swagger swagger = new Swagger20Parser().read(url,null);
        Map<String, Path> path2s = swagger.getPaths();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("conf/endpoint." + system + ".properties"), "utf-8"))) {
          for (Map.Entry<String, Path> mapPath : path2s.entrySet()) {
            writer.write(
                mapPath.getKey().substring(1).replaceAll("/", "_").replace("-", "_").replace("{", "")
                    .replace("}", "")
                    + "=" + mapPath.getKey());
            writer.write("\n");
          }
        }
      } else {
        Paths paths = openAPI.getPaths();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("conf/endpoint." + system + ".properties"), "utf-8"))) {
          for (Map.Entry<String, PathItem> mapPath : paths.entrySet()) {
            writer.write(
                mapPath.getKey().substring(1).replaceAll("/", "_").replace("-", "_").replace("{", "")
                    .replace("}", "")
                    + "=" + mapPath.getKey());
            writer.write("\n");
          }
        }
      }
    }
  }

  public static void main(String[] args) {
  }
}
