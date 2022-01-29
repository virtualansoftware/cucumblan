Feature: Create Quote
  Scenario: Create Quote for existing user
    Given Load driver ANDROID and url on myapp
    When populate the login page on myapp
      | UserName              | LastName | Password  |
      | idaithalam@mobile.com  | Gates    | 1password |