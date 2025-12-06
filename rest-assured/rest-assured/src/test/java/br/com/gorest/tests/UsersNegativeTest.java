package br.com.gorest.tests;

import br.com.gorest.config.ApiConfig;
import br.com.gorest.config.EnvConfig;
import br.com.gorest.models.User;
import br.com.gorest.specs.RequestSpec;
import br.com.gorest.specs.ResponseSpec;

import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UsersNegativeTest {

    static String token;

    @BeforeAll
    static void setup() {
        ApiConfig.setup();
        token = EnvConfig.getToken();
    }

    // ----------------------------------------
    // 1) Criar usuário sem token
    // ----------------------------------------
    @Test
    @DisplayName("Erro 401 ao criar usuário sem token")
    void testCreateUserNoToken() {

        User user = new User("Joao Test", "male",
                "joao_" + System.currentTimeMillis() + "@gmail.com",
                "active");

        given()
                .spec(RequestSpec.defaultSpec("")) // token vazio
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(401)
                .body("message", containsString("Authentication failed"));
    }

    // ----------------------------------------
    // 2) Payload inválido (sem email)
    // ----------------------------------------
    @Test
    @DisplayName("Erro 422 ao enviar payload inválido")
    void testPayloadInvalid() {

        User user = new User();
        user.setName("Teste Sem Email"); // faltando email

        given()
                .spec(RequestSpec.defaultSpec(token))
                .body(user)
                .when()
                .post("/users")
                .then()
                .statusCode(422)
                .body("message", notNullValue());
    }

    // ----------------------------------------
    // 3) Email duplicado
    // ----------------------------------------
    @Test
    @DisplayName("Erro 422 ao criar usuário com email duplicado")
    void testDuplicateEmail() {

        String duplicatedEmail = "duplicado_" + System.currentTimeMillis() + "@gmail.com";

        // cria usuário 1
        User user1 = new User("Lucas", "male", duplicatedEmail, "active");

        given()
                .spec(RequestSpec.defaultSpec(token))
                .body(user1)
                .post("/users")
                .then()
                .statusCode(201);

        // cria usuário 2 com mesmo email
        User user2 = new User("João", "male", duplicatedEmail, "active");

        given()
                .spec(RequestSpec.defaultSpec(token))
                .body(user2)
                .when()
                .post("/users")
                .then()
                .statusCode(422)
                .body("message", notNullValue());
    }

    // ----------------------------------------
    // 4) Buscar usuário inexistente (404)
    // ----------------------------------------
    @Test
    @DisplayName("Erro 404 ao buscar usuário inexistente")
    void testUserNotFound() {

        int fakeId = 99999999;

        given()
                .spec(RequestSpec.defaultSpec(token))
                .when()
                .get("/users/" + fakeId)
                .then()
                .statusCode(404)
                .body("message", containsString("Resource not found"));
    }

    // ----------------------------------------
    // 5) DELETE com id inexistente
    // ----------------------------------------
    @Test
    @DisplayName("Erro 404 ao excluir usuário inexistente")
    void testDeleteNotFound() {

        int fakeId = 99999999;

        given()
                .spec(RequestSpec.defaultSpec(token))
                .when()
                .delete("/users/" + fakeId)
                .then()
                .statusCode(404)
                .body("message", containsString("Resource not found"));
    }
}
