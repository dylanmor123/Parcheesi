import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Game implements IGame {
	protected ArrayList<NPlayer> players = new ArrayList<NPlayer>();
	protected int num_pawns = 4; //number of pawns per player
	protected State game_state;
	protected State prev_state; // for comparison between states for a moving blockade
	
	// implementing Game sequence contract
	private boolean has_registered = false; 
	
	public Game(){
	}

	public void register(IPlayer p) throws Exception{
		NPlayer player = (NPlayer) p;
		this.players.add(player);
		String color = "green";
		if(players.size() == 2){
			color = "red";
		}
		else if(players.size() == 3){
			color = "blue";
		}
		else if(players.size() == 4){
			color = "yellow";
		}
		
		p.startGame(color);
		this.has_registered = true;
	}
	
	public void set_state(State s){
		this.game_state = s;
	}
	
	
	
	private void remove_Player(NPlayer player){
		String color = player.get_color();
		Board curr_board = game_state.get_board();
		ArrayList<HomeRow> homerow = curr_board.get_HomeRow(color);
		HomeCircle homecircle = curr_board.get_HomeCircle(color);
		Home home = curr_board.get_Home(color);
		Space[] spaces = curr_board.get_Spaces();
		
		for(Space s : spaces){
			ArrayList<Pawn> pawns = s.get_pawns();
			if(pawns.get(0).get_color().equals(color)){
				ArrayList<Pawn> pawns_copy = new ArrayList<Pawn>(pawns);
				for(Pawn p : pawns_copy){
					s.remove_Pawn(p);
				}
			}
		}
		
		ArrayList<Pawn> homecircle_pawns = homecircle.get_pawns();
		if(homecircle_pawns.get(0).get_color().equals(color)){
			ArrayList<Pawn> homecircle_pawns_copy = new ArrayList<Pawn>(homecircle_pawns);
			for(Pawn p : homecircle_pawns_copy){
				homecircle.remove_Pawn(p);
			}
		}
		
		ArrayList<Pawn> home_pawns = home.get_pawns();
		if(home_pawns.get(0).get_color().equals(color)){
			ArrayList<Pawn> home_pawns_copy = new ArrayList<Pawn>(home_pawns);
			for(Pawn p : home_pawns_copy){
				home.remove_Pawn(p);
			}
		}
		
		
		for (HomeRow h : homerow){
			ArrayList<Pawn> pawns = h.get_pawns();
			if(pawns.get(0).get_color().equals(color)){
				ArrayList<Pawn> pawns_copy = new ArrayList<Pawn>(pawns);
				for(Pawn p : pawns_copy){
					h.remove_Pawn(p);
				}
			}
		}
		
		this.players.remove(player);
		
		
	}
	
	private int roll_die(){
		return 1 + (int)(Math.random() * 6);
	}
	
	// returns true if somebody has won game; false otherwise
	private boolean game_over(){
		for(NPlayer p : this.players){
			String color = p.get_color();
			Home h = this.game_state.get_board().get_Home(color);
			
			if(h.get_pawns().size() == this.num_pawns){
				return true;
			}
		}
		return false;
	}
	
	public void start() throws Exception{
		// Assignment 8 GUI
		AdminFrame frame = new AdminFrame();
		
		
		
		int num_moves = 0;
		
		// check if players are registered
		if(!has_registered){
			throw new Exception("No players registered");
		}
		
		System.out.println("Starting game");
		frame.setVisible(true);
		
		
		// initialize state
		Board new_board = new Board(this.players, this.num_pawns);
		NPlayer curr_player = this.players.get(0);
		int[] rolls = new int[0];
		this.game_state = new State(new_board, curr_player.toPlayer(), rolls);
		this.prev_state = new State(this.game_state);
		frame.update_pawn_positions(game_state.get_board());
		
		// loop through players until winner
		// generate dice rolls
		// ask player for move
		// if move is illegal, remove cheating player from game
		// double penalty thing
		boolean doubles = false; //true if doubles were rolled
		int num_doubles = 0; //incremented if doubles were rolled; player penalized when num = 3;
		boolean cheated = false; //true if player cheated on turn
		int player_index = 0;
		int num_players = this.players.size();
		
		while(!game_over()){
			// add two rolls
			curr_player = this.players.get(player_index);
			frame.set_status_label(curr_player.get_color() + " player moving");
			int roll1, roll2;
			try {
				roll1 = roll_die();
				roll2 = roll_die();
			} catch (Exception e){
				System.out.println("Error");
				return;
			}
			game_state.add_roll(roll1);
			game_state.add_roll(roll2);
			System.out.println("Rolls: " + roll1 + ", " + roll2);
			
			if (roll1 == roll2){
				System.out.println("Doubles rolled");
				int roll1c = 7 - roll1;
				int roll2c = 7 - roll2;
				game_state.add_roll(roll1c);
				game_state.add_roll(roll2c);
				doubles = true;
				num_doubles++;
				
				if(num_doubles == 3){
					//penalize the current player
					System.out.println("doubles penalty");
					Pawn removed = this.game_state.get_board().remove_furthest(curr_player.get_color());
					
					if(removed != null){
						HomeCircle hc = this.game_state.get_board().get_HomeCircle(curr_player.get_color());
						hc.add_Pawn(removed);
					}
					
					player_index = (player_index + 1) % num_players;
					this.game_state.set_curr_player(this.players.get(player_index));
					int[] rolls_left = new int[0];
					this.game_state.set_rolls(rolls_left);
					num_doubles = 0;
					doubles = false;
					continue;
				}
			}
			
			if(RuleChecker.moves_remaining(curr_player, this.game_state, this.prev_state) && !game_over()){
				IMove[] moves = curr_player.doMove(game_state.get_board(), game_state.get_rolls());
				
				for(IMove move : moves){
					if(!move.is_Legal(this.game_state, this.prev_state)){	
						num_players = this.players.size();
						this.remove_Player(curr_player); // remove cheating player
						cheated = true;
						break;
					}
					this.game_state = move.update_Board(this.game_state);
					
					frame.update_pawn_positions(game_state.get_board());
					
					if(game_over()){
						break;
					}
					
					num_moves++;
					
				}
				
				if(game_over()){
					break;
				}
			}
			
			// update player index, reset flags
			if(cheated){
				cheated = false;
				doubles = false;
				num_doubles = 0;
				if(player_index == num_players){
					player_index = 0;
				}
				else{
					player_index = (player_index + 1) % num_players;
				}
			}
			else if (doubles){
				doubles = false;
			}
			else{
				player_index = (player_index + 1) % num_players;
				num_doubles = 0;
			}
				
			this.game_state.set_curr_player(this.players.get(player_index));
			int[] rolls_left = new int[0];
			this.game_state.set_rolls(rolls_left);
			this.prev_state = new State(this.game_state);
			
		}
		
		// Broken out of loop -> current player wins
		System.out.println(curr_player.get_color() + " player wins!");
		frame.set_status_label(curr_player.get_color() + " player wins!");
		
	}
	
	// Examples
	static Game g;
	static Game g2;
	static Game g3;
	
	static State sinit;
	
	static ArrayList<State> states = new ArrayList<State>();
	
	public static void createExamples() throws Exception{
		g = new Game();
		g2 = new Game();
		g3 = new Game();
		
		Player green = new Player();
		g.register(green);
		green.startGame("green");
		ArrayList<IMove> green_moves = new ArrayList<IMove>();
		green_moves.add(new EnterPiece(new Pawn(0, "green")));
		green_moves.add(new EnterPiece(new Pawn(1, "green")));
		green_moves.add(new EnterPiece(new Pawn(2, "green")));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 0, 2));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 16, 2));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 2, 2));
		green_moves.add(new EnterPiece(new Pawn(2, "green")));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 0, 4));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 0, 4));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 4, 3));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 0, 4));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 4, 3));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 0, 4));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 4, 3));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 7, 3));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 10, 4));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 7, 4));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 11, 3));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 4, 3));
		green_moves.add(new EnterPiece(new Pawn(3, "green")));
		green_moves.add(new MoveMain(new Pawn(3, "green"), 0, 20));
		green_moves.add(new MoveMain(new Pawn(3, "green"), 20, 6));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 7, 20));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 11, 6));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 17, 5));
		green_moves.add(new EnterPiece(new Pawn(0, "green")));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 0, 6));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 6, 5));
		green_moves.add(new MoveMain(new Pawn(2, "green"), 27, 2));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 11, 4));
		green_moves.add(new MoveMain(new Pawn(0, "green"), 15, 2));
		green_moves.add(new MoveMain(new Pawn(3, "green"), 26, 3));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 17, 3));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 20, 5));
		green_moves.add(new MoveMain(new Pawn(3, "green"), 26, 5));
		green_moves.add(new MoveMain(new Pawn(1, "green"), 25, 6));

		green.set_moves(green_moves);
		
		Player blue = new Player();
		g.register(blue);
		blue.startGame("blue");
		ArrayList<IMove> blue_moves = new ArrayList<IMove>();
		blue_moves.add(new EnterPiece(new Pawn(0, "blue")));
		blue_moves.add(new EnterPiece(new Pawn(1, "blue")));
		blue_moves.add(new EnterPiece(new Pawn(2, "blue")));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 17, 5));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 17, 2));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 19, 2));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 22, 6));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 28, 6));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 21, 1));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 22, 1));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 23, 1));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 24, 2));
		blue_moves.add(new EnterPiece(new Pawn(0, "blue")));
		blue_moves.add(new EnterPiece(new Pawn(1, "blue")));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 17, 2));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 19, 2));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 17, 3));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 20, 1));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 21, 3));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 21, 2));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 23, 2));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 25, 4));
		blue_moves.add(new EnterPiece(new Pawn(2, "blue")));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 24, 4));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 28, 1));
		blue_moves.add(new EnterPiece(new Pawn(2, "blue")));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 29, 5));
		blue_moves.add(new MoveMain(new Pawn(0, "blue"), 0, 20));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 29, 2));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 31, 2));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 33, 10));
		blue_moves.add(new MoveMain(new Pawn(1, "blue"), 9, 4));
		blue_moves.add(new MoveHome(new Pawn(1, "blue"), 0, 1));
		blue_moves.add(new MoveMain(new Pawn(2, "blue"), 17, 6));
		blue_moves.add(new EnterPiece(new Pawn(3, "blue")));



		blue.set_moves(blue_moves);
		
		sinit = new State("boards/init.txt",2);
		states.add(sinit);
		for (int i = 1; i <= 60; i++){
			states.add(new State("boards/" + i + ".txt", 2));
		}
		
		g2.set_state(new State("boards/init2.txt", 2));
		g3.set_state(new State("boards/init3.txt", 2));
	}

	public static void main(String argv[]) throws Exception{
		g.start();
		Tester.check(new MoveMain(new Pawn(0, "blue"), 18, 1).is_Legal(g2.game_state, g2.game_state), "move in front of blockade test");
		Tester.check(!(new MoveHome(new Pawn(2, "green"), 0, 5).is_Legal(g3.game_state, g3.game_state)), "blockade home row test");

	}
	
}


