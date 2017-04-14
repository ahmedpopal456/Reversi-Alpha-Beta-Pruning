package reversi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;


public class Othello_Board {

	public enum Player
	{
		BLACK_DISC_PLAYER(1),
		WHITE_DISC_PLAYER(-1),
		NO_DISC(0);

		private final int sign;

		Player(final int sign)
		{
			this.sign = sign;
		}

		public int getSign() // Returns the sign, associated with the current player
		{
			return sign;
		}

		public static Player getMyOpponent(final Player player) // Returns the opponent of the player that is provided
		{
			return player == BLACK_DISC_PLAYER ? WHITE_DISC_PLAYER : BLACK_DISC_PLAYER;
		}
	}


	public class
	gridSquare
	{
		private final int x;
		private final int y;
		private final int index;

		private Othello_Board.Player owner;

		public gridSquare(final int index, final Othello_Board.Player owner) // Creates a new board cell from the parameters given
		{
			this.index = index;
			this.x = index % 8;
			this.y = index / 8;
			this.owner = owner;
		}


		public gridSquare(final int x, final int y)  // 	 * Creates a new empty board cell on the position given.
		{
			this.x = x;
			this.y = y;
			index = y * 8 + x;
			owner = Othello_Board.Player.NO_DISC;
		}


		public gridSquare(final int index) //  Creates a new empty cell on the index given
		{
			this(index, Othello_Board.Player.NO_DISC);
		}


		public void takenSquare(Othello_Board.Player player) // Takes this cell for the player specified
		{
			owner = player;
		}

		public boolean isEmpty() // Returns whether or not the cell is empty and another player can place a disc on it
		{
			return isSquareOwnedByPlayer(Othello_Board.Player.NO_DISC);
		}

		public boolean isSquareOwnedByPlayer(final Othello_Board.Player player) // Returns whether or not the cell is owner by the player specified
		{
			return owner == player;
		}

		public int getX() // Returns the x position on the board of the current cell
		{
			return x;
		}

		public int getY() // Returns the y position on the board of the current cell
		{
			return y;
		}

		public int getIndex() // Returns the index on the board of the current cell
		{
			return index;
		}

		public Othello_Board.Player getOwnerOfCell() // Returns the owner of the current cell
		{
			return owner;
		}


		public boolean cellHasSameOwner(final gridSquare other) // Returns whether or not the current cell has the same owner as the other cell given
		{
			return owner == other.owner;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + index;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;

			if (obj == null)
				return false;

			if (getClass() != obj.getClass())
				return false;

			gridSquare other = (gridSquare) obj;

			if (index != other.index)
				return false;

			return true;
		}
	}

	public SwitchPlayerTurn turnSwapper;

	public class SwitchPlayerTurn {

		private final Semaphore mutex;

		public SwitchPlayerTurn()
		{
			mutex = new Semaphore(0);
		}


		public void startPlayersTurn()
		{
			try
			{
				mutex.acquire();
			}
			catch (InterruptedException ex)
			{
				Thread.currentThread().interrupt();
			}
		}


		public void endPlayersTurn() //  Unblocks the first first Thread that has called TurnSwitcher
		{
			mutex.release();
		}

	}

	private boolean searchType;

	private static final int INITIAL_MIDDLE_TOP_LEFT_INDEX_POSITION = 27;
	private static final int INITIAL_MIDDLE_TOP_RIGHT_INDEX_POSITION = 28;
	private static final int INITIAL_MIDDLE_BOTTOM_LEFT_INDEX_POSITION = 35;
	private static final int INITIAL_MIDDLE_BOTTOM_RIGHT_INDEX_POSITION = 36;

	private final Set<OnBoardChangePerception> observers;

	private final Map<Integer, gridSquare> board;

	private final Othello_Moves checker;

	private final CaptureGridSquare cellTaker;

	private final Heuristics evaluator;

	public boolean getSearchType()
	{
		return searchType;
	}

	public Othello_Board(boolean psearchType)
	{
		turnSwapper = new SwitchPlayerTurn();
		searchType = psearchType;

		board = new LinkedHashMap<Integer, gridSquare>();
		for (int i = 0; i < 8; ++i) {
			for (int j = 0; j < 8; ++j) {
				final gridSquare currentCell = new gridSquare(j, i);
				board.put(currentCell.getIndex(), currentCell);
			}
		}

		checker = new Othello_Moves(this);
		cellTaker = new CaptureGridSquare(checker, this);
		evaluator = new Heuristics();

		observers = new LinkedHashSet<OnBoardChangePerception>();
	}


	public void takeCell(final int cellIndex, final Othello_Board.Player player)   //cellIndex is marked as taken
	{
		final Collection<Othello_Board.gridSquare> takenCells = cellTaker.captureGridSquare(cellIndex, player);
		notificationOfChangeInBoard(takenCells);
		notificationOfChangeInDiscResults(getDiscCountOnBoard(Player.WHITE_DISC_PLAYER), getDiscCountOnBoard(Player.BLACK_DISC_PLAYER));
	}



