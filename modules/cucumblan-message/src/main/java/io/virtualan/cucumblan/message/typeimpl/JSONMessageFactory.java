package io.virtualan.cucumblan.message.typeimpl;

import io.virtualan.cucumblan.core.msg.kafka.MessageContext;
import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.message.type.MessageTypeFactory;
import org.json.JSONObject;

public class JSONMessageFactory implements MessageTypeFactory<String, JSONObject> {

  public JSONMessageFactory(){
  }


  @Override
  public MessageType buildMessage(Object record, String key, JSONObject body)
    throws MessageNotDefinedException {
    if("pet".equalsIgnoreCase(key)) {
      Integer id = body.getInt("id");
      return new JSONMessage(id, body);
    }
    throw new MessageNotDefinedException(key +" message is not defined");
  }

  @Override
  public MessageType buildMessageType(String type, String key, JSONObject body)
    throws MessageNotDefinedException {
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    if(messageType instanceof JSONMessage) {
      return new JSONMessage(body.getInt("id"), body);
    }
    throw new MessageNotDefinedException(key +" message is not defined");
  }

}
