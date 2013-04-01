package misc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import javax.swing.JFrame;

import board.Board;

import misc.Card.CardType;

public class ClueGame extends JFrame {
	
	public static void main(String args[]) {
		ClueGame thisGame = new ClueGame();
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

	public ClueGame(String legend, String layout, String players, String weapons) {
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
		this.setSize(new Dimension(800, 800));
		this.add(board, BorderLayout.CENTER);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public ClueGame() {
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
		this.add(board, BorderLayout.CENTER);
		this.setSize(new Dimension(800, 800));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	public void update() {
		while(true)
			paintChildren(this.getGraphics());
	}

	public void paintChildren(Graphics g) {
		ArrayList<Player> theseGuys = new ArrayList<Player>();
		theseGuys.add(humanPlayer);
		theseGuys.addAll(cpuPlayers);
		for(Player p: theseGuys){
			p.draw(g, board.getCellWidth(), board.getCellHeight());
		}
		
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
	public Object handleSuggestion(String thePerson, String theRoom, String theWeapon, Player thePlayer) {
		ArrayList<Player> thesePlayers = new ArrayList<Player>();
		ArrayList<String> theseStrings = new ArrayList<String>();
		ArrayList<Card> theseCards = new ArrayList<Card>();
		Object answer = null;
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
