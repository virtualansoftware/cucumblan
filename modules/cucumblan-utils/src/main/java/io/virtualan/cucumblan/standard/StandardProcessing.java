package io.virtualan.cucumblan.standard;

import io.restassured.response.ValidatableResponse;

public interface StandardProcessing {
    String getType();

    default String preRequestProcessing(String jsonObject) {
        return null;
    }

    default String postResponseProcessing(ValidatableResponse jsonObject) {
        return null;
    }

    default String actualResponseProcessing(String jsonObject) {
        return null;
    }

    default String postResponseProcessing(String jsonObject) {
        return null;
    }

    default Object responseEvaluator() {
        return null;
    }
}
