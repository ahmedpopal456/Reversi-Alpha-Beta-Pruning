package reversi;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.*;

import reversi.OnPlayerInteraction;
import reversi.OnBoardChangePerception;
import reversi.Othello_Board;


public class UserInterface extends JFrame implements OnBoardChangePerception {

	private static final int FRAME_HEIGHT = 600;
	private static final int FRAME_WIDTH = 558;

	private final BoardLayout boardLayout;


	public UserInterface(final OnPlayerInteraction listener) {
		super();
		setLayout(new BorderLayout(0, 0));

		boardLayout = new BoardLayout(listener);

		final Container container = getContentPane();
		container.add(boardLayout, BorderLayout.CENTER);

		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

		pack();
		setVisible(true);
		setResizable(false);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public static class BoardGridSquares extends JPanel {

		public enum GridSquare
		{
			EMPTY("0x006400"),
			CAPTURED("0x006400"),
			HIGHLIGHTED("0xFF0000");

			String backgroundColor = "";

			GridSquare(final String color)
			{
				this.backgroundColor = color;
			}

			public Color getBackgroundColor()
			{
				return Color.decode(backgroundColor);
			}
		}

		private BufferedImage whiteDiscImage;
		private BufferedImage blackDiscImage;
		private Othello_Board.Player cellOwner;
		private GridSquare gridsquare;


		public BoardGridSquares()
		{
			gridsquare = GridSquare.EMPTY;
			setPreferredSize(new Dimension(100, 100));
			setVisible(true);

			try {

				whiteDiscImage = ImageIO.read(new File("piece_images/whitepiece.png"));
				blackDiscImage = ImageIO.read(new File("piece_images/blackpiece.png"));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		@Override
		protected void paintComponent(final Graphics graphics)
		{
			super.paintComponent(graphics);
			graphics.setColor(gridsquare.getBackgroundColor());
			graphics.fillRect(1, 1, 70, 70);
			graphics.drawImage(getCellImage(),1, 1, 70, 70, null);
		}


		public void take(final Othello_Board.Player owner)
		{
			this.cellOwner = owner;
			gridsquare = GridSquare.CAPTURED;
			repaint();
		}


		public void highlight() {
			gridsquare = GridSquare.HIGHLIGHTED;
			repaint();
		}

		public void clearHighlight() {
			gridsquare = GridSquare.EMPTY;
			repaint();
		}

		@Override
		public void repaint() {
			paintImmediately(0, 0, 100, 100);
		}

		private Image getCellImage()
		{
			if (cellOwner == Othello_Board.Player.WHITE) {
				return whiteDiscImage;
			} else if (cellOwner == Othello_Board.Player.BLACK) {
				return blackDiscImage;
			}
			return null;
		}
	}

	public class BoardLayout extends JPanel {


		private static final String BACKGROUND_COLOR = "0xffffff";
		private final OnPlayerInteraction eventsListener;
		private final Runnable clearHighlightRunnable;


		private class CellMouseListener extends MouseAdapter
		{

			private final int cellIndex;
			public CellMouseListener(final int index) {
				cellIndex = index;
			}

			@Override
			public void mouseClicked(final MouseEvent event) {
				eventsListener.onCellSelected(cellIndex);
			}

		}

		private class ClearHighlightRunnable implements Runnable {

			@Override
			public void run() {
				clearCellHighlight();
			}
		}

		private class TakeCellRunnable implements Runnable {

			private final BoardGridSquares cellLayout;
			private final Othello_Board.Player player;

			public TakeCellRunnable(final BoardGridSquares cellLayout, final Othello_Board.Player player)
			{
				this.cellLayout = cellLayout;
				this.player = player;
			}

			@Override
			public void run() {
				cellLayout.take(player);
			}
		}

		private class HighlightCellRunnable implements Runnable {

			private final BoardGridSquares cellLayout;

			public HighlightCellRunnable(final BoardGridSquares cellLayout) {
				this.cellLayout = cellLayout;
			}

			@Override
			public void run() {
				cellLayout.highlight();
			}
		}


		public BoardLayout(final OnPlayerInteraction listener) // Creates a new board layout, which also creates the cells inside of it (with proper listeners)
		{
			super(new GridLayout(8, 8));

			eventsListener = listener;
			clearHighlightRunnable = new BoardLayout.ClearHighlightRunnable();

			setVisible(true);
			setPreferredSize(getBoardDimension());

			populateCells();
			setBackground(Color.decode(BACKGROUND_COLOR));
		}


		public void onModelChanged(final Collection<Othello_Board.Cell> changedCells)  // Listener callback, runs when the model perceives a change
		{
			for (final Othello_Board.Cell cell : changedCells)
			{
				SwingUtilities.invokeLater(new BoardLayout.TakeCellRunnable(getCellAt(cell.getIndex()), cell.getOwner()));
			}
		}

		public void onNextMovesAcquired(final Collection<Othello_Board.Cell> nextMoves) // After the set of possible moves have been evaluated, this function is called to highlight or clear different cells
		{
			SwingUtilities.invokeLater(clearHighlightRunnable); // Clears the cells

			for (final Othello_Board.Cell cell : nextMoves)
			{
				SwingUtilities.invokeLater(new BoardLayout.HighlightCellRunnable(getCellAt(cell.getIndex())));
			}
		}

		public void clearCellHighlight() // Clear all of the highlighted cells
		{
			for (int i = 0; i < getComponentCount(); ++i)
			{
				final BoardGridSquares boardCellLayout = (BoardGridSquares) getComponent(i);
				boardCellLayout.clearHighlight();
			}
		}

		private BoardGridSquares getCellAt(final int index)
		{
			return (BoardGridSquares) getComponent(index);
		}

		private Dimension getBoardDimension()
		{
			final Dimension boardDimension = new Dimension(8 * 100, 8 * 100);
			return boardDimension;
		}

		private void populateCells() // Create all of the Cells
		{
			for (int i = 0; i < 8; ++i)
			{
				for (int j = 0; j < 8; ++j)
				{
					final BoardGridSquares currentCell = new BoardGridSquares();
					final int cellIndex = i * 8 + j;

					add(currentCell, cellIndex);
					currentCell.addMouseListener(new BoardLayout.CellMouseListener(cellIndex));
				}
			}
		}
	}

	@Override
	public void onBoardChanged(Collection<Othello_Board.Cell> changedCells) {
		boardLayout.onModelChanged(changedCells);
	}


	@Override
	public void onNextMovesAcquired(Collection<Othello_Board.Cell> nextMoves)
	{
		boardLayout.onNextMovesAcquired(nextMoves);
	}

	@Override
	public void onResultChanged(int whiteDiscs, int blackDiscs)
	{
	  System.out.println("White Score:" + whiteDiscs);
	  System.out.println("Black Score:" + blackDiscs);
	}

	public void endGame()
	{
		boardLayout.clearCellHighlight();
	}
}
