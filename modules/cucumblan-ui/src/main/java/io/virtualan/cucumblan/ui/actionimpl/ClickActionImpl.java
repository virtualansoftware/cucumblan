package io.virtualan.cucumblan.ui.actionimpl;

import io.virtualan.cucumblan.ui.action.Action;
import org.openqa.selenium.WebElement;

public class ClickActionImpl implements Action {
    @Override
    public String getType() {
        return "CLICK";
    }

    @Override
    public void perform(String key, WebElement webelement, Object value) {
        webelement.click();
        return;
    }
}
