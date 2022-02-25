Feature: virtualan.json - API Contract validation status

  Scenario: Load initial set of data
    Given provided all the feature level parameters from file

  Scenario: temp - POST api call
    Given a user perform a api action
    And add request with given header params
      | Content-Type | text/xml |
    And add input.xml data file with text/xml given input
    When a user post application/json in /xml/tempconvert.asmx resource on xml
    Then verify the status code is 200
    And verify api response XML File response.xml includes in the response

  Scenario: EDI-271 API test - POST api call
    Given a user perform a api action
    And add the grey value of the key as tag
    And add request with given header params
      | contentType      | application/xml |
      | VirtualanStdType | EDI-271         |
    And add request data inline with text/xml given input
      | <?xml version="1.0" encoding="utf-8"?>                                 |
      | <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"> |
      | <soap:Body>                                                            |
      | <request>                                                              |
      | ISA*00*Authorizat*00*Security I*ZZ*Interchange Sen*ZZ*Interchange      |
      | Rec*141001*1037*^*00501*000031033*0*T*:~                               |
      | GS*HS*Sample Sen*Sample Rec*20141001*1037*123456*X*005010X279A1~       |
      | ST*270*1234*005010X279A1~                                              |
      | BHT*0022*13*10001234*20141001*1319~                                    |
      | HL*1**20*1~                                                            |
      | NM1*PR*2*ABC COMPANY*****PI*842610001~                                 |
      | HL*2*1*21*1~                                                           |
      | NM1*1P*2*BONE AND JOINT CLINIC*****XX*1234567893~                      |
      | HL*3*2*22*0~                                                           |
      | TRN*1*93175-0001*9877281234~                                           |
      | NM1*IL*1*SMITH*ROBERT****MI*11122333301~                               |
      | DMG*D8*19430519~                                                       |
      | DTP*291*D8*20141001~                                                   |
      | EQ*30~                                                                 |
      | SE*13*1234~                                                            |
      | GE*1*123456~                                                           |
      | IEA*1*000031033~                                                       |
      | </request>                                                             |
      | </soap:Body>                                                           |
      | </soap:Envelope>                                                       |
    When a user post text/xml in /bin/3f64e65d-c657-42d5-bcc9-5b13e71ca493 resource on bin
    Then verify the status code is 200
#    And Verify-standard EDI-271 all inline /bin/3f64e65d-c657-42d5-bcc9-5b13e71ca493 api includes following in the response
#      | <soap:envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"> |
#      | <soap:body> |
#      | <request> |
#      | ISA*00*Authorizat*00*Security I*ZZ*Interchange Sen*ZZ*Interchange |
#      | Rec*141001*1037*^*00501*000031033*0*T*:~GS*HS*Sample Sen*Sample |
#      | Rec*20141001*1037*123456*X*005010X279A1~ST*270*1234*005010X279A1~BHT*0022*13*10001234*20161001*1319~HL*1**20*1~NM1*PR*2*ABC COMPANY*****PI*842610001~HL*2*1*21*1~NM1*1P*2*BONE AND JOINT CLINIC*****XX*1234567893~HL*3*2*22*0~TRN*1*93175-0001*9877281234~NM1*IL*1*SMITH*ROBERT****MI*11122333301~DMG*D8*19430519~DTP*291*D8*20141001~EQ*30~SE*13*1234~GE*1*123456~IEA*1*000031033~ |
#      | </request> |
#      | </soap:body> |
#      | </soap:envelope> |
  Scenario: temp - POST api call
    Given a user perform a api action
    And add request with given header params
      | Content-Type | text/xml |
    And add input.xml data file with text/xml given input
    When a user post text/xml in /bin/3f64e65d-c657-42d5-bcc9-5b13e71ca493 resource on bin
    Then verify the status code is 200
    #And Verify-standard EDI-271 all 271_response_actual_sample.xml file xml api includes following in the response