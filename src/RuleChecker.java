import java.util.ArrayList;
import java.util.Arrays;

public class RuleChecker {
	// determines if a given IMove is legal
	// looks at curr_state and uses game rules
	// Entry move
	private static boolean is_Legal(EnterPiece m, State game_state, State prev_state){
		if(m == null){
			return false;
		}
		
		Pawn pawn = m.get_pawn();
		Player player = (Player) game_state.get_curr_player();
		Board b = game_state.get_board();
		
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
		Entry entry = game_state.get_board().get_Entry(player_color);
		if (entry == null){
			return false;
		}
		ArrayList<Pawn> in_entry = entry.get_pawns();
		if(in_entry.size() > 1){
			return false;
		}
		
		// check if die rolls allow entry
		int[] rolls = game_state.get_rolls();
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
	private static boolean is_Legal(MoveMain m, State game_state, State prev_state){
		if(m == null){
			return false;
		}
		
		Pawn pawn = m.get_pawn();
		int start = m.get_start();
		int distance = m.get_distance();
		Player player = (Player) game_state.get_curr_player();
		Board b = game_state.get_board();
		
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
		int curr_space_index = (start + 1) % space_length; // next space on board; could wrap around
		boolean in_home_row = false; // true if list of spaces includes home row
		boolean reached_home = false; // true if list of spaces includes home
		Space curr_space = null;
		ArrayList<HomeRow> homerow = null;
		
		while(spaces_to_check.size() < distance){
			if(!in_home_row){
				curr_space = spaces[curr_space_index];
				spaces_to_check.add(curr_space);
				
				if (pawn_color.equals(curr_space.get_color()) && PreHomeRow.class.isAssignableFrom(curr_space.getClass())){
					homerow = game_state.get_board().get_HomeRow(pawn_color);
					in_home_row = true;
					curr_space_index = 0;
					
				}
				else{
					curr_space_index = (curr_space_index + 1) % space_length; // next space on board; could wrap around
				}
			}
			else if(!reached_home){
				if(curr_space_index == homerow.size()){
					curr_space = game_state.get_board().get_Home(pawn_color);
					spaces_to_check.add(curr_space);
					//check if move is not done - return false if so
					if(spaces_to_check.size() < distance){
						return false;
					}
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
			
			Board prev_board = prev_state.get_board();
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
		int[] rolls = game_state.get_rolls();
		for(int r : rolls){
			if(r == distance){
				return true;
			}
		}
		return false;
		
	}
	
	// Home row move
	private static boolean is_Legal(MoveHome m, State game_state, State prev_state){
		if(m == null){
			return false;
		}
		
		Pawn pawn = m.get_pawn();
		int start = m.get_start();
		int distance = m.get_distance();
		Player player = (Player) game_state.get_curr_player();
		Board b = game_state.get_board();
		
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
			if(curr_space_index == row_length){
				break;
			}
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
			
			Board prev_board = prev_state.get_board();
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
		int[] rolls = game_state.get_rolls();
		for(int r : rolls){
			if(r == distance){
				return true;
			}
		}
		return false;
		
	}
	
	public static boolean is_Legal(IMove move, State game_state, State prev_state){
		if(move == null){
			return false;
		}
		
		if (EnterPiece.class.isAssignableFrom(move.getClass())) {
			EnterPiece m = (EnterPiece) move;
			return is_Legal(m, game_state, prev_state);
		}
		else if(MoveMain.class.isAssignableFrom(move.getClass())){
			MoveMain m = (MoveMain) move;
			return is_Legal(m, game_state, prev_state);
			
		}
		else if(MoveHome.class.isAssignableFrom(move.getClass())){
			MoveHome m = (MoveHome) move;
			return is_Legal(m, game_state, prev_state); 
		}
		else{
			return false;
		}
		
	}
	
	// returns false if all legal moves have been exhausted by the current player; true otherwise
	public static boolean moves_remaining(Player player, State game_state, State prev_state) throws Exception{
		String color = player.get_color();
		boolean moves_remaining = false;
		
		// check all main pawns for moves
		Space[] main_spaces = game_state.get_board().get_Spaces();
		for(int i = 0; i < main_spaces.length; i++){
			Space s = main_spaces[i];
			for(Pawn p: s.get_pawns()){
				if(p.get_color().equals(color)){
					for(int val: game_state.rolls_vals_left){
						moves_remaining = moves_remaining || RuleChecker.is_Legal(new MoveMain(p, i, val), game_state, prev_state);
					}
				}
			}
		}
		
		// check all home row pawns for moves
		ArrayList<HomeRow> row = game_state.get_board().get_HomeRow(color);
		for(int i = 0; i < row.size(); i++){
			HomeRow s = row.get(i);
			for(Pawn p: s.get_pawns()){
				if(p.get_color().equals(color)){
					for(int val: game_state.rolls_vals_left){
						moves_remaining = moves_remaining || RuleChecker.is_Legal(new MoveHome(p, i, val), game_state, prev_state);
					}
				}
			}
		}
		
		// check all home circle pawns for moves
		HomeCircle h = game_state.get_board().get_HomeCircle(color);
		for(Pawn p: h.get_pawns()){
			moves_remaining = moves_remaining || RuleChecker.is_Legal(new EnterPiece(p), game_state, prev_state);
		}
		
		
		return moves_remaining;
	}
}
