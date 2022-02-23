Feature: Create Quote
  Scenario: Create Quote for existing user
    Given Load driver ANDROID and url on myapp
    When enter user information the login page on myapp
      | UserName | idaithalam@mobile.com |
      | LastName | Gates                 |
      | Password | 1password             |