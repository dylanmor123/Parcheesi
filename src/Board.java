import java.util.ArrayList;
import java.util.HashMap;

class Board {
	protected Space[] spaces;
	protected HomeCircle[] home_circles;
	protected Home[] home_spaces;
	protected HashMap<String, ArrayList<HomeRow>> home_rows;
	
	//constructor based on list of players. Called in game.start()
	public Board(ArrayList<Player> players, int num_pawns){
		ArrayList<String> color_list = new ArrayList<String>();
		for(Player p : players){
			color_list.add(p.get_color());
		}
		
		// initialize home_circles, home_spaces, home_rows
		this.home_circles = new HomeCircle[color_list.size()];
		this.home_spaces = new Home[color_list.size()];
		this.home_rows = new HashMap<String, ArrayList<HomeRow>>();
		for(int i = 0; i < color_list.size(); i++){
			ArrayList<Pawn> pawn_list = new ArrayList<Pawn>();
			for(int j = 0; j < num_pawns; j++){
				pawn_list.add(new Pawn(j, color_list.get(i)));
			}
			// 7 spaces in each home row
			ArrayList<HomeRow> home_row = new ArrayList<HomeRow>();
			for(int k = 0; k < 7; k++){
				home_row.add(new HomeRow(color_list.get(i), false, new ArrayList<Pawn>()));
			}
			
			home_circles[i] = new HomeCircle(color_list.get(i), false, pawn_list);
			home_spaces[i] = new Home(color_list.get(i), false, new ArrayList<Pawn>());
			home_rows.put(color_list.get(i), home_row);
			
		}
		
		// initialize remainder of spaces
		// 17 spaces per player
		this.spaces = new Space[players.size() * 17];
		for(int i = 0; i < color_list.size(); i++){
			int start = i * 17;
			// entry space
			this.spaces[start] = new Entry(color_list.get(i), true, new ArrayList<Pawn>());
			
			// 6 unsafe spaces
			for(int j = 1; j < 7; j++){
				this.spaces[start + j] = new Space(null, false, new ArrayList<Pawn>());
			}
			
			// 1 safe space
			this.spaces[start + 7] = new Space(null, true, new ArrayList<Pawn>());
			
			// 4 unsafe spaces
			for(int j = 8; j < 12; j++){
				this.spaces[start + j] = new Space(null, false, new ArrayList<Pawn>());
			}
			
			// 1 pre-home row space
			this.spaces[start + 12] = new PreHomeRow(color_list.get((i + 1) % color_list.size()), true, new ArrayList<Pawn>());
			
			// 4 unsafe spaces
			for(int j = 13; j < 17; j++){
				this.spaces[start + j] = new Space(null, false, new ArrayList<Pawn>());
			} 
		}
		
		
	}
	
	public Board(Space[] spaces, HomeCircle[] home_circles, 
			Home[] home_spaces, HashMap<String, ArrayList<HomeRow>> home_rows) {
		this.spaces = spaces;
		this.home_circles = home_circles;
		this.home_spaces = home_spaces;
		this.home_rows = home_rows;
	}
	
	public Home get_Home(String color){
		  for (Home h : this.home_spaces) {
			  if (h.get_color().equals(color)){
				  return h;
			  }
		  }
		  return null;
	}
	
	public HomeCircle get_HomeCircle(String color){
		  for (HomeCircle h : this.home_circles) {
			  if (h.get_color().equals(color)){
				  return h;
			  }
		  }
		  return null;
	}
	
	public ArrayList<HomeRow> get_HomeRow(String color){
		  return this.home_rows.get(color);
	}
	
	public Space[] get_Spaces(){
		return this.spaces;
	}
	
	public Entry get_Entry(String color){
		for (Space s : this.spaces) {
			  if (s.get_color() == null){
				  continue;
			  }
			  else if (s.get_color().equals(color) && Entry.class.isAssignableFrom(s.getClass())){
				  return (Entry) s;
			  }
		  }
		return null;
	}
	
	@Override
	public boolean equals(Object b){
		if(b == null){
			return false;
		}
		if (!Board.class.isAssignableFrom(b.getClass())) {
			return false;
		}
		Board board = (Board) b;
		boolean same_board = true;
		
		// check if length of space arrays are the same
		if((this.spaces.length != board.spaces.length) || (this.home_circles.length != board.home_circles.length) || (this.home_spaces.length != board.home_spaces.length)){
			return false;
		}
		
		// check if home_rows are equal
		same_board = same_board && this.home_rows.equals(board.home_rows);
		
		//check if spaces are equal
		for(int i = 0; i < this.spaces.length; i++){
			same_board = same_board && this.spaces[i].equals(board.spaces[i]);
		}
		
		//check if home_circles are equal
		for(int i = 0; i < this.home_circles.length; i++){
			same_board = same_board && this.home_circles[i].equals(board.home_circles[i]);
		}

		//check if home_spaces are equal
		for(int i = 0; i < this.home_spaces.length; i++){
			same_board = same_board && this.home_spaces[i].equals(board.home_spaces[i]);
		}
		
		return same_board;
	}
	
