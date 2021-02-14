package io.virtualan.cucumblan.ui.action;

import org.openqa.selenium.WebElement;

public interface Action {
    String getType();
    void perform(String key, WebElement webelement, Object value);
}
