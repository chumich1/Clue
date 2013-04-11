package misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import misc.Card.CardType;

import board.Board;
import board.BoardCell;
import board.RoomCell;


public class ComputerPlayer extends Player {
	
	private char lastRoomVisited;

	public Suggestion createSuggestion(int row, int column, ArrayList<Card> deck, Board board) {
		Suggestion suggestion = new Suggestion();
		String room = board.getRooms().get(board.getRoomCellAt(row, column).getRoomClassifier());
		suggestion.setRoom(new Card (room, CardType.ROOM));
		Collections.shuffle(deck);
		suggestion.setPerson(findValidCard(deck, CardType.PERSON));
		suggestion.setWeapon(findValidCard(deck, CardType.WEAPON));
		return suggestion;
	}
	
	public Solution createAccusation(ArrayList<Card> deck, ArrayList<Card> solution){
		Collections.shuffle(deck);
		Solution tempSol = new Solution(findValidCard(deck, CardType.PERSON).getName(),findValidCard(deck, CardType.WEAPON).getName(),findValidCard(deck, CardType.ROOM).getName());
		return tempSol;
	}
	
	public Card findValidCard(ArrayList<Card> deck, CardType type) {
		ArrayList<Card> knownCards = this.getKnownCards();
		for (Card x : deck) {
			if (x.getCardType().equals(type) && !knownCards.contains(x)) {
				Card returned = new Card(x.getName(), x.getCardType());
				return x;
			}
		}
		return null;
		
	}
	
	public BoardCell pickLocation(Set<BoardCell> targets) {
	
		for (BoardCell selection : targets) {
			if (selection.isRoom()) {
				RoomCell room = (RoomCell) selection;
				if (room.getRoomClassifier() != lastRoomVisited) {
					lastRoomVisited = room.getRoomClassifier();
					return selection;
				}
			}
		}
		Random generator = new Random();
		int random = generator.nextInt();
		if(targets.size() > 0){
			random = Math.abs(random % targets.size());
			Object[] targArr =  targets.toArray();
			return (BoardCell) targArr[random];
		}
		
		return null;
	}

	public ComputerPlayer(String name, String color, int row, int column) {
		super(name, color, row, column);
	}

	public ComputerPlayer() {
		super();
	}
	
	public void updateSeen(Card seen) {
		if(seen != null)
			System.out.println(seen.getName());
		knownCards.add(seen);
	}
	
	public ArrayList<Card> getSeen(){
		return knownCards;
	}
	
	public void updateSeen(ArrayList<Card> seen) {
		knownCards.addAll(seen);
	}

	public char getLastRoomVisited() {
		return lastRoomVisited;
	}

	public void setLastRoomVisited(char lastRoomVisited) {
		this.lastRoomVisited = lastRoomVisited;
	}
	
}
