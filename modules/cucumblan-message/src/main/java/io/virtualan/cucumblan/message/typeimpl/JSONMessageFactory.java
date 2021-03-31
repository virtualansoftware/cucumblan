package io.virtualan.cucumblan.message.typeimpl;

import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.message.type.MessageTypeFactory;
import org.json.JSONObject;

public class JSONMessageFactory implements MessageTypeFactory {
  public JSONMessageFactory(){
  }

  @Override
  public MessageType buildMessage(Object topic, String messageKey, Object value)
      throws MessageNotDefinedException {
    if("pet".equalsIgnoreCase(messageKey)) {
      JSONObject body = (JSONObject) value;
      Integer id = body.getInt("id");
      String type = messageKey;
      return new JSONMessage(id,body,type);
    }
    throw new MessageNotDefinedException(messageKey +" message is not defined");
  }
}
