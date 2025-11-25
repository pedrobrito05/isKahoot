package iskahoot.server;
import iskahoot.model.Question;
import iskahoot.model.Quiz;
import iskahoot.objects.Team;

import java.util.*;

public class Game {

    private final String roomCode;        // id da sala
    private List<Team> team;
    private Quiz quiz;
    private int currentQuestionIndex;

    private final List<String> playerAnswers;
    //indice k representa a resposta do jogador de indice k

    private boolean gameStarted;
    private boolean gameFinished;

    public Game(String roomCode, int numberOfTeams) {
        this.roomCode = roomCode;

        for (int i = 0; i < numberOfTeams; i++) {
            Team team=new Team("Equipa" + (i + 1));
            team.setScore(0);
        }

        this.currentQuestionIndex = 0;

        this.playerAnswers = new ArrayList<>();

        this.gameStarted = false; //so inicia quando todos os jogadores se conectam
        this.gameFinished = false;
    }



    public Question getCurrentQuestion() {
        return quiz.questions.get(currentQuestionIndex);
    }

    public void submitAnswer(int playerIndex, String answer) {
        playerAnswers.set(playerIndex, answer);
    }

    public List<String> getPlayerAnswers() {
        return playerAnswers;
    }

    public void nextQuestion() {
        currentQuestionIndex++;
    }

    public boolean isGameFinished() {
        return currentQuestionIndex >= quiz.questions.size();
    }

}
