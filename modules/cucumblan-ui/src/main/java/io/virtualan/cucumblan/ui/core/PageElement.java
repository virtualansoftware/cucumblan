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

package io.virtualan.cucumblan.ui.core;

import org.openqa.selenium.By;
import  io.virtualan.cucumblan.props.util.StepDefinitionHelper;
/**
 * The type Page element.
 *
 * @author Elan Thangamani
 */
public class PageElement {

    /**
     * The Page element name.
     */
    String name;
    /**
     * The Page element action.
     */
    String action;
    /**
     * The Page element x path.
     */
    String value;

    /**
     * The Page element x path.
     */
    String type;

    /**
     * The Page element Type.
     */
    String findElementType;

    /**
     * Instantiates a new Page element.
     *
     * @param pageElementName   the page element name
     * @param pageElementAction the page element action
     * @param pageElementXPath  the page element x path
     * @param type              the type
     */
    public PageElement(String pageElementName, String pageElementAction, String pageElementXPath,
                       String findElementType, String type) {
        super();
        this.name = pageElementName != null ? StepDefinitionHelper.getActualValue(pageElementName) : pageElementName;
        this.action =  pageElementAction != null ? StepDefinitionHelper.getActualValue(pageElementAction) : pageElementAction;
        this.value = pageElementXPath != null ? StepDefinitionHelper.getActualValue(pageElementXPath) : pageElementXPath;
        this.findElementType = findElementType != null ? StepDefinitionHelper.getActualValue((findElementType)) : findElementType;
        this.type = type != null ? StepDefinitionHelper.getActualValue(type) : type;
    }

    /**
     * Gets page element name.
     *
     * @return the page element name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets page element name.
     *
     * @param name the page element name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets page element action.
     *
     * @return the page element action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets page element sction.
     *
     * @param action the page element action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets page element path.
     *
     * @return the page element path
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets page element x path.
     *
     * @param path the page element x path
     */
    public void setValue(String path) {
        this.value = path;
    }


    public String getFindElementType() {
        return findElementType;
    }

    public void setFindElementType(String findElementType) {
        this.findElementType = findElementType;
    }

    @Override
    public String toString() {
        return "PageElement{" +
                "pageElementName='" + name + '\'' +
                ", pageElementAction='" + action + '\'' +
                ", pageElementValue='" + value + '\'' +
                ", pageElementType'" + type + '\'' +
                ", findElementType'" + findElementType + '\'' +
                '}';
    }


    public By findElement(java.util.Map<String, String> contextValue) {
        String valueStr = io.virtualan.cucumblan.props.util.UIHelper.getUIActualValue(value, contextValue);
        switch (findElementType) {
            case "BY_ID":
                return By.id(valueStr);
            case "BY_NAME":
                return By.name(valueStr);
            case "BY_TAG_NAME":
                return By.tagName(valueStr);
            case "BY_LINK_TEXT":
                return By.linkText(valueStr);
            case "BY_PARTIAL_LINK_TEXT":
                return By.partialLinkText(valueStr);
            case "BY_X_PATH":
                return By.xpath(valueStr);
            case "BY_CSS":
                return By.cssSelector(valueStr);
            case "BY_CLASS_NAME":
                return By.className(valueStr);
            default:
                return By.id(valueStr); // Build Intelligence - self heal later
        }
    }
}
