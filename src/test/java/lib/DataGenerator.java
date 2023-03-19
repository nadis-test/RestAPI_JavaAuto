package lib;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    public static String getRandomEmail(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "learnqa" + timestamp + "@example.com";
    }

    public static String getRandomUsername(int length){
        // choose a Character random from this String
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = (int)(characters.length() * Math.random());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    public static Map<String, String> getGenerationData(){
        Map<String, String> userData = new HashMap<>();
        userData.put("email", getRandomEmail());
        userData.put("password", "123");
        userData.put("username", "learnqa");
        userData.put("firstName", "learnqa");
        userData.put("lastName", "learnqa");

        return userData;
    }

    public static Map<String, String> getGenerationData(Map<String, String> non_default_data){
        //создаем мапу с дефолтными данными
        Map<String, String> default_data = getGenerationData();

        //заводим мапу для рег данных
        Map<String, String> userData = new HashMap<>();
        //создаем массив с названиями дефолтных полей
        String[] keys = {"username", "email", "firstName", "lastName", "password"};
        //в цикле проверяем - если в non_default_values нам пришло значение, отличное от дефолтного,
        // то кладем его в мапу userData
        for (String key : keys){
            if (non_default_data.containsKey(key)){
                userData.put(key, non_default_data.get(key));
            }
            else {
                userData.put(key, default_data.get(key));
            }
        }


        return userData;
    }
}
