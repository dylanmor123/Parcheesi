import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
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

class Player implements IPlayer {
	protected boolean doubles_penalty; //true if third doubles is rolled
	private String color;
	
	// for testing purposes - list of moves in order to make
	private ArrayList<IMove> test_moves = new ArrayList<IMove>();
	
	
	// implementing Player sequence contract
	private boolean has_started = false;
	
	// copy constructor
	public Player(Player p){
		this.doubles_penalty = p.doubles_penalty;
		this.color = p.color;
	}
	
	public Player(){
		this.color = null;
		this.doubles_penalty = false;
	}
	
	public Player(String color, boolean doubles_penalty){
		this.color = color;
		this.doubles_penalty = doubles_penalty;
	}
	
	public String get_color(){
		return color;
	}
	
	public void set_color(String color){
		this.color = color;
	}
	
	public String startGame(String color) throws Exception{
		if(!color.equals("red") && !color.equals("blue") && !color.equals("green") && !color.equals("yellow")){
			throw new Exception("Invalid player color");
		}
		this.color = color;
		this.has_started = true;
		
		return "maurice";
	}
	
	public void DoublesPenalty() throws Exception{
		// check if players has started
		if(!has_started){
			throw new Exception("Player has not started");
		}
		this.doubles_penalty = true;
	}
	
	public void set_moves(ArrayList<IMove> moves){
		this.test_moves =  moves;
	}
	
	public ArrayList<IMove> get_moves(){
		return this.test_moves;
	}
	
	// Assignment 6 - Implementing player strategies
	// "first" if player strategy is to move first pawn, "last" if player strategy is to move last pawn
	private String strategy;
	
	// for tracking the game state at a player level
	private State curr_state;
	private State prev_state;
	
	
	public Player(String strat){
		this.strategy = strat;
		this.doubles_penalty = false;
	}
	// method that returns a list of pawns, in the order that they appear on the board
	// ordered from furthest along to furthest back
	private ArrayList<Pawn> get_pawn_order(Board board){
		// TODO: WRITE METHOD
		ArrayList<Pawn> pawns = new ArrayList<Pawn>();
		
		
		// search in home row
		ArrayList<HomeRow> row = board.get_HomeRow(this.color);
		for(int i = row.size() - 1; i >= 0; i--){
			HomeRow s = row.get(i);
			if(s.get_pawns().size() > 0 && s.get_pawns().get(0).get_color().equals(this.color)){
				pawns.addAll(s.get_pawns());
			}
		}
		
		// search in main ring
		// find entry index
		Entry e = board.get_Entry(this.color);
		Space[] spaces = board.get_Spaces();
		int entry_index = -1;
		for(int k = 0; k < spaces.length; k++){
			if(e.equals(spaces[k])){
				entry_index = k;
				break;
			}
		}
		// k is index of entry in array of spaces
		
		for(int j = (((entry_index - 1) % spaces.length) + spaces.length) % spaces.length; j != entry_index; j = (j - 1) % spaces.length){
			Space s = spaces[j];
			if(s.get_pawns().size() > 0 && s.get_pawns().get(0).get_color().equals(color)){
				pawns.addAll(s.get_pawns());
			}
		}
		
		// add pawns from entry
		if(e.get_pawns().size() > 0 && e.get_pawns().get(0).get_color().equals(color)){
			pawns.addAll(e.get_pawns());
		}
		
		// add pawns from home_circle
		HomeCircle hc = board.get_HomeCircle(color);
		if(hc.get_pawns().size() > 0 && hc.get_pawns().get(0).get_color().equals(color)){
			pawns.addAll(hc.get_pawns());
		}
		
		return pawns;
	}
	
