@regression @create_deck
Feature: Create new deck

  Scenario Outline: I call deckofcards service to create new deck and draw some cards
    When I make a GET request to create new brand deck and draw <Cards to draw> cards from it
    And I make a GET request to get details of the deck
    Then response returns correct data
      | success   | true              |
      | remaining | <Remaining cards> |

    Examples: Cards count
      | Cards to draw | Remaining cards |
      | 2             | 50              |
      | 10            | 42              |
      | 52            | 0               |

  Scenario: I call deckofcards service to create a new deck containing only specific list of cards
    Given cards list
      | AS | AD | AC | AH |
    When I make a GET request to create a new deck containing only given cards
    And I make a GET request to draw given cards
    And I make a GET request to add given cards to new pile
    And I make a GET request to get list of cards in pile
    Then response returns only given cards in pile
