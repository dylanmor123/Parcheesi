import java.util.ArrayList;
import java.util.Arrays;

public class Space {

	protected String color; //NULL when there is no color
	protected boolean safe; //TRUE if cannot be bopped
	protected ArrayList<Pawn> pawns_list; //List of pawns on space n
	
	// copy constructor
	public Space(Space s){
		this.color = s.color;
		this.safe = s.safe;
		this.pawns_list = new ArrayList<Pawn>(s.pawns_list);
	}
	
	public Space(String color, boolean safe, ArrayList<Pawn> pawns_list) {
		this.color = color;
		this.safe = safe;
		this.pawns_list = pawns_list;
	}
	
	public void add_Pawn(Pawn p){
		pawns_list.add(p);
	}	
	
	public boolean remove_Pawn(Pawn p){
		return pawns_list.remove(p);
	}
	
	public String get_color(){
		return this.color;
	}
	
	public boolean get_safe(){
		return this.safe;
	}
	
	public ArrayList<Pawn> get_pawns(){
		return this.pawns_list;
	}
	
	@Override
	public boolean equals(Object s){
		  if(s == null){
			  return false;
		  }
		  if (!Space.class.isAssignableFrom(s.getClass())) {
		      return false;
		  }
		  Space space = (Space) s;
		  boolean same_pawns = true;
		  
		  for (Pawn p : this.pawns_list) {
			  same_pawns = same_pawns && space.pawns_list.contains(p);
		  }
		  
		  boolean same_color = false;
		  
		  if(this.color == null){
			  same_color = this.color == space.color;
		  }
		  else{
			  same_color = this.color.equals(space.color);
		  }
		  
		  return same_pawns && same_color && (this.safe == space.safe);
	}
	
	public boolean bop(String color){
		if (this.pawns_list.size() == 1){
			Pawn curr_pawn = this.get_pawns().get(0);
			if(!curr_pawn.get_color().equals(color)){		
				return true;
			}
		}
		return false;
	}
	
	static Space s1;
	static Space s2;
	static Space s3;
	static Space s4;
	static Space s5;
	
	static void createExamples(){
		if(s1 == null){
			s1 = new Space(null, false, new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green"), new Pawn(1, "green"))));
			s2 = new Space(null, false, new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green"), new Pawn(1, "green"))));
			s3 = new Space("green", false, new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green"), new Pawn(1, "green"))));
			s4 = new Space(null, true, new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "green"), new Pawn(1, "green"))));
			s5 = new Space(null, false, new ArrayList<Pawn>(Arrays.asList(new Pawn(0, "blue"), new Pawn(1, "green"))));
		}
	}
	
	public static void main(String[] argv){
		Tester.check(s1.equals(s2), "equal spaces");
		Tester.check(!s1.equals(s3), "different color spaces");
		Tester.check(!s1.equals(s4), "different safety spaces");
		Tester.check(!s1.equals(s5), "different pawns spaces");
	}
	
	
	
}
