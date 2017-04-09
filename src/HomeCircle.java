import java.util.ArrayList;

public class HomeCircle extends Space {
	
	public HomeCircle(String color, boolean safe, ArrayList<Pawn> pawnsList) {
		
		super(color, safe, pawnsList);
	}
	
	@Override
	public boolean equals(Object s){
		if(s == null){
			return false;
		}
		if (!HomeCircle.class.isAssignableFrom(s.getClass())) {
		    return false;
		}
		return super.equals(s);
	}
}
