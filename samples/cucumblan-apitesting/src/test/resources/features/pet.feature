Feature: Test Pet API

  Scenario: User calls service to READ a pet by its id
    Given pet with an path param petId of 1000
    And add request with given header params
      | contentType | application/json |
    When a user get application/json in pets_petId resource on pet
    Then verify the status code is 500
    And verify across response includes following in the response
      | code    | MISSING_MOCK_DATA                                   |
      | message | Mock response was not added for the given parameter |

  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given create Pet Mock data for the with given input
      | url            | /pets         |
      | type           | Response      |
      | resource       | pets          |
      | httpStatusCode | 201           |
      | input          | INVALID_INPUT |
      | output         | ERROR         |
      | method         | POST          |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then verify the status code is 400
    And verify across response includes following in the response
      | code | Check input Json for the "Mock Request Body", Correct the input/Json!!! |

  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given create Pet Mock data for the with given input
      | url            | /pets                                                                                                                                                                                                                                      |
      | type           | Response                                                                                                                                                                                                                                   |
      | resource       | pets                                                                                                                                                                                                                                       |
      | httpStatusCode | 201                                                                                                                                                                                                                                        |
      | input          | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | output         | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | method         | POST                                                                                                                                                                                                                                       |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then verify the status code is 201
    And verify across response includes following in the response
      | mockStatus.code | Mock created successfully |
    And add the 100 value of the key as petId_post
    And store the id value of the key as petId_post_mock

  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given create Pet Mock data for the with given input
      | url            | /pets                                                                                                                                                                                                                                      |
      | type           | Response                                                                                                                                                                                                                                   |
      | resource       | pets                                                                                                                                                                                                                                       |
      | httpStatusCode | 201                                                                                                                                                                                                                                        |
      | input          | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | output         | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
      | method         | POST                                                                                                                                                                                                                                       |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then verify the status code is 400
    And verify across response includes following in the response
      | code | This Mock request already Present, Change the input Data!!! |

  Scenario: User calls service to CREATE and Create Pet
    Given create a pet with given input
      | category.id   | i~[petId_post] |
      | category.name | Fish-POST      |
      | id            | i~[petId_post] |
      | name          | GoldFish-POST  |
      | photoUrls[0]  | /fish/         |
      | status        | available      |
      | tags[0].id    | i~[petId_post] |
      | tags[0].name  | Fish-POST      |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in pets resource on pet
    Then verify the status code is 201
    And verify pet object schema validation for resource on pet
      | pets_json.schema.json  | true  |
    And verify across response includes following in the response
      | id   | [petId_post]  |
      | name | GoldFish-POST |

  Scenario: Setup a mock service for  Pet with READ API
    Given create Pet Mock data for the with given input
      | url                      | /pets/{petId}                                                                                                                                                                                                                           |
      | type                     | Response                                                                                                                                                                                                                                |
      | resource                 | pets                                                                                                                                                                                                                                    |
      | httpStatusCode           | 200                                                                                                                                                                                                                                     |
      | output                   | {   "category": {     "id": 110,     "name": "Fish-GET"   },   "id": 110,   "name": "GoldFish-GET",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 110,       "name": "Fish-GET"     }   ] } |
      | method                   | GET                                                                                                                                                                                                                                     |
      | availableParams[0].key   | petId                                                                                                                                                                                                                                   |
      | availableParams[0].value | 110                                                                                                                                                                                                                                     |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then verify the status code is 201
    And verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: User calls service to READ a pet by its id
    Given pet with an path param petId of 110
    And add request with given header params
      | contentType | application/json |
    When a user get application/json in pets_petId resource on pet
    Then verify the status code is 200
    And verify across response includes following in the response
      | id   | 110          |
      | name | GoldFish-GET |

  Scenario: Setup a mock service for Pet with DELETE API
    Given create Pet Mock data for the with given input
      | url                      | /pets/{petId}                                                                                                                                                                                                                                    |
      | type                     | Response                                                                                                                                                                                                                                         |
      | resource                 | pets                                                                                                                                                                                                                                             |
      | httpStatusCode           | 200                                                                                                                                                                                                                                              |
      | output                   | {   "category": {     "id": 120,     "name": "Fish-DELETE"   },   "id": 120,   "name": "GoldFish-DELETE",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 120,       "name": "Fish-DELETE"     }   ] } |
      | method                   | DELETE                                                                                                                                                                                                                                           |
      | availableParams[0].key   | petId                                                                                                                                                                                                                                            |
      | availableParams[0].value | 120                                                                                                                                                                                                                                              |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then verify the status code is 201
    And verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: User calls service to DELETE a pet by its id
    Given pet with an path param petId of 120
    When a user delete application/json in pets_petId resource on pet
    Then verify the status code is 200
    And verify across response includes following in the response
      | id   | 120             |
      | name | GoldFish-DELETE |

  Scenario: Setup a mock service for  Pet with PUT API
    Given create PUT Pet Mock data for the with given input
      | url                      | /pets/{petId}                                                                                                                                                                                                                           |
      | type                     | Response                                                                                                                                                                                                                                |
      | resource                 | pets                                                                                                                                                                                                                                    |
      | httpStatusCode           | 200                                                                                                                                                                                                                                     |
      | input                    | {   "category": {     "id": 130,     "name": "Fish-PUT"   },   "id": 130,   "name": "GoldFish-PUT",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 130,       "name": "Fish-PUT"     }   ] } |
      | output                   | {   "category": {     "id": 130,     "name": "Fish-PUT"   },   "id": 130,   "name": "GoldFish-PUT",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 130,       "name": "Fish-PUT"     }   ] } |
      | method                   | PUT                                                                                                                                                                                                                                     |
      | availableParams[0].key   | petId                                                                                                                                                                                                                                   |
      | availableParams[0].value | 130                                                                                                                                                                                                                                     |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices resource on virtualan
    Then verify the status code is 201
    And store jsonString_1 as key and api's . as value
    And verify response with mockStatus includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: User calls service to PUT and Create Pet
    Given pet with an path param petId of 130
    And update with mock data with given input
      | category.id   | i~130        |
      | category.name | Fish-PUT     |
      | id            | i~130        |
      | name          | GoldFish-PUT |
      | photoUrls[0]  | /fish/       |
      | status        | available    |
      | tags[0].id    | i~130        |
      | tags[0].name  | Fish-PUT     |
    And add request with given header params
      | contentType | application/json |
    When a user put application/json in pets_petId resource on pet
    Then verify the status code is 200
    And store jsonString_2 as key and api's . as value
    And verify api response aggregation for api-aggregated-std-type API_AGGREGATE on pet
      | totalMessageCount     |
      | i~2 |
    And verify across response includes following in the response
      | id   | 130          |
      | name | GoldFish-PUT |

  @idai
  Scenario: Attach file with multipart - api call
    Given a user perform a api action
    And add request with given header params
      | contentType | multipart/form-data |
      | Accept      | */*                 |
    And add request with multipart/form-data given multipart-form params
      | filestream  | sample.json                        |
      | serverUrls  | https://live.virtualandemo.com/api |
      | dataload    | APITEST.json                       |
      | execute     | true                               |
      | type        | VIRTUALAN                          |
      | reportTitle | DemoTestReport                     |
    When a user post multipart/form-data in /test resource on idai
    Then the status code is 201
    And verify across response includes following in the response
      | testExecuted | true |