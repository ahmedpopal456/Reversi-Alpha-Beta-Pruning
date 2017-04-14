package reversi;

import reversi.Othello_Board;


public class Heuristics {

	public Heuristics()
	{
	}

	public int TotalHeuristicValue(final Othello_Board board, final Othello_Board.Player player)  // Get overall Heuristic Value
	{
		return    BoardsTotalDiscStabilityHeuristic(board, player)
				+ NextMoveAdvantageHeuristic(board, player) + NumberOfOpponentsPossibleMovesHeuristic(board, player)
				+ MyTotalDiscCountHeuristic(board, player);
	}

	private int BoardsTotalDiscStabilityHeuristic(final Othello_Board board, final Othello_Board.Player player)  // Returns the value of the board given, based on assessing the stability of the discs that are currently on the board for the player specified
	{
		return player.getSign()
				* (board.getNumberOfStableDiscsOnBoard(player) - board.getNumberOfStableDiscsOnBoard(Othello_Board.Player
						.getMyOpponent(player))) * 10;
	}


	private int NextMoveAdvantageHeuristic(final Othello_Board board, final Othello_Board.Player player)   // Returns the value of the board given, based on assessing the chances of the current player having the advantage that the next player will skip his turn
	{
		return (board.getPossibleNextBoards(Othello_Board.Player.getMyOpponent(player)).isEmpty()) ? player.getSign() * 500 : 0;
	}


	private int NumberOfOpponentsPossibleMovesHeuristic(final Othello_Board board, final Othello_Board.Player player)  // Returns the number of possible moves the next player can make taken with the respective weight
	{
		final Othello_Board.Player opponent = Othello_Board.Player.getMyOpponent(player);
		return opponent.getSign() * board.getPossibleNextBoards(opponent).size() * 5;
	}

	private int MyTotalDiscCountHeuristic(final Othello_Board board, final Othello_Board.Player player) // Returns the disc count value of the board for the given player
	{
		return player.getSign()
				* (board.getDiscCountOnBoard(player) - board.getDiscCountOnBoard(Othello_Board.Player.getMyOpponent(player))) * 2;
	}
}
