// represents a move where a player enters a piece
class EnterPiece implements IMove {
  private Pawn pawn;
  public Pawn get_pawn(){
	  return this.pawn;
  }
  EnterPiece(Pawn pawn) {
    this.pawn=pawn;
  }
  
  @Override
	public boolean equals(Object m){
		if(m == null){
			return false;
		}
		if (!EnterPiece.class.isAssignableFrom(m.getClass())) {
			return false;
		}
		
		EnterPiece move = (EnterPiece) m;
		return this.pawn.equals(move.pawn);	
	}
}