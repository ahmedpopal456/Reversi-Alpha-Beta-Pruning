package reversi;

import reversi.Othello_Board;
import reversi.CaptureGridSquare.*;
import reversi.CaptureGridSquare.Direction.*;

import static reversi.CaptureGridSquare.Direction.BOTTOM_DIRECTION;


public class Othello_Moves {

	private final Othello_Board board;

	public Othello_Moves(final Othello_Board board) // Create Object that Checks Moves
	{
		this.board = board;
	}

	public int getClosestCellInDirection(final Direction dir, final Othello_Board.gridSquare cell, final Othello_Board.Player player) // Function returns closest neighbour to a cell, in Direction specified
	{
		if(dir == Direction.TOP_DIRECTION || dir == Direction.BOTTOM_DIRECTION || dir == Direction.RIGHT_DIRECTION|| dir == Direction.LEFT_DIRECTION)
		{
			int[] Check = {0,0,0,0};
			Check = this.getXYClosestIndices(cell,player);

			if (dir == Direction.TOP_DIRECTION || dir == BOTTOM_DIRECTION)
			{
				{
					if(dir == Direction.TOP_DIRECTION)
						return Check[0];
					else
						return Check[1];
				}
			}

			if (dir == Direction.RIGHT_DIRECTION|| dir == Direction.LEFT_DIRECTION)
			{
				{
					if(dir == Direction.LEFT_DIRECTION)
						return Check[2];
					else
						return Check[3];
				}
			}
		}
		if(dir == Direction.MAIN_DIAGONAL_BOTTOM_DIRECTION || dir == Direction.MAIN_DIAGONAL_TOP_DIRECTION || dir == Direction.SECOND_DIAGONAL_BOTTOM_DIRECTION|| dir == Direction.SECOND_DIAGONAL_TOP_DIRECTION)
		{
			int[] Check = {0,0,0,0};
			Check = this.getDiagonalClosestIndices(cell,player);

			if (dir == Direction.MAIN_DIAGONAL_BOTTOM_DIRECTION || dir == Direction.MAIN_DIAGONAL_TOP_DIRECTION)
			{
				{
					if(dir == Direction.MAIN_DIAGONAL_TOP_DIRECTION)
						return Check[0];
					else
						return Check[1];
				}
			}

			if (dir == Direction.SECOND_DIAGONAL_BOTTOM_DIRECTION|| dir == Direction.SECOND_DIAGONAL_TOP_DIRECTION)
			{
				{
					if(dir == Direction.SECOND_DIAGONAL_TOP_DIRECTION)
						return Check[2];
					else
						return Check[3];
				}
			}
		}

		return -1;
	}

	/** GENERAL FUNCTIONS FOR BOTH VERTICAL/HORIZONTAL AND DIAGONAL CHECKS**/


	public boolean IsIndexAccessible(final int cellIndex, final Othello_Board.Player player) // Function checks whether the defined player can play a move at a specific index of the board
	{
		final Othello_Board.gridSquare cell = board.get(cellIndex);
		boolean lIsIndexAccessible;

		boolean lXAccessible =  Math.max(getClosestIndexinXY(cell, player, false, true,true), getClosestIndexinXY(cell, player, true, true,true)) >= 0;
		boolean lYAccessible =  Math.max(getClosestIndexinXY(cell, player, false, true,false), getClosestIndexinXY(cell, player, true, true,false)) >= 0;

		boolean lMDiagAccessible =  Math.max(getClosestDiagIndex(cell, player, false, true,true), getClosestDiagIndex(cell, player, true, true,true)) >= 0;
		boolean lSDiagAccessible =  Math.max(getClosestDiagIndex(cell, player, false, true,false), getClosestIndexinXY(cell, player, true, true,false)) >= 0;

		lIsIndexAccessible = lYAccessible || lXAccessible || lSDiagAccessible || lMDiagAccessible ;

		return (cell.isEmpty() && lIsIndexAccessible);
	}

	/** IS CELL STABLE **/

