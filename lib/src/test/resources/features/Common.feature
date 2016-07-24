Feature: Common behaviour of RxHub

  Scenario Outline: Consumer subscription after subscribed to provider
    Given Provider"P"
    And Consumer"C"
    Given Hub"H" with NodeType <nodeType>
    And Hub"H" is subscribed to Provider"P" with tag "T"
    When Consumer"C" subscribes to Hub"H" with tag "T"
    And Provider"P" emits Event"E"
    Then Consumer"C" should receive Event"E"

    Examples:
      | nodeType        |
      | BehaviorSubject |
      | PublishSubject  |
      | ReplaySubject   |
      | BehaviorRelay   |
      | PublishRelay    |
      | ReplayRelay     |

  Scenario Outline: Consumer subscription before subscribed to provider
    Given Provider"P"
    And Consumer"C"
    And Hub"H" with NodeType <nodeType>
    And Consumer"C" is subscribed to Hub"H" with tag "T"
    When Hub"H" subscribes to Provider"P" with tag "T"
    And Provider"P" emits Event"E"
    Then Consumer"C" should receive Event"E"

    Examples:
      | nodeType        |
      | BehaviorSubject |
      | PublishSubject  |
      | ReplaySubject   |
      | BehaviorRelay   |
      | PublishRelay    |
      | ReplayRelay     |

  Scenario Outline: 2 providers 1 consumer
    Given Provider"P1"
    And Provider"P2"
    And Consumer"C"
    And Hub"H" with NodeType <nodeType>
    And Hub"H" is subscribed to Provider"P1" with tag "T"
    And Hub"H" is subscribed to Provider"P2" with tag "T"
    And Consumer"C" is subscribed to Hub"H" with tag "T"
    When Provider"P1" emits Event"E1"
    And Provider"P2" emits Event"E2"
    Then Consumer"C" should receive Event"E1"
    And Consumer"C" should receive Event"E2"

    Examples:
      | nodeType        |
      | BehaviorSubject |
      | PublishSubject  |
      | ReplaySubject   |
      | BehaviorRelay   |
      | PublishRelay    |
      | ReplayRelay     |

  Scenario Outline: 1 provider 2 consumers
    Given Provider"P"
    And Consumer"C1"
    And Consumer"C2"
    And Hub"H" with NodeType <nodeType>
    And Hub"H" is subscribed to Provider"P" with tag "T"
    And Consumer"C1" is subscribed to Hub"H" with tag "T"
    And Consumer"C2" is subscribed to Hub"H" with tag "T"
    When Provider"P" emits Event"E"
    Then Consumer"C1" should receive Event"E"
    And Consumer"C2" should receive Event"E"

    Examples:
      | nodeType        |
      | BehaviorSubject |
      | PublishSubject  |
      | ReplaySubject   |
      | BehaviorRelay   |
      | PublishRelay    |
      | ReplayRelay     |

  Scenario Outline: 2 consumers + manual emit on the Node
    Given Hub"H" with NodeType <nodeType>
    And Consumer"C1"
    And Consumer"C2"
    And Consumer"C1" is subscribed to Hub"H" with tag "T"
    And Consumer"C2" is subscribed to Hub"H" with tag "T"
    When Event"E" with tag "T" is emitted on Hub"H"
    Then Consumer"C1" should receive Event"E"
    And Consumer"C2" should receive Event"E"

    Examples:
      | nodeType        |
      | BehaviorSubject |
      | PublishSubject  |
      | ReplaySubject   |
      | BehaviorRelay   |
      | PublishRelay    |
      | ReplayRelay     |

  Scenario Outline: Remove provider
    Given Provider"P"
    And Consumer"C"
    And Hub"H" with NodeType <nodeType>
    And Consumer"C" is subscribed to Hub"H" with tag "T"
    And Hub"H" is subscribed to Provider"P" with tag "T"
    When Provider"P" with tag "T" is removed from Hub"H"
    And Provider"P" emits Event"E"
    Then Consumer"C" should not receive Event"E"

    Examples:
      | nodeType        |
      | BehaviorSubject |
      | PublishSubject  |
      | ReplaySubject   |
      | BehaviorRelay   |
      | PublishRelay    |
      | ReplayRelay     |


  Scenario Outline: Remove all providers
    Given Provider"P1"
    Given Provider"P2"
    And Consumer"C"
    And Hub"H" with NodeType <nodeType>
    And Consumer"C" is subscribed to Hub"H" with tag "T1"
    And Consumer"C" is subscribed to Hub"H" with tag "T2"
    And Hub"H" is subscribed to Provider"P1" with tag "T1"
    And Hub"H" is subscribed to Provider"P2" with tag "T2"
    When providers are cleared from Hub"H"
    And Provider"P2" with tag "T2" is removed from Hub"H"
    And Provider"P1" emits Event"E1"
    And Provider"P2" emits Event"E2"
    Then Consumer"C" should not receive Event"E1"
    And Consumer"C" should not receive Event"E2"

    Examples:
      | nodeType        |
      | BehaviorSubject |
      | PublishSubject  |
      | ReplaySubject   |
      | BehaviorRelay   |
      | PublishRelay    |
      | ReplayRelay     |


