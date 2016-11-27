package reversi;
import java.util.Collection;
import reversi.Othello_Board;


public interface OnBoardChangePerception
{
	void onBoardChanged(final Collection<Othello_Board.Cell> changedCells);
	void onResultChanged(final int whiteDiscs, final int blackDiscs);
	void onNextMovesAcquired(final Collection<Othello_Board.Cell> nextMoves);
}
