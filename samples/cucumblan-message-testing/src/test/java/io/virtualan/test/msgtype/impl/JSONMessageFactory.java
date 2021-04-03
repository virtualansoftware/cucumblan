package io.virtualan.test.msgtype.impl;

import io.virtualan.cucumblan.core.msg.kafka.MessageContext;
import io.virtualan.cucumblan.message.exception.MessageNotDefinedException;
import io.virtualan.cucumblan.message.type.MessageType;
import io.virtualan.cucumblan.message.type.MessageTypeFactory;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.JSONObject;

/**
 * The type Json message factory.
 */
public class JSONMessageFactory implements MessageTypeFactory<String, String> {

  /**
   * Instantiates a new Json message factory.
   */
  public JSONMessageFactory(){
  }


  @Override
  public MessageType buildMessage(Object recordObj, String key, String body)
    throws MessageNotDefinedException {
    ConsumerRecord<String, String> record = (ConsumerRecord<String, String>)recordObj;
    if ("virtualan-test-event".equalsIgnoreCase(record.topic())) {
      String id = String.valueOf(new JSONObject(body).getInt("id"));
      return new JSONMessage(id, body.toString());
    } else {
      throw new MessageNotDefinedException(key +" message is not defined");
    }
  }

  @Override
  public MessageType buildMessageType(String type, String key, String body)
    throws MessageNotDefinedException {
    MessageType messageType = MessageContext.getMessageTypes().get(type);
    if(messageType instanceof JSONMessage) {
      return new JSONMessage(String.valueOf(new JSONObject(body).getInt("id")), body.toString());
    }
    throw new MessageNotDefinedException(key +" message is not defined");
  }

}
