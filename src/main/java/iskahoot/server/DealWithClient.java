package iskahoot.server;

import iskahoot.client.ui.GameScreen;
import iskahoot.model.Answer;
import iskahoot.model.Connection;
import iskahoot.model.Question;
import iskahoot.objects.Player;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;

public class DealWithClient extends Thread {

    private final Connection conn;
    private final Game game;
    private String username;
    private String teamCode;
    private String roomCode;
    private CyclicBarrier barrier;
    public DealWithClient(Connection conn, Game game, CyclicBarrier barrier) {
        this.conn = conn;
        this.game = game;
        this.barrier=barrier;
    }


    @Override
    public void run() {
        //variaveis para verificar o tempo demorado a responder
        long tempo1=0;
        long tempo2 = 0;
        long tempo3=0;
        try {

            // Receber identificação inicial
            username = (String) conn.receive();
            teamCode = (String) conn.receive();
            roomCode = (String) conn.receive();

            //codigo para adicionar o jogador a team
            Player player = new Player(username);
            if(game.getTeam(teamCode)!=null){//e se ta cheia
                game.getTeam(teamCode).addPlayer(player);
            }else{
                System.err.print("A equipa nao existe ou esta cheia");
            }



            // Loop principal do jogo
            while (!game.isGameFinished()) {

                try {
                    // Enviar pergunta atual
                    conn.send(game.getCurrentQuestion());
                    tempo1=System.currentTimeMillis();
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
                    tempo2=System.currentTimeMillis();
                    barrier.await();
                }
                //calculo do tempo passado (verificar se deveria ficar aqui)
                tempo3=tempo2-tempo1;
                System.out.println("demoraste " +tempo3+" a responder");
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
