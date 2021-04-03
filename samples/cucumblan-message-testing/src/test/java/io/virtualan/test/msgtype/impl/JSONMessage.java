package io.virtualan.test.msgtype.impl;

import io.virtualan.cucumblan.message.type.MessageType;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.kafka.common.header.Headers;
import org.json.JSONObject;

/**
 * The type Json message.
 */
public class JSONMessage implements MessageType<String, String> {

  private String type = "JSON";
  private String id;
  private String body;

  /**
   * Instantiates a new Json message.
   */
  public JSONMessage() {
  }

  /**
   * Instantiates a new Json message.
   *
   * @param id   the id
   * @param body the body
   */
  public JSONMessage(String id, String body) {
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
  public String getId() {
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
    return new JSONMessage(String.valueOf(body.getInt("id")), message);
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