	// ------------------------------------------------------------------
    // Examples: 

    static Board board1;
    static Space[] spaces1;
    static Home[] homes1;
    static HomeCircle[] homecircles1;
    static HashMap<String, ArrayList<HomeRow>> homerows1;
    static ArrayList<Player> players1;
    
    public static void createExamples() {
		if (board1 == null) {
			ArrayList<Pawn> empty_pawns = new ArrayList<Pawn>();
			spaces1 = new Space[34];
			spaces1[0] = new Entry("green", true, empty_pawns);
			spaces1[1] = new Space(null, false, empty_pawns);
			spaces1[2] = new Space(null, false, empty_pawns);
			spaces1[3] = new Space(null, false, empty_pawns);
			spaces1[4] = new Space(null, false, empty_pawns);
			spaces1[5] = new Space(null, false, empty_pawns);
			spaces1[6] = new Space(null, false, empty_pawns);
			spaces1[7] = new Space(null, true, empty_pawns);
			spaces1[8] = new Space(null, false, empty_pawns);
			spaces1[9] = new Space(null, false, empty_pawns);
			spaces1[10] = new Space(null, false, empty_pawns);
			spaces1[11] = new Space(null, false, empty_pawns);
			spaces1[12] = new PreHomeRow("blue", true, empty_pawns);
			spaces1[13] = new Space(null, false, empty_pawns);
			spaces1[14] = new Space(null, false, empty_pawns);
			spaces1[15] = new Space(null, false, empty_pawns);
			spaces1[16] = new Space(null, false, empty_pawns);
			spaces1[17] = new Entry("blue", true, empty_pawns);
			spaces1[18] = new Space(null, false, empty_pawns);
			spaces1[19] = new Space(null, false, empty_pawns);
			spaces1[20] = new Space(null, false, empty_pawns);
			spaces1[21] = new Space(null, false, empty_pawns);
			spaces1[22] = new Space(null, false, empty_pawns);
			spaces1[23] = new Space(null, false, empty_pawns);
			spaces1[24] = new Space(null, true, empty_pawns);
			spaces1[25] = new Space(null, false, empty_pawns);
			spaces1[26] = new Space(null, false, empty_pawns);
			spaces1[27] = new Space(null, false, empty_pawns);
			spaces1[28] = new Space(null, false, empty_pawns);
			spaces1[29] = new PreHomeRow("green", true, empty_pawns);
			spaces1[30] = new Space(null, false, empty_pawns);
			spaces1[31] = new Space(null, false, empty_pawns);
			spaces1[32] = new Space(null, false, empty_pawns);
			spaces1[33] = new Space(null, false, empty_pawns);
		    
			homes1 = new Home[2];
			homes1[0] = new Home("green", false, empty_pawns);
			homes1[1] = new Home("blue", false, empty_pawns);
			
			ArrayList<Pawn> four_green_pawns = new ArrayList<Pawn>();
			for(int i = 0; i < 4; i++){
				four_green_pawns.add(new Pawn(i, "green"));
			}
			
			ArrayList<Pawn> four_blue_pawns = new ArrayList<Pawn>();
			for(int i = 0; i < 4; i++){
				four_blue_pawns.add(new Pawn(i, "blue"));
			}
			
			homecircles1 = new HomeCircle[2];
			homecircles1[0] = new HomeCircle("green", false, four_green_pawns);
			homecircles1[1] = new HomeCircle("blue", false, four_blue_pawns);
			
			ArrayList<HomeRow> homerow1 = new ArrayList<HomeRow>();
			for(int j = 0; j < 7; j++){
				homerow1.add(new HomeRow("green", false, empty_pawns));
			}
			ArrayList<HomeRow> homerow2 = new ArrayList<HomeRow>();
			for(int j = 0; j < 7; j++){
				homerow2.add(new HomeRow("blue", false, empty_pawns));
			}
			
			homerows1 = new HashMap<String, ArrayList<HomeRow>>();
			homerows1.put("green", new ArrayList<HomeRow>(homerow1));
			homerows1.put("blue", new ArrayList<HomeRow>(homerow2));
			
			players1 = new ArrayList<Player>();
			Player p = new Player();
			p.startGame("green");
			Player q = new Player();
			q.startGame("blue");
			players1.add(p);
			players1.add(q);
			
			board1 = new Board(spaces1, homecircles1, homes1, homerows1);
			
		}
    }

    // ------------------------------------------------------------------
	// Tests: 
	
	public static void main(String argv[]) {
		Board b = new Board(players1, 4);
		
		Tester.check(b.equals(board1), "two-person board init");
	}

	
	
}
