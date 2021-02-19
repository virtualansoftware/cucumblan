Feature: Testing the login validation
Scenario: Login action
Given Load Driver CHROME And URL http://demo.guru99.com/test/newtours/
    When perform the login page action on test
        |Username|Password|
        |username|password|
    Then perform the success page action on test
        | Heading|
        |Login Successfully|
    #And verify Heading has Login Successfully data in the page

