package reversi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import reversi.Heuristics;
import reversi.OnBoardChangePerception;
import reversi.Othello_Moves;
import reversi.CaptureGridSquare;


public class Othello_Board {

	public enum Player
	{
		BLACK(1),
		WHITE(-1),
		UNKNOWN(0);

		private final int sign;

		Player(final int sign)
		{
			this.sign = sign;
		}

		public int getSign() // Returns the sign, associated with the current player
		{
			return sign;
		}

		public static Player getOpponent(final Player player) // Returns the opponent of the <tt>player</tt> that is provided
		{
			return player == BLACK ? WHITE : BLACK;
		}
	}


	public class Cell
	{
		private final int x;
		private final int y;
		private final int index;

		private Othello_Board.Player owner;

		public Cell(final int index, final Othello_Board.Player owner) // Creates a new board cell from the parameters given
		{
			this.index = index;
			this.x = index % 8;
			this.y = index / 8;
			this.owner = owner;
		}


		public Cell(final int x, final int y)  // 	 * Creates a new empty board cell on the position given.
		{
			this.x = x;
			this.y = y;
			index = y * 8 + x;
			owner = Othello_Board.Player.UNKNOWN;
		}


		public Cell(final int index) //  Creates a new empty cell on the index given
		{
			this(index, Othello_Board.Player.UNKNOWN);
		}


		public void take(Othello_Board.Player player) // Takes this cell for the <tt>player</tt> specified
		{
			owner = player;
		}

		public boolean isEmpty() // Returns whether or not the cell is empty and another player can place a disc on it
		{
			return isOwnedBy(Othello_Board.Player.UNKNOWN);
		}

		public boolean isOwnedBy(final Othello_Board.Player player) // Returns whether or not the cell is owner by the <tt>player</tt> specified
		{
			return owner == player;
		}

		public int getX() // Returns the <tt>x</tt> position on the board of the current cell
		{
			return x;
		}

		public int getY() // Returns the <tt>y</tt> position on the board of the current cell
		{
			return y;
		}

		public int getIndex() // Returns the index on the board of the current cell
		{
			return index;
		}

		public Othello_Board.Player getOwner() // Returns the owner of the current cell
		{
			return owner;
		}


		public boolean hasSameOwner(final Cell other) // Returns whether or not the current cell has the same owner as the other</tt> cell given
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

			Cell other = (Cell) obj;

			if (index != other.index)
				return false;

