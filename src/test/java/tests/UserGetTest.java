package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestcase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.AssertionsCustom.*;

public class UserGetTest extends BaseTestcase {
    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = RestAssured
            .get("https://playground.learnqa.ru/api/user/2")
            .andReturn();

        assertJsonHasField(responseUserData, "username");
        assertJsonHasNotField(responseUserData, "email");
        assertJsonHasNotField(responseUserData, "firstName");
        assertJsonHasNotField(responseUserData, "lastName");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com");
        userData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String cookie = this.getCookie(responseGetAuth, "auth_sid"); // вызываем getCookie из lib/BaseTestcase
        String header = this.getHeader(responseGetAuth,"x-csrf-token"); // вызываем getHeader из lib/BaseTestcase
        int user_id = responseGetAuth.jsonPath().getInt("user_id");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/" + user_id)
                .andReturn();

        System.out.println(responseUserData.asString());
        String[] fields = {"username", "email", "firstName", "lastName"};
        assertJsonHasFields(responseUserData, fields);
    }
}
