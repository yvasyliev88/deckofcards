package com.deckofcards.utils;

import java.util.HashMap;
import java.util.Map;

public class DataStorage {

    private static Map providedValues  = new HashMap< String, Object>();

    /*
     * Get stored id value by selected name
     * Use when one test case get data created in another test case
     */
    public static Object get(String name) {
        Object value;

        try {
            value = providedValues.get(name);
        } catch (NullPointerException npe) {
            throw new RuntimeException(String.format("Data storage does not contain parameter [%s]", name));
        }

        return value;
    }

    /*
     * Set stored id value by selected name
     * Use when one test case get data created in another test case
     */
    public static void store(String name, Object id)
    {
        providedValues.put(name, id);
    }


}
