import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class Board {
	protected Space[] spaces;
	protected HomeCircle[] home_circles;
	protected Home[] home_spaces;
	protected HashMap<String, ArrayList<HomeRow>> home_rows;
	
	// copy constructor
	public Board(Board b){
		this.spaces = new Space[b.spaces.length];
		for (int i = 0; i < b.spaces.length; i++){
			if (Entry.class.isAssignableFrom(b.spaces[i].getClass())) {
				this.spaces[i] = new Entry((Entry) b.spaces[i]);
			}
			else if (PreHomeRow.class.isAssignableFrom(b.spaces[i].getClass())) {
				this.spaces[i] = new PreHomeRow((PreHomeRow) b.spaces[i]);
			}
			else{
				this.spaces[i] = new Space(b.spaces[i]);
			}	
		}
		
		this.home_circles = new HomeCircle[b.home_circles.length];
		for (int i = 0; i < b.home_circles.length; i++){
			this.home_circles[i] = new HomeCircle(b.home_circles[i]);
		}
		
		this.home_spaces = new Home[b.home_spaces.length];
		for (int i = 0; i < b.home_spaces.length; i++){
			this.home_spaces[i] = new Home(b.home_spaces[i]);
		}
		
		this.home_rows = new HashMap<String, ArrayList<HomeRow>>(b.home_rows);
	}
	
	//constructor based on list of players. Called in game.start()
	public Board(ArrayList<NPlayer> players, int num_pawns){
		ArrayList<String> color_list = new ArrayList<String>();
		for(NPlayer p : players){
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
	
	
	//constructor from XML string
	public Board(String XMLString) throws SAXException, IOException, ParserConfigurationException{
		Document doc =  XMLUtils.StringtoXML(XMLString);		
		this.home_circles = new HomeCircle[4];
		this.home_spaces = new Home[4];
		this.home_rows = new HashMap<String, ArrayList<HomeRow>>();
		this.spaces = new Space[68];


		Node root = doc.getFirstChild();
		NodeList all_spaces = root.getChildNodes();
		Node Start = all_spaces.item(0);
		Node main = all_spaces.item(1);
		Node homerows = all_spaces.item(2);
		Node home = all_spaces.item(3);

		NodeList start_pawns = Start.getChildNodes();
		NodeList home_pawns = home.getChildNodes();
		NodeList homerow_pawns = homerows.getChildNodes();
		NodeList main_pawns = main.getChildNodes();

		ArrayList<Pawn> green_start_pawns = new ArrayList<Pawn>();
		ArrayList<Pawn> red_start_pawns = new ArrayList<Pawn>();
		ArrayList<Pawn> yellow_start_pawns = new ArrayList<Pawn>();
		ArrayList<Pawn> blue_start_pawns = new ArrayList<Pawn>();
		
		
		//Add all pawns in the starting spaces to their respective Home Circles
		for(int i = 0; i < start_pawns.getLength(); i++){
			String pawn_color = start_pawns.item(i).getFirstChild().getTextContent();
			int pawn_id = Integer.parseInt(start_pawns.item(i).getLastChild().getTextContent());
			Pawn pawn = new Pawn(pawn_id, pawn_color);
			if(pawn_color.equals("green")){
				green_start_pawns.add(pawn);
			}
			
			else if(pawn_color.equals("red")){
				red_start_pawns.add(pawn);
			}
			
			else if(pawn_color.equals("yellow")){
				yellow_start_pawns.add(pawn);
			}
			
			else if(pawn_color.equals("blue")){
				blue_start_pawns.add(pawn);
			}
		}
		
		home_circles[0] = new HomeCircle("green", false, green_start_pawns);
		home_circles[1] = new HomeCircle("red", false, red_start_pawns);
		home_circles[2] = new HomeCircle("yellow", false, yellow_start_pawns);
		home_circles[3] = new HomeCircle("blue", false, blue_start_pawns);
		
		
		
		ArrayList<Pawn> green_home_pawns = new ArrayList<Pawn>();
		ArrayList<Pawn> red_home_pawns = new ArrayList<Pawn>();
		ArrayList<Pawn> yellow_home_pawns = new ArrayList<Pawn>();
		ArrayList<Pawn> blue_home_pawns = new ArrayList<Pawn>();
		
		
		//Add all pawns in the home spaces to their respective Homes
		for(int i = 0; i < home_pawns.getLength(); i++){
			String pawn_color = home_pawns.item(i).getFirstChild().getTextContent();
			int pawn_id = Integer.parseInt(home_pawns.item(i).getLastChild().getTextContent());
			Pawn pawn = new Pawn(pawn_id, pawn_color);
			if(pawn_color.equals("green")){
				green_home_pawns.add(pawn);
			}
			
			else if(pawn_color.equals("red")){
				red_home_pawns.add(pawn);
			}
			
			else if(pawn_color.equals("yellow")){
				yellow_home_pawns.add(pawn);
			}
			
			else if(pawn_color.equals("blue")){
				blue_home_pawns.add(pawn);
			}
		}
		
		home_spaces[0] = new Home("green", false, green_home_pawns);
		home_spaces[1] = new Home("red", false, red_home_pawns);
		home_spaces[2] = new Home("yellow", false, yellow_home_pawns);
		home_spaces[3] = new Home("blue", false, blue_home_pawns);

		// initialize main ring spaces
		// 17 spaces per player
		List<String> color_list = Arrays.asList("green", "red", "blue", "yellow");
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
			this.spaces[start + 12] = new PreHomeRow(color_list.get((i + 1) % 4), true, new ArrayList<Pawn>());
			
			// 4 unsafe spaces
			for(int j = 13; j < 17; j++){
				this.spaces[start + j] = new Space(null, false, new ArrayList<Pawn>());
			} 
		}
		
		for(int i = 0; i < main_pawns.getLength(); i++){
			String pawn_color = main_pawns.item(i).getFirstChild().getFirstChild().getTextContent();
			int pawn_id = Integer.parseInt(main_pawns.item(i).getFirstChild().getLastChild().getTextContent());
			int loc = Integer.parseInt(main_pawns.item(i).getLastChild().getTextContent());
			Pawn pawn = new Pawn(pawn_id, pawn_color);
	        int new_index = (((loc - 5) % this.spaces.length) + this.spaces.length) % this.spaces.length;
	        spaces[new_index].add_Pawn(pawn);
		}
		
		// Create Home Rows
		ArrayList<HomeRow> green_homerow = new ArrayList<HomeRow>();
		ArrayList<HomeRow> red_homerow = new ArrayList<HomeRow>();
		ArrayList<HomeRow> yellow_homerow = new ArrayList<HomeRow>();
		ArrayList<HomeRow> blue_homerow = new ArrayList<HomeRow>();

		for(int k = 0; k < 7; k++){
			green_homerow.add(new HomeRow("green", false, new ArrayList<Pawn>()));
			red_homerow.add(new HomeRow("red", false, new ArrayList<Pawn>()));
			yellow_homerow.add(new HomeRow("yellow", false, new ArrayList<Pawn>()));
			blue_homerow.add(new HomeRow("blue", false, new ArrayList<Pawn>()));
		}
		
		//add pawns to each respective home row
		for(int i = 0; i < homerow_pawns.getLength(); i++){
			String pawn_color = homerow_pawns.item(i).getFirstChild().getFirstChild().getTextContent();
			int pawn_id = Integer.parseInt(homerow_pawns.item(i).getFirstChild().getLastChild().getTextContent());
			int loc = Integer.parseInt(homerow_pawns.item(i).getLastChild().getTextContent());
			Pawn pawn = new Pawn(pawn_id, pawn_color);
			if(pawn_color.equals("green")){
				green_homerow.get(loc).add_Pawn(pawn);
			}

			else if(pawn_color.equals("red")){
				red_homerow.get(loc).add_Pawn(pawn);
			}
			
			else if(pawn_color.equals("yellow")){
				yellow_homerow.get(loc).add_Pawn(pawn);
			}
			
			else if(pawn_color.equals("blue")){
				blue_homerow.get(loc).add_Pawn(pawn);
			}
		}
		
		home_rows.put("green", green_homerow);
		home_rows.put("red", red_homerow);
		home_rows.put("yellow", yellow_homerow);
		home_rows.put("blue", blue_homerow);
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
	
	public PawnLocation get_Pawn_Location(Pawn p){
		//TODO: WRITE METHOD
		// check in spaces
		for(int i = 0; i < this.spaces.length; i++){
			Space s = this.spaces[i];
			if(s.get_pawns() != null && s.get_pawns().contains(p)){
				return new PawnLocation("main", i, s.get_safe());
			}
		}
		
		// check in home row
		ArrayList<HomeRow> hr = this.get_HomeRow(p.get_color());
		for(int i = 0; i < hr.size(); i++){
			HomeRow h = hr.get(i);
			if(h.get_pawns() != null && h.get_pawns().contains(p)){
				return new PawnLocation("home row", i, false);
			}
		}
		
		// check in home circle
		HomeCircle hc = this.get_HomeCircle(p.get_color());
		if(hc.get_pawns() != null && hc.get_pawns().contains(p)){
			return new PawnLocation("home circle", -1, false);
		}
		
		// if not in any of these places, must be in home
		return new PawnLocation("home", -1, false);
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
		Set<String> colors = this.home_rows.keySet();
		for (String color : colors) {
		    same_board = same_board && this.home_rows.get(color).equals(board.home_rows.get(color));
		}
		
		//check if spaces are equal
		for(int i = 0; i < this.spaces.length; i++){
			same_board = same_board && this.spaces[i].equals(board.spaces[i]);
		}
		
		//check if home_circles are equal
		int num_same_home_circles = 0;
		for(int i = 0; i < this.home_circles.length; i++){
			for(int j = 0; j < board.home_circles.length; j++){
				if(this.home_circles[i].equals(board.home_circles[j])){
					num_same_home_circles++;
				};
			}
		}
		same_board = same_board && (num_same_home_circles == 4);
		

		//check if home_spaces are equal
		int num_same_homes = 0;
		for(int i = 0; i < this.home_spaces.length; i++){
			for(int j = 0; j < board.home_spaces.length; j++){
				if(this.home_spaces[i].equals(board.home_spaces[j])){
					num_same_homes++;
				};
			}
		}
		same_board = same_board && (num_same_homes == 4);
		
		return same_board;
	}
	
	// pop furthest pawn of a certain color from board and return it
	// return null if no pawns found that can be popped from board
	public Pawn remove_furthest(String color){
		
		// search in home row
		ArrayList<HomeRow> row = this.get_HomeRow(color);
		for(int i = row.size() - 1; i >= 0; i--){
			HomeRow s = row.get(i);
			if(s.get_pawns().size() > 0 && s.get_pawns().get(0).get_color().equals(color)){
				Pawn p = s.get_pawns().get(0);
				s.remove_Pawn(p);
				return p;
			}
		}
		
		// search in main ring
		// find entry index
		Entry e = this.get_Entry(color);
		int entry_index = -1;
		for(int k = 0; k < this.spaces.length; k++){
			if(e.equals(this.spaces[k])){
				entry_index = k;
				break;
			}
		}
		// k is index of entry in array of spaces
		
		for(int j = (((entry_index - 1) % this.spaces.length) + this.spaces.length) % this.spaces.length; j != entry_index; j = (j - 1) % this.spaces.length){
			Space s = this.spaces[j];
			if(s.get_pawns().size() > 0 && s.get_pawns().get(0).get_color().equals(color)){
				Pawn p = s.get_pawns().get(0);
				s.remove_Pawn(p);
				return p;
			}
		}
		
		return null; // no pawns in vulnerable spaces on the board
	}
	
	public Document BoardtoXML() throws ParserConfigurationException, TransformerException{
        DocumentBuilderFactory dbFactory =
        DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = 
           dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        //create root Board Element
        Element rootElement = doc.createElement("board");
        doc.appendChild(rootElement);
        
        //create start Element within board for homecircles
        Element start = doc.createElement("start");
        for(HomeCircle hc : this.home_circles){
        	for(Pawn p : hc.get_pawns()){
                Element pawn = doc.createElement("pawn");
                Element color = doc.createElement("color");
                Element id = doc.createElement("id");
                color.appendChild(doc.createTextNode(p.get_color()));
                id.appendChild(doc.createTextNode(Integer.toString(p.get_id())));
                pawn.appendChild(color);
                pawn.appendChild(id);
        		start.appendChild(pawn);
        	}
        }
             
        rootElement.appendChild(start);

        //create main element within board for main ring spaces
        Element main = doc.createElement("main");
    	for(int i = 0; i < this.spaces.length; i++){
    		Space s = this.spaces[i];
        	for(Pawn p : s.get_pawns()){
        		Element piece_loc = doc.createElement("piece-loc");
                Element pawn = doc.createElement("pawn");
                Element color = doc.createElement("color");
                Element id = doc.createElement("id");
                Element loc = doc.createElement("loc");
                
                int new_index = (((i + 5) % this.spaces.length) + this.spaces.length) % this.spaces.length;
                
                color.appendChild(doc.createTextNode(p.get_color()));
                id.appendChild(doc.createTextNode(Integer.toString(p.get_id())));
                loc.appendChild(doc.createTextNode(Integer.toString(new_index)));
                pawn.appendChild(color);
                pawn.appendChild(id);
        		piece_loc.appendChild(pawn);
        		piece_loc.appendChild(loc);
        		main.appendChild(piece_loc);
        	}
    	}
        rootElement.appendChild(main);
        
        //create home-rows element within board for home-row spaces
        Element homerows = doc.createElement("home-rows");
        for(ArrayList<HomeRow> home_row : this.home_rows.values()){
        	for(int i = 0; i < home_row.size(); i++){
        		HomeRow hr = home_row.get(i);
            	for(Pawn p : hr.get_pawns()){
            		Element piece_loc = doc.createElement("piece-loc");
                    Element pawn = doc.createElement("pawn");
                    Element color = doc.createElement("color");
                    Element id = doc.createElement("id");
                    Element loc = doc.createElement("loc");
                    
                    color.appendChild(doc.createTextNode(p.get_color()));
                    id.appendChild(doc.createTextNode(Integer.toString(p.get_id())));
                    loc.appendChild(doc.createTextNode(Integer.toString(i)));
                    pawn.appendChild(color);
                    pawn.appendChild(id);
            		piece_loc.appendChild(pawn);
            		piece_loc.appendChild(loc);
            		homerows.appendChild(piece_loc);
            	}
            }
        }
        
        
        rootElement.appendChild(homerows);
        
        //create home element within board for home spaces
        Element home = doc.createElement("home");
        for(Home h : this.home_spaces){
        	for(Pawn p : h.get_pawns()){
                Element pawn = doc.createElement("pawn");
                Element color = doc.createElement("color");
                Element id = doc.createElement("id");
                color.appendChild(doc.createTextNode(p.get_color()));
                id.appendChild(doc.createTextNode(Integer.toString(p.get_id())));
                pawn.appendChild(color);
                pawn.appendChild(id);
        		home.appendChild(pawn);
        	}
        }
        
        rootElement.appendChild(home);
        
        return doc;
        
        
        
	}
	
	// returns true if all pawns of given color are out of home circle
	public boolean all_pawns_out(String color){
		return (this.get_HomeCircle(color).get_pawns().size()) == 0;
	}
	
	// writes board image to png file
	public void to_PNG(String outfilename) throws Exception{
		// write board to xml file
		PrintWriter out = new PrintWriter(outfilename + ".xml");
		String xml = XMLUtils.XMLtoString(this.BoardtoXML());
		out.print(xml);
		out.close();
		
		// start process to generate image
		String[] command = new String[]{"\"C:\\Program Files\\Racket\\Racket.exe\"", "from_findler/show-board.rkt", "--png", outfilename + ".png ", outfilename + ".xml"};
		
		try {
			Process process = Runtime.getRuntime().exec(command);
		    // prints out any message that are usually displayed in the console
		    Scanner scanner = new Scanner(process.getErrorStream());
		    while (scanner.hasNext()) {
		        System.out.println(scanner.nextLine());
		    }
		}catch(IOException e1) {
		    e1.printStackTrace();
		}
	}
	
	// ------------------------------------------------------------------
    // Examples: 

//    static Board board1;
//    static Space[] spaces1;
//    static Home[] homes1;
//    static HomeCircle[] homecircles1;
//    static HashMap<String, ArrayList<HomeRow>> homerows1;
//    static ArrayList<Player> players1;
//    
//    public static void createExamples() throws Exception {
//		if (board1 == null) {
//			ArrayList<Pawn> empty_pawns = new ArrayList<Pawn>();
//			spaces1 = new Space[34];
//			spaces1[0] = new Entry("green", true, empty_pawns);
//			spaces1[1] = new Space(null, false, empty_pawns);
//			spaces1[2] = new Space(null, false, empty_pawns);
//			spaces1[3] = new Space(null, false, empty_pawns);
//			spaces1[4] = new Space(null, false, empty_pawns);
//			spaces1[5] = new Space(null, false, empty_pawns);
//			spaces1[6] = new Space(null, false, empty_pawns);
//			spaces1[7] = new Space(null, true, empty_pawns);
//			spaces1[8] = new Space(null, false, empty_pawns);
//			spaces1[9] = new Space(null, false, empty_pawns);
//			spaces1[10] = new Space(null, false, empty_pawns);
//			spaces1[11] = new Space(null, false, empty_pawns);
//			spaces1[12] = new PreHomeRow("blue", true, empty_pawns);
//			spaces1[13] = new Space(null, false, empty_pawns);
//			spaces1[14] = new Space(null, false, empty_pawns);
//			spaces1[15] = new Space(null, false, empty_pawns);
//			spaces1[16] = new Space(null, false, empty_pawns);
//			spaces1[17] = new Entry("blue", true, empty_pawns);
//			spaces1[18] = new Space(null, false, empty_pawns);
//			spaces1[19] = new Space(null, false, empty_pawns);
//			spaces1[20] = new Space(null, false, empty_pawns);
//			spaces1[21] = new Space(null, false, empty_pawns);
//			spaces1[22] = new Space(null, false, empty_pawns);
//			spaces1[23] = new Space(null, false, empty_pawns);
//			spaces1[24] = new Space(null, true, empty_pawns);
//			spaces1[25] = new Space(null, false, empty_pawns);
//			spaces1[26] = new Space(null, false, empty_pawns);
//			spaces1[27] = new Space(null, false, empty_pawns);
//			spaces1[28] = new Space(null, false, empty_pawns);
//			spaces1[29] = new PreHomeRow("green", true, empty_pawns);
//			spaces1[30] = new Space(null, false, empty_pawns);
//			spaces1[31] = new Space(null, false, empty_pawns);
//			spaces1[32] = new Space(null, false, empty_pawns);
//			spaces1[33] = new Space(null, false, empty_pawns);
//		    
//			homes1 = new Home[2];
//			homes1[0] = new Home("green", false, empty_pawns);
//			homes1[1] = new Home("blue", false, empty_pawns);
//			
//			ArrayList<Pawn> four_green_pawns = new ArrayList<Pawn>();
//			for(int i = 0; i < 4; i++){
//				four_green_pawns.add(new Pawn(i, "green"));
//			}
//			
//			ArrayList<Pawn> four_blue_pawns = new ArrayList<Pawn>();
//			for(int i = 0; i < 4; i++){
//				four_blue_pawns.add(new Pawn(i, "blue"));
//			}
//			
//			homecircles1 = new HomeCircle[2];
//			homecircles1[0] = new HomeCircle("green", false, four_green_pawns);
//			homecircles1[1] = new HomeCircle("blue", false, four_blue_pawns);
//			
//			ArrayList<HomeRow> homerow1 = new ArrayList<HomeRow>();
//			for(int j = 0; j < 7; j++){
//				homerow1.add(new HomeRow("green", false, empty_pawns));
//			}
//			ArrayList<HomeRow> homerow2 = new ArrayList<HomeRow>();
//			for(int j = 0; j < 7; j++){
//				homerow2.add(new HomeRow("blue", false, empty_pawns));
//			}
//			
//			homerows1 = new HashMap<String, ArrayList<HomeRow>>();
//			homerows1.put("green", new ArrayList<HomeRow>(homerow1));
//			homerows1.put("blue", new ArrayList<HomeRow>(homerow2));
//			
//			players1 = new ArrayList<Player>();
//			Player p = new Player();
//			p.startGame("green");
//			Player q = new Player();
//			q.startGame("blue");
//			players1.add(p);
//			players1.add(q);
//			
//			board1 = new Board(spaces1, homecircles1, homes1, homerows1);
//			
//		}
//    }
//
//    // ------------------------------------------------------------------
//	// Tests: 
//	
//	public static void main(String argv[]) {
//		Board b = new Board(players1, 4);
//		
//		Tester.check(b.equals(board1), "two-person board init");
//	}

	
	
}
