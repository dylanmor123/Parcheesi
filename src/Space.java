import java.util.ArrayList;

public class Space {

	protected String color; //NULL when there is no color
	protected boolean safe; //TRUE if cannot be bopped
	protected ArrayList<Pawn> pawnsList; //List of pawns on space n
	
	
	public Space(String color, boolean safe, ArrayList<Pawn> pawnsList) {
		this.color = color;
		this.safe = safe;
		this.pawnsList = pawnsList;
	}
	
}
