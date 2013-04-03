package board;

import java.awt.Color;
import java.awt.Graphics;

public class RoomNames {

	private int row;
	private int column;
	private String name;
	
	public RoomNames(String[] args) {
		if(args.length < 3)
			System.out.println("Error");
		else {
			name = args[0];
			row = Integer.parseInt(args[2]);
			column = Integer.parseInt(args[1]) + 1;
		}
	}
	
	public void drawRoomName(Graphics g, Board board) {
		g.setColor(Color.BLACK);
		g.drawString(name, row*board.getCellWidth(), column*board.getCellHeight());
	}
	
}
