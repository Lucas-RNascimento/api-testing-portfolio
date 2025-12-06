package br.com.gorest.config;

public class EnvConfig {

    public static String getToken() {
        String token = System.getenv("GOREST_TOKEN");

        if (token == null || token.isBlank()) {
            throw new RuntimeException("GOREST_TOKEN não encontrado! Configure como variável de ambiente.");
        }

        return token;
    }
}
