import java.util.ArrayList;
import java.util.Arrays;

public class RuleChecker {	
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
						moves_remaining = moves_remaining || new MoveMain(p, i, val).is_Legal(game_state, prev_state);
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
						moves_remaining = moves_remaining || new MoveHome(p, i, val).is_Legal(game_state, prev_state);
					}
				}
			}
		}
		
		// check all home circle pawns for moves
		HomeCircle h = game_state.get_board().get_HomeCircle(color);
		for(Pawn p: h.get_pawns()){
			moves_remaining = moves_remaining || new EnterPiece(p).is_Legal(game_state, prev_state);
		}
		
		
		return moves_remaining;
	}
}
