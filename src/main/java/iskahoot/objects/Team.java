package iskahoot.objects;

import iskahoot.model.Answer;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String teamName;
    private List<Player> teamList = new ArrayList<>();
    private int score;
    private List<Answer> answers = new ArrayList<>();

    public Team(String teamName){
        this.teamName = teamName;
        this.score = 0;
    }

    public String getTeamName (){
        return this.teamName;
    }

    public List<Player> getTeamList (){
        return this.teamList;
    }

    // 🔧 BUG FIX (necessário)
    public boolean addPlayer(Player player){
        if (teamList.contains(player)) return false;
        teamList.add(player);
        return true;
    }

    public int getNumberOfPlayers(){
        return teamList.size();
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public List<Player> getPlayers(){
        return teamList;
    }

    // Mantido (apesar do nome estranho 😄)
    public int numberOfTeams(){
        return teamList.size();
    }

    public boolean hasPlayer(Player p){
        return teamList.contains(p);
    }

    public synchronized void addScore(int score){
        this.score += score;
    }

    public synchronized void addAnswer(Answer answer){
        answers.add(answer);
    }

    public synchronized List<Answer> getAnswers(){
        return answers;
    }

    // ===============================
    // ➕ MÉTODOS NOVOS (SEM APAGAR NADA)
    // ===============================

    /**
     * Verifica se todos os jogadores da equipa já responderam
     */
    public synchronized boolean allPlayersAnswered(){
        return answers.size() == teamList.size();
    }

    /**
     * Verifica se todos acertaram
     */
    public synchronized boolean allAnswersCorrect(int correctIndex){
        if (!allPlayersAnswered()) return false;

        for (Answer a : answers){
            if (a.getAnswer() != correctIndex){
                return false;
            }
        }
        return true;
    }

    /**
     * Limpa respostas no fim da ronda
     */
    public synchronized void clearAnswers(){
        answers.clear();
    }

    // Mantido exatamente como tinhas
    public synchronized void doublePoints(){
        score *= 2;
        answers.clear();
    }
}
