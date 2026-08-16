Feature: Storing and retrieving objects
  In order to trust memory as a datastore, a stored object must survive a read, update and delete cycle

  Background:
    Given the memory application is alive

  Scenario: A stored object can be read back
    Given the kind "acceptance-crud" holds no objects
    When I store an object with id "lifecycle" and name "first" in kind "acceptance-crud"
    Then the stored object has id "lifecycle"
    And reading id "lifecycle" from kind "acceptance-crud" returns name "first"

  Scenario: A stored object can be updated and then deleted
    Given the kind "acceptance-crud" holds no objects
    And an object with id "lifecycle" and name "first" is stored in kind "acceptance-crud"
    When I update id "lifecycle" in kind "acceptance-crud" to name "second"
    Then reading id "lifecycle" from kind "acceptance-crud" returns name "second"
    When I delete id "lifecycle" from kind "acceptance-crud"
    Then id "lifecycle" is no longer in kind "acceptance-crud"

  Scenario: Every object of a kind is returned together
    Given the kind "acceptance-crud" holds no objects
    And an object with id "first-object" and name "first" is stored in kind "acceptance-crud"
    And an object with id "second-object" and name "second" is stored in kind "acceptance-crud"
    Then kind "acceptance-crud" holds 2 objects
    And kind "acceptance-crud" holds an object with id "first-object"
    And kind "acceptance-crud" holds an object with id "second-object"

  Scenario: A bulk write replaces the whole collection
    Given the kind "acceptance-crud" holds no objects
    And an object with id "first-object" and name "first" is stored in kind "acceptance-crud"
    And an object with id "second-object" and name "second" is stored in kind "acceptance-crud"
    When I bulk store an object with id "only-object" in kind "acceptance-crud"
    Then 1 object was written
    And kind "acceptance-crud" holds 1 objects
    And kind "acceptance-crud" holds an object with id "only-object"

  Scenario: Kind names are case insensitive
    Given the kind "acceptance-crud" holds no objects
    When I store an object with id "cased" and name "first" in kind "Acceptance-Crud"
    Then reading id "cased" from kind "acceptance-crud" returns name "first"

  Scenario: An authenticated caller sees the kinds stored in its own tenant
    Given the kind "acceptance-crud" holds no objects
    When I list the stored kinds
    Then "acceptance-crud" is one of the listed kinds

  Scenario: The list of stored kinds is not public
    When I list the stored kinds without authenticating
    Then the listing is rejected
