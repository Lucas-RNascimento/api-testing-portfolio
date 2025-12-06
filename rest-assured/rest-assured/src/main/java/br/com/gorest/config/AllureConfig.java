package br.com.gorest.config;

import io.qameta.allure.restassured.AllureRestAssured;

public class AllureConfig {

    public static AllureRestAssured getAllureFilter() {
        return new AllureRestAssured();
    }
}
