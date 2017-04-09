import java.util.ArrayList;

public class Game implements IGame {
	protected ArrayList<Player> players;
	protected int num_pawns = 4; //number of pawns per player
	
	public void register(Player p) {
		this.players.add(p);
		String color = "green";
		p.startGame(color);
	}

	public void start() {
		Board b = new Board(this.players, num_pawns);
	}

}