	private boolean isStable(final Othello_Board.gridSquare cell, final Othello_Board.Player player)
	{
		boolean lNegativeHoriz, lPositiveHoriz, lNegativeVertical, lPositiveVertical;
		boolean lNegativeSecondary, lPositiveSecondary, lNegativeMain, lPositiveMain;

		final Othello_Board.Player opponent = Othello_Board.Player.getMyOpponent(player);

		lNegativeHoriz    =   (getClosestIndexinXY(cell, opponent, true, false, false) < 0  && getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, true, false,false) < 0) || (getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, true, false,false) < 0 && getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, true, false,false) < 0);
		lPositiveHoriz    =   (getClosestIndexinXY(cell, opponent, false, false, false) < 0  && getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, false, false,false) < 0)|| (getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, false, false,false) < 0 && getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, false, false,false) < 0);
		lNegativeVertical =   (getClosestIndexinXY(cell, opponent, true, false, true) < 0  && getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, true, false,true) < 0) || (getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, true, false,true) < 0 && getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, true, false,true) < 0);
		lPositiveVertical =   (getClosestIndexinXY(cell, opponent, false, false, true) < 0  && getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, false, false,true) < 0) || (getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, false, false,true) < 0 && getClosestIndexinXY(cell, Othello_Board.Player.NO_DISC, false, false,true) < 0);


		lNegativeSecondary =  getClosestDiagIndex(cell, opponent, true, false,false) < 0  && getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, true, false,false) < 0 || (getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, true, false,false) < 0 && getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, true, false,false) < 0);
		lPositiveSecondary =  getClosestDiagIndex(cell, opponent, false, false,false) < 0  && getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, false, false,false) < 0 || (getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, false, false,false) < 0 && getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, false, false,false) < 0);
		lNegativeMain      =  getClosestDiagIndex(cell, opponent, true, false,true) < 0  && getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, true, false,true) < 0 || (getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, true, false,true) < 0 && getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, true, false,true) < 0);
		lPositiveMain      =  getClosestDiagIndex(cell, opponent, false, false,true) < 0  && getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, false, false,true) < 0 || (getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, false, false,true) < 0 && getClosestDiagIndex(cell, Othello_Board.Player.NO_DISC, false, false,true) < 0);


		return ( lNegativeSecondary || lPositiveSecondary ) && ( lNegativeMain || lPositiveMain) && (lNegativeHoriz || lPositiveHoriz) && (lNegativeVertical || lPositiveVertical);
	}

	public int getNumStableDiscs(final Othello_Board.Player player)  // Return the number of stable discs on the board, for the player
	{
		int result = 0;
		for (int i = 0; i < board.size(); ++i)
		{
			final Othello_Board.gridSquare currentCell = board.get(i);

			if (currentCell.isSquareOwnedByPlayer(player) && (isStable(currentCell, player)))
			{
				++result;
			}
		}
		return result;
	}

	boolean isClosest(final Othello_Board.Player player, int iteration, final Othello_Board.gridSquare currentCell)
	{
		return currentCell.isSquareOwnedByPlayer(player) && (iteration > 1 || player == Othello_Board.Player.NO_DISC);
	}

	boolean isDone(final Othello_Board.Player player, int iteration, final Othello_Board.gridSquare currentCell) // Has the search ended
	{
		return currentCell.isEmpty() || (currentCell.isSquareOwnedByPlayer(player) && iteration == 1);
	}


	/**  FUNCTIONS FOR VERTICAL AND HORIZONTAL CHECKS **/
	//

	public int[] getXYClosestIndices(final Othello_Board.gridSquare cell, final Othello_Board.Player player) // add bool
	{
		int array[] = {0, 0, 0, 0};

		array[0] = getClosestIndexinXY(cell, player, true, true, true); // Top Check
		array[1] = getClosestIndexinXY(cell, player, false, true, true); // Bottom Check
		array[2] = getClosestIndexinXY(cell, player, true, true, false); // Left Check
		array[3] = getClosestIndexinXY(cell, player, false, true, false); // Right Check

		return array;
	}

	protected int getClosestIndexinXY(final Othello_Board.gridSquare cell, final Othello_Board.Player player, final boolean isNegativeDirection, final boolean isStoppingSearch, final boolean isVertical)
	{
		int cellIndex = cell.getIndex();

		for (int i = 1; i < (isVertical ? (isNegativeDirection ? cell.getY() + 1 : 8 - cell.getY()): (isNegativeDirection ? cell.getX() + 1 : 8 - cell.getX())); ++i)
		{
			cellIndex =  isVertical ? (isNegativeDirection ? cellIndex - 8: cellIndex + 8): (isNegativeDirection ? cellIndex - 1 : cellIndex + 1);

			final Othello_Board.gridSquare currentCell = board.get(cellIndex);

			if (isClosest(player, i, currentCell))
				return cellIndex;
			else if (isStoppingSearch && isDone(player, i, currentCell))
				return -1;
		}
		return -1;
	}

	/** END OF CHECK FUNCTIONS FOR VERTICAL AND HORIZONTAL  **/


	/**  FUNCTIONS FOR CHECKS FOR DIAGONAL  **/

	public int[] getDiagonalClosestIndices(final Othello_Board.gridSquare cell, final Othello_Board.Player player) // add bool
	{
		int array[] = {0, 0, 0,0};

		array[0] = getClosestDiagIndex(cell, player, true, true, true);  // Main Top
		array[1] = getClosestDiagIndex(cell, player, false, true, true); // Main Bottom
		array[2] = getClosestDiagIndex(cell, player, true, true, false); // Secondary Top
		array[3] = getClosestDiagIndex(cell, player, false, true, false); // Secondary Bottom

		return array;
	}

	protected int getClosestDiagIndex(final Othello_Board.gridSquare cell, final Othello_Board.Player player, final boolean isNegativeDirection, final boolean isDone, final boolean isMainDiagonal)
	{
		int cellIndex = cell.getIndex();
		int iteration = 1;

		if (!(IsDiagonalMoveAllowed(isNegativeDirection, cellIndex,isMainDiagonal)))
		{
			return -1;
		}

		cellIndex = isMainDiagonal ? (isNegativeDirection ? (cellIndex - 8 - 1) : (cellIndex + 8 + 1)) : (isNegativeDirection ? (cellIndex - 8 + 1) : (cellIndex + 8 - 1)) ;

		while (!(isMainDiagonal ? ((cellIndex % 8 == 0 || cellIndex / 8 == 0 || cellIndex % 8 == 7 || cellIndex / 8 == 7) && cellIndex != 56 && cellIndex != 7):((cellIndex % 8 == 0 || cellIndex / 8 == 0 || cellIndex % 8 == 7 || cellIndex / 8 == 7) && cellIndex != 0 && cellIndex != 63)))
		{
			final Othello_Board.gridSquare currentCell = board.get(cellIndex);

			if (isClosest(player, iteration, currentCell))
				return cellIndex;

			else if (isDone && isDone(player, iteration, currentCell))
				return -1;

			cellIndex = isMainDiagonal ? (isNegativeDirection ? (cellIndex - 8 - 1) :  (cellIndex + 8 + 1)) : (isNegativeDirection ? (cellIndex - 8 + 1) : (cellIndex + 8 - 1)) ;
			++iteration;
		}

		Othello_Board.gridSquare currentCell = board.get(cellIndex);
		return isClosest(player, iteration, currentCell) ? cellIndex : -1;
	}

	private boolean IsDiagonalMoveAllowed(final boolean isNegativeDirection, int cellIndex, final boolean isMainDiagonal)
	{
		return !((!isNegativeDirection && !(isMainDiagonal ? !(cellIndex / 8 == 7 || cellIndex % 8 == 7): !(cellIndex % 8 == 0 || cellIndex / 8 == 7))) || (isNegativeDirection && !(isMainDiagonal ? !(cellIndex / 8 == 0 || cellIndex % 8 == 0):!(cellIndex / 8 == 0 || cellIndex % 8 == 7) )));
	}

	/** MOVES PERMISSION CHECKS FOR DIAGONAL**/

}
