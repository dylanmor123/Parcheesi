import java.util.ArrayList;
import java.util.Arrays;

public class RuleChecker {
	// returns the list of lists of legal sets of moves
	public static ArrayList<ArrayList<IMove>> get_move_lists(IPlayer player, State game_state, State prev_state, int cap) throws Exception{
		
		
		ArrayList<ArrayList<IMove>> moves_list = new ArrayList<ArrayList<IMove>>();
		// no moves remaining - return empty list
		if(!moves_remaining(player, game_state, prev_state)){
			return moves_list;
		}
		
		// get the list of first possible moves to iterate over
		ArrayList<IMove> first_moves = get_possible_moves(player, game_state, prev_state);
		State new_state;
		
		// iterate over first moves
		for(IMove move: first_moves){
			if(moves_list.size() >= cap){
				break;
			}
			new_state = new State(game_state);
			new_state = move.update_Board(new_state);
			
			// recursively get lists of following moves
			ArrayList<ArrayList<IMove>> next_moves = get_move_lists(player, new_state, prev_state, cap);
			
			if(next_moves.size() == 0){
				ArrayList<IMove> path = new ArrayList<IMove>();
				path.add(move);
				moves_list.add(path);
			}
			else{
				for(ArrayList<IMove> rest: next_moves){
					ArrayList<IMove> path = new ArrayList<IMove>(rest);
					path.add(0, move);
					moves_list.add(path);
				}
			}
			
		}
		
		
		
		return moves_list;
	};
	
	// returns a list of all possible single moves from state information
	private static ArrayList<IMove> get_possible_moves(IPlayer player, State game_state, State prev_state) throws Exception{
		String color = player.get_color();
		ArrayList<IMove> moves_possible = new ArrayList<IMove>();
		IMove possible_move;
		
		// check all main pawns for moves
		Space[] main_spaces = game_state.get_board().get_Spaces();
		for(int i = 0; i < main_spaces.length; i++){
			Space s = main_spaces[i];
			for(Pawn p: s.get_pawns()){
				if(p.get_color().equals(color)){
					for(int val: game_state.rolls_vals_left){
						possible_move = new MoveMain(p, i, val);
						
						// if the move is legal, add it
						if(possible_move.is_Legal(game_state, prev_state)){
							moves_possible.add(possible_move);
						}
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
						possible_move = new MoveHome(p, i, val);
						
						// if the move is legal, add it
						if(possible_move.is_Legal(game_state, prev_state)){
							moves_possible.add(possible_move);
						}
					}
				}
			}
		}
		
		// check all home circle pawns for moves
		HomeCircle h = game_state.get_board().get_HomeCircle(color);
		for(Pawn p: h.get_pawns()){
			possible_move = new EnterPiece(p);
			
			// if the move is legal, add it
			if(possible_move.is_Legal(game_state, prev_state)){
				moves_possible.add(possible_move);
			}
		}
		
		return moves_possible;
	}
	
	// returns false if all legal moves have been exhausted by the current player; true otherwise
	public static boolean moves_remaining(IPlayer player, State game_state, State prev_state) throws Exception{
		return get_possible_moves(player, game_state, prev_state).size() != 0;
	}
}
