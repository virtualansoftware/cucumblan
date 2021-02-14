package io.virtualan.cucumblan.ui.actionimpl;

import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.ui.action.Action;
import org.openqa.selenium.WebElement;

public class ReadTextActionImpl implements Action {
    @Override
    public String getType() {
        return "GET_TEXT";
    }

    @Override
    public void perform(String key, WebElement webelement, Object value) {
        String actualData = webelement.getText();
        ScenarioContext.setContext(key, actualData);
        return;
    }
}
