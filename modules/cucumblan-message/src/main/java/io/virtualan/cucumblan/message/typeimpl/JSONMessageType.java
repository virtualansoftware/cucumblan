package io.virtualan.cucumblan.message.typeimpl;

public class JSONMessageType implements io.virtualan.cucumblan.message.type.MessageType<String, String> {
    private String type = "JSONMessageType";
    private String id;
    private String body;

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(JSONMessageType.class.getName());

    private static java.util.Properties jsonMessageTypeMapper = new java.util.Properties();

    static {
        reload();
    }

    public static void reload() {
        try {
            java.io.InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("json-messagetype.properties");
            if (stream == null) {
                stream = io.virtualan.cucumblan.props.ApplicationConfiguration.class.getClassLoader().getResourceAsStream("json-messagetype.properties");
            }
            if (stream != null) {
                jsonMessageTypeMapper.load(stream);
            } else {
                LOGGER.warning("unable to load json-messagetype.properties");
            }
        } catch (Exception var1) {
            LOGGER.warning("json-messagetype.properties not found");
        }

    }

    public JSONMessageType() {
    }

    public JSONMessageType(String id, String body) {
        this.body = body;
        this.id = id;
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

    public String getMessage() {
        return this.body;
    }

    //Mandatory
    public org.json.JSONObject getMessageAsJson() {
        return new org.json.JSONObject(this.body);
    }

    public io.virtualan.cucumblan.message.type.MessageType buildProducerMessage(Object messages,
                    java.util.Map<String, Object> contextParam) throws io.virtualan.cucumblan.message.exception.MessageNotDefinedException {
        String message;
        try {
            org.json.JSONObject body;
            if (messages instanceof java.util.List) {
                message = (String) ((java.util.List) messages).stream().collect(java.util.stream.Collectors.joining());
                body = new org.json.JSONObject(message);
                return buildMessageType(body.toString(), contextParam);
            } else {

                message = io.virtualan.mapson.Mapson.buildMAPsonAsJson((java.util.Map) messages);
                body = new org.json.JSONObject(message);
                return buildMessageType(body.toString(), contextParam);
            }
        } catch (io.virtualan.mapson.exception.BadInputDataException | io.virtualan.cucumblan.message.exception.SkipMessageException exception) {
            throw new io.virtualan.cucumblan.message.exception.MessageNotDefinedException(exception.getMessage());
        }
    }

    //Mandatory
    public io.virtualan.cucumblan.message.type.MessageType buildConsumerMessage(
            org.apache.kafka.clients.consumer.ConsumerRecord<String, String> record, java.util.Map<String, Object> contextParam) throws io.virtualan.cucumblan.message.exception.SkipMessageException {
        return buildMessageType(record.value(), contextParam);
    }

    public String toString() {
        return "JSONMessageType{type='" + this.type + '\'' + ", id=" + this.id + ", body=" + this.body + '}';
    }

    public JSONMessageType buildMessageType(String body,java.util.Map<String, Object> contextParam) throws io.virtualan.cucumblan.message.exception.SkipMessageException {
        if (jsonMessageTypeMapper != null && !jsonMessageTypeMapper.isEmpty()) {
            java.util.List<JSONMessageType> messageTypeList = jsonMessageTypeMapper.entrySet()
                    .stream().filter(x -> {
                        try {
                            if(contextParam.get("EVENT_NAME") != null
                                    && x.getKey().toString().equalsIgnoreCase(contextParam.get("EVENT_NAME").toString())) {
                                Object identifier = com.jayway.jsonpath.JsonPath.read(body, x.getValue().toString());
                                if (identifier != null) return true;
                            }
                        } catch (Exception e) {
                        }
                        return false;
                    }).map(x -> new JSONMessageType(com.jayway.jsonpath.JsonPath.read(body, x.getValue().toString()).toString(), body)).collect(java.util.stream.Collectors.toList());
            if (messageTypeList != null && !messageTypeList.isEmpty()) return messageTypeList.get(0);
        }
        throw new io.virtualan.cucumblan.message.exception.SkipMessageException(body);
    }
}
