// represents a move that starts on one of the home rows
class MoveHome implements IMove {
  Pawn pawn;
  int start;
  int distance;
  
  private int BOARD_LENGTH = 68;
  
  // 0 <= start <= BOARD_LENGTH - 1
  // distance > 0
  
  MoveHome(Pawn pawn, int start, int distance) throws Exception{
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
		if (!MoveHome.class.isAssignableFrom(m.getClass())) {
			return false;
		}
		
		MoveHome move = (MoveHome) m;
		return this.pawn.equals(move.pawn) && (this.start == move.start) && (this.distance == move.distance);	
	}
}