import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Game implements IGame {
	protected ArrayList<Player> players = new ArrayList<Player>();
	protected int num_pawns = 4; //number of pawns per player
	protected Board b; //board for the game
	protected int[] dice_roll;

	public void register(IPlayer p) {
		Player player = (Player) p;
		this.players.add(player);
		String color = "green";
		p.startGame(color);
	}
	
	public void set_Board(Board board){
		this.b = board;
	}
	
	public boolean is_Legal(IMove move){
	}
	

	public int update_Board(IMove move){
		if (move == null){
			  return 0;
		  }
		
		if (EnterPiece.class.isAssignableFrom(move.getClass())) {
			EnterPiece m = (EnterPiece) move;
			Pawn p = m.get_pawn();
			String pawn_color = p.get_color();		   
			HomeCircle h = this.b.get_HomeCircle(pawn_color);
			for (Space s : b.get_Spaces()) {
				  if (s.get_color() == null){
					  continue;
				  }
				  else if (s.get_color().equals(pawn_color) && Entry.class.isAssignableFrom(s.getClass())){
					  s.add_Pawn(p);
					  h.remove_Pawn(p);
					  break;
				  }
			  }
		  }
		
		else if(MoveMain.class.isAssignableFrom(move.getClass())){
			
		}
		return 0;
	}
	
	public void start() {
		this.b = new Board(this.players, num_pawns);
	}
	
	// Examples
	static Game g1;
	static Board b1;
	static ArrayList<Player> players1;
	static EnterPiece move1;
	
	static Board b2;
	static Space[] s2;
	static HomeCircle[] hc2;
	static Home[] h2;
	static HashMap<String, ArrayList<HomeRow>> hr2;
	
	public static void createExamples(){
		if(g1 == null){
			
			Player player = new Player();
			player.startGame("green");
			
			g1 = new Game();
			g1.register(player);
			players1 = new ArrayList<Player>();
			players1.add(player);
			b1 = new Board(players1, 4);
			g1.set_Board(b1);
			
			move1 = new EnterPiece(new Pawn(0, "green"));
			
			// board after entry move
			ArrayList<Pawn> empty_pawns = new ArrayList<Pawn>();
			ArrayList<Pawn> one_pawn = new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green")));
			s2 = new Space[17];
			s2[0] = new Entry("green", true, one_pawn);
			s2[1] = new Space(null, false, empty_pawns);
			s2[2] = new Space(null, false, empty_pawns);
			s2[3] = new Space(null, false, empty_pawns);
			s2[4] = new Space(null, false, empty_pawns);
			s2[5] = new Space(null, false, empty_pawns);
			s2[6] = new Space(null, false, empty_pawns);
			s2[7] = new Space(null, true, empty_pawns);
			s2[8] = new Space(null, false, empty_pawns);
			s2[9] = new Space(null, false, empty_pawns);
			s2[10] = new Space(null, false, empty_pawns);
			s2[11] = new Space(null, false, empty_pawns);
			s2[12] = new PreHomeRow("green", true, empty_pawns);
			s2[13] = new Space(null, false, empty_pawns);
			s2[14] = new Space(null, false, empty_pawns);
			s2[15] = new Space(null, false, empty_pawns);
			s2[16] = new Space(null, false, empty_pawns);
		    
			h2 = new Home[1];
			h2[0] = new Home("green", false, empty_pawns);
			
			ArrayList<Pawn> three_pawns = new ArrayList<Pawn>();
			for(int i = 1; i < 4; i++){
				three_pawns.add(new Pawn(i, "green"));
			}
			
			hc2 = new HomeCircle[1];
			hc2[0] = new HomeCircle("green", false, three_pawns);
			
			ArrayList<HomeRow> hr = new ArrayList<HomeRow>();
			for(int j = 0; j < 7; j++){
				hr.add(new HomeRow("green", false, empty_pawns));
			}
			
			hr2 = new HashMap<String, ArrayList<HomeRow>>();
			hr2.put("green", new ArrayList<HomeRow>(hr));
			
			b2 = new Board(s2, hc2, h2, hr2);
		}
		
	}
	
	public static void main(String[] argv){
		g1.update_Board(move1);
		Tester.check(g1.b.equals(b2), "entry move test");
		
	}
	
}


