import java.util.ArrayList;

public class Home extends Space {
	
	public Home(Home space){
		super(space);
	}
	
	public Home(String color, boolean safe, ArrayList<Pawn> pawnsList) {
		
		super(color, safe, pawnsList);
	}
	
	@Override
	public boolean equals(Object s){
		if(s == null){
			return false;
		}
		if (!Home.class.isAssignableFrom(s.getClass())) {
		    return false;
		}
		return super.equals(s);
	}
}
