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
	
	// Assignment 6 - Implementing player strategies
	// "first" if player strategy is to move first pawn, "last" if player strategy is to move last pawn
	private String strategy;
	public Player(String strat){
		this.strategy = strat;
		this.doubles_penalty = false;
	}
	// method that returns a list of pawns, in the order that they appear on the board
	// ordered from furthest along to furthest back
	private ArrayList<Pawn> get_pawn_order(Board board){
		// TODO: WRITE METHOD
		return null;
	}
	
	
	//--------------------------------------------------------------
	// Examples for testing
	// Advance vs. Enter 1
	static State s1;
	static ArrayList<Pawn> order1;
	static IMove last1;
	static IMove first1;
	
	// Advance vs. Enter 2
	static State s2;
	static IMove last2;
	static IMove first2;
	
	// MoveHome vs. MoveMain
	static State s3;
	static IMove last3;
	static IMove first3;
	
	
	
	public static void createExamples() throws Exception{
		int num_players = 2;
		s1 = new State("boards/1.txt", num_players);
		last1 = new EnterPiece(new Pawn(3, "green"));
		first1 = new MoveMain(new Pawn(0, "green"), 0, 5);
		
		s2 = new State("boards/35.txt", num_players);
		last1 = new EnterPiece(new Pawn(0, "green"));
		first1 = new MoveMain(new Pawn(2, "green"), 27, 5);
		
		
		s2 = new State("boards/35.txt", num_players);
		last1 = new MoveMain(new Pawn(1, "green"), 25, 6);
		first1 = new MoveHome(new Pawn(3, "green"), 27, 5);
	}

}
