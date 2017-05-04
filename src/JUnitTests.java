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
		assertTrue(RuleChecker.is_Legal(legal_entry_move1, legal_entry_state1, legal_entry_state1));
	}
	
	
}
