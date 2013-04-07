package playerTests;

import junit.framework.Assert;

import misc.Card.CardType;
import misc.ClueGame;
import misc.ComputerPlayer;
import misc.HumanPlayer;
import misc.Player;
import misc.Card;
import org.junit.Before;
import org.junit.Test;

public class GameSetupTests {
	
	private ClueGame testGame;

	@Before
	public void setup() {
		testGame = new ClueGame("legend.txt","RoomLayout.csv", "players.txt", "weapons.txt");
		testGame.loadConfigFiles();
	}
	
	// Each tests name, color, starting location
	@Test
	public void testLoadingPeople() {
		testGame.loadPeople();
		Assert.assertEquals(new HumanPlayer("Bob Lob Lah","Yellow",9,0), testGame.getHumanPlayer());
		// Tests person at beginning
		Assert.assertEquals(new ComputerPlayer("Lucille Bluth","Orange",0,19), testGame.getCpuPlayers().get(0));
		// Tests person at end
		Assert.assertEquals(new ComputerPlayer("Steve Holt","Blue",0,6), testGame.getCpuPlayers().get(3));
	}
	
	@Test
	public void testLoadingCards() {
		testGame.loadDeck();
		/* Tests Deck has proper number of cards (first integer: number of rooms (not including walkway)
		plus number people plus number weapons */
		Assert.assertEquals(10+5+7,testGame.getDeck().size());
		// Tests deck contains proper number of each type of card
		Assert.assertEquals(10, testGame.getDeckRoomSize());
		Assert.assertEquals(5, testGame.getDeckPlayerSize());
		Assert.assertEquals(7, testGame.getDeckWeaponSize());
		// Tests one room
		Assert.assertTrue(testGame.getDeck().contains(new Card("Dining Room", CardType.ROOM)));
		// Tests one weapon
		Assert.assertTrue(testGame.getDeck().contains(new Card("Lindsay Bluth F�nke", CardType.PERSON)));
		// Tests one person
		Assert.assertTrue(testGame.getDeck().contains(new Card("drywall", CardType.WEAPON)));
	}
	
	@Test
	public void testDealingCards() {
		// Tests all cards are dealt
		testGame.loadDeck();
		testGame.loadPeople();
		testGame.deal();
		Assert.assertTrue(testGame.getDeck().size() > 0);
		boolean testCardsDealt = false;
		for(Card a : testGame.getDeck()) {
			if(testGame.getHumanPlayer().getCards().contains(a))
				testCardsDealt = true;
			else if(testGame.getClosetCards().contains(a))
				testCardsDealt = true;
			else {
				for(ComputerPlayer b : testGame.getCpuPlayers()) {
					if(b.getCards().contains(a))
						testCardsDealt = true;
				}
			}
		}
		Assert.assertTrue(testCardsDealt);
		// Tests all players have within 1 card of each other
		int numCards = testGame.getHumanPlayer().getCards().size();
		for(Player a : testGame.getCpuPlayers()) {
			Assert.assertTrue(Math.abs(a.getCards().size() - numCards) <= 1);
		}
		// Tests one card is not given to multiple players
		for(Card a : testGame.getDeck()) {
			int i = 0;
			if(testGame.getHumanPlayer().getCards().contains(a))
				i++;
			for(Player b : testGame.getCpuPlayers()) {
				if(b.getCards().contains(a))
					i++;
			}
			if(testGame.getClosetCards().contains(a))
				i++;
			Assert.assertTrue(i == 1);
		}
	}

}
