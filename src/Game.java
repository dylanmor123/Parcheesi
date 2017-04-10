import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Game implements IGame {
	protected ArrayList<Player> players = new ArrayList<Player>();
	protected int num_pawns = 4; //number of pawns per player
	protected State game_state;

	public void register(IPlayer p) {
		Player player = (Player) p;
		this.players.add(player);
		String color = "green";
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
			//Logic for MoveHome move type
		}
		else{
			return false;
		}
		
		return false;
	}
	
	
	
	
	
	
	
	// alters Board in curr_state with move
	// checks if move is legal before update
	public int update_Board(IMove move){
		if (move == null){
			  return 0;
		  }
		
		if (EnterPiece.class.isAssignableFrom(move.getClass())) {
			Board b = this.game_state.get_board();
			EnterPiece m = (EnterPiece) move;
			Pawn p = m.get_pawn();
			String pawn_color = p.get_color();		   
			HomeCircle h = b.get_HomeCircle(pawn_color);
			for (Space s : b.get_Spaces()) {
				  if (s.get_color() == null){
					  continue;
				  }
				  else if (s.get_color().equals(pawn_color) && Entry.class.isAssignableFrom(s.getClass())){
					  s.add_Pawn(p);
					  h.remove_Pawn(p);
					  break;
				  }
			  }
		  }
		
		else if(MoveMain.class.isAssignableFrom(move.getClass())){
			return 0;
		}
		return 0;
	}
	
	public void start() {
		// this.b = new Board(this.players, num_pawns);
	}
	
	// Examples
	// Test entry moves
	static Game g1;
	static Board b1;
	static ArrayList<Player> players1;
	static State st1;
	static int[] rolls1;
	static EnterPiece move1;
	static EnterPiece move2;
	static EnterPiece move3;
	static EnterPiece move4;
	
	static Board b2;
	static Space[] s2;
	static HomeCircle[] hc2;
	static Home[] h2;
	static HashMap<String, ArrayList<HomeRow>> hr2;
	
	// Test main moves
	static Game g2;
	static Board b3;
	static ArrayList<Player> players2;
	static State st2;
	static int[] rolls2;
	static MoveMain move5;
	static MoveMain move6;
	static MoveMain move7;
	
	static Board b4;
	static Space[] s4;
	static HomeCircle[] hc4;
	static Home[] h4;
	static HashMap<String, ArrayList<HomeRow>> hr4;
	
	
	
	public static void createExamples(){
		if(g1 == null){
			// entry examples
			Player player = new Player();
			player.startGame("green");
			
			g1 = new Game();
			g1.register(player);
			players1 = new ArrayList<Player>();
			players1.add(player);
			b1 = new Board(players1, 4);
			rolls1 = new int[2];
			rolls1[0] = 1;
			rolls1[1] = 5;
			st1 = new State(b1, player, rolls1);
			g1.set_state(st1);
			
			move1 = new EnterPiece(new Pawn(0, "green"));
			move2 = new EnterPiece(new Pawn(1, "green"));
			move3 = new EnterPiece(new Pawn(2, "green"));
			move4 = new EnterPiece(new Pawn(0, "red"));
			
			// board after entry move
			ArrayList<Pawn> empty_pawns = new ArrayList<Pawn>();
			ArrayList<Pawn> one_pawn = new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green")));
			s2 = new Space[17];
			s2[0] = new Entry("green", true, one_pawn);
			s2[1] = new Space(null, false, empty_pawns);
			s2[2] = new Space(null, false, empty_pawns);
			s2[3] = new Space(null, false, empty_pawns);
			s2[4] = new Space(null, false, empty_pawns);
			s2[5] = new Space(null, false, empty_pawns);
			s2[6] = new Space(null, false, empty_pawns);
			s2[7] = new Space(null, true, empty_pawns);
			s2[8] = new Space(null, false, empty_pawns);
			s2[9] = new Space(null, false, empty_pawns);
			s2[10] = new Space(null, false, empty_pawns);
			s2[11] = new Space(null, false, empty_pawns);
			s2[12] = new PreHomeRow("green", true, empty_pawns);
			s2[13] = new Space(null, false, empty_pawns);
			s2[14] = new Space(null, false, empty_pawns);
			s2[15] = new Space(null, false, empty_pawns);
			s2[16] = new Space(null, false, empty_pawns);
		    
			h2 = new Home[1];
			h2[0] = new Home("green", false, empty_pawns);
			
			ArrayList<Pawn> three_pawns = new ArrayList<Pawn>();
			for(int i = 1; i < 4; i++){
				three_pawns.add(new Pawn(i, "green"));
			}
			
			hc2 = new HomeCircle[1];
			hc2[0] = new HomeCircle("green", false, three_pawns);
			
			ArrayList<HomeRow> hr = new ArrayList<HomeRow>();
			for(int j = 0; j < 7; j++){
				hr.add(new HomeRow("green", false, empty_pawns));
			}
			
			hr2 = new HashMap<String, ArrayList<HomeRow>>();
			hr2.put("green", new ArrayList<HomeRow>(hr));
			
			b2 = new Board(s2, hc2, h2, hr2);
			
			//main move examples
			Player player1 = new Player();
			Player player2 = new Player();
			player1.startGame("green");
			player2.startGame("blue");
			
			g2 = new Game();
			g2.register(player1);
			g2.register(player2);
			players2 = new ArrayList<Player>();
			players2.add(player1);
			players2.add(player2);
			b3 = new Board(players2, 4);
			rolls2 = new int[2];
			rolls2[0] = 3;
			rolls2[1] = 5;
			st2 = new State(b3, player1, rolls2);
			g2.set_state(st2);
			
			move5 = new MoveMain(new Pawn(0, "green"), 0, 3);
			move6 = new MoveMain(new Pawn(0, "green"), 0, 4);
			move7 = new MoveMain(new Pawn(0, "green"), 2, 3);
			
			// board after main move
			s4 = new Space[34];
			s4[0] = new Entry("green", true, empty_pawns);
			s4[1] = new Space(null, false, empty_pawns);
			s4[2] = new Space(null, false, empty_pawns);
			s4[3] = new Space(null, false, one_pawn);
			s4[4] = new Space(null, false, empty_pawns);
			s4[5] = new Space(null, false, empty_pawns);
			s4[6] = new Space(null, false, empty_pawns);
			s4[7] = new Space(null, true, empty_pawns);
			s4[8] = new Space(null, false, empty_pawns);
			s4[9] = new Space(null, false, empty_pawns);
			s4[10] = new Space(null, false, empty_pawns);
			s4[11] = new Space(null, false, empty_pawns);
			s4[12] = new PreHomeRow("green", true, empty_pawns);
			s4[13] = new Space(null, false, empty_pawns);
			s4[14] = new Space(null, false, empty_pawns);
			s4[15] = new Space(null, false, empty_pawns);
			s4[16] = new Space(null, false, empty_pawns);
			s4[17] = new Entry("blue", true, empty_pawns);
			s4[18] = new Space(null, false, empty_pawns);
			s4[19] = new Space(null, false, empty_pawns);
			s4[20] = new Space(null, false, empty_pawns);
			s4[21] = new Space(null, false, empty_pawns);
			s4[22] = new Space(null, false, empty_pawns);
			s4[23] = new Space(null, false, empty_pawns);
			s4[24] = new Space(null, true, empty_pawns);
			s4[25] = new Space(null, false, empty_pawns);
			s4[26] = new Space(null, false, empty_pawns);
			s4[27] = new Space(null, false, empty_pawns);
			s4[28] = new Space(null, false, empty_pawns);
			s4[29] = new PreHomeRow("blue", true, empty_pawns);
			s4[30] = new Space(null, false, empty_pawns);
			s4[31] = new Space(null, false, empty_pawns);
			s4[32] = new Space(null, false, empty_pawns);
			s4[33] = new Space(null, false, empty_pawns);
		    
			h4 = new Home[2];
			h4[0] = new Home("green", false, empty_pawns);
			h4[1] = new Home("blue", false, empty_pawns);
			
			ArrayList<Pawn> four_pawns = new ArrayList<Pawn>();
			for(int i = 0; i < 4; i++){
				four_pawns.add(new Pawn(i, "blue"));
			}
			
			hc4 = new HomeCircle[2];
			hc4[0] = new HomeCircle("green", false, three_pawns);
			hc4[1] = new HomeCircle("blue", false, four_pawns);
			
			ArrayList<HomeRow> hrblue = new ArrayList<HomeRow>();
			for(int j = 0; j < 7; j++){
				hrblue.add(new HomeRow("blue", false, empty_pawns));
			}
			
			hr4 = new HashMap<String, ArrayList<HomeRow>>();
			hr4.put("green", new ArrayList<HomeRow>(hr));
			hr4.put("blue", new ArrayList<HomeRow>(hrblue));
			
			b4 = new Board(s4, hc4, h4, hr4);
		}
		
	}
	
	public static void main(String[] argv){
		Tester.check(g1.is_Legal(move1), "entry move legal test 1");
		g1.update_Board(move1);
		Tester.check(g1.game_state.get_board().equals(b2), "entry move test");
		Tester.check(!g1.is_Legal(move1), "entry move legal test 2");
		Tester.check(!g1.is_Legal(move4), "entry move legal test 3");
		Tester.check(g1.is_Legal(move2), "entry move legal test 4");
		g1.update_Board(move2);
		Tester.check(!g1.is_Legal(move3), "entry move legal test 5");
		
		g2.update_Board(move1);
		Tester.check(g2.is_Legal(move5), "main move legal test 1");
		Tester.check(!g2.is_Legal(move6), "main move legal test 2");
		Tester.check(!g2.is_Legal(move7), "main move legal test 3");
//		g2.update_Board(move5);
//		Tester.check(g2.game_state.get_board().equals(b4), "main move test");
		
		
		
		
	}
	
}


