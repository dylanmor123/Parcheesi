import java.util.ArrayList;

public class HomeRow extends Space {

	public HomeRow(String color, boolean safe, ArrayList<Pawn> pawnsList) {
		
		super(color, safe, pawnsList);
	}
	
	public HomeRow(HomeRow h){
		super(h);
	}
	
	@Override
	public boolean equals(Object s){
		if(s == null){
			return false;
		}
		if (!HomeRow.class.isAssignableFrom(s.getClass())) {
		    return false;
		}
		return super.equals(s);
	}
}
