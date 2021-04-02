Feature: Testing the Kafka event validation
Scenario: check produce and consume event validation
    Given send message event TEST on the pet with type JSON
        |  {   "category": {     "id": 100,     "name": "string"   },   "id": 200,   "name": "doggie",   "photoUrls": [     "string","text"   ],   "status": "available",   "tags": [     {       "id": 0,       "name": "string"     }   ] } |
    When verify-by-elements TEST contains 200 on the pet
        | id   | i~200|
    Then verify TEST contains 200 on the pet
        |id,name, category/id:name,status|
        |i~200,doggie,i~100:string,available|
    And verify TEST contains 200 on the pet
        |id,name, category/id:name,tags/id:name,status,photoUrls|
        |i~200,doggie,i~100:string,i~0:string\|,available,string\|text\||
