import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PlayerWrapper {
	private IPlayer player;
	
	public PlayerWrapper(IPlayer p){
		this.player = p;
	}
	
	// Assignment 7
	// Networking
	// Give player a socket and have it listen and return appropriate responses
	private Socket socket;
	private PrintStream output;
	private BufferedReader input;
	
	private String startGame_response(Element request_root) throws Exception{
		String color = request_root.getTextContent();
		
		String name = this.player.startGame(color);
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
		board_doc.appendChild(board_doc.importNode(board_node, true));
		Board board = new Board(XMLUtils.XMLtoString(board_doc));
		
		NodeList dice_list = dice_node.getChildNodes();
		int[] dice = new int[dice_list.getLength()];
		for(int i = 0; i < dice.length; i++){
			Node die = dice_list.item(i);
			dice[i] = Integer.parseInt(die.getTextContent());
		}
		
		// get result of doMove
		IMove[] moves = this.player.doMove(board, dice);
		
		Document response_doc = XMLUtils.newDocument();
		
        Element root = response_doc.createElement("moves");
        response_doc.appendChild(root);
        
        
        //Append all enter, main, and home moves to root "moves"
		if (moves.length != 0){
			for(IMove move: moves){
				root.appendChild(response_doc.importNode(move.toXMLDoc().getDocumentElement(), true));
			}
		}
		
        return XMLUtils.XMLtoString(response_doc);
		
	}
	
	private Socket wait_for_connection(String address, int port, int timeoutSecs) throws Exception{
		int num_tries = 0;
		Socket to_return = null;
		while(num_tries < timeoutSecs && to_return == null){
			try{
				to_return = new Socket(address, port);
			}
			catch(Exception e){
				TimeUnit.SECONDS.sleep(1);
				to_return = null;
				continue;
			}			
		}
		
		if(to_return == null){
			throw new ConnectException();
		}
		else{
			return to_return;
		}
	}
	
	public void listen(String address, int port) throws Exception{
		// listen to local machine port
		this.socket = wait_for_connection(address, port, 120);
		this.input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.output = new PrintStream(this.socket.getOutputStream());
		
		// listen for requests through sockets
		String request;
		String response;
		while(true){
			if((request = this.input.readLine()) != null){
				// wait a little while, to see what's going on
				TimeUnit.SECONDS.sleep(1);
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
						this.player.DoublesPenalty();
						Document response_doc = XMLUtils.newDocument();
						response_doc.appendChild(response_doc.createElement("void"));
						response = XMLUtils.XMLtoString(response_doc);
					}
					else{
						throw new Exception();
					}
					
					output.println(response);
				}
				catch (Exception e){
					e.printStackTrace();
					throw new Exception("contract violation - expected valid method call");
				}
				
			}
		}
	}
}
