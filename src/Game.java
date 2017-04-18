import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Game implements IGame {
	protected ArrayList<Player> players = new ArrayList<Player>();
	protected int num_pawns = 4; //number of pawns per player
	protected State game_state;
	protected State prev_state; // for comparison between states for a moving blockade
	
	// for testing - reading rolls from file
	private BufferedReader br; 
	
	public Game() throws FileNotFoundException{
		this.br = new BufferedReader(new FileReader("rolls.txt"));
	}

	public void register(IPlayer p) {
		Player player = (Player) p;
		this.players.add(player);
		String color = "red";
		if(players.size() == 1){
			color = "green";
		}
		else if(players.size() == 2){
			color = "blue";
		}
		
		p.startGame(color);
	}
	
	public void set_state(State s){
		this.game_state = s;
	}
	
	// determines if a given IMove is legal
	// looks at curr_state and uses game rules
	// Entry move
	private boolean is_Legal(EnterPiece m){
		if(m == null){
			return false;
		}
		
		Pawn pawn = m.get_pawn();
		Player player = (Player) this.game_state.get_curr_player();
		Board b = this.game_state.get_board();
		
		// check if player color matches pawn color
		String player_color = player.get_color();
		String pawn_color = pawn.get_color();
		if(! pawn_color.equals(player_color) ){
			return false;
		}
		
		// check if pawn is in player's home circle
		HomeCircle h = b.get_HomeCircle(player_color);
		if(h == null){
			return false;
		}
		ArrayList<Pawn> in_circle = h.get_pawns();
		if(! in_circle.contains(pawn)){
			return false;
		}
		
		// check if entry space is clear to add a pawn
		Entry entry = this.game_state.get_board().get_Entry(player_color);
		if (entry == null){
			return false;
		}
		ArrayList<Pawn> in_entry = entry.get_pawns();
		if(in_entry.size() > 1){
			return false;
		}
		
		// check if die rolls allow entry
		int[] rolls = this.game_state.get_rolls();
		int sum = 0;
		for(int r : rolls){
			if(r == 5){
				return true;
			}
			sum += r;
		}
		
		if(sum == 5){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	// Main ring move
	private boolean is_Legal(MoveMain m){
		if(m == null){
			return false;
		}
		
		Pawn pawn = m.get_pawn();
		int start = m.get_start();
		int distance = m.get_distance();
		Player player = (Player) this.game_state.get_curr_player();
		Board b = this.game_state.get_board();
		
		// check if player color matches pawn color
		String player_color = player.get_color();
		String pawn_color = pawn.get_color();
		if(! pawn_color.equals(player_color) ){
			return false;
		}
		
		// Check if pawn is in space defined by start
		Space[] spaces = b.get_Spaces();
		int space_length = spaces.length;
		if(start < 0 || start >= space_length){
			return false;
		}
		Space s = spaces[start];
		ArrayList<Pawn> in_space = s.get_pawns();
		if(! in_space.contains(pawn)){
			return false;
		}
		
		// Check if pawn can be moved distance tiles without conflict
		// Construct list of spaces to check for blockade
		ArrayList<Space> spaces_to_check = new ArrayList<Space>();
		int curr_space_index = start + 1;
		boolean in_home_row = false; // true if list of spaces includes home row
		boolean reached_home = false; // true if list of spaces includes home
		Space curr_space = null;
		ArrayList<HomeRow> homerow = null;
		
		while(spaces_to_check.size() < distance){
			if(!in_home_row){
				curr_space = spaces[curr_space_index];
				spaces_to_check.add(curr_space);
				
				if (pawn_color.equals(curr_space.get_color()) && PreHomeRow.class.isAssignableFrom(curr_space.getClass())){
					homerow = this.game_state.get_board().get_HomeRow(pawn_color);
					in_home_row = true;
					curr_space_index = 0;
					
				}
				else{
					curr_space_index = (curr_space_index + 1) % space_length; // next space on board; could wrap around
				}
			}
			else if(!reached_home){
				if(curr_space_index == homerow.size()){
					curr_space = this.game_state.get_board().get_Home(pawn_color);
					spaces_to_check.add(curr_space);
				}
				else{
					curr_space = homerow.get(curr_space_index);
					spaces_to_check.add(curr_space);
					curr_space_index++;
				}
				
			}
			else{
				return false; // the move goes beyond the home space and is invalid
			}
		}
		
		// check for blockades
		ArrayList<Pawn> curr_pawns = null;
		for(Space checked_space : spaces_to_check){
			curr_pawns = checked_space.get_pawns();
			if(curr_pawns.size() == 2){
				return false;
			}
		}
		
		// check end space
		Space end_space = spaces_to_check.get(spaces_to_check.size() - 1);
		// check for opposing piece on safe space
		ArrayList<Pawn> end_pawns = end_space.get_pawns();
		if(end_pawns.size() != 0){
			// check for opposing piece on safe space
			if(!end_pawns.get(0).get_color().equals(pawn_color) && end_space.get_safe()){
				return false;
			}
		}
		
		// check if move would cause a blockade that appears in the previous game state
		if((end_pawns.size() == 1) && end_pawns.get(0).get_color().equals(pawn_color)){
			ArrayList<Pawn> blockade = new ArrayList<Pawn>();
			blockade.add(pawn);
			blockade.add(end_pawns.get(0));
			
			Board prev_board = this.prev_state.get_board();
			ArrayList<Space> prev_space_queue = new ArrayList<Space>(Arrays.asList(prev_board.get_Spaces()));
			
			for(Space s2: prev_space_queue){
				if(s2.get_pawns().size() == 2){
					ArrayList<Pawn> candidate = s2.get_pawns();
					if(blockade.containsAll(candidate)){
						return false;
					}
				}
			}
		}
		
		// Check if distance appears in rolls
		int[] rolls = this.game_state.get_rolls();
		for(int r : rolls){
			if(r == distance){
				return true;
			}
		}
		return false;
		
	}
	
	// Home row move
	private boolean is_Legal(MoveHome m){
		if(m == null){
			return false;
		}
		
		Pawn pawn = m.get_pawn();
		int start = m.get_start();
		int distance = m.get_distance();
		Player player = (Player) this.game_state.get_curr_player();
		Board b = this.game_state.get_board();
		
		// check if player color matches pawn color
		String player_color = player.get_color();
		String pawn_color = pawn.get_color();
		if(! pawn_color.equals(player_color) ){
			return false;
		}
		
		// Check if pawn is in space defined by start
		ArrayList<HomeRow> row = b.get_HomeRow(pawn_color);
		int row_length = row.size();
		if(start < 0 || start >= row_length){
			return false;
		}
		HomeRow hr = row.get(start);
		ArrayList<Pawn> in_space = hr.get_pawns();
		if(! in_space.contains(pawn)){
			return false;
		}
		
		// Check if move is too long
		int end = start + distance;
		if(end >= row_length + 1){
			return false;
		}
		
		// Check if pawn can be moved distance tiles without conflict
		// Construct list of spaces to check for blockade
		ArrayList<Space> spaces_to_check = new ArrayList<Space>();
		int curr_space_index = start + 1;
		Space curr_space = null;
		
		while(spaces_to_check.size() < distance){
			curr_space = row.get(curr_space_index);
			spaces_to_check.add(curr_space);
			curr_space_index++;
		}
		
		// check for blockades
		ArrayList<Pawn> curr_pawns = null;
		for(Space checked_space : spaces_to_check){
			curr_pawns = checked_space.get_pawns();
			if(curr_pawns.size() == 2){
				return false;
			}
		}
		
		// check if move would cause a blockade that appears in the previous game state
		Space end_space = spaces_to_check.get(spaces_to_check.size() - 1);
		ArrayList<Pawn> end_pawns = end_space.get_pawns();
		
		if(!Home.class.isAssignableFrom(end_space.getClass()) && (end_pawns.size() == 1) && end_pawns.get(0).get_color().equals(pawn_color)){
			ArrayList<Pawn> blockade = new ArrayList<Pawn>();
			blockade.add(pawn);
			blockade.add(end_pawns.get(0));
			
			Board prev_board = this.prev_state.get_board();
			ArrayList<Space> prev_space_queue = new ArrayList<Space>(Arrays.asList(prev_board.get_Spaces()));
			prev_space_queue.addAll(prev_board.get_HomeRow(pawn_color));
			
			for(Space s2: prev_space_queue){
				if(s2.get_pawns().size() == 2){
					ArrayList<Pawn> candidate = s2.get_pawns();
					if(blockade.containsAll(candidate)){
						return false;
					}
				}
			}
		}
		
		// Check if distance appears in rolls
		int[] rolls = this.game_state.get_rolls();
		for(int r : rolls){
			if(r == distance){
				return true;
			}
		}
		return false;
		
	}
	
	private boolean is_Legal(IMove move){
		if(move == null){
			return false;
		}
		
		if (EnterPiece.class.isAssignableFrom(move.getClass())) {
			EnterPiece m = (EnterPiece) move;
			return is_Legal(m);
		}
		else if(MoveMain.class.isAssignableFrom(move.getClass())){
			MoveMain m = (MoveMain) move;
			return is_Legal(m);
			
		}
		else if(MoveHome.class.isAssignableFrom(move.getClass())){
			MoveHome m = (MoveHome) move;
			return is_Legal(m); 
		}
		else{
			return false;
		}
		
	}
	
	
	// checks whether a pawn will
	// be bopped given a space and color
	private boolean bop(Space s, String color){
		if (s.pawns_list.size() == 1){
			Pawn curr_pawn = s.get_pawns().get(0);
			if(!curr_pawn.get_color().equals(color)){
				HomeCircle curr_pawn_home = this.game_state.get_board().get_HomeCircle(curr_pawn.get_color());
				s.remove_Pawn(curr_pawn);
				curr_pawn_home.add_Pawn(curr_pawn);
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	
	// alters Board in curr_state with move
	// checks if move is legal before update
	private boolean update_Board(EnterPiece m){
		Board b = this.game_state.get_board();
		if (m == null){
			return false;
		}
		if (!is_Legal(m)){
			return false;
		}
		
		Pawn p = m.get_pawn();
		String pawn_color = p.get_color();		   
		HomeCircle h = b.get_HomeCircle(pawn_color);
		Entry e = b.get_Entry(pawn_color);
		boolean bopped = bop(e, pawn_color);
	
		e.add_Pawn(p);
		h.remove_Pawn(p);

		if (!this.game_state.remove_roll(5)){
			this.game_state.set_rolls(new int[0]);
		}
		if(bopped){
			this.game_state.add_roll(20);
		}
		return true;
	}
	
	private boolean update_Board(MoveMain m){
		Board b = this.game_state.get_board();
		if (m == null){
			return false;
		}
		if (!is_Legal(m)){
			return false;
		}
		
		Pawn p = m.get_pawn();
		String pawn_color = p.get_color();	
		int start = m.get_start();
		int distance = m.get_distance();
		Space[] spaces = this.game_state.get_board().get_Spaces();
		int space_length = spaces.length;
		Space starting_space = spaces[start];
		Space ending_space = null;
		int curr_space_index = start;
		
		boolean in_home_row = false; // true if list of spaces includes home row
		boolean reached_home = false; // true if list of spaces includes home
		ArrayList<HomeRow> homerow = null;
		
		for(int i = 0; i <= distance; i++){
			if(!in_home_row){
				ending_space = spaces[curr_space_index];
				
				if (pawn_color.equals(ending_space.get_color()) && PreHomeRow.class.isAssignableFrom(ending_space.getClass())){
					homerow = this.game_state.get_board().get_HomeRow(pawn_color);
					in_home_row = true;
					curr_space_index = 0;
					
				}
				else{
					curr_space_index = (curr_space_index + 1) % space_length; // next space on board; could wrap around
				}
			}
			else if(!reached_home){
				if(curr_space_index == homerow.size()){
					Home home = this.game_state.get_board().get_Home(pawn_color);
					ending_space = home;
					reached_home = true;
				}
				else{
					ending_space = homerow.get(curr_space_index);
					curr_space_index++;
				}
				
			}
		}
		boolean bopped = bop(ending_space, pawn_color);
		
		ending_space.add_Pawn(p);
		starting_space.remove_Pawn(p);

		this.game_state.remove_roll(distance);
		
		if(bopped){
			this.game_state.add_roll(20);
		}
		return true;		
	}
	
	private boolean update_Board(MoveHome m){
		Board b = this.game_state.get_board();
		if (m == null){
			return false;
		}
		if (!is_Legal(m)){
			return false;
		}
		
		Pawn p = m.get_pawn();
		String pawn_color = p.get_color();	
		int start = m.get_start();
		int distance = m.get_distance();
		ArrayList<HomeRow> homerow = this.game_state.get_board().get_HomeRow(pawn_color);
		Space starting_space = homerow.get(start);
		Space ending_space = null;
		if(start + distance < 7){
			ending_space = homerow.get(start+distance);
		}
		else{
			ending_space = this.game_state.get_board().get_Home(pawn_color);
			this.game_state.add_roll(10);
		}
		ending_space.add_Pawn(p);
		starting_space.remove_Pawn(p);
		
		return true;		
	}
	
	
	private boolean update_Board(IMove move){
		Board b = this.game_state.get_board();
		if (move == null){
			  return false;
		  }
		if (!is_Legal(move)){
			return false;
		}
		
		if (EnterPiece.class.isAssignableFrom(move.getClass())) {
			EnterPiece m = (EnterPiece) move;
			return update_Board(m);
		  }
		
		else if(MoveMain.class.isAssignableFrom(move.getClass())){
			MoveMain m = (MoveMain) move;
			return update_Board(m);
		  }
		else{
			MoveHome m = (MoveHome) move;
			return update_Board(m);
		}
	}
	
	private int roll_die(boolean from_file) throws NumberFormatException, IOException{
		if(from_file){
			return Integer.parseInt(br.readLine());
		}
		return 1 + (int)(Math.random() * 6);
	}
	
	// returns true if somebody has won game; false otherwise
	private boolean game_over(){
		for(Player p : this.players){
			String color = p.get_color();
			Home h = this.game_state.get_board().get_Home(color);
			
			if(h.get_pawns().size() == this.num_pawns){
				return true;
			}
		}
		return false;
	}
	
	// returns false if all legal moves have been exhausted by the current player; true otherwise
	private boolean moves_remaining(){
		Player player = this.game_state.curr_player;
		String color = player.get_color();
		boolean moves_remaining = false;
		
		// check all main pawns for moves
		Space[] main_spaces = this.game_state.get_board().get_Spaces();
		for(int i = 0; i < main_spaces.length; i++){
			Space s = main_spaces[i];
			for(Pawn p: s.get_pawns()){
				if(p.get_color().equals(color)){
					for(int val: this.game_state.rolls_vals_left){
						moves_remaining = moves_remaining || this.is_Legal(new MoveMain(p, i, val));
					}
				}
			}
		}
		
		// check all home row pawns for moves
		ArrayList<HomeRow> row = this.game_state.get_board().get_HomeRow(color);
		for(int i = 0; i < row.size(); i++){
			HomeRow s = row.get(i);
			for(Pawn p: s.get_pawns()){
				if(p.get_color().equals(color)){
					for(int val: this.game_state.rolls_vals_left){
						moves_remaining = moves_remaining || this.is_Legal(new MoveHome(p, i, val));
					}
				}
			}
		}
		
		// check all home circle pawns for moves
		HomeCircle h = this.game_state.get_board().get_HomeCircle(color);
		for(Pawn p: h.get_pawns()){
			moves_remaining = moves_remaining || this.is_Legal(new EnterPiece(p));
		}
		
		
		return moves_remaining;
	}
	
	public void start() {
		// for testing purposes
		// set to true if you want to compare with test boards after every successful move
		boolean testing = true;
		int num_moves = 0;
		
		// initialize state
		Board new_board = new Board(this.players, this.num_pawns);
		Player curr_player = this.players.get(0);
		int[] rolls = new int[0];
		this.game_state = new State(new_board, curr_player, rolls);
		this.prev_state = new State(this.game_state);
		
		// loop through players until winner
		// generate dice rolls
		// ask player for move
		// if move is illegal, remove cheating player from game
		// double penalty thing
		boolean doubles = false; //true if doubles were rolled
		int num_doubles = 0; //incremented if doubles were rolled; player penalized when num = 3;
		boolean cheated = false; //true if player cheated on turn
		int player_index = 0;
		int num_players = this.players.size();
		
		while(!game_over()){
			// add two rolls
			curr_player = this.game_state.get_curr_player();
			int roll1, roll2;
			try {
				roll1 = roll_die(testing);
				roll2 = roll_die(testing);
			} catch (Exception e){
				System.out.println("Error");
				return;
			}
			game_state.add_roll(roll1);
			game_state.add_roll(roll2);
			if (roll1 == roll2){
				int roll1c = 7 - roll1;
				int roll2c = 7 - roll2;
				game_state.add_roll(roll1c);
				game_state.add_roll(roll2c);
				doubles = true;
				num_doubles++;
				if(num_doubles == 3){
					//penalize the current player
					Pawn removed = this.game_state.get_board().remove_furthest(curr_player.get_color());
					HomeCircle hc = this.game_state.get_board().get_HomeCircle(curr_player.get_color());
					hc.add_Pawn(removed);
					
					player_index = (player_index + 1) % num_players;
					this.game_state.set_curr_player(this.players.get(player_index));
					num_doubles = 0;
					doubles = false;
					
				}
			}
			
			
			while(moves_remaining() && !game_over()){
				IMove move = curr_player.doMove(game_state.get_board(), game_state.get_rolls());
				
				if(!update_Board(move)){
					System.out.println("Illegal move attempted");
					
					
					// update_Board returns false if player passed illegal move (cheated)
					if(!testing){
						num_players = this.players.size();
						this.players.remove(curr_player); // remove cheating player
						cheated = true;
						break;
					}
					else{
						continue;
					}
					
				}
				
				System.out.println("Move processed");
				num_moves++;
				if(testing){
					State curr_state = Game.states.get(num_moves);
					Tester.check(curr_state.equals(this.game_state), num_moves + " move test");
				}
			}
			
			// update player index, reset flags
			if(cheated){
				cheated = false;
				doubles = false;
				if(player_index == num_players){
					player_index = 0;
				}
				else{
					player_index = (player_index + 1) % num_players;
				}
			}
			else if (doubles){
				doubles = false;
			}
			else{
				player_index = (player_index + 1) % num_players;
			}
				
			this.game_state.set_curr_player(this.players.get(player_index));
			this.prev_state = new State(this.game_state);
			
			if(testing){
				boolean out_of_moves = true; //true if all players are out of preset moves
				for(Player p : this.players){
					out_of_moves = out_of_moves && (p.get_moves().size() == 0);
				}
				if(out_of_moves){
					break;
				}
			}
			
		}
		
		// Broken out of loop -> current player wins
		System.out.println(curr_player.get_color() + " player wins!");
		
	}
	
	// Examples
	static Game g;
	
	static State sinit;
	static ArrayList<State> states = new ArrayList<State>();
	
	public static void createExamples() throws IOException{
		g = new Game();
		
		Player green = new Player();
		g.register(green);
		green.startGame("green");
		ArrayList<IMove> green_moves = new ArrayList<IMove>();
		green_moves.add(new EnterPiece(new Pawn(0, "green")));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 0, 3));
		green_moves.add(new EnterPiece(new Pawn(1, "green")));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 3, 6));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 9, 6));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 15, 3));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 0, 3));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 3, 4));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 15, 1));
		green_moves.add(new EnterPiece(new Pawn(2, "green")));
		green_moves.add(new EnterPiece(new Pawn(3, "green")));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 0, 2));
		//green_moves.add(new MoveMain(new Pawn(2, "green"), 17, 2)); tests moving blockade together from doubles bonus
		green_moves.add(new MoveMain(new Pawn(2, "green"), 2, 2));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 4, 3));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 0, 3));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 16, 1));
		green_moves.add(new MoveMain(new Pawn(3, "green"), 0, 6));
		//green_moves.add(new MoveMain(new Pawn(3, "green"), 6, 3)); tests moving past one's own blockade
		green_moves.add(new MoveMain(new Pawn(1, "green"), 7, 3));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 7, 4));
		green_moves.add(new MoveMain(new Pawn(3, "green"), 6, 2));

		
		green.set_moves(green_moves);
		
		Player blue = new Player();
		g.register(blue);
		blue.startGame("blue");
		ArrayList<IMove> blue_moves = new ArrayList<IMove>();
		blue_moves.add(new EnterPiece(new Pawn(0, "blue")));
		blue_moves.add(new EnterPiece(new Pawn(1, "blue")));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 17, 2));
		blue_moves.add(new EnterPiece(new Pawn(2, "blue")));
		blue_moves.add(new EnterPiece(new Pawn(3, "blue")));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 19, 2));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 21, 5));
		blue_moves.add(new EnterPiece(new Pawn(3, "blue")));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 17, 2));
		blue_moves.add(new MoveMain(new Pawn(2, "blue"), 17, 4));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 19, 5));
		blue_moves.add(new MoveMain(new Pawn(2, "blue"), 21, 5));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 24, 2));
		blue_moves.add(new MoveMain(new Pawn(2, "blue"), 26, 2));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 24, 2));
		blue_moves.add(new EnterPiece(new Pawn(3, "blue")));
		blue_moves.add(new MoveMain(new Pawn(3, "blue"), 17, 20));
		blue_moves.add(new MoveMain(new Pawn(3, "blue"), 3, 4));
		blue_moves.add(new MoveMain(new Pawn(2, "blue"), 28, 4));
		blue_moves.add(new MoveMain(new Pawn(3, "blue"), 3, 4));
		blue_moves.add(new MoveMain(new Pawn(2, "blue"), 32, 4));
		blue_moves.add(new MoveMain(new Pawn(3, "blue"), 3, 2));




		blue.set_moves(blue_moves);
		
		sinit = new State("boards/init.txt",2);
		states.add(sinit);
		for (int i = 1; i <= 35; i++){
			states.add(new State("boards/" + i + ".txt", 2));
		}
	}
	
	public static void main(String argv[]){
		g.start();
	}
	
}


