// represents a move that starts on the main ring
// (but does not have to end up there)

class MoveMain implements IMove {
  protected Pawn pawn;
  protected int start;
  protected int distance;
  
  MoveMain(Pawn pawn, int start, int distance) {
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
}
