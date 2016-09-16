package gui;

import game.Game;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import network.LobbyRequest.LobbyInformationPacket;

/**
 * Shows the screen where a player can join a screen.
 * 
 * @author Anh Pham
 * @author Shobitha Shivakumar
 */
public class JoinScreen extends AbstractScreen implements ActionListener {

	private static final long serialVersionUID = -2027696345721368359L;
	private JButton joinButton;
	private JButton backButton;
	private JTextField name;
	private JTextField port;
	private JTextField ip;

	/**
	 * Creates a new join screen using the passed game object.
	 * 
	 * @param game
	 *            the current game object.
	 */
	public JoinScreen(Game game) {
		super(game);
		useDefaultBackground();
		this.setLayout(new GridBagLayout());
		joinButton = GUIFactory.getStyledFunctionButton("Join");
		backButton = GUIFactory.getStyledFunctionButton("Back");
		port = GUIFactory.getStyledTextField();
		name = GUIFactory.getStyledTextField();
		name.setText("Average Joe");
		port.setText("7777");
		ip = GUIFactory.getStyledTextField();
		JPanel menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.PAGE_AXIS));
		menuPanel.add(GUIFactory.getStyledLabel("Screen name:"));
		menuPanel.add(name);
		menuPanel.add(GUIFactory.getStyledLabel("IP Address:"));
		menuPanel.add(ip);
		menuPanel.add(GUIFactory.getStyledLabel("Port:"));
		menuPanel.add(port);
		menuPanel.setOpaque(false);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.add(backButton);
		buttonPanel.add(joinButton);

		joinButton.addActionListener(this);
		backButton.addActionListener(this);
		buttonPanel.setOpaque(false);

		menuPanel.add(buttonPanel);
		this.add(menuPanel);
	}

	/**
	 * Used to respond appropriately to client input on the front end.
	 * 
	 * @param e
	 *            an action event performed
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == joinButton) {
			if (name.getText().equals("")) {
				name.setBorder(new LineBorder(Color.RED, 2));
			} else {
				name.setBorder(new LineBorder(Color.WHITE));
			}
			if (port.getText().equals("")) {
				port.setBorder(new LineBorder(Color.RED, 2));
			}
			if (ip.getText().equals("")) {
				ip.setBorder(new LineBorder(Color.RED, 2));
			}
			if (!name.getText().equals("") && !port.getText().equals("") && !ip.getText().equals("")) {
				try {
					ip.setBorder(new LineBorder(Color.WHITE));
					port.setBorder(new LineBorder(Color.WHITE));
					int portnumber = Integer.parseInt(port.getText());
					Socket socket = new Socket(ip.getText(), portnumber);
					// send name
					new ObjectOutputStream(socket.getOutputStream()).writeObject(name.getText());
					LobbyInformationPacket lip = (LobbyInformationPacket) new ObjectInputStream(socket.getInputStream()).readObject();
					game.setScreen(new LobbyScreen(socket, lip, game));
				} catch (IOException e1) {
					ip.setBorder(new LineBorder(Color.RED, 2));
					port.setBorder(new LineBorder(Color.RED, 2));
					System.out.println("Error joining game at " + ip.getText() + ":" + port.getText());
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					ip.setBorder(new LineBorder(Color.RED, 2));
					port.setBorder(new LineBorder(Color.RED, 2));
					System.out.println("Error reading lobby information from network.");
					e1.printStackTrace();
				} catch (Exception e2) {
					ip.setBorder(new LineBorder(Color.RED, 2));
					port.setBorder(new LineBorder(Color.RED, 2));
				}
			}
		} else if (e.getSource() == backButton) {
			game.setScreen(new MainMenuScreen(game));
		}
	}

	/**
	 * Sets the name of the player
	 */
	@Override
	public void setName(String nam) {
		name.setName(nam);
	}

	/**
	 * Sets the port of the server you want to join
	 * 
	 * @param nam
	 *            the port number
	 */
	public void setPort(String nam) {
		port.setText(nam);
	}

}
