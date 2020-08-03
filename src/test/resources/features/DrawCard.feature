@regression @draw_card
Feature: Draw cards

  Scenario: I call deckofcards service to draw 5 cards from a bottom of the deck
    Given I make a GET request to create new brand deck
    When I make a GET request to draw 5 cards from a bottom of the deck
    And I make a GET request to get details of the deck
    Then response returns correct data
      | success   | true |
      | remaining | 47   |