	public IMove[] doMove(Board brd, int[] dice) throws Exception{
		// for testing - return first move in list of moves
		// check if players has started
		if(!has_started){
			throw new Exception("Player has not started");
		}
		
		ArrayList<IMove> generated_moves = new ArrayList<IMove>();
		
		if(this.strategy.equals("first")){
			int[] sorted_dice = new int[dice.length];
			System.arraycopy(dice, 0, sorted_dice, 0, dice.length);
			Arrays.sort(sorted_dice);
			
			this.curr_state = new State(brd, this, sorted_dice);
			this.prev_state = new State(curr_state);
			
			
			
			while(RuleChecker.moves_remaining(this, this.curr_state, this.prev_state)){
				PawnLocation loc = null;
				ArrayList<Pawn> ordered_pawns = this.get_pawn_order(this.curr_state.get_board());
				boolean made_move = false;
				IMove move = null;
				
				for(Pawn to_move: ordered_pawns){
					loc = this.curr_state.get_board().get_Pawn_Location(to_move);
					
					// iterate through dice rolls in descending order
					for(int i = this.curr_state.get_rolls().length - 1; i >= 0; i--){
						int roll = this.curr_state.get_rolls()[i];
						if(loc.get_type().equals("home circle")){
							move = new EnterPiece(to_move);
						}
						else if(loc.get_type().equals("main")){
							move = new MoveMain(to_move, loc.get_index(), roll);
						}
						else if(loc.get_type().equals("home row")){
							move = new MoveHome(to_move, loc.get_index(), roll);
						}
						else{
							break; // pawn is in home; no need to try and move
						}
						
						// see if move is legal; if so, update on player-side and add to list of moves to make
						if(move.is_Legal(this.curr_state, this.prev_state)){
							generated_moves.add(move);	
							this.curr_state = move.update_Board(this.curr_state);
							made_move = true;
							break;
						}
					}
					
					if(made_move){
						made_move = false;
						break;
					}
				}
			}
			
		}
		else if(this.strategy.equals("last")){
			int[] sorted_dice = new int[dice.length];
			System.arraycopy(dice, 0, sorted_dice, 0, dice.length);
			Arrays.sort(sorted_dice);
			
			this.curr_state = new State(brd, this, sorted_dice);
			this.prev_state = new State(curr_state);
			
			while(RuleChecker.moves_remaining(this, this.curr_state, this.prev_state)){
				PawnLocation loc = null;
				ArrayList<Pawn> ordered_pawns = this.get_pawn_order(this.curr_state.get_board());
				boolean made_move = false;
				IMove move = null;
				
				// start from back of list of ordered pawns
				for(int j = ordered_pawns.size() - 1; j >= 0; j--){
					Pawn to_move = ordered_pawns.get(j);
					loc = this.curr_state.get_board().get_Pawn_Location(to_move);
					
					// iterate through dice rolls in descending order
					for(int i = this.curr_state.get_rolls().length - 1; i >= 0; i--){
						int roll = this.curr_state.get_rolls()[i];
						if(loc.get_type().equals("home circle")){
							move = new EnterPiece(to_move);
						}
						else if(loc.get_type().equals("main")){
							move = new MoveMain(to_move, loc.get_index(), roll);
						}
						else if(loc.get_type().equals("home row")){
							move = new MoveHome(to_move, loc.get_index(), roll);
						}
						else{
							break; // pawn is in home; no need to try and move
						}
						
						// see if move is legal; if so, update on player-side and add to list of moves to make
						if(move.is_Legal(this.curr_state, this.prev_state)){
							generated_moves.add(move);	
							this.curr_state = move.update_Board(this.curr_state);
							made_move = true;
							break;
						}
					}
					
					if(made_move){
						made_move = false;
						break;
					}
				}
			}	
		}
		
		
		return (IMove[]) generated_moves.toArray();
		
		
	}
	
	public String MovestoXMLString(ArrayList<IMove> generated_moves) throws ParserConfigurationException, TransformerException{
        DocumentBuilderFactory dbFactory =
        DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = 
           dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        
        //create root Move Element
        Element rootElement = doc.createElement("moves");
        doc.appendChild(rootElement);
        
        //<enter-piece> pawn  </enter-piece>
        //<move-piece-main> pawn  start  distance  </move-piece-main>
        //<move-piece-home> pawn  start  distance  </move-piece-home>
        
        
        //Append all enter, main, and home moves to root "moves"
		if (generated_moves.size() != 0){
			for(IMove generated_move: generated_moves){
				rootElement.appendChild(generated_move.toXMLDoc());
			}
		}

        return XMLUtils.XMLtoString(doc);
        
	}
	
