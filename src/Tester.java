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
	Game.createExamples();

	System.out.println("testing ...");
	Pawn.main(argv);
	Space.main(argv);
	Board.main(argv);
	System.out.println("testing with simulated game ...");
	Game.main(argv);
	
	System.out.println("faulty tests: " + faults); 
	System.exit(0);
    }

}
