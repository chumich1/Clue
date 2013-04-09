/*
 * Class: Board
 * Authors: Brandon Rodriguez, Hunter Lang
 * This class is the main class pertaining to the clue board.
 * Also contains logic to calculate targets and adjacencies
 * Relies on BadConfigFormatException, BoardCell, RoomCell, and WalkwayCell
 */

package board;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JPanel;

import misc.Player;

// Board class body

public class Board extends JPanel implements MouseListener {


	// The list of cells for the board
	private ArrayList<BoardCell> cells;

	// The map of rooms in the board
	private Map<Character,String> rooms;

	// Number of rows and columns corresponding to the board
	private int numRows;
	private int numColumns;
	private int currentX;
	private int currentY;
	private int cellWidth;
	private int cellHeight;
	private Point point;
	private ArrayList<RoomNames> roomNames;
	private ArrayList<Player> theseGuys;
	private String csvFilepath, legendFilepath;
	private boolean visited[];
	private Map<Integer, LinkedList<Integer>> adjacencyLists;
	private Set<BoardCell> targets;
	private boolean enabled;
	private Player human;
	private boolean humanTurn;

	// Default constructor for board. Simply initializes the values, nothing else
	public Board() {
		humanTurn = false;
		// Initialize cells, rooms, numRows, numColumns
		initialize();
		loadRoomNames("roomNameLocations.txt");
	}

	// Parameterized constructor, sets all the fields of board using the configuration files
	public Board(String csv, String legend) {
		humanTurn = false;
		// Initialize cells, rooms, numRows, numColumns
		initialize();
		// Create the filepaths for the configuration files
		csvFilepath = csv;
		legendFilepath = legend;
		// Set up the adjacencies list as a blank map
		adjacencyLists = new HashMap<Integer, LinkedList<Integer>>();
		// Set up the targets as a blank set
		targets = new HashSet<BoardCell>();
		loadRoomNames("roomNameLocations.txt");
		calcAdjacencies();
	}
	
	// Initializes default values of cells, rooms, numRows, and numColumns
	private void initialize() {
		// Create the cells list as a blank
		cells = new ArrayList<BoardCell>();
		// Create the rooms map as a blank
		rooms = new HashMap<Character, String>();
		// Initialize the rows and columns to 0
		numRows = 0;
		numColumns = 0;
		theseGuys = new ArrayList<Player>();
		enabled = false;
		addMouseListener(this);

	}
	
	public void paintComponent(Graphics g) {
		currentX = 0;
		currentY = 0;
		cellWidth = (this.getWidth() - this.getWidth() % numColumns) / numColumns;
		cellHeight = (this.getHeight() - this.getHeight() % numRows) / numRows;
		int count = 0;
		for(BoardCell a : cells) {
			a.draw(g, this);
			currentX += cellWidth;
			count++;
			if(count % numColumns == 0) {
				currentY += cellHeight;
				currentX = 0;
			}
		}
		if((targets != null)&&humanTurn)
			for(BoardCell a : targets)
				a.drawHighlighted(g, this);
		for(Player p: theseGuys){
			p.draw(g, cellWidth, cellHeight);
		}
		for(RoomNames r: roomNames) {
			r.drawRoomName(g, this);
		}
		
	}
	
	
	
	public boolean checkAvailability(Player player) {
		//while(point == null){
			//System.out.println(point);
			//repaint();
		//}
		//System.out.println(point.x);
	
		int row;
		int column;
//		System.out.println(targets.size());
		for(BoardCell a: targets) {
			
			
			row = cells.indexOf(a) / numColumns;
			column = cells.indexOf(a) % (row*numColumns);
			System.out.println("Row:" + row + " Column:" + column);
			System.out.println("cellWidth: " + cellWidth + " point.x:" + point.x);
			System.out.println("cellHeight: " + cellHeight + " point.y:" + point.y);
			if((point.x >= column*cellWidth)&&(point.x < (column+1)*cellWidth)) {
				if((point.y >= row*cellHeight)&&(point.y < (row+1)*cellHeight)) {
					System.out.println("HEY");
					player.setLocation(this, a);
					point = null;
					repaint();
					enabled = false;
					humanTurn = false;
					return true;
				}
			}
		}
		
		point = null;
		repaint();
		return false;
	}

	// Method loadConfigFiles relies on two other functions to finish initializing the board
	public void loadConfigFiles() {
		try {
			// Call the two heavy lifter functions
			loadRoomConfig();
			loadBoardConfig();

		} catch(BadConfigFormatException e) { // If one of those throws an error, catch it, print it to the screen
			System.out.println(e);
		}
	}

