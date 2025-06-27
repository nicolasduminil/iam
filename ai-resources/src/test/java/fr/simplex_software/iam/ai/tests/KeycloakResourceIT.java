package fr.simplex_software.iam.ai.tests;

import io.quarkus.test.junit.*;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class KeycloakResourceIT {

  @Test
  public void testAskFailedLoginsQuestion() {
    String userInput = "How many failed logins happened in the last 7 days?";

    given()
      .body(userInput)
      .contentType("text/plain")
      .when()
      .post("/keycloak-nlq/ask")
      .then()
      .statusCode(200)
      .body(not(emptyString()))
      .body(containsStringIgnoringCase("failed login"));  // Basic assertion on the answer
  }

  @Test
  public void testAskRoleUserListQuestion() {
    String userInput = "List users with role 'manager'";

    given()
      .body(userInput)
      .contentType("text/plain")
      .when()
      .post("/keycloak-nlq/ask")
      .then()
      .statusCode(200)
      .body(not(emptyString()))
      .body(anyOf(containsString("user"), containsString("manager")));  // Loose check
  }

  @Test
  public void testAskInvalidQuestion() {
    String userInput = "What's the weather in Paris today?";

    given()
      .body(userInput)
      .contentType("text/plain")
      .when()
      .post("/keycloak-nlq/ask")
      .then()
      .statusCode(200)
      .body(anyOf(
        containsStringIgnoringCase("Sorry"),
        containsStringIgnoringCase("cannot answer"),
        containsStringIgnoringCase("unknown"),
        containsStringIgnoringCase("Keycloak")
      ));
  }
}
