package io.virtualan.cucumblan.props.util;

import io.virtualan.cucumblan.core.msg.kafka.KafkaConsumerClient;

public class EventRequest {

    private KafkaConsumerClient client;
    private String eventName;
    private String type;
    private String id;
    private String resource;
    private int recheck;

    public EventRequest() {
    }

    public KafkaConsumerClient getClient() {
        return client;
    }

    public void setClient(KafkaConsumerClient client) {
        this.client = client;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getRecheck() {
        return recheck;
    }

    public void setRecheck(int recheck) {
        this.recheck = recheck;
    }
}
