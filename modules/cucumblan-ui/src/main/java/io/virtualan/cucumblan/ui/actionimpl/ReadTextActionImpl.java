/*
 *
 *
 *    Copyright (c) 2022.  Virtualan Contributors (https://virtualan.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *     in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software distributed under the License
 *     is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *     or implied. See the License for the specific language governing permissions and limitations under
 *     the License.
 *
 *
 *
 */

package io.virtualan.cucumblan.ui.actionimpl;

import io.virtualan.cucumblan.props.util.ScenarioContext;
import io.virtualan.cucumblan.ui.action.Action;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * The type Read text action.
 *
 * @author Elan Thangamani
 */
public class ReadTextActionImpl implements Action {

    /**
     * Gets type.
     *
     * @return the type
     */
    @Override
    public String getType() {
        return "GET_DATA";
    }

    /**
     * Perform.
     *
     * @param key        the key
     * @param webelement the webelement
     * @param value      the value
     */
    @Override
    public void perform(WebDriver driver, String key, WebElement webelement, Object value, io.virtualan.cucumblan.ui.core.PageElement element)
                throws  Exception{
        Thread.sleep(element.getSleep());
        String actualData = webelement.getText();
        ScenarioContext.setContext(String.valueOf(Thread.currentThread().getId()), key, actualData);
        return;
    }
}
