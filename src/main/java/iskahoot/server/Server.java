package iskahoot.server;

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

        System.out.println("current question: ");
        Game game=new Game("11", 2, QuestionLoader.loadFromFile("/questions.json"));

        try (ServerSocket server = new ServerSocket(8888)) {
            System.out.println("Servidor à escuta na porta 8888...");

            Socket client = server.accept();
            System.out.println("Cliente ligado!");

            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());

            //fazer um array com dois espacos para enviar para o cliente com a pergunta e as opcoes
            out.writeObject(game.getCurrentQuestion());


            out.close();
            in.close();
            client.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
