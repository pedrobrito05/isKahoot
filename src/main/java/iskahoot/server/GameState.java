package iskahoot.server;

public class GameState {
    ArrayList<Game> games;

    public GameState(){
        games=new ArrayList<>();
    }
    public synchronized void add(Game game) {
        games.add(game);
    }

    public synchronized Game getGame(String roomCode) {
        for (Game a: games){
            if (a.getRoomCode().equals(roomCode)){
                return a;
            }
        }
        System.out.println("No game found");
        return null;
    }

    public void removeGame(String roomCode){
        games.removeIf(a-> a.getRoomCode().equals(roomCode));
    }


}
