package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.BaseTestcase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static lib.DataGenerator.getGenerationData;
import static lib.AssertionsCustom.*;

public class UserEditTest extends BaseTestcase {
    @Test
    public void editJustCreatedTest(){
        //GENERATE user
        Map<String, String> userData = getGenerationData();

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        
        //EDIT first name
        String newName = "changedName";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth,"x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth,"auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET user data - проверям изменились ли данные
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", responseGetAuth.getHeader("x-csrf-token"))
                .cookie("auth_sid", responseGetAuth.getCookie("auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        assertJsonByName(responseUserData, "firstName", newName);
    }
}
