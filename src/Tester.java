import static org.junit.Assert.assertEquals;

import java.io.IOException;

class Tester {

    static int faults = 0; 

    static void check(boolean t, String s) {
	if (!t) {
	    faults += 1; 
	    System.out.println("*** test failed: " + s + " *** "); 
	}
    }

    public static void main(String argv[]) throws Exception {
    	
	System.out.println("setting up examples ...");
	Pawn.createExamples();
	Space.createExamples();
	Board.createExamples();
	Player.createExamples();
	Game.createExamples();

	System.out.println("testing ...");
	Pawn.main(argv);
	Space.main(argv);
	Board.main(argv);
	Player.main(argv);
	System.out.println("testing with simulated game ...");
	
	State s = new State("boards/12.txt", 2);
	Board b = s.get_board();
	String xml = XMLUtils.XMLtoString(b.BoardtoXML());
	System.out.println(xml);
	Board xml_board = new Board(xml);
	System.out.println(XMLUtils.XMLtoString(xml_board.BoardtoXML()));
	

	
	Game.main(argv);
	
	System.out.println("faulty tests: " + faults); 
	System.exit(0);
    }

}
