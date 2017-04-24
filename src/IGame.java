interface IGame {

  // add a player to the game
  void register(IPlayer p) throws Exception;
  
  // start a game
  void start() throws Exception;
 
}