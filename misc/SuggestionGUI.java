package misc;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import misc.Card.CardType;

public class SuggestionGUI extends JDialog {
	
	private JComboBox people;
	private JComboBox rooms;
	private JComboBox weapons;
	private JButton submit;
	
	public SuggestionGUI(ArrayList<Card> cards) {
		this.setLayout(new GridLayout(0, 1));
		people = new JComboBox();
		rooms = new JComboBox();
		weapons = new JComboBox();
		submit = createGetGuessButton();
		for(Card a: cards) {
			if(a.getCardType() == CardType.PERSON) {
				people.addItem(a.getName());
			} else if(a.getCardType() == CardType.WEAPON) {
				weapons.addItem(a.getName());
			}
		}
		this.add(people);
		this.add(rooms);
		this.add(weapons);
		this.add(submit);
		this.setSize(400, 400);
	}
	
	public void createSuggestion(String room) {
		rooms = new JComboBox();
		rooms.addItem(room);
		this.setVisible(true);
	}
	
	public ArrayList<String> getSuggestion() {
		ArrayList<String> guesses = new ArrayList<String>();
		guesses.add(people.getSelectedItem().toString());
		guesses.add(rooms.getSelectedItem().toString());
		guesses.add(weapons.getSelectedItem().toString());
		this.setVisible(false);
		return guesses;
	}
	
	private JButton createGetGuessButton() {
		JButton item = new JButton("Submit");
		class MenuItemListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}
}
