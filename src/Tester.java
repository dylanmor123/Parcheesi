class Tester {

    static int faults = 0; 

    static void check(boolean t, String s) {
	if (!t) {
	    faults += 1; 
	    System.out.println("*** test failed: " + s + " *** "); 
	}
    }

    public static void main(String argv[]) {
	System.out.println("setting up examples ...");
	Pawn.createExamples();
	Space.createExamples();
	Board.createExamples();

	System.out.println("testing ...");
	Pawn.main(argv);
	Space.main(argv);
	Board.main(argv);

	System.out.println("faulty tests: " + faults); 
	System.exit(0);
    }

}
