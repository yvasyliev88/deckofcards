package com.deckofcards.hooks;

import com.deckofcards.utils.ApiClient;
import io.cucumber.java.AfterStep;
import io.cucumber.java.BeforeStep;

public class ApiHooks {

    private ApiClient client;

    @BeforeStep
    public void beforeStep() {
        client = ApiClient.getInstance();
    }

    @AfterStep
    public void afterStep() {
        client.removeRequestParams();
        client.removePathParams();
    }
}
