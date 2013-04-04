package misc;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import misc.Card.CardType;

public class MyCardsPanel extends JPanel {
	
	public MyCardsPanel(ArrayList<Card> cards){
		System.out.println(cards.size());
		this.setLayout(new GridLayout(0,1));
		JPanel people = new JPanel(new GridLayout(0, 1));
		JPanel rooms = new JPanel(new GridLayout(0, 1));
		JPanel weapons = new JPanel(new GridLayout(0, 1));
		for(Card c: cards){
			if(c.getCardType() == CardType.PERSON) {
				people.add(new JLabel(c.getName()));
			} else if(c.getCardType() == CardType.ROOM) {
				rooms.add(new JLabel(c.getName()));
			} else if(c.getCardType() == CardType.WEAPON) {
				weapons.add(new JLabel(c.getName()));
			}
		}
		
		Border blackLine = BorderFactory.createLineBorder(Color.BLACK);
		people.setBorder(BorderFactory.createTitledBorder(blackLine, "People"));
		rooms.setBorder(BorderFactory.createTitledBorder(blackLine, "Rooms"));
		weapons.setBorder(BorderFactory.createTitledBorder(blackLine, "Weapons"));
		
		this.add(people);
		this.add(rooms);
		this.add(weapons);
		
	
		
		
		
		
		
	}

}
