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
		
		PlayerWrapper wrap;
		
		if(strat.equals("human")){
			HPlayer p = new HPlayer(name);
			wrap = new PlayerWrapper(p);
		}
		else if(strat.equals("heuristic")){
			Player p = new Player(strat, name);
			p.set_Heuristic(new RandomHeuristic());
			wrap = new PlayerWrapper(p);
		}
		else{
			Player p = new Player(strat, name);
			wrap = new PlayerWrapper(p);
		}
		
		wrap.listen(address, port);
	}
}
