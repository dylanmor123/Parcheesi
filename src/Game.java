import java.util.ArrayList;

public class Game implements IGame {
	protected ArrayList<IPlayer> players;
	public void register(IPlayer p) {
		this.players.add(p);
		String color = "green";
		p.startGame(color);
	}

	public void start() {
		// TODO Auto-generated method stub
		Board b = new Board(players)
	}

}
