package iskahoot.server;
import iskahoot.model.Question;
import iskahoot.model.Quiz;
import iskahoot.objects.Team;

import java.util.*;

public class Game{

    private final String roomCode;        // id da sala
    private List<Team> team;
    private Quiz quiz;
    private int currentQuestionIndex;
    private boolean isActive;
    private final List<String> playerAnswers;
    //indice k representa a resposta do jogador de indice k

    private boolean gameStarted;
    private boolean gameFinished;

    public Game(String roomCode, int numberOfTeams, Quiz quiz) {
        this.roomCode = roomCode;
        this.quiz=quiz;
        for (int i = 0; i < numberOfTeams; i++) {
            Team team=new Team("Equipa" + (i + 1));
            team.setScore(0);
        }

        this.currentQuestionIndex = 0;

        this.playerAnswers = new ArrayList<>();

        this.isActive=true;
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

    public String getRoomCode(){
        return roomCode;
    }

    public void finishGame(){
        this.isActive=false;
    }

    //retorna o indice da resposta correta da current pergunta
    public int getCorrectAnswer(){
        return quiz.getQuestion(currentQuestionIndex).getCorrectIndex();
    }

    public synchronized boolean isAnswerCorrect(int indice){
        return getCorrectAnswer()==indice;
    }


    //apenas para teste
    public String getQuestion(int index){
        return quiz.getQuestion(index).getText();
    }
}
