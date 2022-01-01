Feature: Create Quote
  Scenario: Create Quote for existing user
    Given Load Driver CHROME And URL on css-lakeside
    When enter the login page on css-lakeside
      | Username              | Password  |
      | idaithalam@admin.com  | 1password |
    Then enter the address for the new_quote page on css-lakeside
      | Address          | PostalCode  | City       |
      | 2052 Albin St.   | 62345       | Nashville  |
    And populate required insurance quote information for the request_quote page on css-lakeside
      | StartDate    | InsuranceType        | Deductible |
      |  01/01/2022  | Life Insurance       | 300 CHF    |
    And verify the page contains data in the page
      | //*[text()='Waiting for Quote']   | Waiting for Quote |