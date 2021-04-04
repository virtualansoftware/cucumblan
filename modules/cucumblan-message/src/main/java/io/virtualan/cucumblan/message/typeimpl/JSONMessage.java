package io.virtualan.cucumblan.message.typeimpl;

import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.mapson.Mapson;
import io.virtualan.mapson.exception.BadInputDataException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.kafka.common.header.Header;
import org.json.JSONObject;

/**
 * The type Json message.
 */
public class JSONMessage implements MessageType<Integer, String> {

  private String type = "JSON";
  private Integer id;
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
  public JSONMessage(Integer id, String body) {
    this.body = body;
    this.id = id;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public List<Header> getHeaders() {
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
  public MessageType build(Object messages) throws  MessageNotDefinedException {
    if (messages instanceof List) {
      String message = ((List<String>) messages).stream().collect(Collectors.joining());
      JSONObject body = new JSONObject(message);
      return new JSONMessage(body.getInt("id"), message);
    } else {
      String message = null;
      try {
        message = Mapson.buildMAPsonAsJson((Map<String, String>) messages);
        JSONObject body = new JSONObject(message);
        return new JSONMessage(body.getInt("id"), message);
      } catch (BadInputDataException e) {
        throw new MessageNotDefinedException(e.getMessage());
      }
    }
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
