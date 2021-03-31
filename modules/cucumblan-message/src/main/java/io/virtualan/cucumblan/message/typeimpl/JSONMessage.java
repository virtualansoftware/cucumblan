package io.virtualan.cucumblan.message.typeimpl;

import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
import org.json.JSONObject;

public class JSONMessage implements MessageType {
  public JSONMessage(){
  }
  public JSONMessage(Integer id, JSONObject body, String type) {
    this.type = type;
    this.body = body;
    this.id = id;
  }
  private String type;
  private Integer id;
  private JSONObject body;

  @Override
  public String getType() {
    return type;
  }


  @Override
  public Integer getId() {
    return id;
  }

  @Override
  public JSONObject getMessage() {
    return body;
  }


  @Override
  public MessageType buildMessage(Object topic, String messageKey, Object value)
      throws MessageNotDefinedException {
    return null;
  }
}
