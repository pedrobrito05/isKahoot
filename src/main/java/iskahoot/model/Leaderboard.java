package iskahoot.model;


import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Leaderboard implements Serializable {


    private final Map<String, Integer> scores = new LinkedHashMap<>();

    public void addTeam(String teamName, int score) {
        scores.put(teamName, score);
    }

    public Map<String, Integer> getScores() {
        return scores;
    }
}
