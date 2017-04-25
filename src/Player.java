import java.util.ArrayList;

class Player implements IPlayer {
	protected boolean doubles_penalty; //true if third doubles is rolled
	private String color;
	
	// for testing purposes - list of moves in order to make
	private ArrayList<IMove> moves;
	
	
	// implementing Player sequence contract
	private boolean has_started = false;
	
	// copy constructor
	public Player(Player p){
		this.doubles_penalty = p.doubles_penalty;
		this.color = p.color;
	}
	
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
	
	public void startGame(String color) throws Exception{
		if(!color.equals("red") && !color.equals("blue") && !color.equals("green") && !color.equals("yellow")){
			throw new Exception("Invalid player color");
		}
		this.color = color;
		this.has_started = true;
	}
	
	public IMove doMove(Board brd, int[] dice) throws Exception{
		// for testing - return first move in list of moves
		// check if players has started
		if(!has_started){
			throw new Exception("Player has not started");
		}
		
		return this.moves.remove(0);
		
	}
	
	public void DoublesPenalty() throws Exception{
		// check if players has started
		if(!has_started){
			throw new Exception("Player has not started");
		}
		this.doubles_penalty = true;
	}
	
	public void set_moves(ArrayList<IMove> moves){
		this.moves =  moves;
	}
	
	public ArrayList<IMove> get_moves(){
		return this.moves;
	}

}
