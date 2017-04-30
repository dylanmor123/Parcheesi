import java.util.Arrays;

// represents a move that starts on the main ring
// (but does not have to end up there)

class MoveMain implements IMove {
  protected Pawn pawn;
  protected int start;
  protected int distance;
  
  private int BOARD_LENGTH = 68;
  
  // 0 <= start <= BOARD_LENGTH - 1
  // distance > 0
  MoveMain(Pawn pawn, int start, int distance) throws Exception{
	if(start < 0 || start >= BOARD_LENGTH){
		throw new Exception("Invalid move start");
	}
	
	if(distance <= 0){
		throw new Exception("Invalid move distance");
	}
	
	
	this.pawn=pawn;
	this.start=start;
	this.distance=distance;
  }
  
  public Pawn get_pawn(){
	  return this.pawn;
  }
  
  public int get_start(){
	  return this.start;
  }
  
  public int get_distance(){
	  return this.distance;
  }
  
  	@Override
	public boolean equals(Object m){
		if(m == null){
			return false;
		}
		if (!MoveMain.class.isAssignableFrom(m.getClass())) {
			return false;
		}
		
		MoveMain move = (MoveMain) m;
		return this.pawn.equals(move.pawn) && (this.start == move.start) && (this.distance == move.distance);	
	}
}
