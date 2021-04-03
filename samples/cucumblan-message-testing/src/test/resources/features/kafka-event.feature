Feature: Testing the Kafka event validation
Scenario: check produce and consume event validation 2
    Given send message event TEST on the pet with type JSON
        |  {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 101,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] }|
    When verify-by-elements TEST contains 101 on the pet
        | id            | i~101             |
        | category.name | german shepherd   |
    Then verify TEST contains 101 on the pet
        |id,name, category/id:name,status|
        |i~101,Rocky,i~100:german shepherd,available|
    And verify TEST contains 101 on the pet
        |id,name, category/id:name,tags/id:name,status,photoUrls|
        |i~101,Rocky,i~100:german shepherd,i~101:brown\|,available,string\||
Scenario: check produce and consume event validation 3
    Given send message event TEST on the pet with type JSON
        |  {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 102,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] }|
    When verify-by-elements TEST contains 102 on the pet
        | id            | i~102             |
        | category.name | german shepherd   |
    Then verify TEST contains 102 on the pet
        |id,name, category/id:name,status|
        |i~102,Rocky,i~100:german shepherd,available|
    And verify TEST contains 102 on the pet
        |id,name, category/id:name,tags/id:name,status,photoUrls|
        |i~102,Rocky,i~100:german shepherd,i~101:brown\|,available,string\||