	// One of the heavy lifter functions for the loadConfigFiles method
	// Throws: BadConfigFormatException
	// Loads up the legend file, and populates the legend in the board
	public void loadRoomConfig() throws BadConfigFormatException  {

		// Initialize the scanner
		Scanner legendFile = null;
		try {
			// Attempt to create a new scanner on the legend path
			legendFile = new Scanner(new File(legendFilepath));
		} catch (FileNotFoundException e) {
			// If we can't find the legend, throw an I/O exception under the BadConfigFormatException
			throw new BadConfigFormatException("I/O Error: " + legendFilepath + "not found!");
		}

		// Create an array to hold the legend lines after splitting
		String[] legendSplit;

		// While we have more lines:
		while(legendFile.hasNextLine()) {
			// Split the line at the comma
			legendSplit = legendFile.nextLine().split(", ");
			// If the line has more than one comma, throw an exception
			if(legendSplit.length > 2) {
				throw new BadConfigFormatException(legendFilepath + " contains data in an invalid format");
			}
			// Create a room definition using the legend information and put it into rooms 
			rooms.put(legendSplit[0].charAt(0), legendSplit[1]);
		}
		// Make sure we don't have resource leaks
		legendFile.close();
	}

	// Second heavy lifter function for the loadConfigFiles method
	// Throws: BadConfigFormatException
	// Load up the config for the board, and create the cells
	public void loadBoardConfig() throws BadConfigFormatException {

		// Initialize the scanner to null
		Scanner csvFile = null;
		try {
			// Attempt to create the scanner on the csv file
			csvFile = new Scanner(new File(csvFilepath));
		} catch (FileNotFoundException e) {
			// If something goes wrong, throw an exception
			throw new BadConfigFormatException("csv Filepath was invalid");
		}

		// Create a holder for the current csv line
		String csvLine;

		// Create an array to hold the line after splitting
		String[] csvSplit;

		// While we have more lines (rows) in the csv file:
		while(csvFile.hasNextLine()) {
			// Go to the next line
			csvLine = csvFile.nextLine();
			// Split the line, store it into csvSplit
			csvSplit = csvLine.split(",");
			// Count the row
			++numRows;
			// Process each room classifier in the csv file
			for(int i = 0; i < csvSplit.length; ++i) {

				// If the classifier is a w, make a new walkway cell
				if(csvSplit[i].charAt(0) == 'W') { 
					cells.add(new WalkwayCell());
				} 
				// Otherwise, it must be a room cell, or an invalid cell
				else {
					// If we can't find this cell type in the legend, throw an exception
					if(!rooms.containsKey(csvSplit[i].charAt(0))) throw new BadConfigFormatException("Unrecognized room detected");

					// Otherwise, add a new room cell
					cells.add(new RoomCell(csvSplit[i]));
				}
			}
		}
		
		

		// Calculate the number of columns from the rows and amount of cells
		numColumns = cells.size() / numRows;

		// If there was an error, i.e. the board is not square, throw an exception
		if(numColumns*numRows != cells.size()) {
			throw new BadConfigFormatException("Columns bad");
		}

		// Close resources
		csvFile.close();
		
		// Now that we have all the information, create the visited array.
		visited = new boolean[numRows * numColumns];
	}
	
	public void loadRoomNames(String roomNameTextFile) {
		roomNames = new ArrayList<RoomNames>();
		// Initialize the scanner
		Scanner roomNameFile = null;
		try {
			// Attempt to create a new scanner on the legend path
			roomNameFile = new Scanner(new File(roomNameTextFile));
		} catch (FileNotFoundException e) {
			// If we can't find the legend, throw an I/O exception under the BadConfigFormatException
			System.out.println("Exception in loadRoomNames");
		}

		// Create an array to hold the legend lines after splitting
		String[] roomSplit;

		// While we have more lines:
		while(roomNameFile.hasNextLine()) {
			// Split the line at the comma
			roomSplit = roomNameFile.nextLine().split(",");
			roomNames.add(new RoomNames(roomSplit));
		}
		roomNameFile.close();
	}

	// Calculates the appropriate index on a 1D array given a row and column 
	public int calcIndex(int row, int col) {
		// Outlier / Bad Input Cases
		if (row >= numRows) return -1;
		else if (col >= numColumns) return -1;
		else if (col < 0) return -1;
		else if (row < 0) return -1;

		// If the input is valid, then calculate the index
		else {
			return row * numColumns + col;
		}
	}

	// Calculates the adjacencies for every cell on the board, stores them into the adjacency list
	public void calcAdjacencies() {
		LinkedList<Integer> adjacency;
		for (int i = 0; i < numRows; ++i) {
			for (int j = 0; j < numColumns; ++j) {
				adjacency = new LinkedList<Integer>();
				if(cells.get(calcIndex(i,j)).isDoorway()) {
					RoomCell thisCell = (RoomCell) cells.get(calcIndex(i,j));
					adjacency.add(calcIndex(i + thisCell.getDoorDirection().getX(), j + thisCell.getDoorDirection().getY()));
				} 
				else if(!cells.get(calcIndex(i,j)).isRoom()) {
					if(adjacencyLogic(i,j,i,j+1))
						adjacency.add(calcIndex(i,j+1));
					if(adjacencyLogic(i,j,i,j-1))
						adjacency.add(calcIndex(i,j-1));
					if(adjacencyLogic(i,j,i+1,j))
						adjacency.add(calcIndex(i+1,j));
					if(adjacencyLogic(i,j,i-1,j))
						adjacency.add(calcIndex(i-1,j));
				}
				// Put this list onto the adjacency lists map
				adjacencyLists.put(calcIndex(i, j), adjacency);
			}
		}
	}
	
