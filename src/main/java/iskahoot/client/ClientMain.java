package iskahoot.client;

import iskahoot.client.ui.GameScreen;
import iskahoot.model.Answer;
import iskahoot.model.Connection;
import iskahoot.model.Question;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientMain extends Thread{

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length < 5) {
            System.out.println("args invalidos");
            System.exit(1);
        }
        String ip = args[0];                  // ip do servidor (ex: localhost)
        int port = Integer.parseInt(args[1]); // porta do servidor
        String username = args[2];            // nome do jogador
        String teamCode = args[3];            // nome da equipa
        String roomCode = args[4];            // código da sala
        System.out.println("Username: " + username + " | Equipa: " + teamCode + " | Sala: " + roomCode);



        //inicia a connection
        Connection conn=new Connection(ip,port);




        //enviar dados do cliente
        conn.send(username);
        conn.send(teamCode);
        conn.send(roomCode);



        try {
            while (true) {
                Object obj = conn.receive();
                if (obj instanceof Question) {
                    Question question=(Question)obj;
                    System.out.println(question);
                    GameScreen scr=new GameScreen(username, conn);
                    scr.showQuestion(question);
                } else {
                    break;
                }
            }
        } catch (EOFException e) {
            System.out.println("Conexão fechada pelo servidor.");
            conn.close();
        }


    }



}
