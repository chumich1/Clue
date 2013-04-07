package misc;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;

public class WelcomeSplash extends JDialog {
	
	public WelcomeSplash(Player human) {
		JLabel welcome = new JLabel("You are " + human.getName() + ", press Next Player to begin play");
		JButton ok = createFileExitItem();
		this.setLayout(new GridLayout(0,1));
		add(welcome);
		add(ok);
		this.setSize(new Dimension(300, 150));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}
	
	private JButton createFileExitItem() {
		JButton item = new JButton("OK");
		class MenuItemListener implements ActionListener {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}
	
}
