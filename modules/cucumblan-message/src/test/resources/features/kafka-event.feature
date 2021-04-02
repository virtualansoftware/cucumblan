Feature: Testing the Kafka event validation
Scenario: check produce and consume event validation
    Given send message event TEST on the pet with type JSON
        |  {   "category": {     "id": 100,     "name": "string"   },   "id": 100,   "name": "doggie",   "photoUrls": [     "string"   ],   "status": "available",   "tags": [     {       "id": 0,       "name": "string"     }   ] } |
    Then verify-by-elements TEST contains 100 on the pet
        | id   | 100|

