package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestcase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.AssertionsCustom.*;
import static lib.DataGenerator.getGenerationData;
import static lib.DataGenerator.getRandomEmail;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lib.ApiCoreRequests;

public class UserRegisterTest extends BaseTestcase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void createUserWithExistingEmail(){

        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = getGenerationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        assertResponseCodeEquals(responseCreateAuth, 400);
        assertResponseTextEquals(responseCreateAuth, "Users with email '"+email+"' already exists");
    }

    @Test
    public void createUserWithUniqueEmail(){
        Map<String, String> userData = getGenerationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        assertResponseCodeEquals(responseCreateAuth, 200);
        assertJsonHasField(responseCreateAuth,"id");
    }


}
