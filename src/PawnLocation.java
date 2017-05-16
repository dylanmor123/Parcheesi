public class PawnLocation {
	private String space_type; // "main" - main ring, "home row" - home row, "home" - home, "home circle" - home circle
	private int index;
	private boolean safe;
	
	public PawnLocation(String type, int ind, boolean safe){
		this.space_type = type;
		this.index = ind;
		this.safe = safe;
	}
	
	public String get_type(){
		return this.space_type;
	}
	
	public int get_index(){
		return this.index;
	}
	
	public boolean get_safe(){
		return this.safe;
	}
}
