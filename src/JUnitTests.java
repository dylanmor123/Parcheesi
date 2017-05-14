import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.Arrays;

public class JUnitTests{
	// rules checker tests
	@Test
	public void test_rules_checker() throws Exception{
		// Legal entries
		// 5 in dice
		State legal_entry_state1 = new State("boards/init.txt", 2);
		EnterPiece legal_entry_move1 = new EnterPiece(new Pawn(0, "green"));
		assertTrue(legal_entry_move1.is_Legal(legal_entry_state1, legal_entry_state1));
		
		// 1 and 4 in dice
		State legal_entry_state2 = new State("boards/1-4_enter.txt", 2);
		EnterPiece legal_entry_move2 = new EnterPiece(new Pawn(0, "blue"));
		assertTrue(legal_entry_move2.is_Legal(legal_entry_state2, legal_entry_state2));
		
		// 2 and 3 in dice
		State legal_entry_state3 = new State("boards/2-3_enter.txt", 2);
		EnterPiece legal_entry_move3 = new EnterPiece(new Pawn(0, "blue"));
		assertTrue(legal_entry_move3.is_Legal(legal_entry_state3, legal_entry_state3));
	}
	
	// XML Board Constructor tests
	@Test
	public void test_XML_Board_Constructor() throws Exception{
		State state = new State("boards/Dylansboardtest.txt", 4);
		Board board = state.get_board();
		String xml = XMLUtils.XMLtoString(board.BoardtoXML());
		Board xml_board = new Board(xml);
		assertTrue(board.equals(xml_board));
	}
	
	// XML EnterPiece Constructor tests
	@Test
	public void test_EnterPiece_XML_Constructor() throws Exception{
		Document doc = XMLUtils.StringtoXML("<enter-piece> <pawn> <color> green </color> <id> 0 </id> </pawn> </enter-piece>");
		EnterPiece entry_move = new EnterPiece(doc);
		Document new_doc = entry_move.toXMLDoc();
		EnterPiece new_entry_move = new EnterPiece(new_doc);
		assertTrue(entry_move.equals(new_entry_move));
		
	}
	
	// XML MoveMain Constructor tests
	@Test
	public void test_MoveMain_XML_Constructor() throws Exception{
		Document doc = XMLUtils.StringtoXML("<move-piece-main> <pawn> <color> green </color> <id> 0 </id> </pawn> <start> 5 </start> <distance> 3 </distance> </move-piece-main>");
		MoveMain main_move = new MoveMain(doc);
		Document new_doc = main_move.toXMLDoc();
		MoveMain new_main_move = new MoveMain(new_doc);
		System.out.println(XMLUtils.XMLtoString(doc));
		System.out.println(XMLUtils.XMLtoString(new_doc));
		assertTrue(main_move.equals(new_main_move));
		
	}
	
	// XML MoveHome Constructor tests
	@Test
	public void test_MoveHome_XML_Constructor() throws Exception{
		Document doc = XMLUtils.StringtoXML("<move-piece-home> <pawn> <color> green </color> <id> 0 </id> </pawn> <start> 0 </start> <distance> 2 </distance> </move-piece-home>");
		MoveHome home_move = new MoveHome(doc);
		Document new_doc = home_move.toXMLDoc();
		MoveHome new_home_move = new MoveHome(new_doc);
		System.out.println(XMLUtils.XMLtoString(doc));
		System.out.println(XMLUtils.XMLtoString(new_doc));
		assertTrue(home_move.equals(new_home_move));
		
	}
	
	// NPlayer networking test
	// must run ClientTest in separate process to succeed
//	@Test
//	public void test_NPlayer_Listen() throws Exception{
//		NPlayer nplayer = new NPlayer("green");
//		nplayer.bind(8000);
//		State test_state = new State("boards/12.txt", 2);
//		
//		String name = "johnnie vassar";
//		IMove[] moves = new IMove[3];
//		moves[0] = new MoveHome(new Pawn(0, "green"), 1, 4);
//		moves[1] = new MoveMain(new Pawn(0, "green"), 12, 8);
//		moves[2] = new EnterPiece(new Pawn(0, "green"));
//		
//		assertEquals(name, nplayer.startGame("green"));
//		assertTrue(Arrays.asList(moves).containsAll(Arrays.asList(nplayer.doMove(test_state.get_board(), test_state.get_rolls()))));
//		nplayer.DoublesPenalty();
//	}
	
	
	
}
