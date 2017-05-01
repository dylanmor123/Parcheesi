import java.util.ArrayList;

public class PreHomeRow extends Space {
	public PreHomeRow(PreHomeRow p){
		super(p);
	}
	
	
	public PreHomeRow(String color, boolean safe, ArrayList<Pawn> pawnsList) {
		
		super(color, safe, pawnsList);
	}
	
	@Override
	public boolean equals(Object s){
		if(s == null){
			return false;
		}
		if (!PreHomeRow.class.isAssignableFrom(s.getClass())) {
		    return false;
		}
		return super.equals(s);
	}
}
