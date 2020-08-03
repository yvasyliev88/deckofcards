package com.deckofcards.steps;

import com.deckofcards.models.Card;
import com.deckofcards.utils.ApiClient;
import com.deckofcards.utils.DataStorage;
import com.deckofcards.utils.Endpoints;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class StepDefs {

    private ApiClient client = ApiClient.getInstance();
    private static String pileName = "player1";

    @When("I make a GET request to create new brand deck")
    public void a_user_creates_new_brand_deck() {
        client.sendGet(Endpoints.DECK_NEW_URI, null, null);

        client.validateResponse(200);
        String deckId = client.getJsonValue("deck_id");
        DataStorage.store("deckId", deckId);
    }

    @When("I make a GET request to create new brand deck and draw {int} cards from it")
    public void a_user_creates_new_brand_deck_and_draw_cards(int count) {
        client.sendGet(Endpoints.DECK_NEW_DRAW_URI, null,
                new HashMap<String, Object>() {
                    {
                        put("count", String.valueOf(count));
                    }
                });

        client.validateResponse(200);
        String deckId = client.getJsonValue("deck_id");
        DataStorage.store("deckId", deckId);
    }

    @Then("response status code is {int}")
    public void status_code_is(Integer statusCode) {
        client.validateResponse(statusCode);
    }

    @Given("cards list")
    public void given_cards_list(DataTable dt) {
        List<String> cardsList = dt.row(0);
        DataStorage.store("cardsList", cardsList);
    }

    @When("I make a GET request to create a new deck containing only given cards")
    public void user_creates_a_new_deck_containing_cards() {

        client.sendGet(Endpoints.DECK_NEW_SHUFFLE_URI, null,
                new HashMap<String, Object>() {
                    {
                        put("cards", String.join(",", (List<String>) DataStorage.get("cardsList")));
                    }
                });

        client.validateResponse(200);
        String deckId = client.getJsonValue("deck_id");
        DataStorage.store("deckId", deckId);
    }

    @When("I make a GET request to draw given cards")
    public void user_draws_given_cards() {

        List<String> cardsList = (List<String>) DataStorage.get("cardsList");

        client.sendGet(Endpoints.DECK_ID_DRAW_URI,
                new HashMap<String, Object>() {
                    {
                        put("deckId", DataStorage.get("deckId"));
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("count", cardsList.size());
                    }
                });
        client.validateResponse(200);
    }


    @When("I make a GET request to add given cards to new pile")
    public void user_adds_given_cards_to_pile() {
        client.sendGet(Endpoints.DECK_PILE_ADD_URI,
                new HashMap<String, Object>() {
                    {
                        put("deckId", DataStorage.get("deckId"));
                        put("pileName", pileName);
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("cards", String.join(",", (List<String>) DataStorage.get("cardsList")));
                    }
                });

        client.validateResponse(200);
    }

    @When("I make a GET request to get list of cards in pile")
    public void user_lists_cards_in_pile() {
       client.sendGet(Endpoints.DECK_PILE_LIST_URI,
                new HashMap<String, Object>() {
                    {
                        put("deckId", DataStorage.get("deckId"));
                        put("pileName", pileName);
                    }
                },
                null);

        client.validateResponse(200);
    }

    @Then("response returns only given cards in pile")
    public void response_returns_only_given_cards_in_pile() {
        List<Card> actualCardsList = client.getJsonPath().getList(String.format("piles.%s.cards", pileName), Card.class);
        List<String> cardCodes = actualCardsList.stream().map(card -> card.getCode()).collect(Collectors.toList());
        List<String> expectedCardCodes = (List<String>) DataStorage.get("cardsList");
        assertThat(cardCodes).as("Response contains expected cards list in pile").containsExactlyInAnyOrderElementsOf(expectedCardCodes);
    }

    @Then("I save deck id from response")
    public void store_deck_id() {
        String deckId = client.getJsonValue("deck_id");
        DataStorage.store("deckId", deckId);
    }

    @When("I make a GET request to draw {int} cards from a bottom of the deck")
    public void user_draws_card_from_bottom(int count) {

        client.sendGet(Endpoints.DECK_ID_DRAW_URI,
                new HashMap<String, Object>() {
                    {
                        put("deckId", DataStorage.get("deckId"));
                    }
                },
                new HashMap<String, Object>() {
                    {
                        put("bottom", "");
                        put("count", count);
                    }
                });

        client.validateResponse(200);
    }

    @When("I make a GET request to get details of the deck")
    public void user_gets_deck_details() {
        client.sendGet(Endpoints.DECK_ID_URI,
                new HashMap<String, Object>() {
                    {
                        put("deckId", DataStorage.get("deckId"));
                    }
                },
                null);
        client.validateResponse(200);
    }

    @Then("response returns correct data")
    public void response_returns_correct_data(Map<String,String> responseFields){
        for (Map.Entry<String, String> field : responseFields.entrySet()) {
            if(StringUtils.isNumeric(field.getValue())){
                assertThat(Integer.valueOf(client.getJsonValue(field.getKey())))
                        .as("Value of field '%s' does not equal to expected value %s", field.getKey(), field.getValue())
                        .isEqualTo(Integer.valueOf(field.getValue()));
            }
            else{
                assertThat(client.getJsonValue(field.getKey()))
                        .as("Value of field %s does not equal to expected value %s", field.getKey(), field.getValue())
                        .isEqualTo(field.getValue());
            }
        }
    }
}
