Feature: Create Quote

  Scenario: Login validator
    Given Load driver CHROME and url on css-lakeside
    When enter user information the login page on css-lakeside
      | Username | admin     |
      | Password | 1password |
    And verify the page contains data in the screen on css-lakeside
      | //*[text()='Invalid email address or password'] | Invalid email address or password |
    When enter user information the login page on css-lakeside
      | Username | sddsfds          |
      | Password | 1passsdfsddsword |
    And verify the page contains data in the screen on css-lakeside
      | //*[text()='Invalid email address or password'] | Invalid email address or password |
    And capture Invalid email address screen on css-lakeside
    When enter user information the login page on css-lakeside
      | Username | admin@example.com |
      | Password | 1password         |
    Then enter the address for the new_quote page on css-lakeside
      | Address    | 2052 Albin St. |
      | PostalCode | 62345          |
      | City       | Nashville      |
    And capture enter the address screen on css-lakeside
    And populate required insurance quote information for the request_quote page on css-lakeside
      | StartDate     | 01/01/2022     |
      | InsuranceType | Life Insurance |
      | Deductible    | 300 CHF        |
    And verify the page contains data in the screen on css-lakeside
      | //*[text()='Waiting for Quote'] | Waiting for Quote |
    And capture Waiting for Quote screen on css-lakeside
    And close the driver for css-lakeside

  Scenario: Accept Quote for existing user
    Given Load driver CHROME and url on policy-management
    When user accepts policy on the accept page on policy-management
      | Premium     | 1000 |
      | PolicyLimit | 1000 |
    And capture Waiting for Quote screen on policy-management
    And close the driver for policy-management

