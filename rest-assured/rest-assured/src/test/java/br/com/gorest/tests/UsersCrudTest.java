package br.com.gorest.tests;

import br.com.gorest.config.ApiConfig;
import br.com.gorest.config.EnvConfig;
import br.com.gorest.models.User;
import br.com.gorest.specs.RequestSpec;
import br.com.gorest.specs.ResponseSpec;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsersCrudTest {

    static String token;
    static Integer userId;

    @BeforeAll
    static void setup() {
        ApiConfig.setup();
        token = EnvConfig.getToken();
    }

    // ------------------------------------
    // 1) CREATE
    // ------------------------------------
    @Test
    @Order(1)
    @DisplayName("Criar usuário com sucesso")
    void testCreateUser() {

        User user = new User();
        user.setName("Lucas Auto");
        user.setGender("male");
        user.setEmail("lucas_auto_" + System.currentTimeMillis() + "@gmail.com");
        user.setStatus("active");

        userId =
                given()
                        .spec(RequestSpec.defaultSpec(token))
                        .body(user)
                        .when()
                        .post("/users")
                        .then()
                        .spec(ResponseSpec.status201())
                        .body("id", notNullValue())
                        .extract().path("id");

        System.out.println("USER ID GERADO: " + userId);
    }

    // ------------------------------------
    // 2) GET (validar)
    // ------------------------------------
    @Test
    @Order(2)
    @DisplayName("Buscar usuário criado")
    void testGetUser() {

        given()
                .spec(RequestSpec.defaultSpec(token))
                .when()
                .get("/users/" + userId)
                .then()
                .spec(ResponseSpec.status200())
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"))
                .body("id", equalTo(userId));
    }

    // ------------------------------------
    // 3) UPDATE
    // ------------------------------------
    @Test
    @Order(3)
    @DisplayName("Atualizar usuário")
    void testUpdateUser() {

        // 1. Buscar usuário atual
        User existing =
                given()
                        .spec(RequestSpec.defaultSpec(token))
                        .when()
                        .get("/users/" + userId)
                        .then()
                        .statusCode(200)
                        .extract().as(User.class);

        // 2. Alterar apenas o nome
        existing.setName("Lucas Atualizado");

        // 3. Atualizar enviando TODOS os campos obrigatórios
        given()
                .spec(RequestSpec.defaultSpec(token))
                .body(existing)
                .when()
                .patch("/users/" + userId)
                .then()
                .spec(ResponseSpec.status200())
                .body(matchesJsonSchemaInClasspath("schemas/user-schema.json"))
                .body("name", equalTo("Lucas Atualizado"));
    }

    // ------------------------------------
    // 4) DELETE
    // ------------------------------------
    @Test
    @Order(4)
    @DisplayName("Excluir usuário")
    void testDeleteUser() {

        given()
                .spec(RequestSpec.defaultSpec(token))
                .when()
                .delete("/users/" + userId)
                .then()
                .spec(ResponseSpec.status204());
    }

    // ------------------------------------
    // 5) GET após deletar
    // ------------------------------------
    @Test
    @Order(5)
    @DisplayName("Validar que o usuário não existe mais")
    void testGetUserNotFound() {

        given()
                .spec(RequestSpec.defaultSpec(token))
                .when()
                .get("/users/" + userId)
                .then()
                .statusCode(404);
    }
}
