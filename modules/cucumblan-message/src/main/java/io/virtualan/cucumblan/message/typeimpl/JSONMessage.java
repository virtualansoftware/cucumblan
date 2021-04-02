package io.virtualan.cucumblan.message.typeimpl;

import io.virtualan.cucumblan.message.type.MessageType;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.kafka.common.header.Headers;
import org.json.JSONObject;

public class JSONMessage implements MessageType<Integer, String> {

  private String type = "JSON";
  private Integer id;
  private String body;

  public JSONMessage() {
  }

  public JSONMessage(Integer id, String body) {
    this.body = body;
    this.id = id;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public Headers getHeaders() {
    return null;
  }

  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public String getMessage() {
    return body;
  }

  @Override
  public JSONObject getMessageAsJson() {
    return new JSONObject(body);
  }

  @Override
  public MessageType build(Object messages) {
    String message  =((List<String>)messages).stream().collect(Collectors.joining());
    JSONObject body = new JSONObject(message);
    return new JSONMessage(body.getInt("id"), message);
  }

  @Override
  public String toString() {
    return "JSONMessage{" +
        "type='" + type + '\'' +
        ", id=" + id +
        ", body=" + body +
        '}';
  }
}
