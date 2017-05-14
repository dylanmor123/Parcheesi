import java.net.Inet4Address;

// first argument: port number
// second argument: player name
// third argument: string denoting player strategy

public class PlayerStarter {
	public static void main(String[] argv) throws Exception{
		int port = Integer.parseInt(argv[0]);
		String address = Inet4Address.getLocalHost().getHostAddress();
		String name = argv[1];
		String strat = argv[2];
		
		Player p = new Player(strat, name);
		PlayerWrapper wrap = new PlayerWrapper(p);
		
		wrap.listen(address, port);
	}
}
