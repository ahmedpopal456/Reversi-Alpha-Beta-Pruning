package reversi;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.media.jfxmedia.events.PlayerStateEvent;
import reversi.OnPlayerInteraction;
import reversi.Othello_Board;
import reversi.Othello_Board.Player;

public class Othello_Minimax_Greedy_Solver {

	public class GameMove {

		private int value;
		private Othello_Board board;

		public GameMove(final int value, final Othello_Board board)
		{
			this.value = value;
			this.board = board;
		}

		public GameMove(final GameSolverParameter parameter, final Othello_Board.Player player)
		{
			this.value = parameter.getValue(player);
			this.board = parameter.board;
		}

		public Collection<Othello_Board.gridSquare> getDifference(final Othello_Board other) // Gets the difference between the board in this move and the <tt>other</tt>  board given.
		{
			return board.getDifference(other);
		}


		public GameMove setBoard(final Othello_Board board)  // Set the Othello_Board Associated with this Move
		{
			this.board = board;
			return this;
		}


		public GameMove setValue(final int value) //Sets the value, associated with this move
		{
			this.value = value;
			return this;
		}


		public int getValue() // Returns the value, associated with this move
		{
			return value;
		}

	}

	public class GameSolverParameter {
		public final Othello_Board board;

		public int alpha;
		public int beta;
		public int level;


		public GameSolverParameter increasedLevel(final Othello_Board nextBoard, final GameSolverParameter other) // Creates a new {@link GameSolverParameter} that is copy of the one supplied, but with an increased traversal level.
		{
			return new GameSolverParameter(nextBoard, other.alpha, other.beta, other.level + 1);
		}


		public GameSolverParameter(final Othello_Board board, final int alpha, final int beta, final int level) {    // * Creates a new {@link GameSolverParameter} instance, using the parameter given
			this.board = board;
			this.alpha = alpha;
			this.beta = beta;
			this.level = level;
		}

		public Collection<Othello_Board> getPossibleNextBoards(final Othello_Board.Player player)// 	 * Returns all the next possible board configurations for the
		{
			return board.getPossibleNextBoards(player);
		}


		public int getValue(final Othello_Board.Player player) // Returns the board evaluation for the <tt>player</tt> given.
		{
			return board.getValue(player);
		}

	}

	private final ExecutorService executor;

	private class GameSolverRunnable implements Runnable {

		private int depthLevel;
		private final Othello_Board board;
		private Player player;
		private boolean searchType;
		private final OnPlayerInteraction callback;

		public GameSolverRunnable(final Othello_Board board, final OnPlayerInteraction callback, boolean searchType, Player player, int depth)
		{
			this.depthLevel = depth;
			this.player = player;
			this.board = board;
			this.callback = callback;
			this.searchType = searchType;
		}

		@Override
		public void run()
		{
			if(player.getSign() == -1)
			{
				if (searchType == true)   // MINIMAX ALGORITHM for White Player
				{
					GameMove result = getOptimalMinMoveOrGreedyMove(new GameSolverParameter(board, Integer.MIN_VALUE, Integer.MAX_VALUE,0),depthLevel);
					callback.onBestMoveAcquired(result.getDifference(board));
				}
				else                   //  GREEDY ALGORITHM for White Player
				{
					GameMove result = getOptimalMinMoveOrGreedyMove(new GameSolverParameter(board, Integer.MIN_VALUE, Integer.MAX_VALUE,0), 1);
					callback.onBestMoveAcquired(result.getDifference(board));
				}
			}
			else if(player.getSign() == 1)
			{
				if (searchType == true)   // MINIMAX ALGORITHM for Black Player
				{
					GameMove result = getBestMinMaxOrGreedyMove(new GameSolverParameter(board, Integer.MIN_VALUE, Integer.MAX_VALUE,0),depthLevel);
					callback.onBestMoveAcquired(result.getDifference(board));
				}
				else                   //  GREEDY ALGORITHM for White Player
				{
					GameMove result = getBestMinMaxOrGreedyMove(new GameSolverParameter(board, Integer.MIN_VALUE, Integer.MAX_VALUE,0),1);
					callback.onBestMoveAcquired(result.getDifference(board));
				}
			}
		}

	}


	public Othello_Minimax_Greedy_Solver() {
		executor = Executors.newSingleThreadExecutor();
	}


	public void getMiniMaxOrGreedyMove(final Othello_Board currentBoard, final OnPlayerInteraction callback, boolean searchType, Player player,int depthLevel)  // Finds the optimal move for the white player, given the current board.
	{
		executor.execute(new GameSolverRunnable(currentBoard, callback, searchType,player,depthLevel));
	}

	private GameMove getOptimalMinMoveOrGreedyMove(final GameSolverParameter parameter, int depthLevel)
	{
		if (parameter.level == depthLevel )
		{
			return new GameMove(parameter, Othello_Board.Player.WHITE_DISC_PLAYER);  // Heuristic Value for AI
		}

		final Collection<Othello_Board> gameStates = parameter.getPossibleNextBoards(Othello_Board.Player.WHITE_DISC_PLAYER); // List of Game States that we can go to next
		GameMove nextMove = new GameMove(Integer.MAX_VALUE, parameter.board);

		for (final Othello_Board nextState : gameStates) {
			final GameSolverParameter nextParameter = parameter.increasedLevel(nextState, parameter);
			final GameMove optimalMove = getBestMinMaxOrGreedyMove(nextParameter,depthLevel);

			if (((nextMove.value == optimalMove.value) ? 0 : (nextMove.value > optimalMove.value ? 1 : -1)) >= 0) {
				final int nextValue = optimalMove.getValue();
				nextMove.setBoard(nextState).setValue(nextValue);
				parameter.beta = nextValue;
			}

			if (parameter.beta <= parameter.alpha)
				return nextMove;
		}

		return nextMove;
	}

	private GameMove getBestMinMaxOrGreedyMove(final GameSolverParameter parameter, int depthLevel)
	{
		if (parameter.level == depthLevel)
		{
			return new GameMove(parameter, Othello_Board.Player.BLACK_DISC_PLAYER);  // Heuristic Value for MAX
		}

		final Collection<Othello_Board> gameStates = parameter.getPossibleNextBoards(Othello_Board.Player.BLACK_DISC_PLAYER);
		GameMove nextMove = new GameMove(Integer.MIN_VALUE, parameter.board);

		for (final Othello_Board nextState : gameStates)
		{
			final GameSolverParameter nextParameter = parameter.increasedLevel(nextState, parameter);
			final GameMove optimalMove = getOptimalMinMoveOrGreedyMove(nextParameter, depthLevel); // If Depth Max is Achieved, return is

			if (((nextMove.value == optimalMove.value) ? 0 : (nextMove.value > optimalMove.value ? 1 : -1)) <= 0) {
				final int nextValue = optimalMove.getValue();
				nextMove.setBoard(nextState).setValue(nextValue);
				parameter.alpha = nextValue;
			}

			if (parameter.beta <= parameter.alpha) {
				return nextMove;
			}
		}
		return nextMove;
	}

}
