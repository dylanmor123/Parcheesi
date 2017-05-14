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

// represents a move that starts on one of the home rows
class MoveHome implements IMove {
  Pawn pawn;
  int start;
  int distance;
  
  private int BOARD_LENGTH = 68;
  
  // 0 <= start <= BOARD_LENGTH - 1
  // distance > 0
  
  MoveHome(Pawn pawn, int start, int distance) throws Exception{
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
  
  MoveHome(Document d){
	  
	  Node root = d.getFirstChild();
	  NodeList move_piece_home = root.getChildNodes();
	  Node pawn = move_piece_home.item(0);
	  Node start = move_piece_home.item(1);
	  Node distance = move_piece_home.item(2);
	  String pawn_color = pawn.getFirstChild().getTextContent();
	  int pawn_id = Integer.parseInt(pawn.getLastChild().getTextContent());
	  this.start = Integer.parseInt(start.getTextContent());
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
		if (!MoveHome.class.isAssignableFrom(m.getClass())) {
			return false;
		}
		
		MoveHome move = (MoveHome) m;
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
		if(spaces_to_check.size() != 0){
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
	
	public Document toXMLDoc() throws ParserConfigurationException, TransformerException{
        DocumentBuilderFactory dbFactory =
        DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = 
           dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        //create root Move Element
        Element rootElement = doc.createElement("move-piece-home");
        doc.appendChild(rootElement);
        
        
        //create Element for a Home move 
        Element pawn = doc.createElement("pawn");
        Element color = doc.createElement("color");
        Element id = doc.createElement("id");
        Element start = doc.createElement("start");
        Element distance = doc.createElement("distance");
        color.appendChild(doc.createTextNode(this.pawn.get_color()));
        id.appendChild(doc.createTextNode(Integer.toString(this.pawn.get_id())));
        pawn.appendChild(color);
        pawn.appendChild(id);
        start.appendChild(doc.createTextNode(Integer.toString(this.get_start())));
        distance.appendChild(doc.createTextNode(Integer.toString(this.get_distance())));
		rootElement.appendChild(pawn);
		rootElement.appendChild(start);
		rootElement.appendChild(distance);
		
        return doc;

        
	}
}