	// Assignment 7
	// Networking
	// Give player a socket and have it listen and return appropriate responses
	private Socket socket;
	private PrintStream output;
	private BufferedReader input;
	
	private String startGame_response(Element request_root) throws Exception{
		String color = request_root.getTextContent();
		
		String name = this.startGame(color);
		Document doc = XMLUtils.newDocument();
		Element root = doc.createElement("name");
		root.appendChild(doc.createTextNode(name));
		doc.appendChild(root);
		
		return XMLUtils.XMLtoString(doc);
	}
	
	private String doMove_response(Element request_root) throws Exception{
		Node board_node = request_root.getFirstChild();
		Node dice_node = request_root.getLastChild();
		
		Document board_doc = XMLUtils.newDocument();
		board_doc.appendChild(board_node);
		Board board = new Board(XMLUtils.XMLtoString(board_doc));
		
		NodeList dice_list = dice_node.getChildNodes();
		int[] dice = new int[dice_list.getLength()];
		for(int i = 0; i < dice.length; i++){
			Node die = dice_list.item(i);
			dice[i] = Integer.parseInt(die.getTextContent());
		}
		
		// get result of doMove
		IMove[] moves = this.doMove(board, dice);
		
		Document response_doc = XMLUtils.newDocument();
		
        Element root = response_doc.createElement("moves");
        response_doc.appendChild(root);
        
        
        //Append all enter, main, and home moves to root "moves"
		if (moves.length != 0){
			for(IMove move: moves){
				root.appendChild(move.toXMLDoc());
			}
		}
		
        return XMLUtils.XMLtoString(response_doc);
		
	}
	
	public void listen(String address, int port) throws Exception{
		// listen to local machine port
		this.socket = new Socket(address, port);
		this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.output = new PrintStream(this.socket.getOutputStream());
		
		// listen for requests through sockets
		String request;
		String response;
		while(true){
			if((request = this.input.readLine()) != null){
				try{
					Document x = XMLUtils.StringtoXML(request);
					Element request_root = x.getDocumentElement();
					String method_name = request_root.getTagName();
					if(method_name.equals("start-game")){
						response = this.startGame_response(request_root);
					}
					else if(method_name.equals("do-move")){
						response = this.doMove_response(request_root);
					}
					else if(method_name.equals("doubles-penalty")){
						Document response_doc = XMLUtils.newDocument();
						response_doc.appendChild(response_doc.createElement("void"));
						response = XMLUtils.XMLtoString(response_doc);
					}
					else{
						throw new Exception();
					}
					
					System.out.println(response);
					output.println(response);
				}
				catch (Exception e){
					throw new Exception("contract violation - expected valid method call");
				}
				
			}
		}
	}
	
	
	//--------------------------------------------------------------
	// Examples for testing
	// Advance vs. Enter 1
	static State s1;
	static IMove last1;
	static IMove first1;
	
	// Advance vs. Enter 2
	static State s2;
	static IMove last2;
	static IMove first2;
	
	// MoveHome vs. MoveMain
	static State s3;
	static IMove last3;
	static IMove first3;
	
	// MoveMain vs. MoveMain
	static State s4;
	static IMove last4;
	static IMove first4;
	
	// MoveMain vs. MoveMain
	static State s5;
	static IMove last5;
	static IMove first5;
	
	// MoveMain vs. MoveMain
	static State s6;
	static IMove last6;
	static IMove first6;
	
	// MoveMain vs. MoveMain
	static State s7;
	static IMove last7;
	static IMove first7;
	
	// MoveMain vs. MoveMain
	static State s8;
	static IMove last8;
	static IMove first8;
	
	// MoveMain vs. MoveMain
	static State s9;
	static IMove last9;
	static IMove first9;
	
