package iskahoot.server;

import iskahoot.model.QuestionLoader;
import iskahoot.model.Quiz;
import iskahoot.objects.Player;
import iskahoot.objects.Team;

import java.util.Random;
import java.util.Scanner;

public class ServerTUI extends Thread{
    private GameState games;

    public ServerTUI(GameState games){
        this.games=games;
    }

    public void menuInicial(){
        boolean loop=true;
        Scanner scanner=new Scanner(System.in);
        while (loop){
            System.out.println("Menu de Servidor\n1-Criar novo jogo\n2-Listar jogos existentes\n3-Sair");
            int op=scanner.nextInt();
            switch (op){
                case 1: createGame();
                        break;
                case 2: menuPrint();
                        break;
                case 4: jogadores();
                        break;
                case 3: loop=false;
                        break;

                default: System.out.println("Opcao inválida!!!\n");
            }
        }
    }
    public void createGame(){
        Scanner scanner=new Scanner(System.in);
        Random r=new Random();
        System.out.println("Numero de equipas: ");
        int numberOfTeams=scanner.nextInt();
        System.out.println("Numero de jogadores por equipa: ");
        int playersPerTeam=scanner.nextInt();
        Quiz quiz=QuestionLoader.loadFromFile("/questions.json");
        System.out.println("Numero de perguntas: 1 - "+quiz.numberOfQuestions()+":");
        int numberOfQuestions=scanner.nextInt();
        Quiz quiz2=new Quiz("quiz", quiz.questions, numberOfQuestions);
        String roomCode=Integer.toString(r.nextInt(100000));
        this.games.add(new Game(roomCode, numberOfTeams, playersPerTeam, QuestionLoader.loadFromFile("/questions.json")));
        System.out.println("Nova sala criada:\n-Code: "+roomCode);
    }


    public void menuPrint(){


        for (Game g: games.getGames()){
            System.out.println("Game: "+g.getRoomCode()+" / Terminado ? : "+g.isGameFinished()+"\n");
            for(Team t:g.getTeams()){
                System.out.println("    Team: "+t.getTeamName()+"\n");
                for(Player p: t.getPlayers()){
                    System.out.println("        Player: "+p.getPlayerName());
                }
            }
        }
    }

    public void run(){
        menuInicial();
    }

    public void jogadores(){
        for (Game g: games.getGames()){
            System.out.println("equipa");
            for(Team t:g.getTeams()){
                System.out.println("team");
                for(Player p: t.getPlayers()){
                    System.out.println("Player:");
                }
            }
        }
    }

    public static void main(String[] args){
        GameState games = new GameState();

        ServerTUI srv=new ServerTUI(games);
        srv.menuInicial();

    }
}
