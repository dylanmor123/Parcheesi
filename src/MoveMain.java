import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// represents a move that starts on the main ring
// (but does not have to end up there)

class MoveMain implements IMove {
  protected Pawn pawn;
  protected int start;
  protected int distance;
  
  private int BOARD_LENGTH = 68;
  
  // 0 <= start <= BOARD_LENGTH - 1
  // distance > 0
  MoveMain(Pawn pawn, int start, int distance) throws Exception{
	if(start < 0 || start >= BOARD_LENGTH){
		throw new Exception("Invalid move start");
	}
	
	if(distance <= 0){
		throw new Exception("Invalid move distance");
	}
	
	
	this.pawn=pawn;
	this.start=start;
	this.distance=distance;
  }
  
  MoveMain(Document d){
	  Node root = d.getFirstChild();
	  NodeList move_piece = root.getChildNodes();
	  Node pawn = move_piece.item(0);
	  Node start = move_piece.item(1);
	  Node distance = move_piece.item(2);
	  String pawn_color = pawn.getFirstChild().getTextContent();
	  int pawn_id = Integer.parseInt(pawn.getLastChild().getTextContent());
	  this.start = (((Integer.parseInt(start.getTextContent()) - 5) % this.BOARD_LENGTH) + this.BOARD_LENGTH) % this.BOARD_LENGTH;
	  this.distance = Integer.parseInt(distance.getTextContent());
	  this.pawn = new Pawn(pawn_id, pawn_color);
	  
  }
  
  public Pawn get_pawn(){
	  return this.pawn;
  }
  
  public int get_start(){
	  return this.start;
  }
  
  public int get_distance(){
	  return this.distance;
  }
  
  	@Override
	public boolean equals(Object m){
		if(m == null){
			return false;
		}
		if (!MoveMain.class.isAssignableFrom(m.getClass())) {
			return false;
		}
		
		MoveMain move = (MoveMain) m;
		return this.pawn.equals(move.pawn) && (this.start == move.start) && (this.distance == move.distance);	
	}
  	
  	public boolean is_Legal(State game_state, State prev_state){
		Pawn pawn = this.get_pawn();
		int start = this.get_start();
		int distance = this.get_distance();
		IPlayer player = game_state.get_curr_player();
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
		int curr_space_index;
		boolean in_home_row;
		ArrayList<HomeRow> homerow = null;
		
		if(s instanceof PreHomeRow && s.get_color().equals(pawn_color)){
			curr_space_index = 0; // index of first space in home row
			in_home_row = true;
			homerow = game_state.get_board().get_HomeRow(pawn_color);
		}
		else{
			curr_space_index = (start + 1) % space_length; // next space on board; could wrap around
			in_home_row = false; // true if list of spaces includes home row
		}
		
		boolean reached_home = false; // true if list of spaces includes home
		Space curr_space = null;
		
		while(spaces_to_check.size() < distance){
			if(!in_home_row){
				curr_space = spaces[curr_space_index];
				spaces_to_check.add(curr_space);
				
				if (pawn_color.equals(curr_space.get_color()) && curr_space instanceof PreHomeRow){
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
					//check if move is not done - return false if so
					if(spaces_to_check.size() < distance - 1){
						return false;
					}
					else{
						break;
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
  	
  	public State update_Board(State given_state) throws Exception{
		State game_state = new State(given_state);
		
		Board b = game_state.get_board();
		
		Pawn p = this.get_pawn();
		String pawn_color = p.get_color();	
		int start = this.get_start();
		int distance = this.get_distance();
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
		boolean bopped = ending_space.bop(pawn_color);
		
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
  	
	
	public Document toXMLDoc() throws ParserConfigurationException, TransformerException{
        DocumentBuilderFactory dbFactory =
        DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = 
           dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        //create root Move Element
        Element rootElement = doc.createElement("move-piece-main");
        doc.appendChild(rootElement);
        
       // <move-piece-main> <pawn> <color> green </color> <id> 0 </id> </pawn> <start> 5 </start> 
       // <distance> 3 </distance> </move-piece-main> </moves>
        
        //create Element for a main move 
        Element pawn = doc.createElement("pawn");
        Element color = doc.createElement("color");
        Element id = doc.createElement("id");
        Element start = doc.createElement("start");
        Element distance = doc.createElement("distance");
        color.appendChild(doc.createTextNode(this.pawn.get_color()));
        id.appendChild(doc.createTextNode(Integer.toString(this.pawn.get_id())));
        pawn.appendChild(color);
        pawn.appendChild(id);
        int new_start = (((this.get_start() + 5) % this.BOARD_LENGTH) + this.BOARD_LENGTH) % this.BOARD_LENGTH;
        start.appendChild(doc.createTextNode(Integer.toString(new_start)));
        distance.appendChild(doc.createTextNode(Integer.toString(this.get_distance())));
		rootElement.appendChild(pawn);
		rootElement.appendChild(start);
		rootElement.appendChild(distance);
		
        return doc;

        
	}
}
