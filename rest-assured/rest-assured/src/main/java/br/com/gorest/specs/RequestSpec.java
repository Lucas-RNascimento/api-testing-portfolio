package br.com.gorest.specs;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import br.com.gorest.config.AllureConfig;


public class RequestSpec {

    public static RequestSpecification defaultSpec(String token) {
        return new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + token)
                .addFilter(AllureConfig.getAllureFilter())  // ← AQUI!
                .build();
    }


}
