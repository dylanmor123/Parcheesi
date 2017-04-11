import java.util.ArrayList;
import java.util.Arrays;

public class State {
	protected Board curr_b;
	protected Player curr_player;
	protected int[] rolls_vals_left;
	
	public State(Board b, Player p, int[] rolls){
		this.curr_b = b;
		this.curr_player = p;
		this.rolls_vals_left = rolls;
	}
	
	public Board get_board(){
		return this.curr_b;
	}
	
	public Player get_curr_player(){
		return this.curr_player;
	}
	
	public int[] get_rolls(){
		return this.rolls_vals_left;
	}
	
	public void set_board(Board b){
		this.curr_b = b;
	}
	
	public void set_curr_player(Player p){
		this.curr_player = p;
	}
	
	public void set_rolls(int[] r){
		this.rolls_vals_left = r;
	}
	
	public boolean remove_roll(int r){
		ArrayList<Integer> new_rolls = new ArrayList<Integer>();
		boolean removed = false;
		for(int i=0; i < rolls_vals_left.length; i++){
			if(rolls_vals_left[i] != r) {
				new_rolls.add(rolls_vals_left[i]);
			}
			else if(rolls_vals_left[i] == r){
				removed = true;
			}
		}
		int[] nr = new int[new_rolls.size()];
		for(int i=0, len = new_rolls.size(); i < len; i++){
		   nr[i] = new_rolls.get(i);
		}
		this.set_rolls(nr);
		return removed;
	}
	
	public void add_roll(int r){
		int[] new_rolls = new int[this.rolls_vals_left.length+1];
		for (int i=0; i < rolls_vals_left.length; i++){
			new_rolls[i] = rolls_vals_left[i];
		}
		new_rolls[new_rolls.length] = r;
	}
	
	@Override
	public boolean equals(Object s){
		if(s == null){
			return false;
		}
		if (!State.class.isAssignableFrom(s.getClass())) {
			return false;
		}
		
		State state = (State) s;
		
		return state.get_curr_player().get_color().equals(this.curr_player.get_color()) && state.get_board().equals(this.curr_b) && (state.get_rolls() == this.rolls_vals_left);
	}
}
	
