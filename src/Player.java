import java.util.ArrayList;
import java.util.Arrays;

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
		ArrayList<Pawn> pawns = new ArrayList<Pawn>();
		
		
		// search in home row
		ArrayList<HomeRow> row = board.get_HomeRow(this.color);
		for(int i = row.size() - 1; i >= 0; i--){
			HomeRow s = row.get(i);
			if(s.get_pawns().size() > 0 && s.get_pawns().get(0).get_color().equals(this.color)){
				pawns.addAll(s.get_pawns());
			}
		}
		
		// search in main ring
		// find entry index
		Entry e = board.get_Entry(this.color);
		Space[] spaces = board.get_Spaces();
		int entry_index = -1;
		for(int k = 0; k < spaces.length; k++){
			if(e.equals(spaces[k])){
				entry_index = k;
				break;
			}
		}
		// k is index of entry in array of spaces
		
		for(int j = (((entry_index - 1) % spaces.length) + spaces.length) % spaces.length; j != entry_index; j = (j - 1) % spaces.length){
			Space s = spaces[j];
			if(s.get_pawns().size() > 0 && s.get_pawns().get(0).get_color().equals(color)){
				pawns.addAll(s.get_pawns());
			}
		}
		
		// add pawns from entry
		pawns.addAll(e.get_pawns());
		
		return pawns;
	}
	
	public IMove doMove(Board brd, int[] dice) throws Exception{
		// for testing - return first move in list of moves
		// check if players has started
		if(!has_started){
			throw new Exception("Player has not started");
		}
		
		// sort list of rolls - rolls are in ascending order
		// iterate in reverse to try furthest moves first
		int[] sorted_dice = new int[dice.length];
		System.arraycopy(dice, 0, sorted_dice, 0, dice.length);
		Arrays.sort(sorted_dice);
		
		ArrayList<Pawn> ordered_pawns = this.get_pawn_order(brd);
		IMove move = null;
		
		if(this.moves.size() != 0){
			return this.moves.remove(0);
		}
		else if(this.strategy.equals("first")){
			PawnLocation loc = null;
			for(Pawn to_move: ordered_pawns){
				loc = brd.get_Pawn_Location(to_move);
				
				for(int i = sorted_dice.length - 1; i >= 0; i--){
					if(loc.get_type().equals("home_circle")){
						move = new EnterPiece(to_move);
						if(RulesChecker.isLegal(move, brd)
					}
				}
			}
		}
		
		
	}
	
	
	//--------------------------------------------------------------
	// Examples for testing
	// Advance vs. Enter 1
	static State s1;
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
		
		
		s3 = new State("boards/57.txt", num_players);
		last1 = new MoveMain(new Pawn(1, "green"), 25, 6);
		first1 = new MoveHome(new Pawn(3, "green"), 27, 5);
	}

}
