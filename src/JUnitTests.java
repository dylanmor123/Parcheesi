import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
	
}
