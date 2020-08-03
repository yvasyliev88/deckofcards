package com.deckofcards.tests;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeSuite;

@CucumberOptions(features = "src/test/resources/features", glue = { "com.deckofcards.hooks", "com.deckofcards.steps" },
        monochrome = true,
        plugin = {
                "html:target/cucumber-reports/cucumber-html-report",
                "json:target/cucumber-reports/cucumber.json",
                "rerun:target/cucumber-reports/cucumber-reports/rerun.txt",
                "io.qameta.allure.cucumber5jvm.AllureCucumber5Jvm"
        }
)
public class ApiTestRunner extends AbstractTestNGCucumberTests {

    @BeforeSuite(alwaysRun = true)
    public void suiteSetUp() {
        RestAssured.baseURI = System.getProperty("api.baseUrl");
    }
}
