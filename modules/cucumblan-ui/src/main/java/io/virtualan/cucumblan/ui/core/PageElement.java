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

package io.virtualan.cucumblan.ui.core;

/**
 * The type Page element.
 * @author Elan Thangamani
 **/
public class PageElement {

  /**
   * The Page element name.
   */
  String pageElementName;
  /**
   * The Page element action.
   */
  String pageElementAction;
  /**
   * The Page element x path.
   */
  String pageElementXPath;

  /**
   * Instantiates a new Page element.
   *
   * @param pageElementName   the page element name
   * @param pageElementAction the page element action
   * @param pageElementXPath  the page element x path
   */
  public PageElement(String pageElementName, String pageElementAction, String pageElementXPath) {
    super();
    this.pageElementName = pageElementName;
    this.pageElementAction = pageElementAction;
    this.pageElementXPath = pageElementXPath;
  }

  /**
   * Gets page element name.
   *
   * @return the page element name
   */
  public String getPageElementName() {
    return pageElementName;
  }

  /**
   * Sets page element name.
   *
   * @param pageElementName the page element name
   */
  public void setPageElementName(String pageElementName) {
    this.pageElementName = pageElementName;
  }

  /**
   * Gets page element action.
   *
   * @return the page element action
   */
  public String getPageElementAction() {
    return pageElementAction;
  }

  /**
   * Sets page element sction.
   *
   * @param pageElementAction the page element action
   */
  public void setPageElementSction(String pageElementAction) {
    this.pageElementAction = pageElementAction;
  }

  /**
   * Gets page element x path.
   *
   * @return the page element x path
   */
  public String getPageElementXPath() {
    return pageElementXPath;
  }

  /**
   * Sets page element x path.
   *
   * @param pageElementXPath the page element x path
   */
  public void setPageElementXPath(String pageElementXPath) {
    this.pageElementXPath = pageElementXPath;
  }

}
