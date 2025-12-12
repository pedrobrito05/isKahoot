package iskahoot.server;

import iskahoot.model.Answer;
import iskahoot.model.Question;
import iskahoot.model.Quiz;
import iskahoot.objects.Player;
import iskahoot.objects.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class Game {

    private final String roomCode;        // id da sala
    private List<Team> teams = new ArrayList<>();
    private Quiz quiz;
    private int currentQuestionIndex;

    private int playersPerTeam;
    private int maxPlayers;
    private boolean gameStarted=false;
    private int connectedPlayers=0;
    private int numberOfTeams;
    // Mecanismos de sincronização
    private CyclicBarrier barrier;
    private ModifiedCountdownLatch latch;

    private final List<String> playerAnswers;
    private boolean isActive;

    public Game(String roomCode, int numberOfTeams, int playersPerTeam, Quiz quiz) {
        this.playersPerTeam=playersPerTeam;
        this.maxPlayers=playersPerTeam*numberOfTeams;
        this.roomCode = roomCode;
        this.numberOfTeams=numberOfTeams;
        this.quiz = quiz;
        for (int i = 0; i < numberOfTeams; i++) {
            Team team = new Team("Equipa" + (i + 1));
            team.setScore(0);
            teams.add(team);
        }

        this.currentQuestionIndex = 0;
        this.playerAnswers = new ArrayList<>();
        this.isActive = true;
        // Nota: Não inicializamos o latch/barrier aqui.
        // Eles serão criados automaticamente quando o primeiro jogador tentar responder (via ensureSyncInitialized).
    }

    public synchronized Question getCurrentQuestion() {
        if (isGameFinished()) return null;
        return quiz.questions.get(currentQuestionIndex);
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void submitAnswer(int playerIndex, String answer) {
        // Garante que a lista tem tamanho suficiente para evitar IndexOutOfBounds
        while (playerAnswers.size() <= playerIndex) {
            playerAnswers.add(null);
        }
        playerAnswers.set(playerIndex, answer);
    }

    public List<String> getPlayerAnswers() {
        return playerAnswers;
    }

    // --- LÓGICA DE SINCRONIZAÇÃO CORRIGIDA ---

    /**
     * Avança o índice da pergunta e limpa os sincronizadores antigos.
     * Isto força o 'ensureSyncInitialized' a criar novos na próxima chamada.
     */
    public synchronized void nextQuestion() {
        currentQuestionIndex++;
        barrier = null;
        latch = null;
    }

    /**
     * Prepara o mecanismo de sincronização correto (Latch ou Barrier)
     * baseado no índice da pergunta atual.
     */
    private synchronized void prepareSync() {
        if (isGameFinished()) return;


        int numPlayers = numberOfPlayers();
        // Evitar criar sincronização se não houver jogadores (previne erros de divisão ou bloqueios)
        if (numPlayers == 0) return;

        if (currentQuestionIndex % 2 == 0) {
            // pergunta de equipa Usar ModifiedCountdownLatch
            System.out.println("Ronda " + currentQuestionIndex + ": A configurar Latch para " + numPlayers + " jogadores.");
            barrier = null;
            // IMPORTANTE: O ModifiedCountdownLatch deve ter o construtor atualizado para aceitar o 'endOfRoundAction'
            latch = new ModifiedCountdownLatch(2, 2, 5000, numPlayers, ()->{
                System.out.println("Todos os clientes responderam à pergunta individual");
                nextQuestion();
            });
        } else {
            // Pergunta de equipa Usar CyclicBarrier
            System.out.println("Ronda " + currentQuestionIndex + ": A configurar Barrier para " + numPlayers + " jogadores.");
            latch = null;
            barrier = new CyclicBarrier(numPlayers,()->{
                System.out.println("Todos os clientes responderam à pergunta individual");
                nextQuestion();
            });
        }
    }

    /**
     * Método auxiliar que garante que o objeto de sincronização existe.
     * É chamado sempre que um cliente pede o getBarrier() ou getLatch().
     */
    private synchronized void ensureSyncInitialized() {
        if (barrier == null && latch == null && !isGameFinished()) {
            prepareSync();
        }
    }

    public CyclicBarrier getBarrier() {
        ensureSyncInitialized(); // Se for null, cria o correto
        return barrier;
    }

    public ModifiedCountdownLatch getLatch() {
        ensureSyncInitialized(); // Se for null, cria o correto
        return latch;
    }



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

    public synchronized void addTeam(Team team) {
        teams.add(team);
    }

    public List<Team> getTeams() {
        return this.teams;
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

    // Mantido para compatibilidade, caso seja chamado externamente,
    // mas a lógica agora é gerida automaticamente pelo ensureSyncInitialized.
    public void startNextQuestion() {
        ensureSyncInitialized();
    }

    public synchronized boolean canJoinTeam(String teamName) {
        Team team = getTeam(teamName);
        if (team == null) return false;

        // EXEMPLO: playersPerTeam = 3
        return team.numberOfTeams() < playersPerTeam;
    }
    public synchronized void playerJoined() {
        connectedPlayers++;

        if (connectedPlayers == maxPlayers) {
            gameStarted = true;
            notifyAll();
        }
    }

    public synchronized void waitForGameStart() {
        while (!gameStarted) {
            try {
                wait();
            } catch (InterruptedException ignored) {}
        }
    }

    public Team getTeam(Player player){
        for (Team t: teams){
            if(t.hasPlayer(player)){
                return t;
            }
        }
        System.out.println("nao ha players");
        return null;
    }

    public void verifyDoublePoints(Team team, int[]respostas){
        for(int i=0; i<respostas.length-1; i++){

        }
    }

    public synchronized void doubleIfAllCorrect(Team team){
        if(team.getAnswers().size()==team.getNumberOfPlayers()){
            for(Answer a:team.getAnswers()){
                if(a.getAnswer()!=quiz.getQuestion(currentQuestionIndex).getCorrectIndex()){
                    break;
                }
            }
            team.doublePoints();
        }

    }


    public static void main(String[] args){
        Game game=new Game("1", 2, 1, null);
        Team team=new Team("name");
        Player p=new Player("Name");
        game.addTeam(team);
        team.addPlayer(p);
        System.out.println(game.getTeam(p));
    }
}