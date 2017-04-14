package reversi;

import java.util.Collection;


public class Othello_Game implements OnPlayerInteraction
{
	private final UserInterface gameLayout;
	public final Othello_Board board;
	private Othello_Board.Player currentPlayer;
	private final Othello_Minimax_Greedy_Solver reversiSolver;


	public Othello_Game(boolean searchType)
	{

		gameLayout = new UserInterface(this);
		board = new Othello_Board(searchType);

		currentPlayer = Othello_Board.Player.BLACK_DISC_PLAYER;

		board.addObserver(gameLayout);
		board.startOthelloGame();
		
		reversiSolver = new Othello_Minimax_Greedy_Solver();
	}

	public boolean IsTerminalState()
	{
		if(!board.hasNextLegalMove(Othello_Board.Player.WHITE_DISC_PLAYER) && !board.hasNextLegalMove(Othello_Board.Player.BLACK_DISC_PLAYER))
		{
			final int whiteDiscs = board.getDiscCountOnBoard(Othello_Board.Player.WHITE_DISC_PLAYER);
			final int blackDiscs = board.getDiscCountOnBoard(Othello_Board.Player.BLACK_DISC_PLAYER);

			gameLayout.endOthelloGame();

			if(whiteDiscs > blackDiscs)
				System.out.println("White Player Wins the Match with " + whiteDiscs + " White Discs");
			else
				System.out.println("Black Player Wins the Match with " + blackDiscs + " White Discs");

			return true;
		}
		else
			return false;
	}


	public void waitForPlayerInput()    // Awaits player input, after which it updated the board, UI, etc
	{
		currentPlayer = Othello_Board.Player.BLACK_DISC_PLAYER;

		if (board.hasNextLegalMove(currentPlayer))
		{
			board.setOfPossibleMoves(currentPlayer);

			board.turnSwapper.startPlayersTurn();
		}
	}


	public void SecondPlayerAIMove(int depthLevel)   // Makes the next move on the behalf of the AI
	{
		currentPlayer = Othello_Board.Player.WHITE_DISC_PLAYER;

		if(board.hasNextLegalMove(currentPlayer))
		{
			board.setOfPossibleMoves(currentPlayer); // Find the possible moves and highlight them on the board

			reversiSolver.getMiniMaxOrGreedyMove(board, this, board.getSearchType(),currentPlayer,depthLevel);

			board.turnSwapper.startPlayersTurn();
		}
	}

	public void FirstPlayerAIMove(int depthLevel)   // Makes the next move on the behalf of the AI
	{
		currentPlayer = Othello_Board.Player.BLACK_DISC_PLAYER;

		if(board.hasNextLegalMove(currentPlayer))
		{
			board.setOfPossibleMoves(currentPlayer); // Find the possible moves and highlight them on the board
			reversiSolver.getMiniMaxOrGreedyMove(board, this, board.getSearchType(),currentPlayer,depthLevel);
			board.turnSwapper.startPlayersTurn();
		}
	}


	@Override
	public void onSelectionOfGridSquare(final int cellIndex)
	{
		if (board.isMovePermitted(cellIndex, currentPlayer) && currentPlayer == Othello_Board.Player.BLACK_DISC_PLAYER)
		{
			board.takeCell(cellIndex, currentPlayer);
			board.turnSwapper.endPlayersTurn();
		}
	}

	@Override
	public synchronized void onBestMoveAcquired(Collection<Othello_Board.gridSquare> optimalMove)
	{
		board.acquireGridSquare(optimalMove);
		board.turnSwapper.endPlayersTurn();
	}
}
