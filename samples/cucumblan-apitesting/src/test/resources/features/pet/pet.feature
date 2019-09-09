Feature: Test Pet API
  Background:  Load all Global parameter shared with all the Scenarios
  	Given Provided all the feature level parameters and endpoints
  	| END_POINTS_PROP | endpoint.properties | 
		Then Verify all the feature level parameters are present
  Scenario: User calls service to READ a pet by its id
    Given a pet exists with an id of 1000
    When a user READ the pet by id in api/pets/{id} resource
    Then Verify the status code is 500
    And Verify across response includes following in the response
	| code			| MOCK_DATA_NOT_SET     |
	| message		| Mock Response was not defined for the given input	    |   

  Scenario: Setup a mock service for Pet with CREATE call with "Mock Request Body" validation failure
    Given Create Pet Mock data for the with given input
		| inputJson	| /features/pet/invalid/input.json |
    When tester CREATE the mock data for Pet in virtualservices resource 
    Then Verify the status code is 400
    And Verify across response includes following in the response
		| code			|Check input Json for the "Mock Request Body", Correct the input/Json!!!     |

Scenario: Setup a mock service for Pet for CREATE
    Given Create Pet Mock data for the with given input
		| inputJson	| /features/pet/post/input.json |
    When tester CREATE the mock data for Pet in virtualservices resource 
    Then Verify the status code is 201
    And Verify response with mockStatus includes following in the response
	  | mockStatus.code | Mock created successfully |

Scenario: User calls service to CREATE and Create Pet 
    Given Create a pet with given input
		| inputJson	| /features/pet/post/create_pet.json |
		| id				| 100     	   											 |
		| name		  | GoldFish-POST 										 |
    When a user CREATE the pet with id in api/pets resource
    Then Verify the status code is 201
    And Verify across response includes following in the response
		| id		| 100     	   |
		| name		| GoldFish-POST |
Scenario: Setup a mock service for duplicate Pet with CREATE method
    Given Create Pet Mock data for the with given input
		| inputJson	| /features/pet/post/input.json |
    When tester CREATE the mock data for Pet in virtualservices resource 
    Then Verify the status code is 400
    And Verify across response includes following in the response
	  | code | This Mock request already Present, Change the input Data!!! |

Scenario: Setup a mock service for  Pet with READ API
    Given Create Pet Mock data for the with given input
		| inputJson	| /features/pet/get/input.json |
    When tester CREATE the mock data for Pet in virtualservices resource
    Then Verify the status code is 201
    And Verify response with mockStatus includes following in the response
	  | mockStatus.code | Mock created successfully |

Scenario: User calls service to READ a pet by its id
    Given a pet exists with an id of 110
    When a user READ the pet by id in api/pets/{id} resource
    Then Verify the status code is 200
    And Verify across response includes following in the response
	| id		| 110     	   |
	| name		| GoldFish-GET |
Scenario: Setup a mock service for  Pet with DELETE API
    Given Create Pet Mock data for the with given input
		| inputJson	| /features/pet/delete/input.json |
    When tester CREATE the mock data for Pet in virtualservices resource
    Then Verify the status code is 201
    And Verify response with mockStatus includes following in the response
	  | mockStatus.code | Mock created successfully |

Scenario: User calls service to DELETE a pet by its id
    Given a pet exists with an id of 120
    When a user DELETE the pet by id in api/pets/{id} resource
    Then Verify the status code is 200
    And Verify across response includes following in the response
	| id		| 120     	   |
	| name		| GoldFish-DELETE |

Scenario: Setup a mock service for  Pet with PUT API
   Given Create Pet Mock data for the with given input
		| inputJson	| /features/pet/put/input.json |
    When tester CREATE the mock data for Pet in virtualservices resource 
    Then Verify the status code is 201
    And Verify response with mockStatus includes following in the response
	  | mockStatus.code | Mock created successfully |

Scenario: User calls service to PUT and Create Pet 
    Given a pet exists with an id of 130
    And a pet exists with an id of 130
    And Update a pet with given input
		| inputJson	| /features/pet/put/update_pet.json |
    When a user UPDATE the pet with id in api/pets/{id} resource
    Then Verify the status code is 200
    And Verify across response includes following in the response
	| id		| 130     	   |
	| name		| GoldFish-PUT |	


