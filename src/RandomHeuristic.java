public class RandomHeuristic implements IHeuristic{
	public int eval(Board brd){
		return (int)(Math.random() * 10);
	}
}
