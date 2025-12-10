package iskahoot.server;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class DealWithGame extends Thread{
    private Game game;

    public DealWithGame(Game game){
        this.game=game;
    }
    @Override
    public void run(){

        CyclicBarrier barrier=new CyclicBarrier(game.numberOfPlayers(), ()->{
            //codigo corrido quando todos os jogadores respondem
            System.out.println("Todos os jogadores responderam");
            game.nextQuestion();
        });

    }


}
