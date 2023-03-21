package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.BaseTestcase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.DataGenerator.getGenerationData;
import static lib.AssertionsCustom.*;

public class UserEditTest extends BaseTestcase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Epic("Edit user data cases")
    @Feature("Edit user data")

    @Test
    @Description("This test edit user data just after creation")
    @DisplayName("Test positive: edit user data just after creation")
    public void editJustCreatedTest(){
        //GENERATE user
        Map<String, String> userData = getGenerationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT first name
        String newName = "changedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        String userDataEndpoint = "https://playground.learnqa.ru/api/user/" + userId;
        String token = this.getHeader(responseGetAuth,"x-csrf-token");
        String cookie =this.getCookie(responseGetAuth,"auth_sid");
        Response responseEditData = apiCoreRequests.makePutRequestWithTokenAndCookie(
                userDataEndpoint,
                editData,
                token,
                cookie);

        //GET user data - проверям изменились ли данные
        Response responseUserData = responseUserData = apiCoreRequests.makeGetRequestWithTokenAndCookie(userDataEndpoint, token, cookie);

        assertJsonByName(responseUserData, "firstName", newName);
    }


}
