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

		LEFT(1), RIGHT(1), TOP(8), BOTTOM(8), MAIN_DIAGONAL_TOP(8
			+ 1), MAIN_DIAGONAL_BOTTOM(8 + 1), SECONDARY_DIAGONAL_TOP(8 - 1), SECONDARY_DIAGONAL_BOTTOM(8 - 1);

		private final int increment;

		Direction(final int increment)
		{
			this.increment = increment;
		}

		public int getIncrement() // Returns the cell number with which a cell index is to be incremented in order to move in the direction specified
		 {
			return increment;
		}
	}

	private final Othello_Moves checker;

	private final Othello_Board board;


	public CaptureGridSquare(final Othello_Moves checker, final Othello_Board board) // Create an object
	{
		this.checker = checker;
		this.board = board;
	}

	public Set<Othello_Board.Cell> captureGridSquare(final int cellIndex, final Othello_Board.Player player) // Takes the cell specified and all the other cells that are affected
	{
		final Set<Othello_Board.Cell> takenCells = new LinkedHashSet<Othello_Board.Cell>();

		takenCells.add(board.get(cellIndex));

		for (final Direction direction : Direction.values())
		{
			final int neighbourIndex = checker.getClosestCellInDirection(direction, board.get(cellIndex), player);
			final int startIndex = Math.min(board.get(cellIndex).getIndex(), neighbourIndex);
			final int endIndex = Math.max(board.get(cellIndex).getIndex(), neighbourIndex);

			final Set<Othello_Board.Cell> result = new HashSet<Othello_Board.Cell>();

			for (int i = startIndex; i <= endIndex; i += direction.getIncrement())
			{
				result.add(board.get(i));
			}

			takenCells.addAll(neighbourIndex >= 0 ? result : Collections.<Othello_Board.Cell> emptySet());
		}

		for (final Othello_Board.Cell takenCell : takenCells)
		{
			takenCell.take(player);
		}

		return takenCells;
	}
}
