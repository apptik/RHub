Feature: Specific behaviour for WorkQueueProcessor based Proxies

  Scenario Outline: 1 provider 2 consumers
    Given Hub"H" with ProxyType <proxyType>
    Given Provider"P"
    And Consumer"C1"
    And Consumer"C2"
    And Hub"H" is subscribed to Provider"P" with tag "T"
    And Consumer"C1" is subscribed to Hub"H" with tag "T"
    And Consumer"C2" is subscribed to Hub"H" with tag "T"
    When Provider"P" emits Event"E1"
    Then Consumer"C1" should receive Event"E1"
    And Consumer"C2" xor Consumer"C1" should receive Event"E1"
    When Provider"P" emits Event"E2"
    Then Consumer"C1" xor Consumer"C2" should receive Event"E2"

    Examples:
      | proxyType               |
      | WorkQueueProcessorProxy |
      | WorkQueueSafeProxy      |

  Scenario Outline: 2 consumers + manual emit on the Proxy
    Given Hub"H" with ProxyType <proxyType>
    And Consumer"C1"
    And Consumer"C2"
    And Consumer"C1" is subscribed to Hub"H" with tag "T"
    And Consumer"C2" is subscribed to Hub"H" with tag "T"
    When Event"E1" with tag "T" is emitted on Hub"H"
    And Consumer"C2" xor Consumer"C1" should receive Event"E1"
    When Event"E2" with tag "T" is emitted on Hub"H"
    Then Consumer"C1" xor Consumer"C2" should receive Event"E2"

    Examples:
      | proxyType               |
      | WorkQueueProcessorProxy |
      | WorkQueueSafeProxy      |