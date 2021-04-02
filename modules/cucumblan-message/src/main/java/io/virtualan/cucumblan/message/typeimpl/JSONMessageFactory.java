package io.virtualan.cucumblan.message.typeimpl;

import io.virtualan.cucumblan.core.msg.kafka.MessageContext;
import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.message.type.MessageTypeFactory;
import org.json.JSONObject;

/**
 * The type Json message factory.
 */
public class JSONMessageFactory implements MessageTypeFactory<Integer, String> {

  /**
   * Instantiates a new Json message factory.
   */
  public JSONMessageFactory(){
  }


  @Override
  public MessageType buildMessage(Object record, Integer key, String body)
    throws MessageNotDefinedException {
    Integer id = new JSONObject(body).getInt("id");
    return new JSONMessage(id, body.toString());
   //throw new MessageNotDefinedException(key +" message is not defined");
  }

  @Override
  public MessageType buildMessageType(String type, Integer key, String body)
    throws MessageNotDefinedException {
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    if(messageType instanceof JSONMessage) {
      return new JSONMessage(new JSONObject(body).getInt("id"), body.toString());
    }
    throw new MessageNotDefinedException(key +" message is not defined");
  }

}
