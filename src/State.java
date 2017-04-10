
public class State {
	protected Board curr_b;
	protected IPlayer curr_player;
	protected int[] rolls_vals_left;
	
	public State(Board b, IPlayer p, int[] rolls){
		this.curr_b = b;
		this.curr_player = p;
		this.rolls_vals_left = rolls;
	}
	
	public Board get_board(){
		return this.curr_b;
	}
	
	public IPlayer get_curr_player(){
		return this.curr_player;
	}
	
	public int[] get_rolls(){
		return this.rolls_vals_left;
	}
	
	public void set_board(Board b){
		this.curr_b = b;
	}
	
	public void set_curr_player(IPlayer p){
		this.curr_player = p;
	}
	
	public void set_rolls(int[] r){
		this.rolls_vals_left = r;
	}
	
	

}
