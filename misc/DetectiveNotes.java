package misc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;

import misc.Card.CardType;

public class DetectiveNotes extends JDialog {
	
	public DetectiveNotes(ArrayList<Card> cards) {
		this.setLayout(new GridLayout(3, 2));
		JPanel peoplePan = new JPanel(new GridLayout(0, 2));
		JPanel personGuess = new JPanel();
		JPanel roomsPan = new JPanel(new GridLayout(0, 2));
		JPanel roomGuess = new JPanel();
		JPanel weaponsPan = new JPanel(new GridLayout(0, 2));
		JPanel weaponGuess = new JPanel();
		JComboBox people = new JComboBox();
		JComboBox rooms = new JComboBox();
		JComboBox weapons = new JComboBox();
		for(Card a: cards) {
			if(a.getCardType() == CardType.PERSON) {
				peoplePan.add(new JCheckBox(a.getName()));
				people.addItem(a.getName());
			} else if(a.getCardType() == CardType.ROOM) {
				roomsPan.add(new JCheckBox(a.getName()));
				rooms.addItem(a.getName());
			} else if(a.getCardType() == CardType.WEAPON) {
				weaponsPan.add(new JCheckBox(a.getName()));
				weapons.addItem(a.getName());
			}
		}
		personGuess.add(people, BorderLayout.CENTER);
		roomGuess.add(rooms, BorderLayout.CENTER);
		weaponGuess.add(weapons, BorderLayout.CENTER);
		Border blackLine = BorderFactory.createLineBorder(Color.BLACK);
		peoplePan.setBorder(BorderFactory.createTitledBorder(blackLine, "People"));
		personGuess.setBorder(BorderFactory.createTitledBorder(blackLine, "Person Guess"));
		roomsPan.setBorder(BorderFactory.createTitledBorder(blackLine, "Rooms"));
		roomGuess.setBorder(BorderFactory.createTitledBorder(blackLine, "Room Guess"));
		weaponsPan.setBorder(BorderFactory.createTitledBorder(blackLine, "Weapons"));
		weaponGuess.setBorder(BorderFactory.createTitledBorder(blackLine, "Weapon Guess"));
		this.add(peoplePan);
		this.add(personGuess);
		this.add(roomsPan);
		this.add(roomGuess);
		this.add(weaponsPan);
		this.add(weaponGuess);
		this.setSize(new Dimension(600, 800));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
}
