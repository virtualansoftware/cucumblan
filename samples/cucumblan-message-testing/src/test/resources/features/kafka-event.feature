Feature: Testing the Kafka event validation

  Scenario: check produce and consume event validation 1
    Given Send inline message pets for event TEST on pet with type JSON
      | {   "category": {     "id": 100,     "name": "string"   },   "id": 200,   "name": "doggie",   "photoUrls": [     "string","text"   ],   "status": "available",   "tags": [     {       "id": 0,       "name": "string"     }   ] } |
    And Send inline message pets for event TEST on pet with type JSON
      | {   "category": {     "id": 100,     "name": "string"   },   "id": 200,   "name": "doggie",   "photoUrls": [     "string","text"   ],   "status": "available",   "tags": [     {       "id": 0,       "name": "string"     }   ] } |
    When Verify-by-elements for pets for event TEST contains  200 on pet with type JSON
      | id | i~200 |
    Then Verify for pets for event TEST contains 200 on pet with type JSON
      | id,name, category/id:name,status    |
      | i~200,doggie,i~100:string,available |
    And Verify for pets for event TEST contains 200 on pet with type JSON
      | id,name, category/id:name,tags/id:name,status,photoUrls         |
      | i~200,doggie,i~100:string,i~0:string\|,available,string\|text\| |

  Scenario: check produce and consume event validation 2
    Given Send inline message pets for event TEST on pet with type JSON
      | {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 101,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] } |
    When Verify-by-elements for pets for event TEST contains 101 on pet with type JSON
      | id            | i~101           |
      | category.name | german shepherd |
    Then Verify for pets for event TEST contains 101 on pet with type JSON
      | id,name, category/id:name,status            |
      | i~101,Rocky,i~100:german shepherd,available |
    And Verify for pets for event TEST contains 101 on pet with type JSON
      | id,name, category/id:name,tags/id:name,status,photoUrls            |
      | i~101,Rocky,i~100:german shepherd,i~101:brown\|,available,string\| |

  Scenario: check produce and consume event validation 3
    Given Send inline message pets for event TEST on pet with type JSON
      | {     "category": {         "id": 100,         "name": "german shepherd"     },     "id": 102,     "name": "Rocky",     "photoUrls": [         "string"     ],     "status": "available",     "tags": [         {             "id": 101,             "name": "brown"         }     ] } |
    When Verify-by-elements for pets for event TEST contains 102 on pet with type JSON
      | id            | i~102           |
      | category.name | german shepherd |
    Then Verify for pets for event TEST contains 102 on pet with type JSON
      | id,name, category/id:name,status            |
      | i~102,Rocky,i~100:german shepherd,available |
    And Verify for pets for event TEST contains 102 on pet with type JSON
      | id,name, category/id:name,tags/id:name,status,photoUrls            |
      | i~102,Rocky,i~100:german shepherd,i~101:brown\|,available,string\| |