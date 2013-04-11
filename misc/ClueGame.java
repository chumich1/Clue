package misc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import board.Board;
import board.BoardCell;

import misc.Card.CardType;

public class ClueGame extends JFrame {
	
	public static void main(String args[]) {
		ClueGame thisGame = new ClueGame();
		while(true)
			thisGame.update();
	}

	private ArrayList<Card> deck;
	private ArrayList<Card> closetCards;
	private ArrayList<ComputerPlayer> cpuPlayers;
	private HumanPlayer humanPlayer;
	private Player whosTurn;
	private Board board;
	private String legend;
	private String layout;
	private String players;
	private String weapons;
	private DetectiveNotes these;
	private MyCardsPanel myCards;
	private Random rand = new Random();
	private SuggestionGUI suggestion;
	
	private ControlGUI controller;
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu file = new JMenu("File");
	private JMenuItem viewNotes = new JMenu("View Detective Notes");
	private JMenuItem exit = new JMenu("Exit");
	public boolean suggestionOver;
	private boolean firstTurn;
	

	public ClueGame(String legend, String layout, String players, String weapons) {
		suggestionOver = true;
		firstTurn = false;
		this.legend = legend;
		this.layout = layout;
		this.players = players;
		this.weapons = weapons;
		deck = new ArrayList<Card>();
		closetCards = new ArrayList<Card>();
		cpuPlayers = new ArrayList<ComputerPlayer>();
		humanPlayer = new HumanPlayer();
		board = new Board(layout, legend);
		this.loadConfigFiles();
		board.calcAdjacencies();
		update();
		this.add(board, BorderLayout.CENTER);
		menuBar = new JMenuBar();
		myCards = new MyCardsPanel(this.getHumanPlayer().getCards());
		this.add(myCards, BorderLayout.EAST);
		controller = new ControlGUI(this);
		this.add(controller, BorderLayout.SOUTH);
		file = new JMenu("File");
		viewNotes = new JMenu("View Detective Notes");
		exit = new JMenu("Exit");
		file.add(viewNotes);
		file.add(exit);
		menuBar.add(file);
		this.add(menuBar, BorderLayout.NORTH);
		this.setSize(new Dimension(1100, 800));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		these = new DetectiveNotes(this.getDeck());
		WelcomeSplash displayPlayer = new WelcomeSplash(humanPlayer);
		this.loadDeck();
	}
	
	public ClueGame() {
		suggestionOver = true;
		firstTurn = false;
		legend = "legend.txt";
		layout = "RoomLayout.csv";
		players = "players.txt";
		weapons = "weapons.txt";
		deck = new ArrayList<Card>();
		closetCards = new ArrayList<Card>();
		board = new Board(layout, legend);
		cpuPlayers = new ArrayList<ComputerPlayer>();
		humanPlayer = new HumanPlayer();
		this.loadConfigFiles();
		board.calcAdjacencies();
		update();
		this.add(board, BorderLayout.CENTER);
		this.deal();
		menuBar = new JMenuBar();
		myCards = new MyCardsPanel(this.getHumanPlayer().getCards());
		this.add(myCards, BorderLayout.EAST);
		file = new JMenu("File");
		viewNotes = createDetectiveNotesView();
		exit = createFileExitItem();
		file.add(viewNotes);
		file.add(exit);
		menuBar.add(file);
		this.add(menuBar, BorderLayout.NORTH);
		controller = new ControlGUI(this);
		this.add(controller, BorderLayout.SOUTH);
		this.setSize(new Dimension(1100, 800));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setTitle("The Game of Clue");
		this.setVisible(true);
		these = new DetectiveNotes(this.getDeck());
		WelcomeSplash displayPlayer = new WelcomeSplash(humanPlayer);
		this.loadDeck();
	}
	
	public void manageTurn(){
//		if(!suggestionOver)
//			return;
		if(whosTurn == null)
			whosTurn = humanPlayer;
		if(whosTurn.equals(humanPlayer)) {
			int humanRow = humanPlayer.getRow();
			int humanColumn = humanPlayer.getColumn();
			takeHumanTurn(humanPlayer);
			whosTurn = cpuPlayers.get(0);
		} else {
			takeComputerTurn((ComputerPlayer) whosTurn);
			int newIndex = cpuPlayers.indexOf(whosTurn) + 1;
			if(newIndex < cpuPlayers.size())
				whosTurn = cpuPlayers.get(newIndex);
			else
				whosTurn = humanPlayer;
		}
		repaint();
	}
	