			return true;
		}
	}

	public SwitchPlayerMove turnSwitcher;

	public class SwitchPlayerMove {

		private final Semaphore mutex;

		public SwitchPlayerMove()
		{
			mutex = new Semaphore(0);
		}


		public void startTurn()
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


		public void endTurn() //  Unblocks the first first Thread that has called TurnSwitcher
		{
			mutex.release();
		}

	}

	private boolean searchType;
	/**
	 * {@value}
	 */
	private static final int POSITION_CENTER_TOP_LEFT = 27;

	/**
	 * {@value}
	 */
	private static final int POSITION_CENTER_TOP_RIGHT = 28;

	/**
	 * {@value}
	 */
	private static final int POSITION_CENTER_BOTTOM_LEFT = 35;

	/**
	 * {@value}
	 */
	private static final int POSITION_CENTER_BOTTOM_RIGHT = 36;

	private final Set<OnBoardChangePerception> observers;

	private final Map<Integer, Cell> board;

	private final Othello_Moves checker;

	private final CaptureGridSquare cellTaker;

	private final Heuristics evaluator;

	public boolean getSearchType()
	{
		return searchType;
	}

	public Othello_Board(boolean psearchType)
	{
		turnSwitcher = new SwitchPlayerMove();
		searchType = psearchType;

		board = new LinkedHashMap<Integer, Cell>();
		for (int i = 0; i < 8; ++i) {
			for (int j = 0; j < 8; ++j) {
				final Cell currentCell = new Cell(j, i);
				board.put(currentCell.getIndex(), currentCell);
			}
		}

		checker = new Othello_Moves(this);
		cellTaker = new CaptureGridSquare(checker, this);
		evaluator = new Heuristics();

		observers = new LinkedHashSet<OnBoardChangePerception>();
	}

	/**
	 * Updates the model so that the cell with the <tt>cellIndex</tt> given,
	 * along with all the other cells that are to be taken as the result of
	 * taking the aforementioned cell are marked as taken by the <tt>player</tt>
	 * .
	 * 
	 * @param cellIndex
	 *            the index of the cell that is to be taken
	 * @param player
	 *            the player for which the cell at <tt>cellIndex</tt> is to be
	 *            taken
	 */
	public void takeCell(final int cellIndex, final Othello_Board.Player player)
	{
		final Collection<Othello_Board.Cell> takenCells = cellTaker.captureGridSquare(cellIndex, player);
		notifyBoardChanged(takenCells);
		notifyResultChanged(getDiscCount(Player.WHITE), getDiscCount(Player.BLACK));
	}

	/**
	 * Returns whether or not placing a disc at <tt>cellIndex</tt> for the
	 * <tt>player</tt> provided is legal.
	 * 
	 * @param cellIndex
	 *            the index of the cell at which a disc for the <tt>player</tt>
	 *            is to be placed.
	 * @param player
	 *            the player for which we are checking the move for being legal.
	 * @return whether or not the <tt>player</tt> given can place a disc at the
	 *         cell with <tt>cellIndex</tt>
	 */
	public boolean isMovePermitted(final int cellIndex, final Othello_Board.Player player)
	{
		return checker.IsIndexAccessible(cellIndex, player);
	}

	/**
	 * Adds the <tt>observer</tt> given to the list of the observers that are to
	 * be notified when something changes.
	 * 
	 * @param observer
	 *            the observers that is to be added
	 * @return whether or not the observer has been successfully added to the
	 *         notification list and will be called when something changes
	 */
	public boolean addObserver(final OnBoardChangePerception observer) {
		return observers.add(observer);
	}


	/**
	 * Notifies the model that the now is the turn of the <tt>player</tt>
	 * specified
	 * 
	 * @param player
	 *            the player whose turn it is currently.
	 */
	public void setOfPossibleMoves(final Player player)
	{
		notifyNextMovesAcquired(getNextMoves(player));
	}

	/**
	 * Returns the {@link Cell} instance associated with the <tt>index</tt>
	 * specified
	 * 
	 * @param cellIndex
	 *            the index, the cell corresponding to which is to be retrieved
	 * @return the {@link Cell} instance, associated with the <tt>index</tt>
	 *         given.
	 */
	public Cell get(final int cellIndex) {
		return board.get(cellIndex);
	}

	/**
	 * Returns the {@link Cell} instance, associated with the <tt>x</tt> and
	 * <tt>y</tt> coordinates given.
	 * 
	 * @param x
	 *            the x coordinate of the cell that is to be retrieved
	 * @param y
	 *            the y coordinate of the cell that is to be retrieved
	 * @return the {@link Cell} instance, that is associated with the <tt>x</tt>
	 *         and <tt>y</tt> coordinates given
	 */
	public Cell get(final int x, final int y)
	{
		return board.get(y * 8 + x);
	}

	/**
	 * Returns the next possible moves for the <tt>player</tt> given
	 * 
	 * @param player
	 *            the player for whom the next possible moves are to be
	 *            retrieved
	 * @return all the possible next moves for the <tt>player</tt> given
	 */
	public Collection<Cell> getNextMoves(final Player player)
	{
		final List<Cell> result = new ArrayList<Cell>();

		for (int i = 0; i < size(); ++i)
		{
			if (isMovePermitted(i, player))
			{
				result.add(new Cell(i));
			}
		}
		return result;
	}

	/**
	 * Returns the {@link Othello_Board} instance that correspond to the next possible
	 * moves for the <tt>player</tt> given.
	 * 
	 * @param player
	 *            the player for whom the list of possible next boards is to be
	 *            retrieved.
	 * @return a collection of all the possible next moves for the
	 *         <tt>player</tt> given.
	 */
	public Collection<Othello_Board> getNextBoards(final Player player)
	{
		final List<Othello_Board> result = new ArrayList<Othello_Board>();

		for (int i = 0; i < size(); ++i)
		{
			if (isMovePermitted(i, player))
			{
				final Othello_Board board = new Othello_Board(this.getSearchType());
				for (final Cell cell : this.board.values()) {
					board.get(cell.getIndex()).take(cell.getOwner());
				}
				final Othello_Board newBoard = board;
				newBoard.takeCell(i, player);
				result.add(newBoard);
			}
		}
		return result;
	}

	/**
	 * Returns the board's value for the <tt>player</tt> given.
	 * 
	 * @param player
	 *            the player, for whom the board is to be evaluated.
	 * @return the value of the board as computed for the <tt>player</tt> given.
	 */
	public int getValue(final Player player)
	{
		return evaluator.OverallHeuristic(this, player);
	}

	/**
	 * Returns the cell in the current board, that differ from the ones in the
	 * <tt>other</tt> board given
	 * 
	 * @param other
	 *            the
	 * @return
	 */
	public Collection<Cell> getDifference(final Othello_Board other)
	{
		final List<Cell> difference = new ArrayList<Cell>();
		for (final Cell cell : board.values()) {
			final Cell otherCell = other.get(cell.getIndex());
			if (!cell.hasSameOwner(otherCell)) {
				difference.add(cell);
			}
		}
		return difference;
	}

	/**
	 * Updates the model so that the owners of the <tt>cells</tt> given coincide
	 * with the ones in the current {@link Othello_Board} instance
	 * 
	 * @param cells
	 *            the cells that are to be updated in the board model
	 */
	public void takeCells(final Collection<Cell> cells)
	{
		for (final Cell cell : cells)
		{
			board.get(cell.getIndex()).take(cell.getOwner());
		}
		notifyBoardChanged(cells);
		notifyResultChanged(getDiscCount(Player.WHITE), getDiscCount(Player.BLACK));
	}

	/**
	 * Configure the initial state of the model
	 */
	public void startGame() // make input file here.. that enables us to start wherever we want
	{
		takeCell(POSITION_CENTER_TOP_LEFT, Player.WHITE);
		takeCell(POSITION_CENTER_TOP_RIGHT, Player.BLACK);
		takeCell(POSITION_CENTER_BOTTOM_LEFT, Player.BLACK);
		takeCell(POSITION_CENTER_BOTTOM_RIGHT, Player.WHITE);
	}

	/**
	 * Returns the size of the board as number of cells
	 * 
	 * @return the size of the board as number of cells
	 */
	public int size() {
		return board.size();
	}

	/**
	 * Returns the number of stable discs on the board for the <tt>player</tt>
	 * given
	 * 
	 * @param player
	 *            the player, for whom the number of stable discs is to be
	 *            computed
	 * @return the number of stable discs on the booard for the <tt>player</tt>
	 *         given
	 */
	public int getTotalNumberOfStableDiscs(final Player player)
	{
		return checker.getNumberOfStableDiscs(player);
	}

	/**
	 * Returns whether or not there are possible moves for the <tt>player</tt>
	 * given.
	 * 
	 * @param player
	 *            the player for whom it needs to be checked if there are any
	 *            possible moves.
	 * @return whether or not there are any possible moves for the
	 *         <tt>player</tt> given.
	 */
	public boolean hasNextMove(final Player player)
	{
		return !getNextMoves(player).isEmpty();
	}

	/**
	 * Returns the number of the discs on the board, that are owned by the
	 * <tt>player</tt> specified
	 * 
	 * @param player
	 *            the player for which the number of discs on the board is to be
	 *            computed.
	 * @return the number of discs on the board that belong to the
	 *         <tt>player</tt> specified.
	 */
	public int getDiscCount(Othello_Board.Player player) {
		int result = 0;
		for (final Cell cell : board.values()) {
			if (cell.isOwnedBy(player)) {
				++result;
			}
		}
		return result;
	}

	private void notifyBoardChanged(final Collection<Othello_Board.Cell> changedCells) {
		for (final OnBoardChangePerception observer : observers) {
			observer.onBoardChanged(changedCells);
		}
	}

	private void notifyNextMovesAcquired(final Collection<Othello_Board.Cell> nextMoves)
	{
		for (final OnBoardChangePerception observer : observers)
		{
			observer.onNextMovesAcquired(nextMoves);
		}
	}

	private void notifyResultChanged(final int whiteDiscs, final int blackDiscs) {
		for (final OnBoardChangePerception observer : observers) {
			observer.onResultChanged(whiteDiscs, blackDiscs);
		}
	}

}
