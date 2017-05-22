import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
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
		
		
		
		// testing MoveMain rules
		// Case: entry into home row from adjoining space with nearby blockade
		State pre_homerow_nearby_blockade = new State("boards/main_adjacent_home_row_blockade_nearby.txt", 2);
		MoveMain legal_main_move1 = new MoveMain(new Pawn(0, "green"), 29, 2);
		assertTrue(legal_main_move1.is_Legal(pre_homerow_nearby_blockade, pre_homerow_nearby_blockade));
		
		
		// testing MoveHome rules
		// Case: some weird issue with ruling a home row move a legal move after a move to home
		Board weird_brd = new Board("<board><start></start><main><piece-loc><pawn><color>green</color>0</pawn><loc>8</loc></piece-loc><piece-loc><pawn><color>yellow</color>0</pawn><loc>18</loc></piece-loc></main><home-rows><piece-loc><pawn><color>green</color>1</pawn><loc>4</loc></piece-loc></home-rows><home><pawn><color>green</color>2</pawn><pawn><color>green</color>3</pawn></home></board>");
		int[] weird_dice = new int[]{3, 1};
		MoveHome weird_move1 = new MoveHome(new Pawn(1, "green"), 4, 3);
		MoveMain weird_move2 = new MoveMain(new Pawn(0, "green"), 8, 10);
		MoveMain weird_move3 = new MoveMain(new Pawn(0, "green"), 18, 20);
		MoveHome weird_move4 = new MoveHome(new Pawn(1, "green"), 5, 1);
		Player weird_player = new Player("green", false);
		State weird_state_start = new State(weird_brd, weird_player, weird_dice);
		State weird_state = weird_move1.update_Board(weird_state_start);
		weird_state = weird_move2.update_Board(weird_state);
		weird_state = weird_move3.update_Board(weird_state);
		assertTrue(!weird_move4.is_Legal(weird_state, weird_state_start));
	}
	
	// board updater tests
	@Test
	public void test_board_updater() throws Exception{
		// enter piece bop should give bonus 20 roll
		State pre_enter_bop = new State("boards/two_die_enter_bop.txt", 2);
		EnterPiece move1 = new EnterPiece(new Pawn(0, "green"));
		State post_enter_bop_real = new State("boards/two_die_enter_bop_post.txt", 2);
		State post_enter_bop = move1.update_Board(pre_enter_bop);
		assertTrue(post_enter_bop_real.equals(post_enter_bop));
		
	}
	
	// remove furthest pawn tests
	@Test
	public void test_remove_furthest_pawn(){
		
	}
	
	// moves remaining tests
	@Test
	public void test_moves_remaining() throws Exception{
		// bonus move after bop
		State prev_state1 = new State("boards/moves_remaining_test_1.txt", 2);
		MoveMain move1 = new MoveMain(new Pawn(0, "blue"), 24, 3);
		State curr_state1 = move1.update_Board(prev_state1);
		IPlayer player1 = new Player("blue", false);
		assertTrue(RuleChecker.moves_remaining(player1, curr_state1, prev_state1));
		
	}
	
	// moves list generation tests
	@Test
	public void test_moves_list() throws Exception{
		// tests incomplete
		State test_state1 = new State("boards/main_adjacent_home_row_blockade_nearby.txt", 2);
		IPlayer player1 = new Player("green", false);
		ArrayList<ArrayList<IMove>> lists_of_moves1 = RuleChecker.get_move_lists(player1, test_state1, test_state1);
		assertTrue(true);
		
		State test_state2 = new State("boards/8.txt", 2);
		ArrayList<ArrayList<IMove>> lists_of_moves2 = RuleChecker.get_move_lists(player1, test_state2, test_state2);
		assertTrue(true);
		
		// weird issue with moving in home row not being picked up
		Board weird_brd = new Board("<board><start></start><main><piece-loc><pawn><color>green</color>0</pawn><loc>8</loc></piece-loc><piece-loc><pawn><color>yellow</color>0</pawn><loc>18</loc></piece-loc></main><home-rows><piece-loc><pawn><color>green</color>1</pawn><loc>4</loc></piece-loc></home-rows><home><pawn><color>green</color>2</pawn><pawn><color>green</color>3</pawn></home></board>");
		int[] weird_dice = new int[]{3, 1};
		Player weird_player = new Player("green", false);
		State weird_state_start = new State(weird_brd, weird_player, weird_dice);
		ArrayList<ArrayList<IMove>> lists_of_moves_weird = RuleChecker.get_move_lists(weird_player, weird_state_start, weird_state_start);
		assertTrue(true);
	}
	
	// player move selection tests
	@Test
	public void test_best_player(){
		
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
		Document doc = XMLUtils.StringtoXML("<enter-piece><pawn><color>green</color><id>0</id></pawn></enter-piece>");
		EnterPiece entry_move = new EnterPiece(doc);
		Document new_doc = entry_move.toXMLDoc();
		EnterPiece new_entry_move = new EnterPiece(new_doc);
		assertTrue(entry_move.equals(new_entry_move));
		
	}
	
	// XML MoveMain Constructor tests
	@Test
	public void test_MoveMain_XML_Constructor() throws Exception{
		Document doc = XMLUtils.StringtoXML("<move-piece-main><pawn><color>green</color><id>0</id></pawn><start>5</start><distance>3</distance></move-piece-main>");
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
		Document doc = XMLUtils.StringtoXML("<move-piece-home><pawn><color>green</color><id>0</id></pawn><start>0</start><distance>2</distance></move-piece-home>");
		MoveHome home_move = new MoveHome(doc);
		Document new_doc = home_move.toXMLDoc();
		MoveHome new_home_move = new MoveHome(new_doc);
		System.out.println(XMLUtils.XMLtoString(doc));
		System.out.println(XMLUtils.XMLtoString(new_doc));
		assertTrue(home_move.equals(new_home_move));
		
	}
}