	public void takeComputerTurn(ComputerPlayer currentPlayer){
		int roll = rand.nextInt(6)+1;
		board.startTargets(board.calcIndex(currentPlayer.getRow(), currentPlayer.getColumn()), roll);
		repaint();
		BoardCell thisLocation = currentPlayer.pickLocation(board.getTargets());
		currentPlayer.setLocation(board, thisLocation);
		
		//Finish later
		if(thisLocation.isDoorway())
		{
			Suggestion compSuggestion = new Suggestion();
			Card disprove;
			compSuggestion = currentPlayer.createSuggestion(currentPlayer.getRow(), currentPlayer.getColumn(), this.deck, board);
		
			for(ComputerPlayer p: cpuPlayers)
			{
				
			 if(p.getName().equals(compSuggestion.getPerson().getName())){

				 //need to change setLocation 
				 p.setLocation(board, board.getCellAt(currentPlayer.getRow(), currentPlayer.getColumn()));
				
			 }
			}
			 if(humanPlayer.getName().equals(compSuggestion.getPerson().getName())){
				 System.out.println("NOPE");
				 humanPlayer.setLocation(board, board.getCellAt(currentPlayer.getRow(), currentPlayer.getColumn()));
				
			 }
			 board.paintComponent(board.getGraphics());
			controller.updateGuess(compSuggestion.toString());
			disprove = this.handleSuggestion(compSuggestion.getPerson().getName(), compSuggestion.getRoom().getName(), compSuggestion.getWeapon().getName(), currentPlayer);
			if(disprove != null){
				controller.updateResult(disprove.getName());
			}
			else
				controller.updateResult("None");
			
			for(ComputerPlayer p: this.cpuPlayers){
				if(p.getSeen().contains(disprove)){
					System.out.println("YUP");
					break;
				}
				p.updateSeen(disprove);
			}
			if(currentPlayer.getKnownCards().size()  >= (deck.size() - 3)){
	
				boolean isTrue = false;
				Solution accusation = currentPlayer.createAccusation(deck, closetCards);
				 //new Solution(currentPlayer.findValidCard(currentPlayer.getCards(), CardType.PERSON).getName(), currentPlayer.findValidCard(currentPlayer.getCards(), CardType.WEAPON).getName(), currentPlayer.findValidCard(currentPlayer.getCards(), CardType.ROOM).getName());
				isTrue = this.checkAccusation(accusation);
				if(isTrue)
					controller.updateResult("GAME OVER: "+ currentPlayer.getName()+" WINS");
				
				else
				{
					System.out.println(closetCards.get(0).getName()+" "+closetCards.get(1).getName()+" "+closetCards.get(2).getName()+" ");
					System.out.println(currentPlayer.getName()+" is out of the game.");
					cpuPlayers.remove(currentPlayer);
					controller.updateGuess(accusation.toString());
				}
			}
			
		}
		controller.updateDie(roll);
		controller.updateTurn(currentPlayer.getName());
	}
	
	public void takeHumanTurn(HumanPlayer currentPlayer){
		board.setEnabled(true);
		int roll = rand.nextInt(6)+1;
		board.startTargets(board.calcIndex(currentPlayer.getRow(), currentPlayer.getColumn()), roll);
		board.repaint();
		controller.updateDie(roll);
		controller.updateTurn(currentPlayer.getName());
		board.setHumanTurn(true);
		board.setHuman(currentPlayer);
		board.setGame(this);
	}
	
	public void humanSuggestionMenu() {
		if(board.getCellAt(humanPlayer.getRow(), humanPlayer.getColumn()).isRoom()){
			firstTurn = true;
			suggestionOver = false;
			suggestion = new SuggestionGUI(deck, board.getRoomCellAt(humanPlayer.getRow(), humanPlayer.getColumn()).getRoomName());
		}
	}
	
