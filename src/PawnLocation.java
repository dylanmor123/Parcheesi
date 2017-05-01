public class PawnLocation {
	private String space_type; // "main" - main ring, "home row" - home row, "home" - home, "home circle" - home circle
	private int index;
	
	public PawnLocation(String type, int ind){
		this.space_type = type;
		this.index = ind;
	}
	
	public String get_type(){
		return this.space_type;
	}
	
	public int get_index(){
		return this.index;
	}
}
