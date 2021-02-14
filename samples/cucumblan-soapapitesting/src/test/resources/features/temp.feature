Feature: Test Pet API
  Scenario: Scenario test
    Given add input.xml data file with text/xml given input
    When a user post application/json in /xml/tempconvert.asmx resource on xml
    Then Verify the status code is 200
    And Verify soap response XML includes in the response
     | <?xml version="1.0" encoding="utf-8"?> <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">     <soap:Body>         <CelsiusToFahrenheitResponse xmlns="https://www.w3schools.com/xml/">             <CelsiusToFahrenheitResult>212</CelsiusToFahrenheitResult>         </CelsiusToFahrenheitResponse>     </soap:Body> </soap:Envelope>|
    And Verify soap response XML File response.xml includes in the response