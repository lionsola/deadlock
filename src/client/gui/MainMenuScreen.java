package client.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Main menu screen.
 * 
 * @author Shobitha Shivakumar
 */
public class MainMenuScreen extends AbstractScreen implements ActionListener {

	private static final long serialVersionUID = 3518230790184422507L;
	private JPanel menuPanel;
	private JButton hostButton;
	private JButton joinButton;
	private JButton quitButton;

	/**
	 * Creates a new main menu screen passed with the running game object.
	 * 
	 * @param game
	 *            the current running game
	 */
	public MainMenuScreen(GameWindow game) {
		super(game);
		useDefaultBackground();
		menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel,BoxLayout.Y_AXIS));
		menuPanel.setMaximumSize(new Dimension(400, 600));
		menuPanel.setOpaque(false);
		

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.LINE_END;
		this.add(menuPanel, c);

		hostButton = GUIFactory.getStyledFunctionButton("Host");
		joinButton = GUIFactory.getStyledFunctionButton("Join");
		//settingsButton = GUIFactory.getStyledFunctionButton("Settings");
		quitButton = GUIFactory.getStyledFunctionButton("Quit");

		hostButton.addActionListener(this);
		menuPanel.add(hostButton);
		
		menuPanel.add(Box.createVerticalStrut(10));
		
		joinButton.addActionListener(this);
		menuPanel.add(joinButton);
		//initButton(settingsButton);
		menuPanel.add(Box.createVerticalStrut(10));
		
		quitButton.addActionListener(this);
		menuPanel.add(quitButton);

	}

	/**
	 * Initialise the buttons
	 * 
	 * @param button
	 *            the button to be initialised
	 */
	private void initButton(JButton button) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == hostButton) {
			goToPlay();
		} else if (e.getSource() == joinButton) {
			goToHelp();
		} else if (e.getSource() == quitButton) {
			System.exit(0);
		}
	}

	/**
	 * Go to the play screen
	 */
	private void goToPlay() {
		game.setScreen(new HostScreen(game));
	}

	/*
	 * Go to the help screen
	 */
	private void goToHelp() {
		game.setScreen(new JoinScreen(game));
	}

}
