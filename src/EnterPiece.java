import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// represents a move where a player enters a piece
class EnterPiece implements IMove {
  private Pawn pawn;
  public Pawn get_pawn(){
	  return this.pawn;
  }
  EnterPiece(Pawn pawn) {
    this.pawn=pawn;
  }
  public EnterPiece(Document d){
	  Node root = d.getFirstChild();
	  Node pawn = root.getFirstChild();
	  String pawn_color = pawn.getFirstChild().getTextContent();
	  int pawn_id = Integer.parseInt(pawn.getLastChild().getTextContent());
	  this.pawn = new Pawn(pawn_id, pawn_color);
	  
  }
  
  @Override
	public boolean equals(Object m){
		if(m == null){
			return false;
		}
		if (!EnterPiece.class.isAssignableFrom(m.getClass())) {
			return false;
		}
		
		EnterPiece move = (EnterPiece) m;
		return this.pawn.equals(move.pawn);	
	}
  
  public boolean is_Legal(State game_state, State prev_state){
		Pawn pawn = this.get_pawn();
		IPlayer player = game_state.get_curr_player();
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
  
  public State update_Board(State given_state) throws Exception{
		State game_state = new State(given_state);
		Board b = game_state.get_board();
		
		Pawn p = this.get_pawn();
		String pawn_color = p.get_color();		   
		HomeCircle h = b.get_HomeCircle(pawn_color);
		Entry e = b.get_Entry(pawn_color);
		boolean bopped = e.bop(pawn_color);
		
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
	
	public Document toXMLDoc() throws ParserConfigurationException, TransformerException{
        DocumentBuilderFactory dbFactory =
        DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = 
           dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        //create root Move Element
        Element rootElement = doc.createElement("enter-piece");
        doc.appendChild(rootElement);
        
        
        //create Element for an Enter move 
        Element pawn = doc.createElement("pawn");
        Element color = doc.createElement("color");
        Element id = doc.createElement("id");
        color.appendChild(doc.createTextNode(this.pawn.get_color()));
        id.appendChild(doc.createTextNode(Integer.toString(this.pawn.get_id())));
        pawn.appendChild(color);
        pawn.appendChild(id);
		rootElement.appendChild(pawn);
		
        return doc;
        
	}

}