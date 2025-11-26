package iskahoot.client;

import iskahoot.client.ui.GameScreen;
import iskahoot.model.Question;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientMain extends Thread{

//    public static void main(String[] args) {
//
//        if (args.length < 5) {
//            System.out.println("args invalidos");
//            System.exit(1);
//        }
//
//        String ip = args[0];                  // ip do servidor (ex: localhost)
//        int port = Integer.parseInt(args[1]); // porta do servidor
//        String username = args[2];            // nome do jogador
//        String teamCode = args[3];            // nome da equipa
//        String roomCode = args[4];            // código da sala
//
//        System.out.println("Conectando ao servidor " + ip + ":" + port);
//        System.out.println("Username: " + username + " | Equipa: " + teamCode + " | Sala: " + roomCode);
//
//        // Inicializa GameScreen mantendo interface gráfica
//        GameScreen gameScreen = new GameScreen(username);
//
//        // Mostra perguntas em Swing
//        javax.swing.SwingUtilities.invokeLater(gameScreen::show);
//    }

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

        Socket client = new Socket( "localhost" , 8888);
        ObjectInputStream in =
                new ObjectInputStream ( client.getInputStream());
        ObjectOutputStream out =
                new ObjectOutputStream ( client.getOutputStream());

        Object objectQ=in.readObject();
        Question question=(Question)objectQ;
        System.out.println(question);
        GameScreen scr=new GameScreen(username);
        scr.showQuestion(question);

        out.close();
        in.close();
        client.close ();
    }



}
