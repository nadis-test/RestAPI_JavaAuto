package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestcase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.AssertionsCustom.*;
import static lib.DataGenerator.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lib.ApiCoreRequests;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class UserRegisterTest extends BaseTestcase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Epic("User registration cases")
    @Feature("Registration")

    @Test
    @Description("This test creates user with existing email")
    @DisplayName("Test negative: create user with existing email")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink(value = "CASE-201")
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
    @Description("This test creates user with unique email")
    @DisplayName("Test positive: create user with unique email")
    @Stories({@Story("Positive"), @Story("Acceptance")})
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "CASE-202")
    public void createUserWithUniqueEmail(){
        Map<String, String> userData = getGenerationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        assertResponseCodeEquals(responseCreateAuth, 200);
        assertJsonHasField(responseCreateAuth,"id");
    }

    @Test
    @Description("This test creates user with wrong email format: no '@' symbol")
    @DisplayName("Test negative: create user with wrong email format")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.NORMAL)
    @TmsLink(value = "CASE-203")
    public void createUserWithIncorrectEmailFormat(){
        String email = "testexample.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = getGenerationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        assertResponseCodeEquals(responseCreateAuth, 400);
        assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @ParameterizedTest
    @Description("This test creates user with missing registration data")
    @DisplayName("Test negative: create user with missing registration data")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.NORMAL)
    @TmsLink(value = "CASE-204")

    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void createUserWithMissingUserDataValue(String userDataField){

        Map<String, String> userData = getGenerationData();
        userData.remove(userDataField);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        assertResponseCodeEquals(responseCreateAuth, 400);
        assertResponseTextEquals(responseCreateAuth, "The following required params are missed: "+ userDataField);
    }

    @Test
    @Description("This test creates user with wrong format username: only one symbol short")
    @DisplayName("Test negative: create user with too short username")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.NORMAL)
    @TmsLink(value = "CASE-205")
    public void createUserWithTooShortUsername(){
        String username = getRandomUsername(1);
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = getGenerationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        assertResponseCodeEquals(responseCreateAuth, 400);
        assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @Test
    @Description("This test creates user with wrong format username: more 250 symbols")
    @DisplayName("Test negative: create user with too long username")
    @Stories({@Story("Negative"), @Story("Regression")})
    @Severity(SeverityLevel.NORMAL)
    @TmsLink(value = "CASE-207")
    public void createUserWithTooLongUsername(){
        String username = getRandomUsername(251);
        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = getGenerationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);

        assertResponseCodeEquals(responseCreateAuth, 400);
        assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }

}