	private JMenuItem createFileExitItem() {
		JMenuItem item = new JMenuItem("Exit");
		class MenuItemListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}
	
	private JMenuItem createDetectiveNotesView() {
		JMenuItem item = new JMenuItem("View Detective Notes");
		class MenuItemListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				these.setVisible(true);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}
	
	public void update() {
		if(suggestionOver&&firstTurn) {
			ArrayList<String> theseFellas = suggestion.getSuggestion();
			System.out.println(theseFellas.get(0).toString() + theseFellas.get(1).toString() + theseFellas.get(2).toString());
			Card disprove = this.handleSuggestion(theseFellas.get(0).toString(), theseFellas.get(1).toString(), theseFellas.get(2).toString(), humanPlayer);
			if(disprove != null){
				controller.updateResult(disprove.getName());
				for(ComputerPlayer p: this.cpuPlayers){
					p.updateSeen(disprove);
				}
			}
			else
				controller.updateResult("None");
			firstTurn = false;
		}
		repaint();
		drawPlayers(this.getGraphics());
	}

	public void drawPlayers(Graphics g) {
		ArrayList<Player> theseGuys = new ArrayList<Player>();
		theseGuys.add(humanPlayer);
		theseGuys.addAll(cpuPlayers);
		board.setTheseGuys(theseGuys);
	}
	
