import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NPlayer implements IPlayer{
	private String color;
	private String name;
	
	private Socket socket;
	private PrintStream output;
	private BufferedReader input;
	
	public NPlayer(int port) throws IOException{
		this.bind(port);
	}
	
	public NPlayer(String color) throws IOException{
		this.color = color;
	}
	
	public NPlayer(NPlayer p){
		this.name = p.name;
		this.color = p.color;
	}
	
	public Player toPlayer(){
		return new Player(color, false);
	}
	
	public void bind(int port) throws IOException{
		this.socket = (new ServerSocket(port)).accept();
		this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.output = new PrintStream(this.socket.getOutputStream());
	}
	
	public String get_color(){
		return this.color;
	}
	
	private String write_startGame_XML(String color) throws Exception{
		// construct start-game XML request
        Document start_doc = XMLUtils.newDocument();
        
        Element root = start_doc.createElement("start-game");
		root.appendChild(start_doc.createTextNode(color));
		start_doc.appendChild(root);
		
		return XMLUtils.XMLtoString(start_doc);
	}
	
	public String startGame(String color) throws Exception{		
		String request = write_startGame_XML(color);
		System.out.println(request);
		
		// send to client
		output.println(request);
		
		// parse response from client player
		String result;
		String name;
		while(true){
			if((result = this.input.readLine()) != null){
				// if there's something to read, check if it's a valid response
				try{
					Document x = XMLUtils.StringtoXML(result);
					Element response_root = x.getDocumentElement();
					if(!response_root.getTagName().equals("name") || response_root.getTextContent() == null){
						throw new Exception();
					}
					
					name = response_root.getTextContent();
					break;
				}
				catch (Exception e){
					System.out.println(e);
					throw new Exception("sequence contract violation - expected valid name response");
				}
				
			}
		}
		this.name = name;
		this.color = color;
		return name;
	}
	
	private String write_doMove_XML(Board board, int[] dice) throws Exception{
		// construct do-move XML request
        Document doMove_doc = XMLUtils.newDocument();
        
        Node root = doMove_doc.createElement("do-move");
		doMove_doc.appendChild(root);
		
		Node b_node = doMove_doc.importNode(board.BoardtoXML().getDocumentElement(), true);
		Node d_node = doMove_doc.createElement("dice");
		for(int d : dice){
			Element die = doMove_doc.createElement("die");
			die.appendChild(doMove_doc.createTextNode(Integer.toString(d)));
			d_node.appendChild(die);
		}
		
		root.appendChild(b_node);
		root.appendChild(d_node);
		
		return XMLUtils.XMLtoString(doMove_doc);
	}
	
	public IMove[] doMove(Board board, int[] dice) throws Exception{
		String request = this.write_doMove_XML(board, dice);
		System.out.println(request);
		
		// send to client
		output.println(request);
		
		// parse response from client player
		String result;
		ArrayList<IMove> out_moves = new ArrayList<IMove>();
		while(true){
			if((result = this.input.readLine()) != null){
				// if there's something to read, check if it's a valid response
				try{
					Document x = XMLUtils.StringtoXML(result);
					Element response_root = x.getDocumentElement();
					if(!response_root.getTagName().equals("moves")){
						throw new Exception();
					}
					
					//parse moves
					NodeList moves = response_root.getChildNodes();
					for(int i = 0; i < moves.getLength(); i++){
						//make new doc for conversion purposes
						Document move_doc = XMLUtils.newDocument();
						Node move = moves.item(i);
						move = move_doc.importNode(move, true);
						move_doc.appendChild(move);
						
						if(move.getNodeName().equals("enter-piece")){
							out_moves.add(new EnterPiece(move_doc));
						}
						else if(move.getNodeName().equals("move-piece-main")){
							out_moves.add(new MoveMain(move_doc));
						}
						else if(move.getNodeName().equals("move-piece-home")){
							out_moves.add(new MoveHome(move_doc));
						}
						else{
							// if the move isn't one of those valid types, response is invalid
							throw new Exception();
						}
						
					}
					
					break;
				}
				catch (Exception e){
					System.out.println(e);
					throw new Exception("sequence contract violation - expected valid moves response");
				}
				
			}
		}
		
		IMove[] results = new IMove[out_moves.size()];
		out_moves.toArray(results);
		
		return results;	
	}
	
	private String write_DoublesPenalty_XML() throws TransformerException, ParserConfigurationException{
		// construct doubles-penalty XML request
        Document doubles_doc = XMLUtils.newDocument();
        
        Element root = doubles_doc.createElement("doubles-penalty");
		doubles_doc.appendChild(root);
		
		return XMLUtils.XMLtoString(doubles_doc);
	}
	
	public void DoublesPenalty() throws Exception{
		String request = this.write_DoublesPenalty_XML();
		System.out.println(request);
		
		// parse response from client player
		String result;
		while(true){
			if((result = this.input.readLine()) != null){
				// if there's something to read, check if it's a valid response
				try{
					Document x = XMLUtils.StringtoXML(result);
					Element response_root = x.getDocumentElement();
					if(!response_root.getTagName().equals("void")){
						throw new Exception();
					}
					break;
				}
				catch (Exception e){
					System.out.println(e);
					throw new Exception("sequence contract violation - expected valid void response");
				}
				
			}
		}
	}
	

}
