import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.Socket;

public class ClientTest {
	public static void main(String[] argv) throws Exception{
		String local_ip = Inet4Address.getLocalHost().getHostAddress();
		Socket socket = new Socket(local_ip, 8000);
		PrintStream output = new PrintStream(socket.getOutputStream());
		
		String name = "johnnie vassar";
		String name_xml = "<name> johnnie vassar </name>";
		output.println(name_xml);
		
		IMove[] moves = new IMove[3];
		moves[0] = new MoveHome(new Pawn(0, "green"), 1, 4);
		moves[1] = new MoveMain(new Pawn(0, "green"), 12, 8);
		moves[2] = new EnterPiece(new Pawn(0, "green"));
		
		String moves_xml = "<moves> <move-piece-home> <pawn> <color> green </color> <id> 0 </id> </pawn> <start> 1 </start> <distance> 4 </distance> </move-piece-home> <move-piece-main> <pawn> <color> green </color> <id> 0 </id> </pawn> <start> 12 </start> <distance> 8 </distance> </move-piece-main> <enter-piece> <pawn> <color> green </color> <id> 0 </id> </pawn> </enter-piece> </moves>";
		
		output.println(moves_xml);
		
		String doubles_xml = "<void> </void>";
		output.println(doubles_xml);
		
		System.out.println("Hit enter to terminate");
		System.console().readLine();
	}
}
