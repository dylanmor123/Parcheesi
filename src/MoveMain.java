// represents a move that starts on the main ring
// (but does not have to end up there)

class MoveMain implements IMove {
  Pawn pawn;
  int start;
  int distance;
  
  MoveMain(Pawn pawn, int start, int distance) {
    this.pawn=pawn;
    this.start=start;
    this.distance=distance;
  }
}
