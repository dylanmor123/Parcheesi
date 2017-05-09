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
	System.out.println(XMLUtils.XMLtoString(b.BoardtoXML()));
	System.out.println(b.Board("<board><start><pawn><color>yellow</color><id>3</id></pawn><pawn><color>yellow</color><id>1</id></pawn><pawn><color>yellow</color><id>0</id></pawn><pawn><color>red</color><id>2</id></pawn><pawn><color>red</color><id>1</id></pawn><pawn><color>red</color><id>0</id></pawn><pawn><color>blue</color><id>3</id></pawn><pawn><color>blue</color><id>2</id></pawn><pawn><color>blue</color><id>1</id></pawn></start><main><piece-loc><pawn><color>yellow</color><id>2</id></pawn><loc>63</loc></piece-loc><piece-loc><pawn><color>green</color><id>2</id></pawn><loc>47</loc></piece-loc><piece-loc><pawn><color>green</color><id>1</id></pawn><loc>47</loc></piece-loc><piece-loc><pawn><color>green</color><id>3</id></pawn><loc>30</loc></piece-loc><piece-loc><pawn><color>green</color><id>0</id></pawn><loc>28</loc></piece-loc><piece-loc><pawn><color>red</color><id>3</id></pawn><loc>26</loc></piece-loc><piece-loc><pawn><color>blue</color><id>0</id></pawn><loc>17</loc></piece-loc></main><home-rows /><home /></board>"));

	
//	Game.main(argv);
	
	System.out.println("faulty tests: " + faults); 
	System.exit(0);
    }

}
