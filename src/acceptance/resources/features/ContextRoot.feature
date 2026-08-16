Feature: Context Root of this API
  In order to use the memory API, it must be available

  Scenario: ContextRoot https
    Given the memory application is alive
    When I navigate to "https://memory.data.trevorism.com"
    Then the API returns a link to the help page

  Scenario: Ping https
    Given the memory application is alive
    When I navigate to /ping on "https://memory.data.trevorism.com"
    Then pong is returned, to indicate the service is alive
