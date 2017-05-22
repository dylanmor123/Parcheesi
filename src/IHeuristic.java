// interface that can be used to build different heuristics

public interface IHeuristic {
	// function that takes a board and returns that heuristic's evaluated value of the board
	public int eval(Board brd);
}
