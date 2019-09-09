@book
Feature: Test book API
  Background:  Load all Global parameter shared with all the Scenarios
  	Given Provided all the feature level parameters and endpoints
  	| END_POINTS_PROP | endpoint.properties | 
		Then Verify all the feature level parameters are present 

Scenario: User calls service to CREATE and Create book 
    Given Create a book with given input
		| inputJson	| /features/book/post/create.json |
		| author		  | Elan Thangamani 										 |
    When a user CREATE the book with id in api/books resource
    Then Verify the status code is 201
    And Verify across response includes following in the response
		| author		| Elan Thangamani |
		And Store the id value of the key as "book_id"

