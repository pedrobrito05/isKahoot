package iskahoot.client;

import iskahoot.client.ui.GameScreen;
import iskahoot.model.Connection;
import iskahoot.model.Question;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;

public class ClientMain {

    public static void main(String[] args) {
        // 1. Validação de argumentos de entrada
        if (args.length < 5) {
            System.err.println("Erro: Argumentos insuficientes.");
            System.out.println("Uso: java ClientMain <ip> <porta> <username> <teamCode> <roomCode>");
            System.exit(1);
        }

        String ip = args[0];
        int port = Integer.parseInt(args[1]);
        String username = args[2];
        String teamCode = args[3];
        String roomCode = args[4];

        System.out.println("A ligar ao servidor " + ip + ":" + port + "...");
        System.out.println("Jogador: " + username + " | Equipa: " + teamCode + " | Sala: " + roomCode);

        Connection conn = null;
        // Usamos um array de um elemento para poder aceder à referência dentro do lambda do Swing
        final GameScreen[] screenHolder = new GameScreen[1];

        try {
            // 2. Estabelecer conexão e enviar dados iniciais de registo
            conn = new Connection(ip, port);
            conn.send(username);
            conn.send(teamCode);
            conn.send(roomCode);

            final Connection finalConn = conn;

            // 3. Iniciar a Interface Gráfica (na Event Dispatch Thread do Swing)
            // A janela começa em estado de "Aguardando..."
            SwingUtilities.invokeAndWait(() -> {
                screenHolder[0] = new GameScreen(username, finalConn);
            });

            // 4. Loop principal de receção de objetos do servidor
            while (true) {
                // Bloqueia aqui até que o servidor envie algo (Pergunta ou comando de fim)
                Object obj = conn.receive();

                if (obj instanceof Question) {
                    Question question = (Question) obj;

                    // Atualiza a janela existente com a nova pergunta
                    SwingUtilities.invokeLater(() -> {
                        if (screenHolder[0] != null) {
                            screenHolder[0].updateQuestion(question);
                        }
                    });
                } else {
                    // Se receber um objeto que não é Question (null, String de fim, etc)
                    System.out.println("Sinal de fim de jogo recebido.");
                    break;
                }
            }

            // 5. Se o loop quebrar normalmente, mostra tela de fim de jogo
            SwingUtilities.invokeLater(() -> {
                if (screenHolder[0] != null) {
                    screenHolder[0].showGameOver("Fim do Jogo");
                }
            });

        } catch (EOFException e) {
            // Captura quando o servidor fecha a ligação abruptamente
            System.out.println("Conexão terminada pelo servidor.");
            SwingUtilities.invokeLater(() -> {
                if (screenHolder[0] != null) {
                    screenHolder[0].showGameOver("O servidor encerrou a sessão.");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro de conexão: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}