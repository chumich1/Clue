/*
 * ABSTRACT Class: BoardCell, parent of WalkwayCell and RoomCell
 * Authors: Brandon Rodriguez, Hunter Lang
 * This class provides the framework needed for the different types of cells in the game
 */
package board;

import java.awt.Graphics;

// BoardCell class body
public abstract class BoardCell {
	
	// Cells require that they have a row and a column
	private int row;
	private int column;
	
	// "is" functions are set to false then overridden in the appropriate cell to suit that cell's needs
	public boolean isWalkway() {
		return false;
	}
	public boolean isRoom() {
		return false;
	}
	public boolean isDoorway() {
		return false;
	}
	
	abstract void draw(Graphics g, Board b);
	
	
}
