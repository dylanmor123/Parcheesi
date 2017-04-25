import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class State {
	protected Board curr_b;
	protected Player curr_player;
	protected int[] rolls_vals_left = new int[0];
	
	// private boolean for sequence contract (add_roll* . remove_roll*)*
	private boolean has_rolls = false;
	
	// copy constructor for state
	public State(State s){
		this.curr_b = new Board(s.curr_b);
		this.curr_player = new Player(s.curr_player);
		this.rolls_vals_left = s.rolls_vals_left;
	}
	
	// construct game state from text file
	public State(String filepath, int num_players) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(filepath)); 
	    for(String line; (line = br.readLine()) != null; ) {
	        if(line.equals("_Player")){
	        	String color = br.readLine();
	        	boolean doubles_penalty = Boolean.parseBoolean(br.readLine());
	        	this.curr_player = new Player(color, doubles_penalty);
	        }
	        else if(line.equals("_Rolls")){
	        	line = br.readLine();
	        	while(!line.equals("_Board")){
	        		this.add_roll(Integer.parseInt(line));
	        		line = br.readLine();
	        	}
	        }
	        else{ // everything after _Board
	        	Space[] spaces = new Space[17 * num_players];
	        	int i = 0;
	        	
	        	while(!line.equals("_HomeCircle")){
	        		String type = line;
	        		String color = br.readLine();
	        		if(color.equals("null")){
	        			color = null;
	        		}
	        		boolean safe = Boolean.parseBoolean(br.readLine());
	        		line = br.readLine();
	        		ArrayList<Pawn> pawns = new ArrayList<Pawn>();
	        		while(line.equals("_Pawn")){
	        			String pawn_color = br.readLine();
	        			int id = Integer.parseInt(br.readLine());
	        			pawns.add(new Pawn(id, pawn_color));
	        			line = br.readLine();
	        		}
	        		
	        		Space s = null;
	        		
	        		if(type.equals("_Entry")){
	        			s = new Entry(color, safe, pawns);
	        		}
	        		else if (type.equals("_Space")){
	        			s = new Space(color, safe, pawns);
	        		}
	        		else{
	        			s = new PreHomeRow(color, safe, pawns);
	        		}
	        		
	        		spaces[i] = s;
	        		i++;
	        		
	        	}
	        	
	        	HomeCircle[] home_circles = new HomeCircle[num_players];
	        	i = 0;
	        	
	        	while(!line.equals("_Home")){
	        		String color = br.readLine();
	        		boolean safe = Boolean.parseBoolean(br.readLine());
	        		line = br.readLine();
	        		ArrayList<Pawn> pawns = new ArrayList<Pawn>();
	        		while(line.equals("_Pawn")){
	        			String pawn_color = br.readLine();
	        			int id = Integer.parseInt(br.readLine());
	        			pawns.add(new Pawn(id, pawn_color));
	        			line = br.readLine();
	        		}
	        		
	        		home_circles[i] = new HomeCircle(color, safe, pawns);
	        		i++;
	        		
	        	}
	        	
	        	Home[] homes= new Home[num_players];
	        	i = 0;
	        	
	        	while(!line.equals("_HomeRows")){
	        		String color = br.readLine();
	        		boolean safe = Boolean.parseBoolean(br.readLine());
	        		line = br.readLine();
	        		ArrayList<Pawn> pawns = new ArrayList<Pawn>();
	        		while(line.equals("_Pawn")){
	        			String pawn_color = br.readLine();
	        			int id = Integer.parseInt(br.readLine());
	        			pawns.add(new Pawn(id, pawn_color));
	        			line = br.readLine();
	        		}
	        		
	        		homes[i] = new Home(color, safe, pawns);
	        		i++;
	        	}
	        	
	        	HashMap<String, ArrayList<HomeRow>> home_rows = new HashMap<String, ArrayList<HomeRow>>();
	        	
	        	while(line != null){
	        		ArrayList<HomeRow> row = new ArrayList<HomeRow>();
	        		line = br.readLine();
	        		String key = null;
	        		while(line != null && line.equals("_Color")){
	        			key = br.readLine();
	        			line = br.readLine();
	        			while(line != null && line.equals("_HomeRow")){
	        				String space_color = br.readLine();
	        				boolean safe = Boolean.parseBoolean(br.readLine());
	        				line = br.readLine();
	        				ArrayList<Pawn> pawns = new ArrayList<Pawn>();
	    	        		while(line != null && line.equals("_Pawn")){
	    	        			String pawn_color = br.readLine();
	    	        			int id = Integer.parseInt(br.readLine());
	    	        			pawns.add(new Pawn(id, pawn_color));
	    	        			line = br.readLine();
	    	        		}
	    	        		
	    	        		row.add(new HomeRow(space_color, safe, pawns));
	        			}
	        			home_rows.put(key, row);
		        		row = new ArrayList<HomeRow>();
	        		}
	        		
	        	}
	        	
	        	this.curr_b = new Board(spaces, home_circles, homes, home_rows);
	        }
	    }
	}
	
	public State(Board b, Player p, int[] rolls){
		this.curr_b = b;
		this.curr_player = p;
		this.rolls_vals_left = rolls;
	}
	
	public Board get_board(){
		return this.curr_b;
	}
	
	public Player get_curr_player(){
		return this.curr_player;
	}
	
	public int[] get_rolls(){
		return this.rolls_vals_left;
	}
	
	public void set_board(Board b){
		this.curr_b = b;
	}
	
	public void set_curr_player(Player p){
		this.curr_player = p;
	}
	
	public void set_rolls(int[] r){
		this.rolls_vals_left = r;
		if(r.length == 0)
			this.has_rolls = false;
		else
			this.has_rolls = true;
	}
	
	public boolean remove_roll(int r) throws Exception{
		if(!this.has_rolls){
			throw new Exception("Invalid roll removals");
		}
		
		ArrayList<Integer> new_rolls = new ArrayList<Integer>();
		boolean removed = false;
		for(int i=0; i < rolls_vals_left.length; i++){
			if(rolls_vals_left[i] != r || removed) {
				new_rolls.add(rolls_vals_left[i]);
			}
			else if(rolls_vals_left[i] == r){
				removed = true;
			}
		}
		int[] nr = new int[new_rolls.size()];
		for(int i=0; i < new_rolls.size(); i++){
		   nr[i] = new_rolls.get(i);
		}
		this.set_rolls(nr);
		return removed;
	}
	
	public void add_roll(int r) throws Exception{
		this.has_rolls = true;
		
		if(r <= 0){
			throw new Exception("Non-negative roll value added");
		}
		
		int[] new_rolls = new int[this.rolls_vals_left.length+1];
		for (int i=0; i < rolls_vals_left.length; i++){
			new_rolls[i] = rolls_vals_left[i];
		}
		new_rolls[new_rolls.length - 1] = r;
		
		this.rolls_vals_left = new_rolls;
	}
	
	@Override
	public boolean equals(Object s){
		if(s == null){
			return false;
		}
		if (!State.class.isAssignableFrom(s.getClass())) {
			return false;
		}
		
		State state = (State) s;
		int[] rolls1 = state.rolls_vals_left;
		int[] rolls2 = this.rolls_vals_left;
		Arrays.sort(rolls1);
		Arrays.sort(rolls2);
		return state.get_curr_player().get_color().equals(this.curr_player.get_color()) && state.get_board().equals(this.curr_b) && (Arrays.equals(rolls1, rolls2));
	}
}
	
