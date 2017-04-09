class Player implements IPlayer {
	protected boolean doubles_penalty; //true if third doubles is rolled
	private String color;
	
	public String get_color(){
		return color;
	}
	
	public void set_color(String color){
		this.color = color;
	}
	
	public void startGame(String color) {
		this.color = color;
	}
	
	public IMove doMove(Board brd, int[] dice){
		return null;
		
	}
	
	public void DoublesPenalty() {
		this.doubles_penalty = true;
	}

}
