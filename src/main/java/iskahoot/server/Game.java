package iskahoot.server;

import iskahoot.model.Answer;
import iskahoot.model.Leaderboard;
import iskahoot.model.Question;
import iskahoot.model.Quiz;
import iskahoot.objects.Player;
import iskahoot.objects.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class Game {

    private final String roomCode;
    private List<Team> teams = new ArrayList<>();
    private Quiz quiz;
    private int currentQuestionIndex;

    private int playersPerTeam;
    private int maxPlayers;
    private boolean gameStarted = false;
    private int connectedPlayers = 0;
    private int numberOfTeams;

    // Sincronização
    private CyclicBarrier barrier;
    private ModifiedCountdownLatch latch;

    private final List<String> playerAnswers;
    private boolean isActive;

    public Game(String roomCode, int numberOfTeams, int playersPerTeam, Quiz quiz) {
        this.playersPerTeam = playersPerTeam;
        this.maxPlayers = playersPerTeam * numberOfTeams;
        this.roomCode = roomCode;
        this.numberOfTeams = numberOfTeams;
        this.quiz = quiz;

        for (int i = 0; i < numberOfTeams; i++) {
            Team team = new Team("Equipa" + (i + 1));
            team.setScore(0);
            teams.add(team);
        }

        this.currentQuestionIndex = 0;
        this.playerAnswers = new ArrayList<>();
        this.isActive = true;
    }

    public synchronized Question getCurrentQuestion() {
        if (isGameFinished()) return null;
        return quiz.questions.get(currentQuestionIndex);
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void submitAnswer(int playerIndex, String answer) {
        while (playerAnswers.size() <= playerIndex) {
            playerAnswers.add(null);
        }
        playerAnswers.set(playerIndex, answer);
    }

    public List<String> getPlayerAnswers() {
        return playerAnswers;
    }

    // ================= SINCRONIZAÇÃO =================

    private synchronized void prepareSync() {
        if (isGameFinished()) return;

        int numPlayers = numberOfPlayers();
        if (numPlayers == 0) return;

        if (currentQuestionIndex % 2 == 0) {
            System.out.println("Ronda " + currentQuestionIndex +
                    ": A configurar Latch para " + numPlayers + " jogadores.");
            barrier = null;
            latch = new ModifiedCountdownLatch(
                    2, 2, 5000, numPlayers,
                    () -> {
                        System.out.println("Todos os clientes responderam à pergunta (Latch)");
                        showTeamsScore();
                        nextQuestion();
                        startNextQuestion(); // prepara próxima ronda
                    }
            );
        } else {
            System.out.println("Ronda " + currentQuestionIndex +
                    ": A configurar Barrier para " + numPlayers + " jogadores.");
            latch = null;
            barrier = new CyclicBarrier(numPlayers, () -> {
                System.out.println("Todos os clientes responderam à pergunta (Barrier)");
                teams.forEach(this::doubleIfAllCorrect); // <-- CORREÇÃO: Adicionar pontuação aqui
                showTeamsScore();
                nextQuestion();
                startNextQuestion(); // prepara próxima ronda
            });
        }
    }

    public CyclicBarrier getBarrier() {
        return barrier;
    }

    public ModifiedCountdownLatch getLatch() {
        return latch;
    }

    // ================= ESTADO DO JOGO =================

    public boolean isGameFinished() {
        return currentQuestionIndex >= quiz.questions.size();
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void finishGame() {
        this.isActive = false;
    }

    public int getCorrectAnswer() {
        if (isGameFinished()) return -1;
        return quiz.getQuestion(currentQuestionIndex).getCorrectIndex();
    }

    public synchronized boolean isAnswerCorrect(Answer answer) {
        return getCorrectAnswer() == answer.getAnswer();
    }

    // ================= EQUIPAS =================

    public synchronized void addTeam(Team team) {
        teams.add(team);
    }

    public List<Team> getTeams() {
        return teams;
    }

    public Team getTeam(String teamName) {
        for (Team t : teams) {
            if (t.getTeamName().equals(teamName)) {
                return t;
            }
        }
        return null;
    }

    public int numberOfPlayers() {
        return numberOfTeams * playersPerTeam;
    }

    public synchronized void startNextQuestion() {
        prepareSync(); // inicializa barrier/latch para a ronda atual
    }

    public synchronized boolean canJoinTeam(String teamName) {
        Team team = getTeam(teamName);
        if (team == null) return false;
        return team.getNumberOfPlayers() < playersPerTeam;
    }

    public synchronized void playerJoined() {
        connectedPlayers++;
        if (connectedPlayers == maxPlayers) {
            gameStarted = true;
            notifyAll();
            startNextQuestion(); // iniciar primeira ronda
        }
    }

    public synchronized void waitForGameStart() {
        while (!gameStarted) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }
    }

    public Team getTeam(Player player) {
        for (Team t : teams) {
            if (t.hasPlayer(player)) {
                return t;
            }
        }
        return null;
    }

    // ================= PONTUAÇÃO =================

    public synchronized void doubleIfAllCorrect(Team team) {
        if (team.getAnswers().isEmpty()) return;

        int correctIndex = quiz.getQuestion(currentQuestionIndex).getCorrectIndex();
        boolean allCorrect = true;
        int pointsThisRound = 0;

        for (Answer a : team.getAnswers()) {
            if (a.getAnswer() == correctIndex) {
                pointsThisRound += quiz.getQuestion(currentQuestionIndex).getPoints();
            } else {
                allCorrect = false;
            }
        }

        team.addScore(pointsThisRound); // soma pontos individuais
        if (allCorrect) {
            team.doublePoints(); // duplica se todos acertaram
        }

        team.clearAnswers(); // limpar respostas da equipa
    }

    public void showTeamsScore() {
        for (Team t : teams) {
            System.out.println("Team: " + t.getTeamName() + " -> " + t.getScore());
        }
    }

    // ================= AUXILIARES =================

    private synchronized void nextQuestion() {
        currentQuestionIndex++;
        barrier = null;
        latch = null;

        for (Team t : teams) {
            t.clearAnswers();
        }
    }

    public synchronized Leaderboard buildLeaderboard() {
        Leaderboard lb = new Leaderboard();
        for (Team t : teams) {
            lb.addTeam(t.getTeamName(), t.getScore());
        }
        return lb;
    }


}
