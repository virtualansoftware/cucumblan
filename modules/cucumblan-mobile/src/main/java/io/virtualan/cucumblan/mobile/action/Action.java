/*
 *
 *
 *    Copyright (c) 2021.  Virtualan Contributors (https://virtualan.io)
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

package io.virtualan.cucumblan.mobile.action;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;

/**
 * The interface Action.
 * @author Elan Thangmani
 */
public interface Action {

    /**
     * Gets type.
     *
     * @return the type
     */
    String getType();

    /**
     * Perform.
     *
     * @param key        the key
     * @param webelement the webelement
     * @param value      the value
     */
    void perform(AppiumDriver driver, String key, MobileElement webelement, Object value);
}
