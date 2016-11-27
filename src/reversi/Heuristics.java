package reversi;

import reversi.Othello_Board;


public class Heuristics {

	public Heuristics()
	{
	}

	public int OverallHeuristic(final Othello_Board board, final Othello_Board.Player player)  // Get overall Heuristic Value
	{
		return    NetStabilityHeuristic(board, player)
				+ HeuristicBasedOnNextTurn(board, player) + HeuristicNumberOfMove(board, player)
				+ HeuristicBasedOnCurrentDiscs(board, player);
	}

	private int NetStabilityHeuristic(final Othello_Board board, final Othello_Board.Player player)  // Returns the value of the board given, based on assessing the stability of the discs that are currently on the board for the player specified
	{
		return player.getSign()
				* (board.getTotalNumberOfStableDiscs(player) - board.getTotalNumberOfStableDiscs(Othello_Board.Player
						.getOpponent(player))) * 10;
	}


	private int HeuristicBasedOnNextTurn(final Othello_Board board, final Othello_Board.Player player)   // Returns the value of the board given, based on assessing the chances of the current player having the advantage that the next player will skip his turn
	{
		return (board.getNextBoards(Othello_Board.Player.getOpponent(player)).isEmpty()) ? player.getSign() * 500 : 0;
	}


	private int HeuristicNumberOfMove(final Othello_Board board, final Othello_Board.Player player)  // Returns the number of possible moves the next player can make taken with the respective weight
	{
		final Othello_Board.Player opponent = Othello_Board.Player.getOpponent(player);
		return opponent.getSign() * board.getNextBoards(opponent).size() * 5;
	}

	private int HeuristicBasedOnCurrentDiscs(final Othello_Board board, final Othello_Board.Player player) // Returns the disc count value of the board for the given player
	{
		return player.getSign()
				* (board.getDiscCount(player) - board.getDiscCount(Othello_Board.Player.getOpponent(player))) * 2;
	}
}
