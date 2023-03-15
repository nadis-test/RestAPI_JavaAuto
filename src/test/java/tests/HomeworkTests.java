package tests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import lib.AssertionsCustom;

import static org.junit.jupiter.api.Assertions.*;

public class HomeworkTests {

    @Test
    public void parseJSON() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        System.out.println("\nJSON response");
        response.prettyPrint();

        String message = response.get("messages.message[1]");
        if (message != null) {
            System.out.println("\nsecond message text:");
            System.out.println(message);
        } else {
            System.out.println("\nsecond message does not exist");
        }
    }

    @Test
    public void printAllRedirects() {
        int redirects_number = 0;
        int statusCode = 0;
        String url = "https://playground.learnqa.ru/api/long_redirect";
        String location;
        while (statusCode != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(url)
                    .andReturn();

            Headers headers = response.getHeaders();
            System.out.println("\nHeaders print on iteration = " + redirects_number);
            System.out.println(headers);

            statusCode = response.getStatusCode();
            System.out.println("\nStatusCode: " + statusCode);

            url = response.getHeader("location");
            System.out.println("Redirect address (location): " + url);

            redirects_number++;
        }

        System.out.println("Final redirects number: " + (redirects_number - 1));
    }

    @Test
    public void checkLongJobStatus() throws InterruptedException {
        String status_ready = "Job is ready";
        String status_not_ready = "Job is NOT ready";
        String error = "";
        String status = status_not_ready;
        int time = 0;

        // 1 заводим задачу
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();

        //получаем токен
        String token = response.getString("token");
        System.out.println("Token value: " + token);
        //получаем время джобы
        time = response.getInt("seconds");
        System.out.println("Time value: " + time);

        while ((status.compareTo(status_not_ready) == 0)) {
            //выполняем запрос с токеном
            JsonPath response1 = RestAssured
                    .given()
                    .queryParam("token", token)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();
            //извлекаем ошибку из ответа
            error = response1.getString("error");
            //если оишбки нет
            if (error == null) {
                // печатаем ответ
                System.out.println("\nResponse print");
                response1.prettyPrint();
                // узнаем статус
                status = response1.getString("status");
                System.out.println("\nJob status: " + status);
                // если job is not ready, то запускаем ожидание
                if (status.compareTo(status_not_ready) == 0) {
                    // запускаем ожидание
                    System.out.println("Time wait millisec: " + (time) * 1000);
                    Thread.sleep((time) * 1000);
                }
                // если job is ready, то проверяем наличие результата и печатаем его
                else if (status.compareTo(status_ready) == 0) {
                    String result = response1.getString("result");
                    if (result != null) {
                        System.out.println("\nJob result: " + result);
                    } else {
                        System.out.println("\nError: 'result' is missing in response");
                    }
                }
            } else {
                System.out.println("\nError: " + error);
            }

        }
    }

    @Test
    public void findPassword() throws FileNotFoundException {

        Set<String> passwords = new HashSet<>();

        String fileName = "/Users/n.porotkova/GitHub/RestAPI_JavaAuto/top_passwords_SplashData.csv";

        Scanner sc = new Scanner(new File(fileName));
        sc.useDelimiter("\n");
        while (sc.hasNext())
            {
                passwords.addAll(Arrays.asList(sc.next().split(",")));
            }
        sc.close();

        System.out.println("\n Top passwords list: " + passwords);

        String[] password_strings = new String[passwords.size()];
        passwords.toArray(password_strings);

        String login = "super_admin";
        Map<String, String> payload = new HashMap<>();
        payload.put("login", login);

        String status_no_auth = "You are NOT authorized";
        String status_auth = "You are authorized";
        String cookie_status = status_no_auth;
        String current_password = "";
        int password_number = 0;

        while ((cookie_status.compareTo(status_no_auth) == 0) && (password_number < passwords.size()) ) {

            //берем пароль из массива
            payload.put("password", password_strings[password_number]);

            current_password = payload.get("password");
            System.out.println("\nlogin: " + login);
            System.out.println("password: " + current_password);
            //делаем запрос на авторизацию с этим паролем
            Response response = RestAssured
                    .given()
                    .body(payload)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
            int response_code = response.getStatusCode();
            if (response_code != 200) {
                System.out.println("login is wrong or missing");
            }
            else {
                //получаем значение куки
                String authCookie = response.getCookie("auth_cookie");
                //создаем куки для передачи в запрос
                Map<String, String> cookies = new HashMap<>();
                cookies.put("auth_cookie", authCookie);
                System.out.println("auth_cookie value: " + authCookie);

                //вызываем запрос проверки куки и передаем туда созданную куки
                Response response_check_cookie = RestAssured
                        .given()
                        .cookies(cookies)
                        .when()
                        .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                        .andReturn();

                cookie_status = response_check_cookie.htmlPath().getNode("body").toString();
                System.out.println("check_auth_cookie message: " + cookie_status);
                password_number++;
            }
        }

        if (cookie_status == "You are NOT authorized") {
            System.out.println("\ncorrect password was NOT found");
        }
        else {
            System.out.println("\ncorrect password was found:" + current_password);
        }
    }

    @ParameterizedTest
    @ValueSource (strings = {"1234567890123456", "123456789012345", "1234567890", ""})
    public void testShortString(String string){
        assertTrue(string.length() > 15, "String length is equal or less than 15 symbols");

    }

    @Test
    public void printCookiesNamesAndValues(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        //создаем хранилище всех потенциальных кук в response
        Map<String, String> cookies = new HashMap<>();
        //получаем куки в наше хранилище
        cookies = response.getCookies();

        //получаем все куки без значений
        Set<String> cookie_names = cookies.keySet();
        //проверяем, что в ответе есть хоть какие-то куки
        assertFalse(cookie_names.isEmpty(), "There is no cookies");

        //создаем итератор, чтобы обойти все куки и получить их значения
        Iterator itr = cookie_names.iterator();

        while (itr.hasNext()){
            String cookie_name = itr.next().toString();
            String cookie_value = cookies.get(cookie_name);
            //печатаем название и значение
            System.out.println("cookie: " + cookie_name + " = " + cookie_value);
        }
    }

    @Test
    public void checkCookieName(){
        String exp_name = "HomeWork";
        String exp_value = "hw_value";

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        String value = response.getCookie(exp_name);

        assertTrue(response.getCookies().containsKey(exp_name), "response has no cookie '" + exp_name + "'");
        assertEquals(exp_value, value, "cookie '" + exp_name + "' has wrong value: " + value);
    }



    @Test
    public void checkResponseHeaders(){
        String exp_name = "x-secret-homework-header";
        String exp_value = "Some secret value";

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        response.prettyPrint();
        //получаем хэдеры
        Headers headers = response.getHeaders();
        //проверяем, что в ответе есть хоть какие-то хэдеры
        assertTrue(headers.exist(), "There is no headers");
        //проверяем, что в ответе есть нужный хэдер
        assertTrue(headers.hasHeaderWithName(exp_name), "response has no header '" + exp_name + "'");
        //проверяем, что у нужного хэдера правильное значение
        String value = headers.getValue(exp_name);
        assertEquals(exp_value, value, "header '" + exp_name + "' has wrong value: " + value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"'Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30'&Mobile&No&Android",
    "'Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1'&Mobile&Chrome&iOS",
    "'Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)'&Googlebot&Unknown&Unknown",
    "'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0'&Web&Chrome&No",
    "'Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1'&Mobile&No&iPhone"})
    public void getParametersByUserAgent(String data){
        System.out.println("data: " + data);
        //разбираем строку с параметрами теста на отдельные значения
        String[] values = data.split("&", -1);
        //если параметров не 4 (UA, platform_expected, browser, device_expected) - тест не продолжаем
        assertEquals(4, values.length, "Wrong number of test parameters");
        String userAgent = values[0];
        String platform_expected = values[1];
        String browser_expected = values[2];
        String device_expected = values[3];

        Response response = RestAssured
                .given()
                .header("user-agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();

        AssertionsCustom.assertJsonStringByName(response, "platform", platform_expected);
        AssertionsCustom.assertJsonStringByName(response, "browser", browser_expected);
        AssertionsCustom.assertJsonStringByName(response, "device", device_expected);
    }
}


