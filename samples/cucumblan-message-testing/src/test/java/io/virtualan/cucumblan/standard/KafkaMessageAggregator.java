package io.virtualan.cucumblan.standard;

import io.virtualan.cucumblan.standard.StandardProcessing;

public class KafkaMessageAggregator implements StandardProcessing {


    @Override
    public String getType() {
        return "AGGREGATE";
    }

    @Override
    public Object responseEvaluator() {
        java.util.Map<String, Object> mapAggregator = io.virtualan.cucumblan.core.msg.kafka.MessageContext.getEventContextMap("MOCK_RESPONSE");
        int count = 0;
        if(mapAggregator != null) {
            for (java.util.Map.Entry<String, Object> entry : mapAggregator.entrySet()) {
                io.virtualan.test.msgtype.impl.JSONMessage jsonmsg = (io.virtualan.test.msgtype.impl.JSONMessage) entry.getValue();
                if ("german shepherd".equals(jsonmsg.getMessageAsJson().getJSONObject("category").getString("name"))) {
                    count++;
                }
            }
        }
        org.json.JSONObject object = new org.json.JSONObject();
        object.put("totalMessageCount", count);
        return object;
    }


}
