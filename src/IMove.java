interface IMove {
	boolean is_Legal(State curr_state, State prev_state);
	
	State update_Board(State curr_state) throws Exception;
	
	
}
