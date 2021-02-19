Feature: virtualan.json - API Contract validation status
  Scenario: Load initial set of data
    Given Provided all the feature level parameters from file
  Scenario: temp - POST api call
    Given a user perform a api action
    And add request with given header params
      | Content-Type                   | text/xml                         |
    And add input.xml data file with text/xml given input
    When a user post application/json in /xml/tempconvert.asmx resource on xml
    Then Verify the status code is 200
    And Verify api response XML File response.xml includes in the response
