package reversi;

import reversi.Othello_Board;
import reversi.Othello_Game;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;


public class MainFile
{
	public static void main(String[] args)
	{
		/** PLAYER INPUT PHASE **/

		boolean isCheck = false;
        boolean isPlayervsAI = false;
        boolean isSearchAlgorithmMinMax = false;
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		int depthLevel = 5;

		do
		{   try
			{
				System.out.print("For an AI vs Player Game (TRUE); For an AI vs AI Game (FALSE)");
				Scanner n = new Scanner(System.in);
				isPlayervsAI = n.nextBoolean();

				isCheck = true;

			} catch (InputMismatchException e) {
				System.out.println("Invalid input!");
			}
		} while (!isCheck);

		isCheck = false;

		do
		{try
		{
			System.out.print("For an AI with MINMAX (TRUE); For an AI with GREEDY (FALSE)");
			Scanner n = new Scanner(System.in);
			isSearchAlgorithmMinMax = n.nextBoolean();

			isCheck = true;

		}
		catch (InputMismatchException e)
		{
			System.out.println("Invalid input!");
		}
		} while (!isCheck);

		float lStartTime, lEndTime;


		final Othello_Game Othello_Manager = new Othello_Game(isSearchAlgorithmMinMax);
		lGenerateOutputFile(Othello_Manager.board, "Initial State",isPlayervsAI,isSearchAlgorithmMinMax,timeStamp);

		lStartTime = System.nanoTime();

		if(!isPlayervsAI)
		{
			while (!Othello_Manager.IsTerminalState())
			{
				Othello_Manager.FirstPlayerAIMove(depthLevel);
				lGenerateOutputFile(Othello_Manager.board, "Black Move",isPlayervsAI,isSearchAlgorithmMinMax,timeStamp);

				Othello_Manager.SecondPlayerAIMove(depthLevel);
				lGenerateOutputFile(Othello_Manager.board, "White Move",isPlayervsAI,isSearchAlgorithmMinMax,timeStamp);
			}
		}
		else
		{
			while (!Othello_Manager.IsTerminalState())
			{
				Othello_Manager.waitForPlayerInput();
				lGenerateOutputFile(Othello_Manager.board, "Black Move",isPlayervsAI,isSearchAlgorithmMinMax,timeStamp);

				Othello_Manager.SecondPlayerAIMove(depthLevel);
				lGenerateOutputFile(Othello_Manager.board, "White Move",isPlayervsAI,isSearchAlgorithmMinMax,timeStamp);
			}
		}

		lEndTime = System.nanoTime();

		System.out.println(lEndTime - lStartTime);
	}

	public static void lGenerateOutputFile(Othello_Board othello_board, String player,boolean isPlayervsAI, boolean isSearchAlgorithmMinMax,  String timestamp)
	{
		int numberOfUnknownSquares = 0;
		Character lChar[] = new Character[64];
		BufferedWriter writer = null;

		for (int i = 0; i < 64; i++)
		{
			boolean isBlack = othello_board.get(i).isSquareOwnedByPlayer(Othello_Board.Player.BLACK_DISC_PLAYER);
			boolean isWhite = othello_board.get(i).isSquareOwnedByPlayer(Othello_Board.Player.WHITE_DISC_PLAYER);

			if (isBlack)
				lChar[i] = "B".toString().charAt(0);
			else if (isWhite)
				lChar[i] = "W".toString().charAt(0);
			else
			{
				lChar[i] = "0".toString().charAt(0);
				numberOfUnknownSquares++;
			}
		}

		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream((isPlayervsAI ? "PlayervsAI":"AIvsAI") + "_" + (isSearchAlgorithmMinMax? "MinMax":"Greedy") + "_" + timestamp + ".txt", true)));

			writer.newLine();
			writer.newLine();
			writer.write(numberOfUnknownSquares > 0 ? player: "Terminal State");
			writer.newLine();
			writer.newLine();

			for (int j = 0; j < 8; j++)
			{
				writer.write(lChar[j * 8].toString() + " " + lChar[j * 8 + 1].toString() + " " + lChar[j * 8 + 2].toString() + " " + lChar[j * 8 + 3].toString() +
						" " + lChar[j * 8 + 4].toString() + " " + lChar[j * 8 + 5].toString() + " " + lChar[j * 8 + 6].toString() + " " + lChar[j * 8 + 7].toString());

				writer.newLine();
				writer.flush();
			}
		}
		catch (IOException ex)
		{
			// report
		}
		finally
		{
			try
			{
				writer.close();
			} catch (Exception ex) {/*ignore*/}
		}
	}
}
