package io.virtualan.cucumblan.message.typeimpl;


import java.util.Map.Entry;

public class ProtoBuffMessageType implements
    io.virtualan.cucumblan.message.type.MessageType<String, byte[]> {

  private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
      .getLogger(io.virtualan.cucumblan.message.typeimpl.ProtoBuffMessageType.class.getName());
  private static final com.google.protobuf.util.JsonFormat.Parser jsonParser = com.google.protobuf.util.JsonFormat
      .parser().ignoringUnknownFields();
  private static java.util.Properties protoMessageTypeMapper = new java.util.Properties();

  static {
    reload();
  }

  private String type = "ProtoBuffMessageType";
  private String id;
  private String body;
  private byte[] originalBody;

  public ProtoBuffMessageType() {
  }

  public ProtoBuffMessageType(String id, String body, byte[] originalBody) {
    this.body = body;
    this.originalBody = originalBody;
    this.id = id;
  }

  public static void reload() {
    try {
      java.io.InputStream stream = Thread.currentThread().getContextClassLoader()
          .getResourceAsStream("proto-messagetype.properties");
      if (stream == null) {
        stream = io.virtualan.cucumblan.props.ApplicationConfiguration.class.getClassLoader()
            .getResourceAsStream("proto-messagetype.properties");
      }
      if (stream != null) {
        protoMessageTypeMapper.load(stream);
      } else {
        LOGGER.warning("unable to load proto-messagetype.properties");
      }
    } catch (Exception var1) {
      LOGGER.warning("proto-messagetype.properties not found");
    }

  }

  public String getType() {
    return this.type;
  }

  public java.util.List<org.apache.kafka.common.header.Header> getHeaders() {
    return null;
  }

  //Mandatory
  public String getId() {
    return this.id;
  }

  public String getKey() {
    return this.id;
  }

  public byte[] getMessage() {
    return this.originalBody;
  }

  //Mandatory
  public org.json.JSONObject getMessageAsJson() {
    return new org.json.JSONObject(this.body);
  }

  public io.virtualan.cucumblan.message.type.MessageType buildProducerMessage(Object messages,
      java.util.Map<String, Object> contextParam)
      throws io.virtualan.cucumblan.message.exception.MessageNotDefinedException {
    String message;
    try {
      org.json.JSONObject body;
      if (messages instanceof java.util.List) {
        message = (String) ((java.util.List) messages).stream()
            .collect(java.util.stream.Collectors.joining());
        body = new org.json.JSONObject(message);
        return null;//buildMessageType(body.toString(), contextParam);
      } else {

        message = io.virtualan.mapson.Mapson.buildMAPsonAsJson((java.util.Map) messages);
        body = new org.json.JSONObject(message);
        return null; //buildMessageType(body.toString(), contextParam);
      }
    } catch (io.virtualan.mapson.exception.BadInputDataException exception) {
      throw new io.virtualan.cucumblan.message.exception.MessageNotDefinedException(
          exception.getMessage());
    }
  }

  //Mandatory
  public io.virtualan.cucumblan.message.type.MessageType buildConsumerMessage(
      org.apache.kafka.clients.consumer.ConsumerRecord<String, byte[]> record,
      java.util.Map<String, Object> contextParam)
      throws io.virtualan.cucumblan.message.exception.SkipMessageException {
    return deserialize(record.value(), contextParam);
  }

  public String toString() {
    return "ProtoBuffMessageType{type='" + this.type + '\'' + ", id=" + this.id + ", body="
        + this.body + '}';
  }


    public io.virtualan.cucumblan.message.typeimpl.ProtoBuffMessageType serialize(String jsonbody,
        java.util.Map<String, Object> contextParam)
        throws io.virtualan.cucumblan.message.exception.SkipMessageException {
        if (protoMessageTypeMapper != null && !protoMessageTypeMapper.isEmpty()) {
            for (Entry protoMessageTypeEntry : protoMessageTypeMapper.entrySet()) {
                try {
                    if (contextParam.get("EVENT_NAME") != null
                        && protoMessageTypeEntry.getKey().toString()
                        .equalsIgnoreCase(contextParam.get("EVENT_NAME").toString())) {
                        byte[] body = serialize(protoMessageTypeEntry.getValue().toString(),
                            jsonbody);
                        if (body != null) {
                            String identifier = com.jayway.jsonpath.JsonPath.read(jsonbody,
                                protoMessageTypeEntry.getValue().toString());
                            if (identifier != null) {
                                return new io.virtualan.cucumblan.message.typeimpl.ProtoBuffMessageType(
                                    com.jayway.jsonpath.JsonPath
                                        .read(body, protoMessageTypeEntry.getValue().toString()),
                                    jsonbody, body);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warning("Unable to process message :" + e.getMessage());
                }
            }
        }
        throw new io.virtualan.cucumblan.message.exception.SkipMessageException(
            "Unable to build the message");
    }

  public io.virtualan.cucumblan.message.typeimpl.ProtoBuffMessageType deserialize(byte[] body,
      java.util.Map<String, Object> contextParam)
      throws io.virtualan.cucumblan.message.exception.SkipMessageException {
    if (protoMessageTypeMapper != null && !protoMessageTypeMapper.isEmpty()) {

      for (Entry protoMessageTypeEntry : protoMessageTypeMapper.entrySet()) {
        try {
          if (contextParam.get("EVENT_NAME") != null
              && protoMessageTypeEntry.getKey().toString()
              .equalsIgnoreCase(contextParam.get("EVENT_NAME").toString())) {
            String bodyJson = deserialize(
                protoMessageTypeEntry.getValue().toString(), body);
            if (bodyJson != null) {
              String identifier = com.jayway.jsonpath.JsonPath.read(bodyJson,
                  protoMessageTypeEntry.getValue().toString());
              if (identifier != null) {
                return new io.virtualan.cucumblan.message.typeimpl.ProtoBuffMessageType(
                    com.jayway.jsonpath.JsonPath
                        .read(body, protoMessageTypeEntry.getValue().toString()),
                    bodyJson, body);
              }
            }
          }
        } catch (Exception e) {
            LOGGER.warning("Unable to process message :" + e.getMessage());
        }
      }
    }
    throw new io.virtualan.cucumblan.message.exception.SkipMessageException(
        "Unable to find the message");
  }

  public byte[] serialize(String classname, String messages)
      throws ClassNotFoundException, NoSuchMethodException, java.lang.reflect.InvocationTargetException, IllegalAccessException, java.io.IOException {
    Class clazz = Class.forName(classname);
    java.lang.reflect.Method builderGetter = clazz.getDeclaredMethod("newBuilder");
    com.google.protobuf.GeneratedMessageV3.Builder builder = (com.google.protobuf.GeneratedMessageV3.Builder) builderGetter
        .invoke(null);
    jsonParser.merge(new java.io.StringReader(messages), builder);
    return builder.build().toByteArray();
  }

  public String deserialize(String classname, byte[] payload)
      throws com.google.protobuf.InvalidProtocolBufferException {
    try {
      com.google.gson.Gson g = new com.google.gson.Gson();
      Class clazz = Class.forName(classname);
      java.lang.reflect.Method builderGetter = clazz.getDeclaredMethod("newBuilder");
      com.google.protobuf.GeneratedMessageV3.Builder builder = (com.google.protobuf.GeneratedMessageV3.Builder) builderGetter
          .invoke(null);
      jsonParser
          .merge(new java.io.InputStreamReader(new java.io.ByteArrayInputStream(payload)), builder);
      String body = g.toJson(builder);
      return body;
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e;
    } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
      throw new RuntimeException("Error parsing JSON message", e);
    } catch (java.io.IOException | ClassNotFoundException e) {
      throw new RuntimeException("Error creating read stream for JSON message", e);
    }
  }
}