	public boolean isMovePermitted(final int cellIndex, final Othello_Board.Player player)	//is placing disc here legal
	{
		return checker.IsIndexAccessible(cellIndex, player);
	}


	public boolean addObserver(final OnBoardChangePerception observer) 	//observer for when change is found
	{
		return observers.add(observer);
	}


	public void setOfPossibleMoves(final Player player)		//let game know that its this players turn
	{
		notificationOfNextMoveAcquisition(getPossibleNextMoves(player));
	}


	public gridSquare get(final int cellIndex)		//return index of square
	{
		return board.get(cellIndex);
	}

	public gridSquare get(final int x, final int y)		//returns position of the square
	{
		return board.get(y * 8 + x);
	}


	public Collection<gridSquare> getPossibleNextMoves(final Player player)		//next possible legal moves
	{
		final List<gridSquare> result = new ArrayList<gridSquare>();

		for (int i = 0; i < size(); ++i)
		{
			if (isMovePermitted(i, player))
			{
				result.add(new gridSquare(i));
			}
		}
		return result;
	}


	public Collection<Othello_Board> getPossibleNextBoards(final Player player)		//next possible boards
	{
		final List<Othello_Board> result = new ArrayList<Othello_Board>();

		for (int i = 0; i < size(); ++i)
		{
			if (isMovePermitted(i, player))
			{
				final Othello_Board board = new Othello_Board(this.getSearchType());
				for (final gridSquare cell : this.board.values()) {
					board.get(cell.getIndex()).takenSquare(cell.getOwnerOfCell());
				}
				final Othello_Board newBoard = board;
				newBoard.takeCell(i, player);
				result.add(newBoard);
			}
		}
		return result;
	}


	public int getValue(final Player player)
	{
		return evaluator.TotalHeuristicValue(this, player);
	}


	public Collection<gridSquare> getDifference(final Othello_Board other)		//cells on new board that are different from old
	{
		final List<gridSquare> difference = new ArrayList<gridSquare>();
		for (final gridSquare cell : board.values()) {
			final gridSquare otherCell = other.get(cell.getIndex());
			if (!cell.cellHasSameOwner(otherCell)) {
				difference.add(cell);
			}
		}
		return difference;
	}


	public void acquireGridSquare(final Collection<gridSquare> cells)
	{
		for (final gridSquare cell : cells)
		{
			board.get(cell.getIndex()).takenSquare(cell.getOwnerOfCell());
		}
		notificationOfChangeInBoard(cells);
		notificationOfChangeInDiscResults(getDiscCountOnBoard(Player.WHITE_DISC_PLAYER), getDiscCountOnBoard(Player.BLACK_DISC_PLAYER));
	}


	public void startOthelloGame() // make input file here.. that enables us to start wherever we want
	{
		takeCell(INITIAL_MIDDLE_TOP_LEFT_INDEX_POSITION, Player.WHITE_DISC_PLAYER);
		takeCell(INITIAL_MIDDLE_TOP_RIGHT_INDEX_POSITION, Player.BLACK_DISC_PLAYER);
		takeCell(INITIAL_MIDDLE_BOTTOM_LEFT_INDEX_POSITION, Player.BLACK_DISC_PLAYER);
		takeCell(INITIAL_MIDDLE_BOTTOM_RIGHT_INDEX_POSITION, Player.WHITE_DISC_PLAYER);
	}


	public int size() 		//return number of squares on board
	{
		return board.size();
	}


	public int getNumberOfStableDiscsOnBoard(final Player player)		//number of stable discs on board
	{
		return checker.getNumStableDiscs(player);
	}


	public boolean hasNextLegalMove(final Player player)		//check if there is a possible move
	{
		return !getPossibleNextMoves(player).isEmpty();
	}


	public int getDiscCountOnBoard(Othello_Board.Player player) 		//number of disc on the board
	{
		int result = 0;
		for (final gridSquare cell : board.values()) {
			if (cell.isSquareOwnedByPlayer(player)) {
				++result;
			}
		}
		return result;
	}

	private void notificationOfChangeInBoard(final Collection<Othello_Board.gridSquare> changedCells)
	{
		for (final OnBoardChangePerception observer : observers) {
			observer.onChangeInBoard(changedCells);
		}
	}

	private void notificationOfNextMoveAcquisition(final Collection<Othello_Board.gridSquare> nextMoves)
	{
		for (final OnBoardChangePerception observer : observers)
		{
			observer.onOccuranceOfNextMove(nextMoves);
		}
	}

	private void notificationOfChangeInDiscResults(final int whiteDiscs, final int blackDiscs) {
		for (final OnBoardChangePerception observer : observers) {
			observer.onChangeInDiscResults(whiteDiscs, blackDiscs);
		}
	}

}
