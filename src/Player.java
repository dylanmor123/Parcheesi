import java.util.ArrayList;
import java.util.Arrays;

class Player implements IPlayer {
	protected boolean doubles_penalty; //true if third doubles is rolled
	private String color;
	
	// for testing purposes - list of moves in order to make
	private ArrayList<IMove> test_moves = new ArrayList<IMove>();
	
	
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
		this.test_moves =  moves;
	}
	
	public ArrayList<IMove> get_moves(){
		return this.test_moves;
	}
	
	// Assignment 6 - Implementing player strategies
	// "first" if player strategy is to move first pawn, "last" if player strategy is to move last pawn
	private String strategy;
	
	// list of moves generated by a player as it examines a new turn
	private ArrayList<IMove> generated_moves = new ArrayList<IMove>();
	
	// for tracking the game state at a player level
	private State curr_state;
	private State prev_state;
	
	
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
		if(e.get_pawns().size() > 0 && e.get_pawns().get(0).get_color().equals(color)){
			pawns.addAll(e.get_pawns());
		}
		
		// add pawns from home_circle
		HomeCircle hc = board.get_HomeCircle(color);
		if(hc.get_pawns().size() > 0 && hc.get_pawns().get(0).get_color().equals(color)){
			pawns.addAll(hc.get_pawns());
		}
		
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
		
		
		
		
		if(this.test_moves.size() != 0){
			return this.test_moves.remove(0);
		}
		else if(this.generated_moves.size() != 0){
			return this.generated_moves.remove(0);
		}
		else if(this.strategy.equals("first")){
			this.curr_state = new State(brd, this, dice);
			this.prev_state = new State(curr_state);
			
			while(RuleChecker.moves_remaining(this, this.curr_state, this.prev_state)){
				PawnLocation loc = null;
				ArrayList<Pawn> ordered_pawns = this.get_pawn_order(this.curr_state.get_board());
				boolean made_move = false;
				IMove move = null;
				
				for(Pawn to_move: ordered_pawns){
					loc = this.curr_state.get_board().get_Pawn_Location(to_move);
					
					// iterate through dice rolls in descending order
					for(int i = sorted_dice.length - 1; i >= 0; i--){
						int roll = sorted_dice[i];
						if(loc.get_type().equals("home circle")){
							move = new EnterPiece(to_move);
						}
						else if(loc.get_type().equals("main")){
							move = new MoveMain(to_move, loc.get_index(), roll);
						}
						else if(loc.get_type().equals("home row")){
							move = new MoveHome(to_move, loc.get_index(), roll);
						}
						else{
							break; // pawn is in home; no need to try and move
						}
						
						// see if move is legal; if so, update on player-side and add to list of moves to make
						if(RuleChecker.is_Legal(move, this.curr_state, this.prev_state)){
							this.generated_moves.add(move);	
							this.curr_state = BoardUpdater.update_Board(move, this.curr_state);
							made_move = true;
							break;
						}
					}
					
					if(made_move){
						made_move = false;
						break;
					}
				}
			}
			
			return this.generated_moves.remove(0);
			
		}
		else if(this.strategy.equals("last")){
			this.curr_state = new State(brd, this, dice);
			this.prev_state = new State(curr_state);
			
			while(RuleChecker.moves_remaining(this, this.curr_state, this.prev_state)){
				PawnLocation loc = null;
				ArrayList<Pawn> ordered_pawns = this.get_pawn_order(this.curr_state.get_board());
				boolean made_move = false;
				IMove move = null;
				
				// start from back of list of ordered pawns
				for(int j = ordered_pawns.size() - 1; j >= 0; j--){
					Pawn to_move = ordered_pawns.get(j);
					loc = this.curr_state.get_board().get_Pawn_Location(to_move);
					
					// iterate through dice rolls in descending order
					for(int i = sorted_dice.length - 1; i >= 0; i--){
						int roll = sorted_dice[i];
						if(loc.get_type().equals("home circle")){
							move = new EnterPiece(to_move);
						}
						else if(loc.get_type().equals("main")){
							move = new MoveMain(to_move, loc.get_index(), roll);
						}
						else if(loc.get_type().equals("home row")){
							move = new MoveHome(to_move, loc.get_index(), roll);
						}
						else{
							break; // pawn is in home; no need to try and move
						}
						
						// see if move is legal; if so, update on player-side and add to list of moves to make
						if(RuleChecker.is_Legal(move, this.curr_state, this.prev_state)){
							this.generated_moves.add(move);	
							this.curr_state = BoardUpdater.update_Board(move, this.curr_state);
							made_move = true;
							break;
						}
					}
					
					if(made_move){
						made_move = false;
						break;
					}
				}
			}
			
			return this.generated_moves.remove(0);
		}
		
		
		return null;
		
		
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
		last2 = new EnterPiece(new Pawn(0, "green"));
		first2 = new MoveMain(new Pawn(2, "green"), 27, 5);
		
		
		s3 = new State("boards/57.txt", num_players);
		last3 = new MoveMain(new Pawn(1, "green"), 25, 6);
		first3 = new MoveHome(new Pawn(3, "green"), 27, 5);
	}
	
	public static void main(String[] argv) throws Exception{
		Player p_first = new Player("first");
		Player p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first1.equals(p_first.doMove(s1.get_board(), s1.get_rolls())), "first - test 1");
		Tester.check(last1.equals(p_last.doMove(s1.get_board(), s1.get_rolls())), "last - test 1");
	}

}
