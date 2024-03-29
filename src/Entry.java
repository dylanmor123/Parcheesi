import java.util.ArrayList;

public class Entry extends Space {
	public Entry(Entry e){
		super(e);
	}
	public Entry(String color, boolean safe, ArrayList<Pawn> pawnsList) {
		super(color, safe, pawnsList);
	}
	
	@Override
	public boolean equals(Object s){
		if(s == null){
			return false;
		}
		if (!Entry.class.isAssignableFrom(s.getClass())) {
		    return false;
		}
		return super.equals(s);
	}

}