	// This function verifies that the adjacency is correct for the given cell, (i0,j0)
		private boolean adjacencyLogic(int i0, int j0, int i1, int j1) {
			// If calcIndex detects an issue, it is not correct. return false.
			if(calcIndex(i1,j1) != -1) {
				// Otherwise, if calcIndex says its fine and the cell is a doorway
				if(cells.get(calcIndex(i1,j1)).isDoorway()) {
					// Make a room cell from the doorway cell we're checking
					RoomCell thisRoom = (RoomCell) cells.get(calcIndex(i1,j1));
					// If the you remove the differences in distance between the cells and now the x's and y's are equal, return true 
					if(i1 + thisRoom.getDoorDirection().getX() == i0 && j1 + thisRoom.getDoorDirection().getY() == j0) return true;
					// Otherwise return false
					return false;
				}
				// If this is not a doorway cell, return the opposite of isRoom from the target cell
				else {
					return !cells.get(calcIndex(i1,j1)).isRoom();
				}
			}
			return false;
		}
	
	// Start targets uses calcTargets to calculate the correct targets for moving in the game
	public void startTargets(int location, int steps) {
		
		// Initialize targets to a blank set
		targets = new HashSet<BoardCell>();
		
		// Set the current location to true
		visited[location] = true;
		
		// Call calcTargets
		calcTargets(location, steps);
		visited[location] = false;
	}
	
	// Does the heavy lifting for startTargets, populates the targets list for a current location given a number of steps
	private void calcTargets(int location, int steps) {
		
		// Create a list to hold the adjacent cells
		LinkedList<Integer> adjacentCells = new LinkedList<Integer>();
		
		// For all the cells in the adjacency lists at this given location
		for(int adjCell : adjacencyLists.get(location)) {
			// If the cell has not yet been visited, add it to the adjacentCells list
			if(visited[adjCell] == false) {
				adjacentCells.add(adjCell);
			}
		}
		
		// For all cells in the adjacentCells list
		for(int adjCell : adjacentCells) {
			// Set the cell to visited
			visited[adjCell] = true;
			// Create a cell using the adjacent cell, generalizes the adjacent cell
			BoardCell thisCell = cells.get(adjCell);
			// If the steps is now 1 or this is a doorway, add the cell
			if(steps == 1 || thisCell.isDoorway()) {
				targets.add(thisCell);
			} 
			// Otherwise, recursively call calctargets until primary if is true
			else {
				calcTargets(adjCell, steps - 1);
			}
			// Set the visited back to false after the targets is done calculating
			visited[adjCell] = false;
		}
	}
	
	
	// Returns the appropriate cell given a row and column. Uses calcIndex to get cell location
	// If the cell is wrong, or cannot be found, returns null
	public RoomCell getRoomCellAt(int row, int column) {
		if(cells.get(calcIndex(row, column)).isRoom()) {
			return (RoomCell) cells.get(calcIndex(row, column));
		} else {
			return null;
		}
	}
	
	// Returns the appropriate board cell at a certain index
	public BoardCell getCellAt(int row, int column) {
		return cells.get(calcIndex(row, column));
	}
	
	//
	// Getters for all standard instance variables
	//
	
	public Set<BoardCell> getTargets() {
		return targets;

	}
	
	public LinkedList<Integer> getAdjList(int location) {
		return adjacencyLists.get(location);
	}

	public ArrayList<BoardCell> getCells() {
		return cells;
	}
	
	public Map<Character, String> getRooms() {
		return rooms;
	}
	
	public int getNumRows() {
		return numRows;
	}
	
	public int getNumColumns() {
		return numColumns;
	}
	
	public int getCurrentX() {
		return currentX;
	}

	public void setCurrentX(int currentX) {
		this.currentX = currentX;
	}

	public int getCurrentY() {
		return currentY;
	}

	public void setCurrentY(int currentY) {
		this.currentY = currentY;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public void setCellWidth(int width) {
		this.cellWidth = width;
	}

	public int getCellHeight() {
		return cellHeight;
	}

	public void setCellHeight(int heigth) {
		this.cellHeight = cellHeight;
	}
	
	public ArrayList<Player> getTheseGuys() {
		return theseGuys;
	}

	public void setTheseGuys(ArrayList<Player> theseGuys) {
		this.theseGuys = theseGuys;
	}

	public Point getPoint() {
		return point;
	}
	
	public void resetPoint() {
		point = null;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean getEnabled() {
		return enabled;
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
		if(!enabled)
			return;
		
		point = (e.getPoint());
		System.out.println(point.x + " " + point.y);
		System.out.println("Cell width:" + cellWidth);
		System.out.println("Cell height: " + cellHeight);
		repaint();
		
		if(humanTurn) {
			humanTurn = !checkAvailability(human);
		}
		
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean isHumanTurn() {
		return humanTurn;
	}

	public void setHumanTurn(boolean humanTurn) {
		this.humanTurn = humanTurn;
	}

	public Player getHuman() {
		return human;
	}

	public void setHuman(Player human) {
		this.human = human;
	}
	
	

}
