package iskahoot.model;

import java.io.Serializable;
import java.util.List;


public class Question implements Serializable {

    private String question;
    private int points;           // pontuacao
    private int correct;          // indice da resposta certa
    private List<String> options;
    private boolean team;

    public Question(String question, int points, int correct, List<String> options, boolean team) {
        this.question = question;
        this.points = points;
        this.correct = correct;
        this.options = options;
        this.team = team;
    }

    // Getters para usar no resto do código
    public String getText() { return question; }

    public int getPoints() { return points; }

    public int getCorrectIndex() { return correct; }

    public List<String> getOptions() { return options; }

    public boolean isTeamQuestion() { return team; }


}