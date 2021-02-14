package io.virtualan.cucumblan.ui.actionimpl;

import io.virtualan.cucumblan.ui.action.Action;
import org.openqa.selenium.WebElement;

public class SendKeyActionImpl implements Action {
    @Override
    public String getType() {
        return "SEND_KEY";
    }

    @Override
    public void perform(String key, WebElement webelement, Object value) {
        webelement.clear();
        webelement.sendKeys((String)value);
        return;
    }
}
