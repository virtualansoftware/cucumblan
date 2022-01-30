Feature: Test Kafka API
  Scenario: Setup a mock service for Kafka for POST 1
    Given create Pet Mock data for the with given input
      | brokerUrl                | localhost:9092                                                                                                                                                                                                                                                                         |
      | input                    | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }                                             |
      | output                   | {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 101,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] } |
      | requestTopicOrQueueName  | virtualan.input                                                                                                                                                                                                                                                                        |
      | responseTopicOrQueueName | virtualan.output                                                                                                                                                                                                                                                                       |
      | type                     | Response                                                                                                                                                                                                                                                                               |
      | resource                 | virtualan.input                                                                                                                                                                                                                                                                        |
      | requestType              | KAFKA                                                                                                                                                                                                                                                                                  |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices/message resource on virtualan
    Then verify the status code is 201
    And verify across response includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: Setup a mock service for Kafka for POST 2
    Given create Pet Mock data for the with given input
      | brokerUrl                | localhost:9092                                                                                                                                                                                                                                                                         |
      | input                    | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 110,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }                                             |
      | output                   | {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 102,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] } |
      | requestTopicOrQueueName  | virtualan.input                                                                                                                                                                                                                                                                        |
      | responseTopicOrQueueName | virtualan.output                                                                                                                                                                                                                                                                       |
      | type                     | Response                                                                                                                                                                                                                                                                               |
      | resource                 | virtualan.input                                                                                                                                                                                                                                                                        |
      | requestType              | KAFKA                                                                                                                                                                                                                                                                                  |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices/message resource on virtualan
    Then verify the status code is 201
    And verify across response includes following in the response
      | mockStatus.code | Mock created successfully |

  Scenario: Setup a mock service for Kafka for POST 3
    Given create Pet Mock data for the with given input
      | brokerUrl                | localhost:9092                                                                                                                                                                                                                                                                         |
      | input                    | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 110,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] }                                             |
      | output                   | {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 102,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] } |
      | requestTopicOrQueueName  | virtualan.input                                                                                                                                                                                                                                                                        |
      | responseTopicOrQueueName | virtualan.output                                                                                                                                                                                                                                                                       |
      | type                     | Response                                                                                                                                                                                                                                                                               |
      | resource                 | virtualan.input                                                                                                                                                                                                                                                                        |
      | requestType              | KAFKA                                                                                                                                                                                                                                                                                  |
    And add request with given header params
      | contentType | application/json |
    When a user post application/json in virtualservices/message resource on virtualan
    Then verify the status code is 400
    And verify across response includes following in the response
      | code | This Mock request already Present, Change the input Data!!! |

  Scenario: check produce and consume event validation 1
    Given send inline message pets for event MOCK_REQUEST on pet with type JSON
      | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 100,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
    And pause message PROCESSING for process for 2000 milliseconds
    When verify-by-elements for pets for event MOCK_RESPONSE contains 101 on pet with type JSON
      | id            | i~101           |
      | category.name | german shepherd |
    Then verify for pets for event MOCK_RESPONSE contains 101 on pet with type JSON
      | id,name, category/id:name,status            |
      | i~101,Rocky,i~100:german shepherd,available |
    And verify for pets for event MOCK_RESPONSE contains 101 on pet with type JSON
      | id,name, category/id:name,tags/id:name,status,photoUrls            |
      | i~101,Rocky,i~100:german shepherd,i~101:brown\|,available,string\| |

  Scenario: check produce and consume event validation 2
    Given send inline message pets for event MOCK_REQUEST on pet with type JSON
      | {   "category": {     "id": 100,     "name": "Fish-POST"   },   "id": 110,   "name": "GoldFish-POST",   "photoUrls": [     "/fish/"   ],   "status": "available",   "tags": [     {       "id": 100,       "name": "Fish-POST"     }   ] } |
    When verify-by-elements for pets for event MOCK_RESPONSE contains 102 on pet with type JSON
      | id            | i~102           |
      | category.name | german shepherd |
    Then verify for pets for event MOCK_RESPONSE contains 102 on pet with type JSON
      | id,name, category/id:name,status            |
      | i~102,Rocky,i~100:german shepherd,available |
    And verify for pets for event MOCK_RESPONSE contains 102 on pet with type JSON
      | id,name, category/id:name,tags/id:name,status,photoUrls            |
      | i~102,Rocky,i~100:german shepherd,i~101:brown\|,available,string\| |