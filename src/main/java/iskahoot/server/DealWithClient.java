package iskahoot.server;

import iskahoot.client.ui.GameScreen;
import iskahoot.model.Answer;
import iskahoot.model.Connection;
import iskahoot.model.Question;
import iskahoot.objects.Player;

import java.io.IOException;

public class DealWithClient extends Thread {

    private final Connection conn;
    private final Game game;
    private String username;
    private String teamCode;
    private String roomCode;

    public DealWithClient(Connection conn, Game game) {
        this.conn = conn;
        this.game = game;
    }


    @Override
    public void run() {

        try {

            // Receber identificação inicial
            username = (String) conn.receive();
            teamCode = (String) conn.receive();
            roomCode = (String) conn.receive();

            Player player = new Player(username);

            // Loop principal do jogo
            while (!game.isGameFinished()) {

                try {
                    // Enviar pergunta atual
                    conn.send(game.getCurrentQuestion());
                } catch (IOException e) {
                    System.err.println("Erro ao enviar pergunta para " + username);
                    break; // sai do ciclo, o cliente caiu
                }

                Object obj;
                try {
                    obj = conn.receive();
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Erro ao receber resposta do cliente " + username);
                    break; // cliente desconectado
                }

                if (obj instanceof Answer) {
                    Answer answer = (Answer) obj;
                    System.out.println("Pergunta recebida do cliente: " + answer.getAnswer());
                    game.nextQuestion();
                }
            }

        } catch (Exception e) {
            System.err.println("Erro inesperado no DealWithClient: " + e.getMessage());
            e.printStackTrace();

        } finally {
            // Fechar ligação de forma segura
            try {
                conn.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar ligação do cliente " + username);
            }
        }
    }
}
