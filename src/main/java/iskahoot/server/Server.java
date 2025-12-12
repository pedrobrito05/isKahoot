package iskahoot.server;

import iskahoot.model.Connection;
import iskahoot.model.QuestionLoader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        GameState games = new GameState();

        Thread menu=new ServerTUI(games);



        try (ServerSocket server = new ServerSocket(8888)) {
            System.out.println("Servidor à escuta na porta 8888...");
            menu.start();
            while (true) {
                Socket client = server.accept();
                System.out.println("Cliente ligado!");

                Connection conn = new Connection(client);

                // DealWithClient escolhe o game certo pelo roomCode
                Thread clients=new DealWithClient(conn, games);
                clients.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TUI
}