	// MoveMain vs. MoveMain
	static State s10;
	static IMove last10;
	static IMove first10;
	
	// MoveMain vs. MoveMain
	static State s11;
	static IMove last11;
	static IMove first11;
	
	// MoveMain vs. MoveMain
	static State s12;
	static IMove last12;
	static IMove first12;
	
	// MoveMain vs. MoveMain
	static State s13;
	static IMove last13;
	static IMove first13;
	
	// MoveMain vs. MoveMain
	static State s14;
	static IMove last14;
	static IMove first14;
	
	// EnterPiece vs. MoveMain
	static State s15;
	static IMove last15;
	static IMove first15;
	
	// MoveMain vs. MoveMain
	static State s16;
	static IMove last16;
	static IMove first16;
	
	
	public static void createExamples() throws Exception{
		int num_players = 2;
		
		s1 = new State("boards/1.txt", num_players);
		last1 = new EnterPiece(new Pawn(3, "green"));
		first1 = new MoveMain(new Pawn(0, "green"), 0, 5);
		
		s2 = new State("boards/35.txt", num_players);
		last2 = new EnterPiece(new Pawn(0, "green"));
		first2 = new MoveMain(new Pawn(2, "green"), 27, 5);
		
		s3 = new State("boards/57.txt", num_players);
		last3 = new MoveMain(new Pawn(1, "green"), 25, 6);
		first3 = new MoveHome(new Pawn(3, "green"), 1, 6);
			
		s4 = new State("boards/2.txt", num_players);
		last4 = new MoveMain(new Pawn(1, "green"), 0, 2);
		first4 = new MoveMain(new Pawn(0, "green"), 0, 2);
		
		s5 = new State("boards/3.txt", num_players);
		last5 = new MoveMain(new Pawn(1, "green"), 0, 2);
		first5 = new MoveMain(new Pawn(0, "green"), 2, 2);
		
		s6 = new State("boards/7.txt", num_players);
		last6 = new MoveMain(new Pawn(2, "green"), 0, 3 );
		first6 = new MoveMain(new Pawn(1, "green"), 4, 4);
		
		s7 = new State("boards/8.txt", num_players);
		last7 = new MoveMain(new Pawn(2, "green"), 0, 4);
		first7 = new MoveMain(new Pawn(0, "green"), 7, 4);
		
		s8 = new State("boards/9.txt", num_players);
		last8 = new MoveMain(new Pawn(2, "green"), 0, 4);
		first8 = new MoveMain(new Pawn(0, "green"), 7, 4);
		
		s9 = new State("boards/11.txt", num_players);
		last9 = new MoveMain(new Pawn(2, "green"), 4, 4);
		first9 = new MoveMain(new Pawn(0, "green"), 10, 4);
		
		s10 = new State("boards/12.txt", num_players);
		last10 = new MoveMain(new Pawn(2, "green"), 4, 4);
		first10 = new MoveMain(new Pawn(0, "green"), 14, 4);
		
		s11 = new State("boards/13.txt", num_players);
		last11 = new MoveMain(new Pawn(2, "green"), 4, 3);
		first11 = new MoveMain(new Pawn(1, "green"), 11, 3);
			
		s12 = new State("boards/25.txt", num_players);
		last12 = new MoveMain(new Pawn(3, "green"), 0, 20);
		first12 = new MoveMain(new Pawn(1, "green"), 11, 20);
		
		s13 = new State("boards/26.txt", num_players);
		last13 = new MoveMain(new Pawn(2, "green"), 7, 6);
		first13 = new MoveMain(new Pawn(3, "green"), 20, 6);
		
		s14 = new State("boards/27.txt", num_players);
		last14 = new MoveMain(new Pawn(2, "green"), 7, 20);
		first14 = new MoveMain(new Pawn(1, "green"), 11, 20);
		
		s15 = new State("boards/35.txt", num_players);
		last15 = new EnterPiece(new Pawn(0, "green"));
		first15 = new MoveMain(new Pawn(2, "green"), 27, 5);
		
		s16 = new State("boards/39.txt", num_players);
		last16 = new MoveMain(new Pawn(0, "green"), 6, 5);
		first16 = new MoveMain(new Pawn(2, "green"), 27, 5);
	}
	
