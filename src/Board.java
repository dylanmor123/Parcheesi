import java.util.ArrayList;
import java.util.HashMap;

class Board {
	protected Space[] spaces;
	protected HomeCircle[] home_circles;
	protected Home[] home_spaces;
	protected HashMap<String, HomeRow[]> home_rows;
	
	//constructor based on list of players. Called in game.start()
	public Board(ArrayList<Player> players, int num_pawns){
		ArrayList<Pawn> empty_pawn_list = new ArrayList<Pawn>();
		ArrayList<String> color_list = new ArrayList<String>();
		for(Player p : players){
			color_list.add(p.color);
		}
		
		// initialize home_circles, home_spaces, home_rows
		this.home_circles = new HomeCircle[color_list.size()];
		this.home_spaces = new Home[color_list.size()];
		this.home_rows = new HashMap<String, HomeRow[]>();
		for(int i = 0; i < color_list.size(); i++){
			ArrayList<Pawn> pawn_list = new ArrayList<Pawn>();
			for(int j = 0; j < num_pawns; j++){
				pawn_list.add(new Pawn(j, color_list.get(i)));
			}
			// 7 spaces in each home row
			HomeRow[] home_row = new HomeRow[7];
			for(int k = 0; k < 7; k++){
				home_row[k] = new HomeRow(color_list.get(i), false, empty_pawn_list);
			}
			
			home_circles[i] = new HomeCircle(color_list.get(i), false, pawn_list);
			home_spaces[i] = new Home(color_list.get(i), false, empty_pawn_list);
			home_rows.put(color_list.get(i), home_row);
			
		}
		
		// initialize remainder of spaces
		// 17 spaces per player
		this.spaces = new Space[players.size() * 17];
		for(int i = 0; i < color_list.size(); i++){
			int start = i * 17;
			// entry space
			this.spaces[start] = new Entry(color_list.get(i), true, empty_pawn_list);
			
			// 6 unsafe spaces
			for(int j = 1; j < 7; j++){
				this.spaces[start + j] = new Space(null, false, empty_pawn_list);
			}
			
			// 1 safe space
			this.spaces[start + 6] = new Space(null, true, empty_pawn_list);
			
			// 4 unsafe spaces
			for(int j = 8; j < 12; j++){
				this.spaces[start + j] = new Space(null, false, empty_pawn_list);
			}
			
			// 1 pre-home row space
			this.spaces[start + 12] = new PreHomeRow(color_list.get(i), true, empty_pawn_list);
			
			// 4 unsafe spaces
			for(int j = 13; j < 17; j++){
				this.spaces[start + j] = new Space(null, false, empty_pawn_list);
			} 
		}
		
		
	}
	
	public Board(Space[] spaces, HomeCircle[] home_circles, 
			Home[] home_spaces, HashMap<String, HomeRow[]> home_rows) {
		this.spaces = spaces;
		this.home_circles = home_circles;
		this.home_spaces = home_spaces;
		this.home_rows = home_rows;
	}
	
	
	
	
	
	
}
