Feature: Create Quote

  Scenario: Login validator
    Given Load driver CHROME and url on css-lakeside
    When enter user information the login page on css-lakeside
      | Username | admin     |
      | Password | 1password |
    And verify the page contains data in the page
      | //*[text()='Invalid email address or password'] | Invalid email address or password |
    When enter user information the login page on css-lakeside
      | Username | sddsfds          |
      | Password | 1passsdfsddsword |
    And verify the page contains data in the page
      | //*[text()='Invalid email address or password'] | Invalid email address or password |
    When enter user information the login page on css-lakeside
      | Username | sddsfds          |
      | Password | 1passsdfsddsword |
    And verify the page contains data in the page
      | //*[text()='Invalid email address or password'] | Invalid email address or password |

  Scenario: Create Quote for existing user
    Given Load driver CHROME and url on css-lakeside
    When enter user information the login page on css-lakeside
      | Username | admin@example.com |
      | Password | 1password         |
    Then enter the address for the new_quote page on css-lakeside
      | Address    | 2052 Albin St. |
      | PostalCode | 62345          |
      | City       | Nashville      |
    And populate required insurance quote information for the request_quote page on css-lakeside
      | StartDate     | 01/01/2022     |
      | InsuranceType | Life Insurance |
      | Deductible    | 300 CHF        |
    And verify the page contains data in the page
      | //*[text()='Waiting for Quote'] | Waiting for Quote |

  Scenario: Accept Quote for existing user
    Given Load driver CHROME and url on policy-management
    When user accepts policy on the accept page on policy-management
      | Premium     | 1000 |
      | PolicyLimit | 1000 |