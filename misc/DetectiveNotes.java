package misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class DetectiveNotes extends JDialog {
	
	public DetectiveNotes(ArrayList<Card> cards) {
		this.setLayout(new GridLayout(2, 3));
		JPanel peoplePan = new JPanel(new GridLayout(0, 2));
		JPanel personGuess = new JPanel();
		JPanel roomsPan = new JPanel(new GridLayout(0, 2));
		JPanel roomGuess = new JPanel();
		JPanel weaponsPan = new JPanel(new GridLayout(0, 2));
		JPanel weaponGuess = new JPanel();
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
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
}
