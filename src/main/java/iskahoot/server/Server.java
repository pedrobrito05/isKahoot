package iskahoot.server;

import iskahoot.model.Answer;
import iskahoot.model.Connection;
import iskahoot.model.QuestionLoader;
import iskahoot.model.Quiz;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CyclicBarrier;


public class Server {
    public static void main(String[] args) {

        Game game = new Game("11", 2, QuestionLoader.loadFromFile("/questions.json"));

        //cyclicbarrrier faz as threads esperarem umas pelas outras, agora teste apenas com dois jogadores
        CyclicBarrier barrier=new CyclicBarrier(2, ()->{
            //codigo corrido quando todos os jogadores respondem
            System.out.println("Todos os jogadores responderam");
            game.nextQuestion();
        });

        try (ServerSocket server = new ServerSocket(8888)) {
            System.out.println("Servidor à escuta na porta 8888...");

            while (true) {
                Socket client = server.accept();
                System.out.println("Cliente ligado!");

                // cria handler para o cliente
                Connection conn = new Connection(client);
                DealWithClient handler = new DealWithClient(conn, game, barrier);
                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



