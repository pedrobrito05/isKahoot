package iskahoot.model;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class QuestionLoader {

    public static Quiz loadFromFile(String resourcePath) {


        try (Reader reader = new InputStreamReader(
                QuestionLoader.class.getResourceAsStream(resourcePath))) {

            if (reader == null) {
                System.out.println("Arquivo " + resourcePath + " não encontrado no classpath!");
                return null;
            }

            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            Gson gson = new Gson();

            var quizzes = root.getAsJsonArray("quizzes");
            JsonObject quizObj = quizzes.get(0).getAsJsonObject();

            Quiz quiz = gson.fromJson(quizObj, Quiz.class);
            return quiz;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}