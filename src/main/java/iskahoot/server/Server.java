package iskahoot.server;

import iskahoot.model.QuestionLoader;
import iskahoot.model.Quiz;

public class Server {
    //TODO o server é que verifica se a resposta esta correta


    public static void main(String[] args){
        System.out.println("current question: ");
        //Quiz quiz=QuestionLoader.loadFromFile("src/main/resources/questions.json");
        Game game=new Game("11", 2, QuestionLoader.loadFromFile("/questions.json"));

    }
}
