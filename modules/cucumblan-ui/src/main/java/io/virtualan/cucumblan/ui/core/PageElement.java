package io.virtualan.cucumblan.ui.core;

public class PageElement {

  String pageElementName;
  String pageElementAction;
  String pageElementXPath;

  public PageElement(String pageElementName, String pageElementAction, String pageElementXPath) {
    super();
    this.pageElementName = pageElementName;
    this.pageElementAction = pageElementAction;
    this.pageElementXPath = pageElementXPath;
  }

  public String getPageElementName() {
    return pageElementName;
  }

  public void setPageElementName(String pageElementName) {
    this.pageElementName = pageElementName;
  }

  public String getPageElementAction() {
    return pageElementAction;
  }

  public void setPageElementSction(String pageElementAction) {
    this.pageElementAction = pageElementAction;
  }

  public String getPageElementXPath() {
    return pageElementXPath;
  }

  public void setPageElementXPath(String pageElementXPath) {
    this.pageElementXPath = pageElementXPath;
  }

}