	public static void main(String[] argv) throws Exception{
		Player p_first = new Player("first");
		Player p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first1.equals(p_first.doMove(s1.get_board(), s1.get_rolls())), "first - test 1");
		Tester.check(last1.equals(p_last.doMove(s1.get_board(), s1.get_rolls())), "last - test 1");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first2.equals(p_first.doMove(s2.get_board(), s2.get_rolls())), "first - test 2");
		Tester.check(last2.equals(p_last.doMove(s2.get_board(), s2.get_rolls())), "last - test 2");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first3.equals(p_first.doMove(s3.get_board(), s3.get_rolls())), "first - test 3");
		Tester.check(last3.equals(p_last.doMove(s3.get_board(), s3.get_rolls())), "last - test 3");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first4.equals(p_first.doMove(s4.get_board(), s4.get_rolls())), "first - test 4");
		Tester.check(last4.equals(p_last.doMove(s4.get_board(), s4.get_rolls())), "last - test 4");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first5.equals(p_first.doMove(s5.get_board(), s5.get_rolls())), "first - test 5");
		Tester.check(last5.equals(p_last.doMove(s5.get_board(), s5.get_rolls())), "last - test 5");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first6.equals(p_first.doMove(s6.get_board(), s6.get_rolls())), "first - test 6");
		Tester.check(last6.equals(p_last.doMove(s6.get_board(), s6.get_rolls())), "last - test 6");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first7.equals(p_first.doMove(s7.get_board(), s7.get_rolls())), "first - test 7");
		Tester.check(last7.equals(p_last.doMove(s7.get_board(), s7.get_rolls())), "last - test 7");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first8.equals(p_first.doMove(s8.get_board(), s8.get_rolls())), "first - test 8");
		Tester.check(last8.equals(p_last.doMove(s8.get_board(), s8.get_rolls())), "last - test 8");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first9.equals(p_first.doMove(s9.get_board(), s9.get_rolls())), "first - test 9");
		Tester.check(last9.equals(p_last.doMove(s9.get_board(), s9.get_rolls())), "last - test 9");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first10.equals(p_first.doMove(s10.get_board(), s10.get_rolls())), "first - test 10");
		Tester.check(last10.equals(p_last.doMove(s10.get_board(), s10.get_rolls())), "last - test 10");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first11.equals(p_first.doMove(s11.get_board(), s11.get_rolls())), "first - test 11");
		Tester.check(last11.equals(p_last.doMove(s11.get_board(), s11.get_rolls())), "last - test 11");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first12.equals(p_first.doMove(s12.get_board(), s12.get_rolls())), "first - test 12");
		Tester.check(last12.equals(p_last.doMove(s12.get_board(), s12.get_rolls())), "last - test 12");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first13.equals(p_first.doMove(s13.get_board(), s13.get_rolls())), "first - test 13");
		Tester.check(last13.equals(p_last.doMove(s13.get_board(), s13.get_rolls())), "last - test 13");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first14.equals(p_first.doMove(s14.get_board(), s14.get_rolls())), "first - test 14");
		Tester.check(last14.equals(p_last.doMove(s14.get_board(), s14.get_rolls())), "last - test 14");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first15.equals(p_first.doMove(s15.get_board(), s15.get_rolls())), "first - test 15");
		Tester.check(last15.equals(p_last.doMove(s15.get_board(), s15.get_rolls())), "last - test 15");
		
		p_first = new Player("first");
		p_last = new Player("last");
		
		p_first.startGame("green");
		p_last.startGame("green");
		
		Tester.check(first16.equals(p_first.doMove(s16.get_board(), s16.get_rolls())), "first - test 16");
		Tester.check(last16.equals(p_last.doMove(s16.get_board(), s16.get_rolls())), "last - test 16");
		
	}

}
