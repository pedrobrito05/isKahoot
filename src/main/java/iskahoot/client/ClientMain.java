package iskahoot.client;

import iskahoot.client.ui.GameScreen;
import iskahoot.model.Connection;
import iskahoot.model.Question;
import iskahoot.model.Leaderboard;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) {

        // === 1. Validação de argumentos ===
        if (args.length < 5) {
            System.err.println("Uso: java ClientMain <ip> <porta> <username> <teamCode> <roomCode>");
            System.exit(1);
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        String teamCode = args[3];
        String roomCode = args[4];

        Connection conn = null;
        final GameScreen[] screenHolder = new GameScreen[1];

        try {
            // === 2. Conexão ===
            conn = new Connection(ip, port);
            conn.send(username);
            conn.send(teamCode);
            conn.send(roomCode);

            Connection finalConn = conn;

            // === 3. Criar GUI (EDT) ===
            SwingUtilities.invokeAndWait(() ->
                    screenHolder[0] = new GameScreen(username, finalConn)
            );

            // === 4. Loop de receção ===
            while (true) {
                Object obj = conn.receive();

                if (obj instanceof Question question) {

                    SwingUtilities.invokeLater(() -> {
                        if (screenHolder[0] != null) {
                            screenHolder[0].updateQuestion(question);
                        }
                    });

                } else if (obj instanceof Leaderboard leaderboard) {

                    SwingUtilities.invokeLater(() -> {
                        if (screenHolder[0] != null) {
                            screenHolder[0].updateLeaderboard(leaderboard);
                        }
                    });

                } else if (obj instanceof String msg && msg.equals("GAME_OVER")) {

                    break;

                } else {
                    System.out.println("Objeto desconhecido recebido: " + obj);
                }
            }

            // === 5. Fim de jogo ===
            SwingUtilities.invokeLater(() -> {
                if (screenHolder[0] != null) {
                    screenHolder[0].showGameOver("Fim do jogo! Obrigado por jogar.");
                }
            });

        } catch (EOFException e) {

            System.out.println("Servidor encerrou a ligação.");
            SwingUtilities.invokeLater(() -> {
                if (screenHolder[0] != null) {
                    screenHolder[0].showGameOver("O servidor terminou o jogo.");
                }
            });

        } catch (Exception e) {

            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Erro de ligação: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
