package br.com.gorest.config;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;

public class ApiConfig {

    public static void setup() {

        RestAssured.baseURI = "https://gorest.co.in/public/v2";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 5000)
                        .setParam("http.socket.timeout", 5000)
                        .setParam("http.connection-manager.timeout", 5000)
                );
    }
}
