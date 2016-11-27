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

		currentPlayer = Othello_Board.Player.BLACK;

		board.addObserver(gameLayout);
		board.startGame();
		
		reversiSolver = new Othello_Minimax_Greedy_Solver();
	}

	public boolean IsTerminalState()
	{
		if(!board.hasNextMove(Othello_Board.Player.WHITE) && !board.hasNextMove(Othello_Board.Player.BLACK))
		{
			final int whiteDiscs = board.getDiscCount(Othello_Board.Player.WHITE);
			final int blackDiscs = board.getDiscCount(Othello_Board.Player.BLACK);

			gameLayout.endGame();

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
		currentPlayer = Othello_Board.Player.BLACK;

		if (board.hasNextMove(currentPlayer))
		{
			board.setOfPossibleMoves(currentPlayer);

			board.turnSwitcher.startTurn();
		}
	}


	public void SecondPlayerAIMove(int depthLevel)   // Makes the next move on the behalf of the AI
	{
		currentPlayer = Othello_Board.Player.WHITE;

		if(board.hasNextMove(currentPlayer))
		{
			board.setOfPossibleMoves(currentPlayer); // Find the possible moves and highlight them on the board

			reversiSolver.getMiniMaxOrGreedyMove(board, this, board.getSearchType(),currentPlayer,depthLevel);

			board.turnSwitcher.startTurn();
		}
	}

	public void FirstPlayerAIMove(int depthLevel)   // Makes the next move on the behalf of the AI
	{
		currentPlayer = Othello_Board.Player.BLACK;

		if(board.hasNextMove(currentPlayer))
		{
			board.setOfPossibleMoves(currentPlayer); // Find the possible moves and highlight them on the board
			reversiSolver.getMiniMaxOrGreedyMove(board, this, board.getSearchType(),currentPlayer,depthLevel);
			board.turnSwitcher.startTurn();
		}
	}


	@Override
	public void onCellSelected(final int cellIndex)
	{
		if (board.isMovePermitted(cellIndex, currentPlayer) && currentPlayer == Othello_Board.Player.BLACK)
		{
			board.takeCell(cellIndex, currentPlayer);
			board.turnSwitcher.endTurn();
		}
	}

	@Override
	public synchronized void onOptimalMoveReceived(Collection<Othello_Board.Cell> optimalMove)
	{
		board.takeCells(optimalMove);
		board.turnSwitcher.endTurn();
	}
}
