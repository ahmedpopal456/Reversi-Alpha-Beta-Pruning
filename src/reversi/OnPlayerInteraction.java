package reversi;
import reversi.Othello_Board;

import java.util.Collection;

public interface OnPlayerInteraction
{
	void onCellSelected(final int cellIndex);
	void onOptimalMoveReceived(final Collection<Othello_Board.Cell> optimalMoves);
}
