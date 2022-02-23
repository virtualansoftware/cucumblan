package io.virtualan.cucumblan.standard;

public interface StandardProcessing {
    String getType();

    String preRequestProcessing(String jsonObject);

    String postResponseProcessing(String jsonObject);
}