	public void loadConfigFiles() {
		board.loadConfigFiles();
		loadPeople();
		loadDeck();
	}
	public void loadPeople() {
		cpuPlayers = new ArrayList<ComputerPlayer>();
		Scanner peopleFile = null;
		try {
			peopleFile = new Scanner(new File(players));
		} catch (FileNotFoundException e) {
			System.out.println("Players file not found");
		}
		String[] peopleSplit;
		while(peopleFile.hasNextLine()) {
			peopleSplit = peopleFile.nextLine().split(",");
			System.out.println(peopleSplit.length);
			if(peopleSplit[0].charAt(0) == '+') {
				humanPlayer = new HumanPlayer(peopleSplit[0].substring(1), peopleSplit[1], Integer.parseInt(peopleSplit[2]), 
						Integer.parseInt(peopleSplit[3]));
			} else {
				cpuPlayers.add(new ComputerPlayer(peopleSplit[0], peopleSplit[1], Integer.parseInt(peopleSplit[2]), 
						Integer.parseInt(peopleSplit[3])));
			}
		}
		peopleFile.close();
	}
	public void loadDeck() {
		deck = new ArrayList<Card>();
		Scanner peopleFile = null;
		try {
			peopleFile = new Scanner(new File(players));
		} catch (FileNotFoundException e) {
			System.out.println("Players file not found");
		}
		String[] peopleSplit;
		while(peopleFile.hasNextLine()) {
			peopleSplit = peopleFile.nextLine().split(",");
			if(peopleSplit[0].charAt(0) == '+') {
				deck.add(new Card(peopleSplit[0].substring(1), CardType.PERSON));
			} else {
				deck.add(new Card(peopleSplit[0], CardType.PERSON));
			}
		}
		peopleFile.close();
		
		Scanner roomFile = null;
		try {
			roomFile = new Scanner(new File(legend));
		} catch (FileNotFoundException e) {
			System.out.println("Legend file not found");
		}
		String[] roomSplit;
		while(roomFile.hasNextLine()) {
			roomSplit = roomFile.nextLine().split(", ");
			if(!roomSplit[1].equalsIgnoreCase("Closet")) {
				deck.add(new Card(roomSplit[1], CardType.ROOM));
			}
		}
		roomFile.close();
		
		Scanner weaponFile = null;
		try {
			weaponFile = new Scanner(new File(weapons));
		} catch (FileNotFoundException e) {
			System.out.println("Weapons file not found");
		}
		String weaponSplit;
		while(weaponFile.hasNextLine()) {
			weaponSplit = weaponFile.nextLine();
			deck.add(new Card(weaponSplit, CardType.WEAPON));
		}
		weaponFile.close();
	}
	public void deal() {
		int i = 0;
		humanPlayer.resetCards();
		for(Player a : cpuPlayers)
			a.resetCards();
		boolean weaponInSolution = false;
		boolean personInSolution = false;
		boolean roomInSolution = false;
		Collections.shuffle(deck);
		for(Card a : deck) {
			CardType theType = a.getCardType();
			if((!weaponInSolution)&&(theType==CardType.WEAPON)) {
				closetCards.add(a);
				weaponInSolution = true;
			} else if((!personInSolution)&&(theType==CardType.PERSON)) {
				closetCards.add(a);
				personInSolution = true;
			} else if((!roomInSolution)&&(theType==CardType.ROOM)) {
				closetCards.add(a);
				roomInSolution = true;
			} else if(i == 0) {
				humanPlayer.giveCard(a);
				i++;
			} else if(i > 0) {
				cpuPlayers.get(i - 1).giveCard(a);
				i++;
			}
			if(i > cpuPlayers.size())
				i = 0;
		}
	}
	public boolean checkAccusation(Solution solution) {
		String person = null;
		String weapon = null;
		String room = null;
		for(Card a : closetCards) {
			if(a.getCardType() == CardType.PERSON)
				person = a.getName();
			else if(a.getCardType() == CardType.WEAPON)
				weapon = a.getName();
			else if(a.getCardType() == CardType.ROOM)
				room = a.getName();
		}
		return solution.checkSolution(person, weapon, room);
	}
	public Card handleSuggestion(String thePerson, String theRoom, String theWeapon, Player thePlayer) {
		ArrayList<Player> thesePlayers = new ArrayList<Player>();
		ArrayList<String> theseStrings = new ArrayList<String>();
		ArrayList<Card> theseCards = new ArrayList<Card>();
		Card answer = null;
		theseStrings.add(theRoom);
		theseStrings.add(theWeapon);
		theseStrings.add(thePerson);
		thesePlayers.add(humanPlayer);
		for(Player a : cpuPlayers)
			thesePlayers.add(a);
		thesePlayers.remove(thePlayer);
		for(Player a : thesePlayers) {
			for(Card b : a.getCards()) {
				theseCards.add(b);
			}
		}
		Collections.shuffle(theseCards);
		for(Card a : theseCards) {
			if(theseStrings.contains(a.getName())) {
				answer = a;
				break;
			}
		}
		return answer;
	}
	public int getDeckWeaponSize() {
		return getRoomSizeCardType(CardType.WEAPON);
	}
	public int getDeckPlayerSize() {
		return getRoomSizeCardType(CardType.PERSON);
	}
	public int getDeckRoomSize() {
		return getRoomSizeCardType(CardType.ROOM);
	}
	private int getRoomSizeCardType(CardType cardType) {
		int rooms = 0;
		for(Card a : deck) {
			if(a.getCardType() == cardType)
				rooms++;
		}
		return rooms;
	}
	
	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
		this.board = board;
	}

	public ArrayList<Card> getDeck() {
		return deck;
	}
	public void setDeck(ArrayList<Card> deck) {
		this.deck = deck;
	}
	public ArrayList<Card> getClosetCards() {
		return closetCards;
	}
	public void setClosetCards(ArrayList<Card> closetCards) {
		this.closetCards = closetCards;
	}
	public ArrayList<ComputerPlayer> getCpuPlayers() {
		return cpuPlayers;
	}
	public void setCpuPlayers(ArrayList<ComputerPlayer> cpuPlayers) {
		this.cpuPlayers = cpuPlayers;
	}
	public HumanPlayer getHumanPlayer() {
		return humanPlayer;
	}
	public void setHumanPlayer(HumanPlayer bob) {
		humanPlayer = bob;
	}
	public Player getWhosTurn() {
		return whosTurn;
	}
	public void setWhosTurn(Player whosTurn) {
		this.whosTurn = whosTurn;
	}
	public void addPlayer(ComputerPlayer player) {
		cpuPlayers.add(player);
	}
	public void addPlayer(HumanPlayer player) {
		humanPlayer = player;
	}
	public void resetPlayers() {
		humanPlayer = null;
		cpuPlayers = new ArrayList<ComputerPlayer>();
	}
}
