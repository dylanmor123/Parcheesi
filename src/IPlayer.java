
interface IPlayer {

  // inform the player that a game has started
  // and what color the player is.
  void startGame(String color) throws Exception;

  // ask the player what move they want to make
  IMove doMove(Board brd, int[] dice) throws Exception;

  // inform the player that they have suffered
  // a doubles penalty
  void DoublesPenalty() throws Exception;
}