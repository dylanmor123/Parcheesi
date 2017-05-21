import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class HPlayer implements IPlayer, ActionListener{
	private String name;
	private String color;
	private boolean doubles_penalty;
	private State curr_state;
	private State prev_state;
	private boolean has_started = false;
	
	private HPlayerFrame frame;
	
	private ArrayList<IMove> generated_moves;
	
	public HPlayer(HPlayer h){
		this.name = h.name;
		this.doubles_penalty = h.doubles_penalty;
		this.color = h.color;
	}
	
	public HPlayer(String name) throws Exception{
		this.name = name;
		this.doubles_penalty = false;
		this.frame = new HPlayerFrame(this);
	}
	
	public String get_color(){
		return this.color;
	}
	
	public String startGame(String color) throws Exception{
		if(!color.equals("red") && !color.equals("blue") && !color.equals("green") && !color.equals("yellow")){
			throw new Exception("Invalid player color");
		}
		
		this.color = color;
		this.generated_moves = new ArrayList<IMove>();
		frame.startGame(color);
		this.has_started = true;
		return this.name;
	}
	
	public IMove[] doMove(Board brd, int[] dice) throws Exception{
		if(!this.has_started){
			throw new Exception("Player didn't start");
		}
		
		
		// update GUI
		this.frame.update_board(brd, this.color);
		this.frame.update_rolls(dice);
		this.frame.ready_for_move();
		
		
		// wait for all legal moves to be made
		this.curr_state = new State(brd, this, dice);
		this.prev_state = new State(curr_state);
		
		while(RuleChecker.moves_remaining(this, curr_state, prev_state)){
			Thread.sleep(1000);
		}
		
		// process and return generated moves
		IMove[] results = new IMove[generated_moves.size()];
		generated_moves.toArray(results);
		
		this.generated_moves = new ArrayList<IMove>();
		
		// update GUI for end of turn
		this.frame.move_over();
		this.frame.set_state("start");
		
		return results;
		
	}
	
	public void DoublesPenalty(){
		this.doubles_penalty = true;
	}
	
	public void actionPerformed(ActionEvent e){
		String button_name = e.getActionCommand();
		
		if(button_name.equals("Make Move")){
			int id_index = this.frame.get_Pawn_Spinner().getSelectedIndex();
			List<String> distances = this.frame.get_Roll_Spinner().getSelectedValuesList();
			
			if(id_index == -1 || distances.size() == 0){
				return;
			}
			int id = Integer.parseInt((String) this.frame.get_Pawn_Spinner().getModel().getElementAt(id_index)) - 1;
			
			// construct move
			Board b = this.curr_state.get_board();
			Pawn p = new Pawn(id, this.color);
			PawnLocation loc = b.get_Pawn_Location(p);
			
			// handling multiple values summed together on entry
			int distance = 0;
			if(distances.size() >= 2){
				if(!loc.get_type().equals("home circle")){
					this.frame.get_Illegal().setText("Too many rolls chosen. Try again.");
					return;
				}
			}
			for(String d : distances){
				distance += Integer.parseInt(d);
			}
			
			IMove possible_move;
			if(loc.get_type().equals("home circle")){
				possible_move = new EnterPiece(p);
			}
			else if(loc.get_type().equals("main")){
				try {
					possible_move = new MoveMain(p, loc.get_index(), distance);
				} catch (Exception e1) {
					return;
				}
			}
			else if(loc.get_type().equals("home row")){
				try {
					possible_move = new MoveHome(p, loc.get_index(), distance);
				} catch (Exception e1) {
					return;
				}
			}
			else{
				return;
			}
			
			// if move is legal, do it and update GUI
			if(possible_move.is_Legal(this.curr_state, this.prev_state)){
				if(possible_move instanceof EnterPiece && distance != 5){
					this.frame.get_Illegal().setText("Rolls do not sum to 5. Try again.");
				}
				else{
					try {
						State new_state = possible_move.update_Board(this.curr_state);
						this.frame.set_state("curr");
						this.frame.update_board(new_state.get_board(), this.color);
						this.frame.update_rolls(new_state.get_rolls());
						this.curr_state = new State(new_state);
						this.generated_moves.add(possible_move);
						this.frame.get_Illegal().setText("");
					} catch (Exception e1) {
						return;
					}
				}
			}
			else{
				this.frame.get_Illegal().setText("Illegal move attempted. Try again.");
			}
		}
		else if(button_name.equals("Undo Moves")){
			try {
				this.generated_moves = new ArrayList<IMove>();
				this.curr_state = new State(this.prev_state);
				this.frame.set_state("start");
				this.frame.update_board(this.curr_state.get_board(), this.color);
				this.frame.update_rolls(this.curr_state.get_rolls());
			} catch (Exception e1) {
				return;
			}
		}
	}
	
	public static void main(String[] argv) throws Exception{
		HPlayer h = new HPlayer("name");
		h.startGame("green");
		
		Board b = new Board("<board> <start> <pawn> <color> yellow </color> <id> 3 </id> </pawn> <pawn> <color> yellow </color> <id> 2 </id> </pawn> <pawn> <color> yellow </color> <id> 1 </id> </pawn> <pawn> <color> yellow </color> <id> 0 </id> </pawn> <pawn> <color> red </color> <id> 3 </id> </pawn> <pawn> <color> red </color> <id> 2 </id> </pawn> <pawn> <color> red </color> <id> 1 </id> </pawn> <pawn> <color> red </color> <id> 0 </id> </pawn> <pawn> <color> green </color> <id> 3 </id> </pawn> <pawn> <color> green </color> <id> 2 </id> </pawn> <pawn> <color> green </color> <id> 1 </id> </pawn> <pawn> <color> green </color> <id> 0 </id> </pawn> <pawn> <color> blue </color> <id> 3 </id> </pawn> <pawn> <color> blue </color> <id> 2 </id> </pawn> <pawn> <color> blue </color> <id> 1 </id> </pawn> <pawn> <color> blue </color> <id> 0 </id> </pawn> </start> <main> </main> <home-rows> </home-rows> <home> </home> </board>");
		int[] dice = new int[]{2, 5};
		
		IMove[] moves = h.doMove(b, dice);
		
		for(IMove move: moves){
			System.out.println(XMLUtils.XMLtoString(move.toXMLDoc()));
		}
	}
}
