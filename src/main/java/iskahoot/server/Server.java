package iskahoot.server;

import iskahoot.model.Answer;
import iskahoot.model.QuestionLoader;
import iskahoot.model.Quiz;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class Server {
    //TODO fazer ligacao entre servidor e cliente :)  (nao faco ideia como fazer)


//    public static void main(String[] args){
//        System.out.println("current question: ");
//        Game game=new Game("11", 2, QuestionLoader.loadFromFile("/questions.json"));
//        while(!game.isGameFinished()){
//            System.out.println(game.getCurrentQuestion().getText()+"\n"+game.getCurrentQuestion().getOptions()+ "\n opcao correta: "+game.getCurrentQuestion().getCorrectIndex());
//            game.nextQuestion();
//        }
//
//    }

    //ESTOU APENAS A FAZER O TESTE DE LIGACAO ENTRE CLIENTE E SERVIDOR
    public static void main(String[] args) {


        Game game=new Game("11", 2, QuestionLoader.loadFromFile("/questions.json"));

        try (ServerSocket server = new ServerSocket(8888)) {
            System.out.println("Servidor à escuta na porta 8888...");

            Socket client = server.accept();
            System.out.println("Cliente ligado!");

            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());

            //APENAS TESTE DE ENVIAR E RECEBER RESPOSTA
            while(!game.isGameFinished()){
                out.writeObject(game.getCurrentQuestion());
                Object obj=in.readObject();
                Answer answer=(Answer)obj;
                System.out.println(answer.getAnswer());
                game.nextQuestion();
            }



            out.close();
            in.close();
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
