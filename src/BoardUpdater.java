import java.util.ArrayList;

public class BoardUpdater {
	// checks whether a pawn will
	// be bopped given a space and color
	private static boolean bop(Space s, String color){
		if (s.pawns_list.size() == 1){
			Pawn curr_pawn = s.get_pawns().get(0);
			if(!curr_pawn.get_color().equals(color)){		
				return true;
			}
		}
		return false;
	}
	
	// alters Board in curr_state with move
	// checks if move is legal before update
	private static State update_Board(EnterPiece m, State given_state) throws Exception{
		State game_state = new State(given_state);
		
		Board b = game_state.get_board();
		if (m == null){
			throw new Exception("No move given");
		}
		
		Pawn p = m.get_pawn();
		String pawn_color = p.get_color();		   
		HomeCircle h = b.get_HomeCircle(pawn_color);
		Entry e = b.get_Entry(pawn_color);
		boolean bopped = bop(e, pawn_color);
		
		if(bopped){
			Pawn curr_pawn = e.get_pawns().get(0);
			HomeCircle curr_pawn_home = game_state.get_board().get_HomeCircle(curr_pawn.get_color());
			e.remove_Pawn(curr_pawn);
			curr_pawn_home.add_Pawn(curr_pawn);
			
			game_state.add_roll(20);
		}
		
		e.add_Pawn(p);
		h.remove_Pawn(p);

		if (!game_state.remove_roll(5)){
			game_state.set_rolls(new int[0]);
		}
		
		return game_state;
	}
	
	private static State update_Board(MoveMain m, State given_state) throws Exception{
		State game_state = new State(given_state);
		
		Board b = game_state.get_board();
		if (m == null){
			throw new Exception("No move given");
		}
		
		Pawn p = m.get_pawn();
		String pawn_color = p.get_color();	
		int start = m.get_start();
		int distance = m.get_distance();
		Space[] spaces = game_state.get_board().get_Spaces();
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
					Home home = game_state.get_board().get_Home(pawn_color);
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
		
		if(bopped){
			Pawn curr_pawn = ending_space.get_pawns().get(0);
			HomeCircle curr_pawn_home = game_state.get_board().get_HomeCircle(curr_pawn.get_color());
			ending_space.remove_Pawn(curr_pawn);
			curr_pawn_home.add_Pawn(curr_pawn);
			
			game_state.add_roll(20);
		}
		
		ending_space.add_Pawn(p);
		starting_space.remove_Pawn(p);

		game_state.remove_roll(distance);
		
		if(reached_home){
			game_state.add_roll(10);
		}
		return game_state;		
	}
	
	private static State update_Board(MoveHome m, State given_state) throws Exception{
		State game_state = new State(given_state);
		
		Board b = game_state.get_board();
		if (m == null){
			throw new Exception("No move given");
		}
		
		Pawn p = m.get_pawn();
		String pawn_color = p.get_color();	
		int start = m.get_start();
		int distance = m.get_distance();
		ArrayList<HomeRow> homerow = game_state.get_board().get_HomeRow(pawn_color);
		Space starting_space = homerow.get(start);
		Space ending_space = null;
		if(start + distance < 7){
			ending_space = homerow.get(start+distance);
		}
		else{
			ending_space = game_state.get_board().get_Home(pawn_color);
			game_state.add_roll(10);
		}
		ending_space.add_Pawn(p);
		starting_space.remove_Pawn(p);
		game_state.remove_roll(distance);

		
		return game_state;		
	}
	
	
	public static State update_Board(IMove move, State given_state) throws Exception{
		if (EnterPiece.class.isAssignableFrom(move.getClass())) {
			EnterPiece m = (EnterPiece) move;
			return update_Board(m, given_state);
		  }
		
		else if(MoveMain.class.isAssignableFrom(move.getClass())){
			MoveMain m = (MoveMain) move;
			return update_Board(m, given_state);
		  }
		else{
			MoveHome m = (MoveHome) move;
			return update_Board(m, given_state);
		}
	}
}
