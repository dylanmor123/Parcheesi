public class RandomHeuristic implements IHeuristic{
	public int eval(Board brd, String color){
		return (int)(Math.random() * 10);
	}
}
