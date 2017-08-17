package client.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import editor.DataManager;
import server.network.LobbyServer;
import shared.network.Connection;
import shared.network.GameModeData;
import shared.network.LobbyRequest.LobbyInformationPacket;

/**
 * Screen which allows a player to host a game.
 * 
 * @author Madyan Al-Jazaeri
 * @author Anh D Pham
 * @author Shobitha Shivakumar
 */
public class HostScreen extends AbstractScreen implements ActionListener {

	private static final long serialVersionUID = -8617632006677046360L;
	// available maps
	public static final String[] MAP_LIST = {"alpha","house","house2","highstreet","highstreet2","test","mansion"};
	
	private int currentMode;
	private JLabel modeLabel;
	private JButton modeLeft;
	private JButton modeRight;
	
	
	private int currentMap;
	private Image[] scaledMap;
	private JLabel map;
	private JLabel mapLabel;
	private JButton left;
	private JButton right;
	
	private JButton hostButton;
	private JButton backButton;
	private JTextField port;
	private JTextField name;

	/**
	 * Creates a new Host Screen in the game loop.
	 * 
	 * @param game
	 *            The game loop which the host screen will be used in.
	 */
	public HostScreen(GameWindow game) {
		super(game);
		useDefaultBackground();
		initialiseMaps();
		this.setLayout(new GridBagLayout());

		// main panel
		JPanel menuPanel = GUIFactory.getTransparentPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.PAGE_AXIS));
		
		// button panel
		JPanel buttonPanel = GUIFactory.getTransparentPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));

		hostButton = GUIFactory.getStyledFunctionButton("Host");
		backButton = GUIFactory.getStyledFunctionButton("Back");
		port = GUIFactory.getStyledTextField();
		port.setText("7777");
		name = GUIFactory.getStyledTextField();
		name.setText("Host");
		map = new JLabel(new ImageIcon(scaledMap[currentMap]));
		mapLabel = GUIFactory.getStyledLabel("Map: " + MAP_LIST[currentMap]);
		mapLabel.setHorizontalAlignment(SwingConstants.CENTER);
		left = GUIFactory.getStyledFunctionButton("<");
		right = GUIFactory.getStyledFunctionButton(">");
		
		modeLeft = GUIFactory.getStyledFunctionButton("<");
		modeRight = GUIFactory.getStyledFunctionButton(">");
		modeLabel = GUIFactory.getStyledLabel("Mode: " + GameModeData.modes[currentMode].name);
		JPanel modePanel = GUIFactory.getTransparentPanel();
		modePanel.setLayout(new BorderLayout());
		modePanel.add(modeLeft, BorderLayout.WEST);
		modePanel.add(modeLabel, BorderLayout.CENTER);
		modePanel.add(modeRight, BorderLayout.EAST);
		
		
		// map selection panel
		JPanel mapPanel = GUIFactory.getTransparentPanel();
		mapPanel.setLayout(new BorderLayout());
		mapPanel.add(left, BorderLayout.WEST);
		mapPanel.add(map, BorderLayout.CENTER);
		mapPanel.add(mapLabel, BorderLayout.NORTH);
		mapPanel.add(right, BorderLayout.EAST);

		menuPanel.add(GUIFactory.getStyledLabel("Name:"));
		menuPanel.add(name);
		menuPanel.add(GUIFactory.getStyledLabel("Port:"));
		menuPanel.add(port);
		menuPanel.add(modePanel);
		menuPanel.add(mapPanel);

		buttonPanel.add(backButton);
		buttonPanel.add(hostButton);
		menuPanel.add(buttonPanel);
		this.add(menuPanel);
		this.setBackground(Color.BLACK);

		left.addActionListener(this);
		right.addActionListener(this);
		hostButton.addActionListener(this);
		backButton.addActionListener(this);

	}

	/**
	 * Loads the map images for each type of map.
	 */
	/**
	 * Initialise the maps that are to be displayed on the host screen.
	 */
	private void initialiseMaps() {
		currentMap = 0;
		scaledMap = new Image[MAP_LIST.length];
		try {
			for (int i = 0; i < MAP_LIST.length; i++) {
				Image mapImage = DataManager.loadImage("/map/" + MAP_LIST[i] + ".png");
				scaledMap[i] = mapImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			}
		} catch (IOException e) {
			System.err.println("Error while loading map images");
			e.printStackTrace();
			System.exit(-1);
		}
		GameModeData.initialize();
	}

	/***
	 * @param e
	 *            an action event that was performed, this method handles any client input on the
	 *            front end.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == hostButton) {
			if (name.getText().equals("")) {
				name.setBorder(new LineBorder(Color.RED, 2));
			} else {
				name.setBorder(new LineBorder(Color.WHITE));
			}
			if (port.getText().equals("")) {
				port.setBorder(new LineBorder(Color.RED, 2));
			}
			if (!name.getText().equals("") && !port.getText().equals("")) {
				try {
					// create new network
					port.setBorder(new LineBorder(Color.WHITE));
					int portnumber = Integer.parseInt(port.getText());
					
					LobbyServer lobbyServer = new LobbyServer(portnumber, currentMap);
					

					Socket socket = new Socket("localhost", portnumber);
					Connection connection = new Connection(socket);
					connection.send(name.getText());
					
					LobbyInformationPacket lip = (LobbyInformationPacket) connection.receive();
					game.setScreen(new LobbyScreen(lobbyServer, connection, lip, game));
				} catch (IOException e1) {
					port.setBorder(new LineBorder(Color.RED, 2));
					System.out.println("Error creating network on port " + port.getText());
					e1.printStackTrace();
				}
			}
		} else if (e.getSource() == left) {
			currentMap--;
			if (currentMap < 0) {
				currentMap = MAP_LIST.length - 1;
			}
			map.setIcon(new ImageIcon(scaledMap[currentMap]));
			mapLabel.setText("Map: " + MAP_LIST[currentMap]);
		} else if (e.getSource() == right) {
			currentMap++;
			if (currentMap > MAP_LIST.length - 1) {
				currentMap = 0;
			}
			map.setIcon(new ImageIcon(scaledMap[currentMap]));
			mapLabel.setText("Map: " + MAP_LIST[currentMap]);
		} else if (e.getSource() == backButton) {
			game.setScreen(new MainMenuScreen(game));
		} else if (e.getSource() == modeLeft) {
			currentMode--;
			if (currentMode <0) {
				currentMode = GameModeData.modes.length-1;
			}
			modeLabel.setText("Mode: " + GameModeData.modes[currentMode].name);
		} else if (e.getSource() == modeRight) {
			currentMode ++;
			if (currentMode > GameModeData.modes.length) {
				currentMode = 0;
			}
			modeLabel.setText("Mode: " + GameModeData.modes[currentMode].name);
		}
	}

}
