Feature: Creating an object that already exists is a conflict
  In order to tell a lost race from a bad payload, a duplicate create must answer 409

  Scenario: Creating the same id twice
    Given the memory application is alive
    And an object with id "acceptance-conflict" of type "acceptance-conflict" exists
    When I create an object with id "acceptance-conflict" of type "acceptance-conflict" again
    Then the create is rejected as a conflict

  Scenario: Creating an id that is not taken
    Given the memory application is alive
    And no object with id "acceptance-free" of type "acceptance-conflict" exists
    When I create an object with id "acceptance-free" of type "acceptance-conflict"
    Then the object is created
