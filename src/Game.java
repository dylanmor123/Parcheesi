import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Game implements IGame {
	protected ArrayList<Player> players = new ArrayList<Player>();
	protected int num_pawns = 4; //number of pawns per player
	protected State game_state;
	
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
	public boolean is_Legal(IMove move){
		if(move == null){
			return false;
		}
		
		if (EnterPiece.class.isAssignableFrom(move.getClass())) {
			EnterPiece m = (EnterPiece) move;
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
		else if(MoveMain.class.isAssignableFrom(move.getClass())){
			MoveMain m = (MoveMain) move;
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
			int curr_space_index = start;
			boolean in_home_row = false; // true if list of spaces includes home row
			boolean reached_home = false; // true if list of spaces includes home
			Space curr_space = null;
			ArrayList<HomeRow> homerow = null;
			
			while(spaces_to_check.size() <= distance){
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
						Home home = this.game_state.get_board().get_Home(pawn_color);
						spaces_to_check.add(home);
						reached_home = true;
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
				if(curr_pawns.size() == 0){
					continue;
				}
				else if(!curr_pawns.get(0).get_color().equals(pawn_color) && curr_pawns.size() == 2){
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
				// check for two of own pieces
				else if(end_pawns.get(0).get_color().equals(pawn_color) && end_pawns.size() == 2){
					return false;
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
		else if(MoveHome.class.isAssignableFrom(move.getClass())){
			MoveHome m = (MoveHome) move;
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
			
			// Check if distance appears in rolls
			int[] rolls = this.game_state.get_rolls();
			for(int r : rolls){
				if(r == distance){
					return true;
				}
			}
			return false; 
		}
		else{
			return false;
		}
		
	}
	
	
	// checks whether a pawn will
	// be bopped given a space and color
	
	public boolean bop(Space s, String color){
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
	public boolean update_Board(IMove move){
		Board b = this.game_state.get_board();
		if (move == null){
			  return false;
		  }
		if (!is_Legal(move)){
			return false;
		}
		
		if (EnterPiece.class.isAssignableFrom(move.getClass())) {
			EnterPiece m = (EnterPiece) move;
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
		
		else if(MoveMain.class.isAssignableFrom(move.getClass())){
			MoveMain m = (MoveMain) move;
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
		else{
			MoveHome m = (MoveHome) move;
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
	}
	
	public int roll_die(boolean from_file) throws NumberFormatException, IOException{
		if(from_file){
			return Integer.parseInt(br.readLine());
		}
		return 1 + (int)(Math.random() * 6);
	}
	
	// returns true if somebody has won game; false otherwise
	public boolean game_over(){
		for(Player p : this.players){
			String color = p.get_color();
			Home h = this.game_state.get_board().get_Home(color);
			
			if(h.get_pawns().size() == this.num_pawns){
				return true;
			}
		}
		return false;
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
		
		// loop through players until winner
		// generate dice rolls
		// ask player for move
		// if move is illegal, remove cheating player from game
		// double penalty thing
		boolean won = false;
		boolean doubles = false; //true if doubles were rolled
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
			}
			
			
			while(game_state.get_rolls().length != 0 && !game_over()){
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
					State curr_state = this.states.get(num_moves);
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
					continue;
				}
			}
			else{
				player_index = (player_index + 1) % num_players;
				this.game_state.set_curr_player(this.players.get(player_index));
			}
			
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
		
		blue.set_moves(blue_moves);
		
		sinit = new State("boards/init.txt",2);
		states.add(sinit);
		for (int i = 1; i <= 14; i++){
			states.add(new State("boards/" + i + ".txt", 2));
		}
	}
	
	public static void main(String argv[]){
		g.start();
	}
	
	
//	// Test entry moves
//	static Game g1;
//	static Board b1;
//	static ArrayList<Player> players1;
//	static State st1;
//	static int[] rolls1;
//	static EnterPiece move1;
//	static EnterPiece move2;
//	static EnterPiece move3;
//	static EnterPiece move4;
//	
//	static Board b2;
//	static Space[] s2;
//	static HomeCircle[] hc2;
//	static Home[] h2;
//	static HashMap<String, ArrayList<HomeRow>> hr2;
//	
//	// Test main moves
//	static Game g2;
//	static Board b3;
//	static ArrayList<Player> players2;
//	static State st2;
//	static int[] rolls2;
//	static MoveMain move5;
//	static MoveMain move6;
//	static MoveMain move7;
//	
//	static Board b4;
//	static Space[] s4;
//	static HomeCircle[] hc4;
//	static Home[] h4;
//	static HashMap<String, ArrayList<HomeRow>> hr4;
//	
//	// Test home row moves
//	static Game g3;
//	static Board b5;
//	static ArrayList<Player> players3;
//	static State st3;
//	static int[] rolls3;
//	static MoveHome move8;
//	static MoveHome move9;
//	static MoveHome move10;
//	
//	static Board b6;
//	static Space[] s6;
//	static HomeCircle[] hc6;
//	static Home[] h6;
//	static HashMap<String, ArrayList<HomeRow>> hr6;
//	
//	// Test blockade check
//	static Game gblock;
//	static Board bblock;
//	static Space[] sblock;
//	static HomeCircle[] hcblock;
//	static Home[] hblock;
//	static HashMap<String, ArrayList<HomeRow>> hrblock;
//	static ArrayList<Player> playersblock;
//	static State stblock;
//	static int[] rollsblock;
//	static MoveMain moveblock1;
//	static MoveMain moveblock2;
//	
//	
//	// Test preventing move onto safe space with opposing pawn
//	static Game gsafe;
//	static Board bsafe;
//	static Space[] ssafe;
//	static HomeCircle[] hcsafe;
//	static Home[] hsafe;
//	static HashMap<String, ArrayList<HomeRow>> hrsafe;
//	static ArrayList<Player> playerssafe;
//	static State stsafe;
//	static int[] rollssafe;
//	static MoveMain movesafe;
//	
//	// Test wraparound main move
//	static Game gwrap;
//	static Board bwrap1;
//	static Board bwrap2;
//	static Space[] swrap1;
//	static HomeCircle[] hcwrap1;
//	static Home[] hwrap1;
//	static HashMap<String, ArrayList<HomeRow>> hrwrap1;
//	static Space[] swrap2;
//	static HomeCircle[] hcwrap2;
//	static Home[] hwrap2;
//	static HashMap<String, ArrayList<HomeRow>> hrwrap2;
//	static ArrayList<Player> playerswrap;
//	static State stwrap;
//	static int[] rollswrap;
//	static MoveMain moveprewrap;
//	static MoveMain movewrap;
//	
//	// Test move into home row from outside
//	static Game ghome;
//	static Board bhome1;
//	static Space[] shome1;
//	static HomeCircle[] hchome1;
//	static Home[] hhome1;
//	static HashMap<String, ArrayList<HomeRow>> hrhome1;
//	static Board bhome2;
//	static Space[] shome2;
//	static HomeCircle[] hchome2;
//	static Home[] hhome2;
//	static HashMap<String, ArrayList<HomeRow>> hrhome2;
//	static ArrayList<Player> playershome;
//	static State sthome;
//	static int[] rollshome;
//	static MoveMain movehome;
//	
//	
//	public static void createExamples(){
//		if(g1 == null){
//			// entry examples
//			Player player = new Player();
//			player.startGame("green");
//			
//			g1 = new Game();
//			g1.register(player);
//			players1 = new ArrayList<Player>();
//			players1.add(player);
//			b1 = new Board(players1, 4);
//			rolls1 = new int[2];
//			rolls1[0] = 1;
//			rolls1[1] = 5;
//			st1 = new State(b1, player, rolls1);
//			g1.set_state(st1);
//			
//			move1 = new EnterPiece(new Pawn(0, "green"));
//			move2 = new EnterPiece(new Pawn(1, "green"));
//			move3 = new EnterPiece(new Pawn(2, "green"));
//			move4 = new EnterPiece(new Pawn(0, "red"));
//			
//			// board after entry move
//			ArrayList<Pawn> empty_pawns = new ArrayList<Pawn>();
//			ArrayList<Pawn> one_pawn = new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green")));
//			s2 = new Space[17];
//			s2[0] = new Entry("green", true, one_pawn);
//			s2[1] = new Space(null, false, empty_pawns);
//			s2[2] = new Space(null, false, empty_pawns);
//			s2[3] = new Space(null, false, empty_pawns);
//			s2[4] = new Space(null, false, empty_pawns);
//			s2[5] = new Space(null, false, empty_pawns);
//			s2[6] = new Space(null, false, empty_pawns);
//			s2[7] = new Space(null, true, empty_pawns);
//			s2[8] = new Space(null, false, empty_pawns);
//			s2[9] = new Space(null, false, empty_pawns);
//			s2[10] = new Space(null, false, empty_pawns);
//			s2[11] = new Space(null, false, empty_pawns);
//			s2[12] = new PreHomeRow("green", true, empty_pawns);
//			s2[13] = new Space(null, false, empty_pawns);
//			s2[14] = new Space(null, false, empty_pawns);
//			s2[15] = new Space(null, false, empty_pawns);
//			s2[16] = new Space(null, false, empty_pawns);
//		    
//			h2 = new Home[1];
//			h2[0] = new Home("green", false, empty_pawns);
//			
//			ArrayList<Pawn> three_pawns = new ArrayList<Pawn>();
//			for(int i = 1; i < 4; i++){
//				three_pawns.add(new Pawn(i, "green"));
//			}
//			
//			hc2 = new HomeCircle[1];
//			hc2[0] = new HomeCircle("green", false, three_pawns);
//			
//			ArrayList<HomeRow> hr = new ArrayList<HomeRow>();
//			for(int j = 0; j < 7; j++){
//				hr.add(new HomeRow("green", false, empty_pawns));
//			}
//			
//			hr2 = new HashMap<String, ArrayList<HomeRow>>();
//			hr2.put("green", new ArrayList<HomeRow>(hr));
//			
//			b2 = new Board(s2, hc2, h2, hr2);
//			
//			//main move examples
//			Player player1 = new Player();
//			Player player2 = new Player();
//			player1.startGame("green");
//			player2.startGame("blue");
//			
//			g2 = new Game();
//			g2.register(player1);
//			g2.register(player2);
//			players2 = new ArrayList<Player>();
//			players2.add(player1);
//			players2.add(player2);
//			b3 = new Board(players2, 4);
//			rolls2 = new int[2];
//			rolls2[0] = 3;
//			rolls2[1] = 5;
//			st2 = new State(b3, player1, rolls2);
//			g2.set_state(st2);
//			
//			move5 = new MoveMain(new Pawn(0, "green"), 0, 3);
//			move6 = new MoveMain(new Pawn(0, "green"), 0, 4);
//			move7 = new MoveMain(new Pawn(0, "green"), 2, 3);
//			
//			// board after main move
//			s4 = new Space[34];
//			s4[0] = new Entry("green", true, empty_pawns);
//			s4[1] = new Space(null, false, empty_pawns);
//			s4[2] = new Space(null, false, empty_pawns);
//			s4[3] = new Space(null, false, one_pawn);
//			s4[4] = new Space(null, false, empty_pawns);
//			s4[5] = new Space(null, false, empty_pawns);
//			s4[6] = new Space(null, false, empty_pawns);
//			s4[7] = new Space(null, true, empty_pawns);
//			s4[8] = new Space(null, false, empty_pawns);
//			s4[9] = new Space(null, false, empty_pawns);
//			s4[10] = new Space(null, false, empty_pawns);
//			s4[11] = new Space(null, false, empty_pawns);
//			s4[12] = new PreHomeRow("green", true, empty_pawns);
//			s4[13] = new Space(null, false, empty_pawns);
//			s4[14] = new Space(null, false, empty_pawns);
//			s4[15] = new Space(null, false, empty_pawns);
//			s4[16] = new Space(null, false, empty_pawns);
//			s4[17] = new Entry("blue", true, empty_pawns);
//			s4[18] = new Space(null, false, empty_pawns);
//			s4[19] = new Space(null, false, empty_pawns);
//			s4[20] = new Space(null, false, empty_pawns);
//			s4[21] = new Space(null, false, empty_pawns);
//			s4[22] = new Space(null, false, empty_pawns);
//			s4[23] = new Space(null, false, empty_pawns);
//			s4[24] = new Space(null, true, empty_pawns);
//			s4[25] = new Space(null, false, empty_pawns);
//			s4[26] = new Space(null, false, empty_pawns);
//			s4[27] = new Space(null, false, empty_pawns);
//			s4[28] = new Space(null, false, empty_pawns);
//			s4[29] = new PreHomeRow("blue", true, empty_pawns);
//			s4[30] = new Space(null, false, empty_pawns);
//			s4[31] = new Space(null, false, empty_pawns);
//			s4[32] = new Space(null, false, empty_pawns);
//			s4[33] = new Space(null, false, empty_pawns);
//		    
//			h4 = new Home[2];
//			h4[0] = new Home("green", false, empty_pawns);
//			h4[1] = new Home("blue", false, empty_pawns);
//			
//			ArrayList<Pawn> four_pawns = new ArrayList<Pawn>();
//			for(int i = 0; i < 4; i++){
//				four_pawns.add(new Pawn(i, "blue"));
//			}
//			
//			hc4 = new HomeCircle[2];
//			hc4[0] = new HomeCircle("green", false, three_pawns);
//			hc4[1] = new HomeCircle("blue", false, four_pawns);
//			
//			ArrayList<HomeRow> hrblue = new ArrayList<HomeRow>();
//			for(int j = 0; j < 7; j++){
//				hrblue.add(new HomeRow("blue", false, empty_pawns));
//			}
//			
//			hr4 = new HashMap<String, ArrayList<HomeRow>>();
//			hr4.put("green", new ArrayList<HomeRow>(hr));
//			hr4.put("blue", new ArrayList<HomeRow>(hrblue));
//			
//			b4 = new Board(s4, hc4, h4, hr4);
//			
//			// home row move examples
//			Player player3 = new Player();
//			player.startGame("green");
//			
//			g3 = new Game();
//			g3.register(player3);
//			players3 = new ArrayList<Player>();
//			players3.add(player3);
//			b5 = new Board(players1, 4);
//			rolls3 = new int[2];
//			rolls3[0] = 1;
//			rolls3[1] = 5;
//			
//			
//			move8 = new MoveHome(new Pawn(0, "green"), 5, 1);
//			move9 = new MoveHome(new Pawn(0, "green"), 5, 5);
//			move10 = new MoveHome(new Pawn(0, "green"), 2, 1);
//			
//			// board after entry move
//			s6 = new Space[17];
//			s6[0] = new Entry("green", true, empty_pawns);
//			s6[1] = new Space(null, false, empty_pawns);
//			s6[2] = new Space(null, false, empty_pawns);
//			s6[3] = new Space(null, false, empty_pawns);
//			s6[4] = new Space(null, false, empty_pawns);
//			s6[5] = new Space(null, false, empty_pawns);
//			s6[6] = new Space(null, false, empty_pawns);
//			s6[7] = new Space(null, true, empty_pawns);
//			s6[8] = new Space(null, false, empty_pawns);
//			s6[9] = new Space(null, false, empty_pawns);
//			s6[10] = new Space(null, false, empty_pawns);
//			s6[11] = new Space(null, false, empty_pawns);
//			s6[12] = new PreHomeRow("green", true, empty_pawns);
//			s6[13] = new Space(null, false, empty_pawns);
//			s6[14] = new Space(null, false, empty_pawns);
//			s6[15] = new Space(null, false, empty_pawns);
//			s6[16] = new Space(null, false, empty_pawns);
//		   
//			h6 = new Home[1];
//			h6[0] = new Home("green", false, empty_pawns);
//			
//			hc6 = new HomeCircle[1];
//			hc6[0] = new HomeCircle("green", false, three_pawns);
//			
//			ArrayList<Pawn> single_pawn = new ArrayList<Pawn>();
//			single_pawn.add(new Pawn(0, "green"));
//			ArrayList<HomeRow> row3 = new ArrayList<HomeRow>();
//			for(int j = 0; j < 5; j++){
//				row3.add(new HomeRow("green", false, empty_pawns));
//			}
//			row3.add(new HomeRow("green", false, single_pawn));
//			for(int j = 6; j < 6; j++){
//				row3.add(new HomeRow("green", false, empty_pawns));
//			}
//			
//			hr6 = new HashMap<String, ArrayList<HomeRow>>();
//			hr6.put("green", row3);
//			
//			b6 = new Board(s6, hc6, h6, hr6);
//			
//			st3 = new State(b6, player3, rolls3);
//			g3.set_state(st3);
//			
//			// blockaded move examples
//			Player playerblock = new Player();
//			
//			gblock = new Game();
//			gblock.register(playerblock);
//			
//			rollsblock = new int[2];
//			rollsblock[0] = 1;
//			rollsblock[1] = 5;
//			
//			
//			moveblock1 = new MoveMain(new Pawn(0, "green"), 5, 1);
//			moveblock2 = new MoveMain(new Pawn(0, "green"), 5, 5);
//			
//			// board with blockade move
//			ArrayList<Pawn> blockade = new ArrayList<Pawn>();
//			blockade.add(new Pawn(0, "blue"));
//			blockade.add(new Pawn(1, "blue"));
//			
//			sblock = new Space[17];
//			sblock[0] = new Entry("green", true, empty_pawns);
//			sblock[1] = new Space(null, false, empty_pawns);
//			sblock[2] = new Space(null, false, empty_pawns);
//			sblock[3] = new Space(null, false, empty_pawns);
//			sblock[4] = new Space(null, false, empty_pawns);
//			sblock[5] = new Space(null, false, one_pawn);
//			sblock[6] = new Space(null, false, empty_pawns);
//			sblock[7] = new Space(null, true, blockade);
//			sblock[8] = new Space(null, false, empty_pawns);
//			sblock[9] = new Space(null, false, empty_pawns);
//			sblock[10] = new Space(null, false, empty_pawns);
//			sblock[11] = new Space(null, false, empty_pawns);
//			sblock[12] = new PreHomeRow("green", true, empty_pawns);
//			sblock[13] = new Space(null, false, empty_pawns);
//			sblock[14] = new Space(null, false, empty_pawns);
//			sblock[15] = new Space(null, false, empty_pawns);
//			sblock[16] = new Space(null, false, empty_pawns);
//		   
//			hblock = new Home[1];
//			hblock[0] = new Home("green", false, empty_pawns);
//			
//			hcblock = new HomeCircle[1];
//			hcblock[0] = new HomeCircle("green", false, three_pawns);
//			
//			ArrayList<HomeRow> r = new ArrayList<HomeRow>();
//			for(int j = 0; j < 7; j++){
//				r.add(new HomeRow("green", false, empty_pawns));
//			}
//			
//			hrblock = new HashMap<String, ArrayList<HomeRow>>();
//			hrblock.put("green", r);
//			
//			bblock = new Board(sblock, hcblock, hblock, hrblock);
//			
//			stblock = new State(bblock, playerblock, rollsblock);
//			gblock.set_state(stblock);
//			
//			// safe space move examples
//			Player playersafe = new Player();
//			
//			gsafe = new Game();
//			gsafe.register(playersafe);
//			
//			rollssafe = new int[2];
//			rollssafe[0] = 1;
//			rollssafe[1] = 5;
//			
//			
//			movesafe = new MoveMain(new Pawn(0, "green"), 7, 5);
//			
//			// board with safe move
//			ArrayList<Pawn> opposing_pawn = new ArrayList<Pawn>();
//			opposing_pawn.add(new Pawn(0, "blue"));
//			
//			ssafe = new Space[17];
//			ssafe[0] = new Entry("green", true, empty_pawns);
//			ssafe[1] = new Space(null, false, empty_pawns);
//			ssafe[2] = new Space(null, false, empty_pawns);
//			ssafe[3] = new Space(null, false, empty_pawns);
//			ssafe[4] = new Space(null, false, empty_pawns);
//			ssafe[5] = new Space(null, false, empty_pawns);
//			ssafe[6] = new Space(null, false, empty_pawns);
//			ssafe[7] = new Space(null, true, one_pawn);
//			ssafe[8] = new Space(null, false, empty_pawns);
//			ssafe[9] = new Space(null, false, empty_pawns);
//			ssafe[10] = new Space(null, false, empty_pawns);
//			ssafe[11] = new Space(null, false, empty_pawns);
//			ssafe[12] = new PreHomeRow("green", true, opposing_pawn);
//			ssafe[13] = new Space(null, false, empty_pawns);
//			ssafe[14] = new Space(null, false, empty_pawns);
//			ssafe[15] = new Space(null, false, empty_pawns);
//			ssafe[16] = new Space(null, false, empty_pawns);
//		   
//			hsafe = new Home[1];
//			hsafe[0] = new Home("green", false, empty_pawns);
//			
//			hcsafe = new HomeCircle[1];
//			hcsafe[0] = new HomeCircle("green", false, three_pawns);
//			
//			hrsafe = new HashMap<String, ArrayList<HomeRow>>();
//			hrsafe.put("green", r);
//			
//			bsafe = new Board(ssafe, hcsafe, hsafe, hrsafe);
//			
//			stsafe = new State(bsafe, playersafe, rollssafe);
//			gsafe.set_state(stsafe);
//			
//			// examples for wraparound move
//			Player playerwrap = new Player();
//			
//			gwrap = new Game();
//			gwrap.register(playerwrap);
//			playerswrap = new ArrayList<Player>();
//			playerswrap.add(playerwrap);
//			rollswrap = new int[2];
//			rollswrap[0] = 1;
//			rollswrap[1] = 5;
//			
//			
//			movewrap = new MoveMain(new Pawn(0, "green"), 16, 5);
//			
//			// board before wraparound move
//			one_pawn = new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green")));
//			swrap1 = new Space[17];
//			swrap1[0] = new Entry("green", true, new ArrayList<Pawn>());
//			swrap1[1] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[2] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[3] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[4] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[5] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[6] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[7] = new Space(null, true, new ArrayList<Pawn>());
//			swrap1[8] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[9] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[10] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[11] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[12] = new PreHomeRow("green", true, new ArrayList<Pawn>());
//			swrap1[13] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[14] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[15] = new Space(null, false, new ArrayList<Pawn>());
//			swrap1[16] = new Space(null, false, one_pawn);
//		   
//			hwrap1 = new Home[1];
//			hwrap1[0] = new Home("green", false, new ArrayList<Pawn>());
//			
//			hcwrap1 = new HomeCircle[1];
//			hcwrap1[0] = new HomeCircle("green", false, three_pawns);
//			
//			ArrayList<HomeRow> rowwrap = new ArrayList<HomeRow>();
//			for(int j = 0; j < 7; j++){
//				rowwrap.add(new HomeRow("green", false, new ArrayList<Pawn>()));
//			}
//			
//			hrwrap1 = new HashMap<String, ArrayList<HomeRow>>();
//			hrwrap1.put("green", rowwrap);
//			
//			bwrap1 = new Board(swrap1, hcwrap1, hwrap1, hrwrap1);
//			stwrap = new State(bwrap1, playerwrap, rollswrap);
//			gwrap.set_state(stwrap);
//			
//			// board after wraparound move
//			one_pawn = new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green")));
//			swrap2 = new Space[17];
//			swrap2[0] = new Entry("green", true, new ArrayList<Pawn>());
//			swrap2[1] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[2] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[3] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[4] = new Space(null, false, one_pawn);
//			swrap2[5] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[6] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[7] = new Space(null, true, new ArrayList<Pawn>());
//			swrap2[8] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[9] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[10] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[11] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[12] = new PreHomeRow("green", true, new ArrayList<Pawn>());
//			swrap2[13] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[14] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[15] = new Space(null, false, new ArrayList<Pawn>());
//			swrap2[16] = new Space(null, false, new ArrayList<Pawn>());
//		   
//			hwrap2 = new Home[1];
//			hwrap2[0] = new Home("green", false, new ArrayList<Pawn>());
//			
//			hcwrap2 = new HomeCircle[1];
//			hcwrap2[0] = new HomeCircle("green", false, three_pawns);
//			
//			
//			hrwrap2 = new HashMap<String, ArrayList<HomeRow>>();
//			hrwrap2.put("green", rowwrap);
//			
//			bwrap2 = new Board(swrap2, hcwrap2, hwrap2, hrwrap2);
//			
//			// examples for move into home row from outside
//			Player playerhome = new Player();
//			
//			ghome = new Game();
//			ghome.register(playerhome);
//			playershome = new ArrayList<Player>();
//			playershome.add(playerhome);
//			rollshome = new int[2];
//			rollshome[0] = 1;
//			rollshome[1] = 5;
//			
//			
//			movehome = new MoveMain(new Pawn(0, "green"), 11, 5);
//			
//			// board before home move
//			one_pawn = new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green")));
//			shome1 = new Space[17];
//			shome1[0] = new Entry("green", true, new ArrayList<Pawn>());
//			shome1[1] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[2] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[3] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[4] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[5] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[6] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[7] = new Space(null, true, new ArrayList<Pawn>());
//			shome1[8] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[9] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[10] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[11] = new Space(null, false, one_pawn);
//			shome1[12] = new PreHomeRow("green", true, new ArrayList<Pawn>());
//			shome1[13] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[14] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[15] = new Space(null, false, new ArrayList<Pawn>());
//			shome1[16] = new Space(null, false, new ArrayList<Pawn>());
//		   
//			hhome1 = new Home[1];
//			hhome1[0] = new Home("green", false, new ArrayList<Pawn>());
//			
//			hchome1 = new HomeCircle[1];
//			hchome1[0] = new HomeCircle("green", false, three_pawns);
//			
//			ArrayList<HomeRow> rowhome = new ArrayList<HomeRow>();
//			for(int j = 0; j < 7; j++){
//				rowhome.add(new HomeRow("green", false, new ArrayList<Pawn>()));
//			}
//			
//			hrhome1 = new HashMap<String, ArrayList<HomeRow>>();
//			hrhome1.put("green", rowhome);
//			
//			bhome1 = new Board(shome1, hchome1, hhome1, hrhome1);
//			sthome = new State(bhome1, playerwrap, rollshome);
//			ghome.set_state(sthome);
//			
//			// board after wraparound move
//			one_pawn = new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green")));
//			shome2 = new Space[17];
//			shome2[0] = new Entry("green", true, new ArrayList<Pawn>());
//			shome2[1] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[2] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[3] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[4] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[5] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[6] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[7] = new Space(null, true, new ArrayList<Pawn>());
//			shome2[8] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[9] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[10] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[11] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[12] = new PreHomeRow("green", true, new ArrayList<Pawn>());
//			shome2[13] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[14] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[15] = new Space(null, false, new ArrayList<Pawn>());
//			shome2[16] = new Space(null, false, new ArrayList<Pawn>());
//		   
//			hhome2 = new Home[1];
//			hhome2[0] = new Home("green", false, new ArrayList<Pawn>());
//			
//			hchome2 = new HomeCircle[1];
//			hchome2[0] = new HomeCircle("green", false, three_pawns);
//			
//			rowhome = new ArrayList<HomeRow>();
//			for(int j = 0; j < 3; j++){
//				rowhome.add(new HomeRow("green", false, new ArrayList<Pawn>()));
//			}
//			rowhome.add(new HomeRow("green", false, one_pawn));
//			for(int j = 4; j < 7; j++){
//				rowhome.add(new HomeRow("green", false, new ArrayList<Pawn>()));
//			}
//			
//			hrhome2 = new HashMap<String, ArrayList<HomeRow>>();
//			hrhome2.put("green", rowhome);
//			
//			bhome2 = new Board(shome2, hchome2, hhome2, hrhome2);
//		}
//		
//	}
//	
//	public static void main(String[] argv){
//		Tester.check(g1.is_Legal(move1), "entry move legal test 1");
//		g1.update_Board(move1);
//		Tester.check(g1.game_state.get_board().equals(b2), "entry move test");
//		Tester.check(!g1.is_Legal(move1), "entry move legal test 2");
//		Tester.check(!g1.is_Legal(move4), "entry move legal test 3");
//		Tester.check(!g1.is_Legal(move2), "entry move legal test 4");
//		g1.update_Board(move2);
//		Tester.check(!g1.is_Legal(move3), "entry move legal test 5");
//		
//		g2.update_Board(move1);
//		Tester.check(g2.is_Legal(move5), "main move legal test 1");
//		Tester.check(!g2.is_Legal(move6), "main move legal test 2");
//		Tester.check(!g2.is_Legal(move7), "main move legal test 3");
//		g2.update_Board(move5);
//		Tester.check(g2.game_state.get_board().equals(b4), "main move test");
//		
//		Tester.check(g3.is_Legal(move8), "home row move legal test 1");
//		Tester.check(!g3.is_Legal(move9), "home row move legal test 2");
//		Tester.check(!g3.is_Legal(move10), "home row move legal test 3");
//		
//		Tester.check(gblock.is_Legal(moveblock1), "blockade move legal test 1");
//		Tester.check(!gblock.is_Legal(moveblock2), "blockade move legal test 2");
//		
//		Tester.check(!gsafe.is_Legal(movesafe), "safe space move legal test 1");
//		
//		Tester.check(gwrap.is_Legal(movewrap), "wraparound move legal test");
//		gwrap.update_Board(movewrap);
//		Tester.check(gwrap.game_state.get_board().equals(bwrap2), "wraparound move test");
//		
//		Tester.check(ghome.is_Legal(movehome), "move to home legal test");
//		ghome.update_Board(movehome);
//		Tester.check(ghome.game_state.get_board().equals(bhome2), "move to home test");
//
//
//		
//		
//		
//		
//	}
	
}


