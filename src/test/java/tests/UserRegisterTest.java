package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestcase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.AssertionsCustom.*;
import static lib.DataGenerator.getRandomEmail;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserRegisterTest extends BaseTestcase {
    @Test
    public void createUserWithExistingEmail(){
        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", "123");
        userData.put("username", "learnqa");
        userData.put("firstName", "learnqa");
        userData.put("lastName", "learnqa");

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        assertResponseCodeEquals(responseCreateAuth, 400);
        assertResponseTextEquals(responseCreateAuth, "Users with email '"+email+"' already exists");
    }

    @Test
    public void createUserWithUniqueEmail(){
        String email = getRandomEmail();
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", "123");
        userData.put("username", "learnqa");
        userData.put("firstName", "learnqa");
        userData.put("lastName", "learnqa");

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        assertResponseCodeEquals(responseCreateAuth, 200);
        assertJsonHasKey(responseCreateAuth,"id");
    }
}
