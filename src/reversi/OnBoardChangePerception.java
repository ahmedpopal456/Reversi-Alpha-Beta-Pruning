package reversi;
import java.util.Collection;
import reversi.Othello_Board;


public interface OnBoardChangePerception
{
	void onChangeInBoard(final Collection<Othello_Board.gridSquare> changedCells);
	void onChangeInDiscResults(final int whiteDiscs, final int blackDiscs);
	void onOccuranceOfNextMove(final Collection<Othello_Board.gridSquare> nextMoves);
}
