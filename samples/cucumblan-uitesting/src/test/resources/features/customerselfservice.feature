Feature: Create a Quote

  Scenario: Load a driver
    Given As a user I want to validate create quote on css-lakeside
    When Load driver CHROME and url on css-lakeside

  Scenario: Invalid Email address validation
    Given As a user I want to validate email with login on css-lakeside
    When enter user information the login page on css-lakeside
      | Username | admin     |
      | Password | 1password |
    Then verify the page contains data in the screen on css-lakeside
      | //*[text()='Invalid email address or password'] | Invalid email address or password |

  Scenario: Incorrect password validation
    Given As a user I want to validate login on css-lakeside
    When enter user information the login page on css-lakeside
      | Username | sddsfds          |
      | Password | 1passsdfsddsword |
    Then verify the page contains data in the screen on css-lakeside
      | //*[text()='Invalid email address or password'] | Invalid email address or password |
    And capture Invalid email address screen on css-lakeside

  Scenario: User Login
    Given As a user I want to validate create quote on css-lakeside
    When enter user information the login page on css-lakeside
      | Username | admin@example.com |
      | Password | 1password         |

  Scenario: Create a quote - step enter user address
    Given As a user I want to validate create quote on css-lakeside
    Then enter the address for the new_quote page on css-lakeside
      | Address    | 2052 Albin St. |
      | PostalCode | 62345          |
      | City       | Nashville      |
    And capture after enter the address screen on css-lakeside

  Scenario: Create a quote
    Given As a user I want to validate create quote on css-lakeside
    And populate required insurance quote information for the request_quote page on css-lakeside
      | StartDate     | 01/01/2022     |
      | InsuranceType | Life Insurance |
      | Deductible    | 300 CHF        |
    And verify the page contains data in the screen on css-lakeside
      | //*[text()='Waiting for Quote'] | Waiting for Quote |

  Scenario: Update user information
    Given As a user I want to validate create quote on css-lakeside
    And update profile info for the profile page on css-lakeside
      | Address    | 20 Wolf Rd |
      | PostalCode | 88888      |
    And capture after updated screen on css-lakeside
#    And compare-screen after updated with expected/expected_screen.png screen on css-lakeside

  Scenario: Customer Self-Service Auth - api call
    Given as a insurance user perform login api action
    And add content type with given header params
      | contentType | application/json |
    And create login information with given input
      | password | 1password |
      | email    | admin@example.com    |
    When logging in using post application/json in /auth resource on customer
    Then the status code is 200
    And verify user email information includes following in the response
      | email | admin@example.com |
    And store token as key and api's token as value

  Scenario: GetCustomerByLogin - api call
    Given a user perform a api action
    And add request with given header params
      | contentType  | application/json |
      | X-Auth-Token | [token]          |
    When a user get application/json in /user resource on customer
    Then the status code is 200
    And verify across response includes following in the response
      | email | admin@example.com |
    And store customerId as key and api's customerId as value

  Scenario: GetCustomerInfoByCustomerId - api call
    Given a user perform a api action
    And add request with given header params
      | contentType  | application/json |
      | X-Auth-Token | [token]          |
    When a user get application/json in /customers/[customerId] resource on customer
    Then the status code is 200
    And verify across response includes following in the response
      | streetAddress | 20 Wolf Rd |

  Scenario: Close the driver
    Given As a user I want to validate create quote on css-lakeside
    When close the driver for css-lakeside

#  Scenario: Accept Quote for existing user
#    Given Load driver CHROME and url on policy-management
#    When user accepts policy on the accept page on policy-management
#      | Premium     | 1000 |
#      | PolicyLimit | 1000 |
#    And capture Waiting for Quote screen on policy-management
#    And close the driver for policy-management

