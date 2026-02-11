package iskahoot.server;

import iskahoot.model.Answer;
import iskahoot.model.Connection;
import iskahoot.model.Question;
import iskahoot.objects.Player;
import iskahoot.objects.Team;

import java.io.IOException;
import java.util.concurrent.CyclicBarrier;

public class DealWithClient extends Thread {

    private final Connection conn;
    private final GameState games;
    private Game game;
    private String username;
    private String teamCode;
    private String roomCode;

    public DealWithClient(Connection conn, GameState games) {
        this.conn = conn;
        this.games = games;
    }

    @Override
    public void run() {
        try {
            username = (String) conn.receive();
            teamCode = (String) conn.receive();
            roomCode = (String) conn.receive();

            game = games.getGame(roomCode);
            if (game == null) {
                conn.close();
                return;
            }

            Player player = new Player(username);
            Team team = game.getTeam(teamCode);

            if (!game.canJoinTeam(teamCode)) {
                conn.send("Equipa cheia");
                return;
            }

            team.addPlayer(player);
            game.playerJoined();
            game.waitForGameStart();

            while (!game.isGameFinished()) {

                Question question = game.getCurrentQuestion();
                if (question == null) break;

                conn.send(question);

                Object obj = conn.receive();
                if (!(obj instanceof Answer)) continue;

                Answer answer = (Answer) obj;
                team.addAnswer(answer);

                CyclicBarrier barrier = game.getBarrier();
                ModifiedCountdownLatch latch = game.getLatch();

                if (game.getCurrentQuestionIndex() % 2 == 0) {

                    int factor = 1;
                    if (latch != null) {
                        factor = latch.countdown();
                    }

                    if (answer.getAnswer() == question.getCorrectIndex()) {
                        team.addScore(question.getPoints() * factor);
                    }

                    if (latch != null) {
                        latch.await();
                        conn.send(game.buildLeaderboard());
                    }

                } else {

                    if (barrier != null) {
                        barrier.await();
                        conn.send(game.buildLeaderboard());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (IOException ignored) {}
        }
    }
}
