Feature: Testing the login validation
Scenario: Login action
Given Load Driver CHROME And URL on css-lakeside
    When perform the login page action on css-lakeside
        |   Username            |    Password   |
        |   admin@example.com   |   1password   |
    Then perform the success page action on test
        | Heading|
        |Login Successfully|
    And verify Heading has Login Successfully data in the page

