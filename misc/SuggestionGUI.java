package misc;

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
	private boolean ready;
	
	public SuggestionGUI(ArrayList<Card> cards) {
		JComboBox people = new JComboBox();
		JComboBox rooms = new JComboBox();
		JComboBox weapons = new JComboBox();
		
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
		ready = false;
	}
	
	public ArrayList<String> getSuggestion(String room) {
		ready = false;
		if(rooms.getItemCount() > 0)
			rooms.removeAll();
		rooms.addItem(room);
		this.setVisible(true);
		while(!ready);
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
				ready = true;
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}
}
