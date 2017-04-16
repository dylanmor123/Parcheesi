import java.util.ArrayList;

class Player implements IPlayer {
	protected boolean doubles_penalty; //true if third doubles is rolled
	private String color;
	
	// for testing purposes - list of moves in order to make
	private ArrayList<IMove> moves;
	
	public Player(){
		this.color = null;
		this.doubles_penalty = false;
	}
	
	public Player(String color, boolean doubles_penalty){
		this.color = color;
		this.doubles_penalty = doubles_penalty;
	}
	
	public String get_color(){
		return color;
	}
	
	public void set_color(String color){
		this.color = color;
	}
	
	public void startGame(String color) {
		this.color = color;
	}
	
	public IMove doMove(Board brd, int[] dice){
		// for testing - return first move in list of moves
		return this.moves.remove(0);
		
	}
	
	public void DoublesPenalty() {
		this.doubles_penalty = true;
	}
	
	public void set_moves(ArrayList<IMove> moves){
		this.moves =  moves;
	}
	
	public ArrayList<IMove> get_moves(){
		return this.moves;
	}

}
