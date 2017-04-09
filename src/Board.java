import java.util.ArrayList;
import java.util.HashMap;

class Board {
	protected Space[] spaces;
	protected HomeCircle[] home_circles;
	protected Home[] home_spaces;
	protected HashMap<String, ArrayList<HomeRow>> home_rows;
	
	//constructor based on list of players. Called in game.start()
	public Board(ArrayList<Player> players, int num_pawns){
		ArrayList<Pawn> empty_pawn_list = new ArrayList<Pawn>();
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
				home_row.add(new HomeRow(color_list.get(i), false, empty_pawn_list));
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
			this.spaces[start + 7] = new Space(null, true, empty_pawn_list);
			
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
			Home[] home_spaces, HashMap<String, ArrayList<HomeRow>> home_rows) {
		this.spaces = spaces;
		this.home_circles = home_circles;
		this.home_spaces = home_spaces;
		this.home_rows = home_rows;
	}
	
	@Override
	public boolean equals(Object b){
		if(b == null){
			return false;
		}
		System.out.println("check");
		if (!Board.class.isAssignableFrom(b.getClass())) {
			return false;
		}
		System.out.println("check 1");
		Board board = (Board) b;
		boolean same_board = true;
		
		// check if length of space arrays are the same
		if((this.spaces.length != board.spaces.length) || (this.home_circles.length != board.home_circles.length) || (this.home_spaces.length != board.home_spaces.length)){
			return false;
		}
		System.out.println("check 2");
		
		// check if home_rows are equal
		same_board = same_board && this.home_rows.equals(board.home_rows);
		System.out.println("check 3");
		
		//check if spaces are equal
		for(int i = 0; i < this.spaces.length; i++){
			same_board = same_board && this.spaces[i].equals(board.spaces[i]);
		}
		System.out.println("check 4");
		
		//check if home_circles are equal
		for(int i = 0; i < this.home_circles.length; i++){
			same_board = same_board && this.home_circles[i].equals(board.home_circles[i]);
		}
		System.out.println("check 5");

		//check if home_spaces are equal
		for(int i = 0; i < this.home_spaces.length; i++){
			same_board = same_board && this.home_spaces[i].equals(board.home_spaces[i]);
		}
		System.out.println("check 6");
		
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
			spaces1 = new Space[17];
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
			spaces1[12] = new PreHomeRow("green", true, empty_pawns);
			spaces1[13] = new Space(null, false, empty_pawns);
			spaces1[14] = new Space(null, false, empty_pawns);
			spaces1[15] = new Space(null, false, empty_pawns);
			spaces1[16] = new Space(null, false, empty_pawns);
		    
			homes1 = new Home[1];
			homes1[0] = new Home("green", false, empty_pawns);
			
			ArrayList<Pawn> four_pawns = new ArrayList<Pawn>();
			for(int i = 0; i < 4; i++){
				four_pawns.add(new Pawn(i, "green"));
			}
			
			homecircles1 = new HomeCircle[1];
			homecircles1[0] = new HomeCircle("green", false, four_pawns);
			
			ArrayList<HomeRow> homerow1 = new ArrayList<HomeRow>();
			for(int j = 0; j < 7; j++){
				homerow1.add(new HomeRow("green", false, empty_pawns));
			}
			
			homerows1 = new HashMap<String, ArrayList<HomeRow>>();
			homerows1.put("green", new ArrayList<HomeRow>(homerow1));
			
			players1 = new ArrayList<Player>();
			Player p = new Player();
			p.startGame("green");
			players1.add(p);
			
			board1 = new Board(spaces1, homecircles1, homes1, homerows1);
			
		}
    }

    // ------------------------------------------------------------------
	// Tests: 
	
	public static void main(String argv[]) {
		System.out.println(players1);
		Board b = new Board(players1, 4);
		
		Tester.check(b.equals(board1), "one-person board init");
	}

	
	
}
