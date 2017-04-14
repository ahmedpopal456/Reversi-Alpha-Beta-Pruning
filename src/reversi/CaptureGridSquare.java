package reversi;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import reversi.Othello_Board;
import reversi.Othello_Moves;

public class CaptureGridSquare
{

	public enum Direction
	{

		LEFT_DIRECTION(1), RIGHT_DIRECTION(1), TOP_DIRECTION(8), BOTTOM_DIRECTION(8), MAIN_DIAGONAL_TOP_DIRECTION(8
			+ 1), MAIN_DIAGONAL_BOTTOM_DIRECTION(8 + 1), SECOND_DIAGONAL_TOP_DIRECTION(8 - 1), SECOND_DIAGONAL_BOTTOM_DIRECTION(8 - 1);

		private final int directionalValue;

		Direction(final int directionalValue)
		{
			this.directionalValue = directionalValue;
		}

		public int getDirectionalValue() // Returns the cell number with which a cell index is to be incremented in order to move in the direction specified
		 {
			return directionalValue;
		}
	}

	private final Othello_Moves checkMoves;

	private final Othello_Board othelloBoard;


	public CaptureGridSquare(final Othello_Moves checkMoves, final Othello_Board othelloBoard) // Create an object
	{
		this.checkMoves = checkMoves;
		this.othelloBoard = othelloBoard;
	}

	public Set<Othello_Board.gridSquare> captureGridSquare(final int gridSquareIndex, final Othello_Board.Player player) // Takes the cell specified and all the other cells that are affected
	{
		final Set<Othello_Board.gridSquare> capturedGridSquares = new LinkedHashSet<Othello_Board.gridSquare>();

		capturedGridSquares.add(othelloBoard.get(gridSquareIndex));

		for (final Direction direction : Direction.values())
		{
			final int closestNeighbouringIndex = checkMoves.getClosestCellInDirection(direction, othelloBoard.get(gridSquareIndex), player);
			final int intialIndex = Math.min(othelloBoard.get(gridSquareIndex).getIndex(), closestNeighbouringIndex);
			final int finalIndex = Math.max(othelloBoard.get(gridSquareIndex).getIndex(), closestNeighbouringIndex);

			final Set<Othello_Board.gridSquare> result = new HashSet<Othello_Board.gridSquare>();

			for (int i = intialIndex; i <= finalIndex; i += direction.getDirectionalValue())
			{
				result.add(othelloBoard.get(i));
			}

			capturedGridSquares.addAll(closestNeighbouringIndex >= 0 ? result : Collections.<Othello_Board.gridSquare> emptySet());
		}

		for (final Othello_Board.gridSquare capturedGridSquare : capturedGridSquares)
		{
			capturedGridSquare.takenSquare(player);
		}

		return capturedGridSquares;
	}
}
