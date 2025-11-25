package iskahoot.client;

import iskahoot.client.ui.GameScreen;

public class ClientMain {

    public static void main(String[] args) {

        if (args.length < 5) {
            System.out.println("args invalidos");
            System.exit(1);
        }

        String ip = args[0];                  // ip do servidor (ex: localhost)
        int port = Integer.parseInt(args[1]); // porta do servidor
        String username = args[2];            // nome do jogador
        String teamCode = args[3];            // nome da equipa
        String roomCode = args[4];            // código da sala

        System.out.println("Conectando ao servidor " + ip + ":" + port);
        System.out.println("Username: " + username + " | Equipa: " + teamCode + " | Sala: " + roomCode);

        // Inicializa GameScreen mantendo interface gráfica
        GameScreen gameScreen = new GameScreen(username);

        // Mostra perguntas em Swing
        javax.swing.SwingUtilities.invokeLater(gameScreen::show);
    }
}
