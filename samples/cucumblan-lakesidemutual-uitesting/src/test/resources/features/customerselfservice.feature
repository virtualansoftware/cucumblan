Feature: Testing the login validation
  Scenario: Login action
    Given Load Driver CHROME And URL on css-lakeside
    When perform the login page action on css-lakeside
      | Username          | Password  |
      | admin@example.com | 1password |
    Then verify the page contains contains data in the page
      | //p[1]                  | You haven't submitted any insurance quote requests yet. |