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

/**
 * The type Click action.
 *
 * @author Elan Thangamani
 */
public class EnterActionImpl implements io.virtualan.cucumblan.ui.action.Action {

    /**
     * Gets type.
     *
     * @return the type
     */
    @Override
    public String getType() {
        return "ENTER";
    }

    /**
     * Perform.
     *
     * @param key        the key
     * @param webelement the webelement
     * @param value      the value
     */
    @Override
    public void perform(org.openqa.selenium.WebDriver driver, String key, org.openqa.selenium.WebElement webelement, Object value) {
        webelement.sendKeys(org.openqa.selenium.Keys.RETURN);
        return;
    }
}
