import java.io.FileNotFoundException;
import java.io.IOException;

public class GameStarter {
	public static void main(String[] argv) throws Exception{
		Game g = new Game();
		for(int i = 0; i < 4; i++){
			int port = 8000 + i;
			System.out.println("Waiting on port " + port);
			g.register(new NPlayer(port));
		}
		
		g.start();
	}
}
