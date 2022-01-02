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

package io.virtualan.cucumblan.mobile.core;

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
  String xPath;

  /**
   * The Page element x path.
   */
  String type;

  /**
   * Instantiates a new Page element.
   *
   * @param pageElementName   the page element name
   * @param pageElementAction the page element action
   * @param pageElementXPath  the page element x path
   * @param type              the type
   */
  public PageElement(String pageElementName, String pageElementAction, String pageElementXPath,
      String type) {
    super();
    this.name = pageElementName;
    this.action = pageElementAction;
    this.xPath = pageElementXPath;
    this.type = type;
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
   * Sets page element name.
   *
   * @param name the page element name
   */
  public void setName(String name) {
    this.name = name;
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
   * Gets page element x path.
   *
   * @return the page element x path
   */
  public String getXPath() {
    return xPath;
  }

  /**
   * Sets page element x path.
   *
   * @param xPath the page element x path
   */
  public void setXPath(String xPath) {
    this.xPath = xPath;
  }

  @Override
  public String toString() {
    return "PageElement{" +
        "pageElementName='" + name + '\'' +
        ", pageElementAction='" + action + '\'' +
        ", pageElementXPath='" + xPath + '\'' +
        ", pageElementType'" + type + '\'' +
        '}';
  }
}
