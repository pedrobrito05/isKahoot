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

    private String teamCode;

    private String roomCode;



    private Answer receivedAnswer=null;

    private boolean answer=false;

    public DealWithClient(Connection conn, GameState games) {

        this.conn = conn;

        this.games = games;

    }


    @Override

    public void run() {

        long tempo1=0,tempo2,tempo3;


        try {

// Receber identificação inicial

            username = (String) conn.receive();

            teamCode = (String) conn.receive();

            roomCode = (String) conn.receive();


// Receber game

            game = games.getGame(roomCode);


            if (game == null) {

                System.err.println("No game found for room: " + roomCode);

                conn.close();

                return;

            }


// Adicionar jogador à equipa

            Player player = new Player(username);


            if (game.canJoinTeam(teamCode)) {

                game.getTeam(teamCode).addPlayer(player);

                game.playerJoined(); // incrementa o contador de jogadores juntados

            } else {

                conn.send("Equipa cheia");

                return;

            }

            game.waitForGameStart(); // <-- só depois disso o cliente começa a mostrar perguntas







// Loop principal do jogo

            while (!game.isGameFinished()) {


                try {

// Enviar pergunta atual

                    conn.send(game.getCurrentQuestion());


                } catch (IOException e) {

                    System.err.println("Erro ao enviar pergunta para " + username);

                    break;

                }


                Object obj;

                try {

                    obj = conn.receive();

                    tempo2 = System.currentTimeMillis(); // Para o tempo assim que recebe

                } catch (IOException | ClassNotFoundException e) {

                    System.err.println("Erro ao receber resposta do cliente " + username);

                    break;

                }


                if (obj instanceof Answer) {

                    Answer answer = (Answer) obj;

                    System.out.println("Resposta recebida de " + username);





                    CyclicBarrier currentBarrier = game.getBarrier();

                    ModifiedCountdownLatch currentLatch = (currentBarrier == null) ? game.getLatch() : null;


                    if (currentBarrier != null) {

                        try {

                            currentBarrier.await();

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                    }

                    else if (currentLatch != null) {

// Usamos a variável LOCAL 'currentLatch'

                        currentLatch.countdown();


                        try {

// Mesmo que o countdown tenha metido o game.latch a null,

                            currentLatch.await();

                        } catch (InterruptedException e) {

                            e.printStackTrace();

                        }

                    }

                    else {

                        System.err.println("Erro: Nenhum mecanismo de sincronização definido!");

                    }

                }


// Cálculo do tempo

                tempo3 = tempo2 - tempo1;

                System.out.println(username + " demorou " + tempo3 + "ms a responder");

            }


        } catch (Exception e) {

            System.err.println("Erro inesperado no DealWithClient: " + e.getMessage());

            e.printStackTrace();


        } finally {

            try {

                conn.close();

            } catch (IOException e) {

                System.err.println("Erro ao fechar ligação do cliente " + username);

            }

        }

    }

} 