import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {
    @Test
    public void testRestAssured(){
        Map<String, String> params = new HashMap<>(); //объявляем переменную для хранения payload - набора параметров и значений для нашего запроса
        params.put("name","John"); //заполняем хэшмэпу значениями

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String name = response.get("answer2");
        if (name == null ) {
            System.out.println("The key 'answer2' is absent");
        }
        else {
            System.out.println(name);
        }
        //System.out.println(response.htmlPath().getNode("body").toString());
        //response.prettyPrint();
    }

}
