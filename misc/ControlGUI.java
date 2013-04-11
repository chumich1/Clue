package misc;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ControlGUI extends JPanel {
	
	private JTextField whoseTurn;
	private JTextArea die, guess, guessResult;
	private ClueGame thisGame;

	public ControlGUI(ClueGame c) {
		createLayout();
		thisGame = c;
	}
	
	public void updateDie(int roll) {
		die.setText(Integer.toString(roll));
	}
	
	public void updateTurn(String name) {
		whoseTurn.setText(name);
	}
	
	public void updateGuess(String theGuess) {
		guess.setText(theGuess);
	}
	
	public void updateResult(String result) {
		guessResult.setText(result);
	}
	
	private void createLayout() {
		JPanel mainHolder = new JPanel();
		JPanel whosTurnStuff = new JPanel();
		JPanel dieStuff = new JPanel();
		JPanel guessStuff = new JPanel();
		JPanel guessResultStuff = new JPanel();
		add(mainHolder, BorderLayout.CENTER);
		mainHolder.setLayout(new GridLayout(2, 3));
		whosTurnStuff.setLayout(new GridLayout(2, 1));
		dieStuff.setLayout(new GridLayout(2, 1));
		guessStuff.setLayout(new GridLayout(2, 1));
		guessResultStuff.setLayout(new GridLayout(2, 1));
		JLabel whoseTurnLabel = new JLabel("Whose turn?");
		JLabel dieLabel = new JLabel("Die:");
		JLabel guessLabel = new JLabel("Guess:");
		JLabel guessResultLabel = new JLabel("Guess Result:");
		whoseTurn = new JTextField(30);
		JButton nextPlayerButton = createNextPlayerItem();
		JButton makeAccusationButton = new JButton("Make an accusation");
		die = new JTextArea(2, 1);
		guess = new JTextArea(2, 1);
		guessResult = new JTextArea(2, 1);
		whosTurnStuff.add(whoseTurnLabel);
		whosTurnStuff.add(whoseTurn);
		dieStuff.add(dieLabel);
		dieStuff.add(die);
		guessStuff.add(guessLabel);
		guessStuff.add(guess);
		guessResultStuff.add(guessResultLabel);
		guessResultStuff.add(guessResult);
		mainHolder.add(whosTurnStuff);
		mainHolder.add(nextPlayerButton);
		mainHolder.add(makeAccusationButton);
		mainHolder.add(dieStuff);
		mainHolder.add(guessStuff);
		mainHolder.add(guessResultStuff);
		this.setSize(200,50);
		setVisible(true);
	}

	public void setWhoseTurn(JTextField whoseTurn) {
		this.whoseTurn = whoseTurn;
	}

	public void setDie(JTextArea die) {
		this.die = die;
	}

	public void setGuess(JTextArea guess) {
		this.guess = guess;
	}

	public void setGuessResult(JTextArea guessResult) {
		this.guessResult = guessResult;
	}
	
	private JButton createNextPlayerItem() {
		JButton item = new JButton("Next Player");
		class MenuItemListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!thisGame.getBoard().isHumanTurn())
					thisGame.manageTurn();
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}

}
