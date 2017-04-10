// represents a move where a player enters a piece
class EnterPiece implements IMove {
  private Pawn pawn;
  public Pawn get_pawn(){
	  return this.pawn;
  }
  EnterPiece(Pawn pawn) {
    this.pawn=pawn;
  }
}