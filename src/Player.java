import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	
	public String startGame(String color) throws Exception{
		if(!color.equals("red") && !color.equals("blue") && !color.equals("green") && !color.equals("yellow")){
			throw new Exception("Invalid player color");
		}
		this.color = color;
		this.has_started = true;
		
		return this.name;
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
	
	// for tracking the game state at a player level
	private State curr_state;
	private State prev_state;
	
	private String name;
	public Player(String strat, String name){
		this.strategy = strat;
		this.doubles_penalty = false;
		this.name = name;
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
		
		for(int j = (((entry_index - 1) % spaces.length) + spaces.length) % spaces.length; j != entry_index; j = (((j - 1) % spaces.length) + spaces.length) % spaces.length){
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
	
	public IMove[] doMove(Board brd, int[] dice) throws Exception{
		// for testing - return first move in list of moves
		// check if players has started
		if(!has_started){
			throw new Exception("Player has not started");
		}
		
		ArrayList<IMove> generated_moves = new ArrayList<IMove>();
		
		if(this.strategy.equals("first")){
			int[] sorted_dice = new int[dice.length];
			System.arraycopy(dice, 0, sorted_dice, 0, dice.length);
			Arrays.sort(sorted_dice);
			
			this.curr_state = new State(brd, this, sorted_dice);
			this.prev_state = new State(curr_state);
			
			
			
			while(RuleChecker.moves_remaining(this, this.curr_state, this.prev_state)){
				PawnLocation loc = null;
				ArrayList<Pawn> ordered_pawns = this.get_pawn_order(this.curr_state.get_board());
				boolean made_move = false;
				IMove move = null;
				
				for(Pawn to_move: ordered_pawns){
					loc = this.curr_state.get_board().get_Pawn_Location(to_move);
					
					// iterate through dice rolls in descending order
					for(int i = this.curr_state.get_rolls().length - 1; i >= 0; i--){
						int roll = this.curr_state.get_rolls()[i];
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
						if(move.is_Legal(this.curr_state, this.prev_state)){
							generated_moves.add(move);	
							this.curr_state = move.update_Board(this.curr_state);
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
			
		}
		else if(this.strategy.equals("last")){
			int[] sorted_dice = new int[dice.length];
			System.arraycopy(dice, 0, sorted_dice, 0, dice.length);
			Arrays.sort(sorted_dice);
			
			this.curr_state = new State(brd, this, sorted_dice);
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
					for(int i = this.curr_state.get_rolls().length - 1; i >= 0; i--){
						int roll = this.curr_state.get_rolls()[i];
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
						if(move.is_Legal(this.curr_state, this.prev_state)){
							generated_moves.add(move);	
							this.curr_state = move.update_Board(this.curr_state);
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
		}
		else if(this.strategy.equals("heuristic")){
			this.curr_state = new State(brd, this, dice);
			generated_moves = this.get_best_moves(this.curr_state);
		}
		
		
		IMove[] results = new IMove[generated_moves.size()];
		generated_moves.toArray(results);
		
		return results;
		
		
	}
	
	
	// Assignment 9 - choosing best move
	private IHeuristic heuristic;
	
	public void set_Heuristic(IHeuristic h){
		this.heuristic = h;
	}
	
	// use given heuristic to return ArrayList of best possible moves
	private ArrayList<IMove> get_best_moves(State start_state) throws Exception{
		ArrayList<IMove> best_moves = new ArrayList<IMove>();
		int max_value = Integer.MIN_VALUE;
		int cap = 500; //number of possible moves to check before stopping
		
		ArrayList<ArrayList<IMove>> possible_moves_lists = RuleChecker.get_move_lists(this, start_state, start_state, cap);
		
		for(ArrayList<IMove> path: possible_moves_lists){
			State final_state = new State(start_state);
			for(IMove move: path){
				final_state = move.update_Board(final_state);
			}
			
			int value = this.heuristic.eval(final_state.get_board(), this.color);
			
			if(value > max_value){
				max_value = value;
				best_moves = path;
			}
		}
		
		return best_moves;
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
	
	// MoveMain vs. MoveMain
	static State s4;
	static IMove last4;
	static IMove first4;
	
	// MoveMain vs. MoveMain
	static State s5;
	static IMove last5;
	static IMove first5;
	
	// MoveMain vs. MoveMain
	static State s6;
	static IMove last6;
	static IMove first6;
	
	// MoveMain vs. MoveMain
	static State s7;
	static IMove last7;
	static IMove first7;
	
	// MoveMain vs. MoveMain
	static State s8;
	static IMove last8;
	static IMove first8;
	
	// MoveMain vs. MoveMain
	static State s9;
	static IMove last9;
	static IMove first9;
	
	// MoveMain vs. MoveMain
	static State s10;
	static IMove last10;
	static IMove first10;
	
	// MoveMain vs. MoveMain
	static State s11;
	static IMove last11;
	static IMove first11;
	
	// MoveMain vs. MoveMain
	static State s12;
	static IMove last12;
	static IMove first12;
	
	// MoveMain vs. MoveMain
	static State s13;
	static IMove last13;
	static IMove first13;
	
	// MoveMain vs. MoveMain
	static State s14;
	static IMove last14;
	static IMove first14;
	
	// EnterPiece vs. MoveMain
	static State s15;
	static IMove last15;
	static IMove first15;
	
	// MoveMain vs. MoveMain
	static State s16;
	static IMove last16;
	static IMove first16;
	
	
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
		first3 = new MoveHome(new Pawn(3, "green"), 1, 6);
			
		s4 = new State("boards/2.txt", num_players);
		last4 = new MoveMain(new Pawn(1, "green"), 0, 2);
		first4 = new MoveMain(new Pawn(0, "green"), 0, 2);
		
		s5 = new State("boards/3.txt", num_players);
		last5 = new MoveMain(new Pawn(1, "green"), 0, 2);
		first5 = new MoveMain(new Pawn(0, "green"), 2, 2);
		
		s6 = new State("boards/7.txt", num_players);
		last6 = new MoveMain(new Pawn(2, "green"), 0, 3 );
		first6 = new MoveMain(new Pawn(1, "green"), 4, 4);
		
		s7 = new State("boards/8.txt", num_players);
		last7 = new MoveMain(new Pawn(2, "green"), 0, 4);
		first7 = new MoveMain(new Pawn(0, "green"), 7, 4);
		
		s8 = new State("boards/9.txt", num_players);
		last8 = new MoveMain(new Pawn(2, "green"), 0, 4);
		first8 = new MoveMain(new Pawn(0, "green"), 7, 4);
		
		s9 = new State("boards/11.txt", num_players);
		last9 = new MoveMain(new Pawn(2, "green"), 4, 4);
		first9 = new MoveMain(new Pawn(0, "green"), 10, 4);
		
		s10 = new State("boards/12.txt", num_players);
		last10 = new MoveMain(new Pawn(2, "green"), 4, 4);
		first10 = new MoveMain(new Pawn(0, "green"), 14, 4);
		
		s11 = new State("boards/13.txt", num_players);
		last11 = new MoveMain(new Pawn(2, "green"), 4, 3);
		first11 = new MoveMain(new Pawn(1, "green"), 11, 3);
			
		s12 = new State("boards/25.txt", num_players);
		last12 = new MoveMain(new Pawn(3, "green"), 0, 20);
		first12 = new MoveMain(new Pawn(1, "green"), 11, 20);
		
		s13 = new State("boards/26.txt", num_players);
		last13 = new MoveMain(new Pawn(2, "green"), 7, 6);
		first13 = new MoveMain(new Pawn(3, "green"), 20, 6);
		
		s14 = new State("boards/27.txt", num_players);
		last14 = new MoveMain(new Pawn(2, "green"), 7, 20);
		first14 = new MoveMain(new Pawn(1, "green"), 11, 20);
		
		s15 = new State("boards/35.txt", num_players);
		last15 = new EnterPiece(new Pawn(0, "green"));
		first15 = new MoveMain(new Pawn(2, "green"), 27, 5);
		
		s16 = new State("boards/39.txt", num_players);
		last16 = new MoveMain(new Pawn(0, "green"), 6, 5);
		first16 = new MoveMain(new Pawn(2, "green"), 27, 5);
	}

}
