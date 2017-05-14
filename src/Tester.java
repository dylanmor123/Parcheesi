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

}
