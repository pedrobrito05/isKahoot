package iskahoot.objects;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String teamName;
    private List<Player> teamList=new ArrayList<>();
    private int score;


    public Team(String teamName){
        this.teamName=teamName;
        this.score=0;
    }

    public String getTeamName (){
        return this.teamName;
    }

    public List<Player> getTeamList (){
        return this.teamList;
    }

    public boolean addPlayer(Player player){
        if (teamList.contains(player.getPlayerName())) return false;

        teamList.add(player); return true;
    }

    public int numberOfTeams(){
       return teamList.size();
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score=score;
    }
}
