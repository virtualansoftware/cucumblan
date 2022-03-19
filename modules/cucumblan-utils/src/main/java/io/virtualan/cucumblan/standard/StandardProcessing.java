package io.virtualan.cucumblan.standard;

public interface StandardProcessing {
    String getType();

    default String preRequestProcessing(String jsonObject) {
        return null;
    }

    default String postResponseProcessing(String jsonObject) {
        return null;
    }

    default Object responseEvaluator() {
        return null;
    }
}
