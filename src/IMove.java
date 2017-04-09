interface IMove {}

// represents a move where a player enters a piece
class EnterPiece implements IMove {
  Pawn pawn;  
  EnterPiece(Pawn pawn) {
    this.pawn=pawn;
  }
}