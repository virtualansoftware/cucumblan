Feature: Create Quote
  Scenario: Create Quote for existing user
    Given Load driver CHROME and url on css-lakeside
    When enter user information the login page on css-lakeside
      | Username  | idaithalam@admin.com  |
      | Password  | 1password |
    Then enter the address for the new_quote page on css-lakeside
      | Address      | 2052 Albin St.  |
      | PostalCode   | 62345           |
      |  City        | Nashville       |
    And populate required insurance quote information for the request_quote page on css-lakeside
      | StartDate     |  01/01/2022     |
      | InsuranceType | Life Insurance  |
      | Deductible    | 300 CHF         |
    And verify the page contains data in the page
      | //*[text()='Waiting for Quote']   | Waiting for Quote |