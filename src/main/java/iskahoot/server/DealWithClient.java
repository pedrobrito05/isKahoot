package iskahoot.server;

import iskahoot.model.Answer;
import iskahoot.model.Connection;
import iskahoot.objects.Player;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;

public class DealWithClient extends Thread {

    private final Connection conn;
    private final GameState games;
    private Game game;
    private String username;

    // Objeto usado apenas para sincronizar as duas threads (o "Monitor")
    private final Object lock = new Object();

    // Variável partilhada onde a thread de leitura coloca a resposta
    private Answer sharedAnswer = null;

    // Controlo para parar a thread de leitura no fim
    private volatile boolean running = true;

    public DealWithClient(Connection conn, GameState games) {
        this.conn = conn;
        this.games = games;
    }

    @Override
    public void run() {
        try {
            // --- HANDSHAKE INICIAL ---
            username = (String) conn.receive();
            String teamCode = (String) conn.receive();
            String roomCode = (String) conn.receive();

            game = games.getGame(roomCode);
            if (game == null) {
                conn.close();
                return;
            }

            Player player = new Player(username);
            if (game.canJoinTeam(teamCode)) {
                game.getTeam(teamCode).addPlayer(player);
                game.playerJoined();
            } else {
                conn.send("Equipa cheia");
                conn.close();
                return;
            }

            game.waitForGameStart();

            // --- INICIAR THREAD DE LEITURA (LISTENER) ---
            // Esta thread fica num loop infinito a ler do socket e a avisar a thread principal
            Thread listenerThread = new Thread(() -> {
                while (running) {
                    try {
                        Object obj = conn.receive(); // Bloqueia aqui

                        if (obj instanceof Answer) {
                            synchronized (lock) {
                                sharedAnswer = (Answer) obj; // Guarda a resposta
                                lock.notify(); // ACORDA a thread principal imediatamente
                            }
                        }
                    } catch (Exception e) {
                        // Se der erro na leitura (ex: socket fechou), paramos o loop
                        if (running) System.out.println("Listener parou para " + username);
                        break;
                    }
                }
            });
            listenerThread.start();

            // --- LOOP DO JOGO ---
            while (!game.isGameFinished()) {

                // Limpar resposta anterior antes de enviar nova pergunta
                synchronized (lock) {
                    sharedAnswer = null;
                }

                try {
                    conn.send(game.getCurrentQuestion());
                } catch (IOException e) {
                    break;
                }

                long tempoInicio = System.currentTimeMillis();
                Answer finalAnswer = null;

                // --- ESPERAR PELA RESPOSTA OU TIMEOUT ---
                synchronized (lock) {
                    // Se a resposta ainda não chegou, vamos dormir
                    if (sharedAnswer == null) {
                        try {
                            // Espera 30s OU até que o listener faça notify()
                            lock.wait(30_000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    // Quando o wait acaba (ou pelo tempo, ou pelo notify), pegamos no valor
                    finalAnswer = sharedAnswer;
                }
                // ----------------------------------------

                if (finalAnswer != null) {
                    long tempoFim = System.currentTimeMillis();
                    System.out.println(username + " respondeu: " + finalAnswer.getAnswer() + " (" + (tempoFim-tempoInicio) + "ms)");
                } else {
                    System.out.println("Timeout: " + username + " não respondeu.");
                    finalAnswer = new Answer(-1, 30_000);
                }

                // Sincronização (Barreiras)
                CyclicBarrier currentBarrier = game.getBarrier();
                ModifiedCountdownLatch currentLatch = (currentBarrier == null) ? game.getLatch() : null;

                if (currentBarrier != null) {
                    try { currentBarrier.await(); } catch (Exception e) {}
                } else if (currentLatch != null) {
                    currentLatch.countdown();
                    try { currentLatch.await(); } catch (InterruptedException e) {}
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            running = false; // Avisa o listener para parar (se não estiver bloqueado no receive)
            try {
                conn.close(); // Isto vai fazer o receive() lançar exceção e matar o listener
            } catch (IOException e) {}
        }
    }
}