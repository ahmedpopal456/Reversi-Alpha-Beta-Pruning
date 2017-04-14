package reversi;
import reversi.Othello_Board;

import java.util.Collection;

public interface OnPlayerInteraction
{
	void onSelectionOfGridSquare(final int cellIndex);
	void onBestMoveAcquired(final Collection<Othello_Board.gridSquare> optimalMoves);
}
