package GUIs;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import board.BoardCell;

import misc.ClueGame;

public class BoardGUI extends JFrame {

	private JPanel boardPanel;
	
	public BoardGUI() {
		setSize(new Dimension(800,800));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ClueGame theGame = new ClueGame("legend.txt", "RoomLayout.csv", "players.txt", "weapons.txt");
		boardPanel = new JPanel(new GridLayout(theGame.getBoard().getNumRows(),theGame.getBoard().getNumColumns()));
		for(BoardCell a : theGame.getBoard().getCells()) {
			if(a.isWalkway())
				;
		}
	}
	
	public static void main(String[] args) {
		BoardGUI testGUI = new BoardGUI();
		testGUI.setVisible(true);
	}

}
