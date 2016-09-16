package gui;

import game.Game;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import network.LobbyRequest;
import network.LobbyRequest.ChangeArenaRequest;
import network.LobbyRequest.ChangeCharacterRequest;
import network.LobbyRequest.ChatRequest;
import network.LobbyRequest.LobbyInformationPacket;
import network.LobbyRequest.NewPlayerRequest;
import network.LobbyRequest.PlayerLeaveRequest;
import network.LobbyRequest.StartGameRequest;
import network.LobbyRequest.SwitchTeamRequest;
import network.LobbyRequest.ToggleReadyRequest;
import network.LobbyServer;
import sound.AudioManager;
import sound.MusicPlayer;
import character.CharacterFactory;

/**
 * GUI view to show the lobby screen where the player waits before launching the game.
 * 
 * @author Anh Pham
 * @author Shobitha Shivakumar
 */
public class LobbyScreen extends AbstractScreen implements ActionListener {

	private static final long serialVersionUID = -5689724117739992298L;
	private LobbyServer lobbyServer;
	private Socket socket;
	private JButton playButton;
	private JButton readyButton;

	private JButton team1Button;
	private JButton team2Button;

	private String arenaName;
	private JLabel typeIcon;
	private JLabel typeName;
	private JButton left;
	private JButton right;
	private JButton switchTeam;

	private TeamListModel team1Model;
	private TeamListModel team2Model;

	// place holder, use each type's icon later
	private final ClientPlayer clientPlayer;
	private List<ClientPlayer> team1;
	private List<ClientPlayer> team2;
	//private String config = "";//"Settings shown here\nSetting1\nSetting2\nSetting3"; 

	private int currentType = 0;

	private ChatPanel chatPanel;

	/**
	 * Creates a new LobbyScreen used in the game loop
	 * 
	 * @param socket
	 *            The socket of the lobby
	 * @param lip
	 *            The Lobby info packet of the lobby
	 * @param game
	 *            The game loop which the lobby screen will be used in
	 */
	public LobbyScreen(Socket socket, LobbyInformationPacket lip, Game game) {
		super(game);
		useDefaultBackground();
		this.socket = socket;
		initLobbyInfo(lip);
		clientPlayer = findPlayer(lip.id);
		initUI(false);
		new Thread(requestListener).start();
	}

	/**
	 * Creates a new LobbyScreen used in the game loop
	 * 
	 * @param lobbyServer
	 *            The lobby server of the lobby
	 * @param socket
	 *            The socket of the lobby
	 * @param lip
	 *            The Lobby info packet of the lobby
	 * @param game
	 *            The game loop which the lobby screen will be used in
	 */
	public LobbyScreen(LobbyServer lobbyServer, Socket socket, LobbyInformationPacket lip, Game game) {
		super(game);
		useDefaultBackground();

		this.lobbyServer = lobbyServer;
		this.socket = socket;
		initLobbyInfo(lip);
		clientPlayer = findPlayer(lip.id);
		initUI(true);
		new Thread(requestListener).start();
	}

	/**
	 * Initialise the lobby info
	 * 
	 * @param lip
	 *            The lobby information packet of the lobby
	 */
	private void initLobbyInfo(LobbyInformationPacket lip) {
		team1 = new ArrayList<ClientPlayer>();
		team2 = new ArrayList<ClientPlayer>();
		arenaName = lip.gameConfig.arena;
		for (ClientPlayer p : lip.clientPlayers) {
			if (p.team == 0) {
				team1.add(p);
			} else {
				team2.add(p);
			}
		}
	}

	/**
	 * Initialise the UI of the Lobby screen
	 * 
	 * @param isHost
	 *            True if you are the host
	 */
	private void initUI(boolean isHost) {
		// border width
		int bw = game.getHeight() / 5;
		this.setBorder(new EmptyBorder(bw, bw, bw / 2, bw));
		// team panels
		JPanel teamPanel = new JPanel();
		teamPanel.setOpaque(false);
		// teamPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		teamPanel.setLayout(new GridBagLayout());
		teamPanel.setMaximumSize(new Dimension(game.getWidth() * 7 / 10, game.getHeight() * 8 / 10));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);

		c.weightx = 1;
		team1Button = GUIFactory.getStyledButton("  Team 1");
		team1Button.addActionListener(this);
		teamPanel.add(team1Button, c);
		c.gridx = 1;
		team2Button = GUIFactory.getStyledButton("  Team 2");
		team2Button.addActionListener(this);
		teamPanel.add(team2Button, c);
		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		teamPanel.add(GUIFactory.getStyledSeparator(), c);
		c.gridy++;
		c.gridwidth = 1;
		c.weighty = 1;

		team1Model = new TeamListModel(team1);
		final JList<ClientPlayer> team1List = new JList<ClientPlayer>(team1Model);

		team1List.setOpaque(false);
		team1List.setCellRenderer(playerRenderer);
		c.anchor = GridBagConstraints.NORTH;
		teamPanel.add(team1List, c);

		c.gridx = 1;
		team2Model = new TeamListModel(team2);
		final JList<ClientPlayer> team2List = new JList<ClientPlayer>(team2Model);
		team2List.setOpaque(false);
		team2List.setCellRenderer(playerRenderer);
		teamPanel.add(team2List, c);

		c.gridx = 0;
		c.weighty = 0;
		c.gridy++;
		c.gridwidth = 2;
		teamPanel.add(GUIFactory.getStyledSeparator(), c);

		c.gridy++;

		JPanel settingPanel = GUIFactory.getTransparentPanel();

		JPanel characterPanel = GUIFactory.getTransparentPanel();
		// characterPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));

		left = GUIFactory.getStyledFunctionButton("<");
		right = GUIFactory.getStyledFunctionButton(">");
		left.addActionListener(this);
		right.addActionListener(this);
		characterPanel.add(left);
		// place holder
		JPanel selectedCharacter = GUIFactory.getTransparentPanel();
		typeIcon = new JLabel();
		selectedCharacter.add(typeIcon);
		typeName = GUIFactory.getStyledLabel("");
		selectedCharacter.add(typeName);
		setTypeSelection(0);
		characterPanel.add(selectedCharacter);
		characterPanel.add(right);

		settingPanel.add(characterPanel);
		settingPanel.add(Box.createHorizontalStrut(50));
		// settingPanel.add(GUIFactory.getStyledLabel("(Map goes here)"));
		// place holder for map
		JLabel map = null;
		try {
			Image mapImage = ImageIO.read(new FileInputStream("resource/map/" + arenaName + ".bmp"));
			Image scaledMap = mapImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
			map = new JLabel(new ImageIcon(scaledMap));
		} catch (IOException e) {
			System.out.println("Error loading map image");
			e.printStackTrace();
			map = new JLabel("Error loading map");
		}
		settingPanel.add(map);
		teamPanel.add(settingPanel, c);

		c.gridy++;
		teamPanel.add(GUIFactory.getStyledSeparator(), c);

		c.gridy++;
		c.weighty = 0.5;
		c.anchor = GridBagConstraints.SOUTH;
		c.fill = GridBagConstraints.BOTH;
		chatPanel = new ChatPanel();
		// chatPanel.getTextArea().setRows(5);

		teamPanel.add(chatPanel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.gridy++;
		teamPanel.add(GUIFactory.getStyledSeparator(), c);

		c.gridy++;

		JPanel buttonPanel = GUIFactory.getTransparentPanel();
		switchTeam = GUIFactory.getStyledFunctionButton("Switch team");
		switchTeam.addActionListener(this);
		buttonPanel.add(switchTeam);
		if (isHost) {
			// THE HOST CAN START GAME AND CHANGE SETTINGS
			playButton = GUIFactory.getStyledFunctionButton("Play");
			playButton.addActionListener(this);
			buttonPanel.add(playButton);
		}
		readyButton = GUIFactory.getStyledFunctionButton("Ready");
		readyButton.addActionListener(this);
		buttonPanel.add(readyButton);

		teamPanel.add(buttonPanel, c);
		chatPanel.getInputLabel().setText(clientPlayer.name + ": ");
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		this.add(teamPanel);
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "doSomething");
		this.getActionMap().put("doSomething", new AbstractAction() {
			private static final long serialVersionUID = 399731857081319049L;

			@Override
	        public void actionPerformed(ActionEvent arg0) {
	        	if (chatPanel.isTyping()&&!chatPanel.getInput().equals("")) {
    				sendRequest(new ChatRequest(clientPlayer.id,chatPanel.getInput()));
    				chatPanel.resetInput();
    				chatPanel.getScroller().getVerticalScrollBar().setValue(chatPanel.getTextArea().getHeight());
    			} else {
    				chatPanel.getScroller().getVerticalScrollBar().setValue(chatPanel.getTextArea().getHeight());
    				chatPanel.startTyping();
    			}
	        }
	    });
	    
	    // add the ability to kick other players
	    if (isHost) {
    	    team1List.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent event) {
                    int index = team1List.locationToIndex(event.getPoint());
                    lobbyServer.removePlayer(team1List.getModel().getElementAt(index).id);
                }
            });
    	    team2List.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent event) {
                    int index = team2List.locationToIndex(event.getPoint());
                    lobbyServer.removePlayer(team2List.getModel().getElementAt(index).id);
                }
            });
	    }
	}

	@Override
	public void update() {}

	// TODO also load image / stuff
	// not sure if the host should be allowed to change arena in the lobby
	// or just let him choose it when creating the game (more commonly seen)
	// -> simplicity -> just let him choose arena & settings at room creation
	/**
	 * Loads the arena from a specified name
	 * 
	 * @param name
	 *            The name of the arena
	 */
	private void loadArena(String name) {
		arenaName = name;
	}

	/**
	 * Sets the Type selection
	 * 
	 * @param type
	 *            The type
	 */
	private void setTypeSelection(int type) {
		final int color = 1;
		typeIcon.setIcon(new ImageIcon(CharacterFactory.getImage(type, color)));
		typeName.setText(CharacterFactory.getName(type));
		sendRequest(new ChangeCharacterRequest(clientPlayer.id, currentType));
	}

	/**
	 * Finds a player on the lobby
	 * 
	 * @param i
	 *            The id of the player
	 * @return Returns the ClientPlayer of the player to be found
	 */
	private ClientPlayer findPlayer(int i) {
		ClientPlayer p = null;
		for (ClientPlayer p2 : team1) {
			if (p2.id == i) {
				p = p2;
				break;
			}
		}
		if (p == null) {
			for (ClientPlayer p2 : team2) {
				if (p2.id == i) {
					p = p2;
					break;
				}
			}
		}
		return p;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == playButton) {
			try {
				// lobbyServer.changeArena("test40");
				lobbyServer.startGame();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (arg0.getSource() == switchTeam) {
			// atm only team game with 2 teams, so
			int to = clientPlayer.team == 0 ? 1 : 0;
			sendRequest(new SwitchTeamRequest(clientPlayer.id, to));
		} else if (arg0.getSource() == readyButton) {
			sendRequest(new ToggleReadyRequest(clientPlayer.id, !clientPlayer.active));
		} else if (arg0.getSource() == left) {
			currentType--;
			if (currentType < 0)
				currentType = CharacterFactory.MAXTYPE;
			setTypeSelection(currentType);
		} else if (arg0.getSource() == right) {
			currentType++;
			if (currentType > CharacterFactory.MAXTYPE)
				currentType = 0;
			setTypeSelection(currentType);
		} else if (arg0.getSource() == team1Button) {
			if (lobbyServer != null) {
				lobbyServer.addAIPlayer(0, clientPlayer.type);
			}
		} else if (arg0.getSource() == team2Button) {
			if (lobbyServer != null) {
				lobbyServer.addAIPlayer(1, clientPlayer.type);
			}
		}
	}

	/**
	 * Sends a request to the lobby
	 * 
	 * @param request
	 *            The request to be sent
	 */
	private void sendRequest(LobbyRequest request) {
		try {
			new ObjectOutputStream(socket.getOutputStream()).writeObject(request);
		} catch (IOException e) {
			System.out.println("Error sending request " + request.getClass() + " to network.");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onEscape() {
		super.onEscape();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (lobbyServer != null) {
			lobbyServer.end();
			lobbyServer = null;
		}
	}

	private Runnable requestListener = new Runnable() {
		@Override
		public void run() {
			while (true) {
				try {
					ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
					System.out.println("Message stream available: " + ois.available());
					Object message = ois.readObject();
					if (message instanceof ChangeArenaRequest) {
						loadArena(((ChangeArenaRequest) message).arenaName);
					} else if (message instanceof NewPlayerRequest) {
						NewPlayerRequest request = ((NewPlayerRequest) message);
						ClientPlayer p = request.newPlayer;
						if (p.team == 0) {
							team1.add(p);
							team1Model.invalidate();
						} else {
							team2.add(p);
							team2Model.invalidate();
						}
					} else if (message instanceof PlayerLeaveRequest) {
						PlayerLeaveRequest request = ((PlayerLeaveRequest) message);
						if (request.id == clientPlayer.id) {
							onEscape();
							break;
						}
						ClientPlayer p = findPlayer(request.id);
						team1.remove(p);
						team2.remove(p);
						team1Model.invalidate();
						team2Model.invalidate();
					} else if (message instanceof StartGameRequest) {
						AudioManager.stopMusic();
						game.setScreen(new GameScreen(game, clientPlayer.id, socket, arenaName, team1, team2));
						AudioManager.playMusic("menumusic.wav", MusicPlayer.DEFAULT_VOLUME - 15);
						break;
					} else if (message instanceof SwitchTeamRequest) {
						SwitchTeamRequest request = ((SwitchTeamRequest) message);
						List<ClientPlayer> to = request.desTeam == 0 ? team1 : team2;
						List<ClientPlayer> from = request.desTeam == 0 ? team2 : team1;
						ClientPlayer p = findPlayer(request.playerId);
						p.team = request.desTeam;
						from.remove(p);
						to.add(p);
						team1Model.invalidate();
						team2Model.invalidate();
					} else if (message instanceof ToggleReadyRequest) {
						ToggleReadyRequest request = ((ToggleReadyRequest) message);
						ClientPlayer p = findPlayer(request.id);
						p.active = request.ready;
						(p.team == 0 ? team1Model : team2Model).invalidate();
					} else if (message instanceof ChangeCharacterRequest) {
						ChangeCharacterRequest request = ((ChangeCharacterRequest) message);
						ClientPlayer p = findPlayer(request.playerId);
						p.type = request.typeId;
						(p.team == 0 ? team1Model : team2Model).invalidate();
					} else if (message instanceof ChatRequest) {
						ChatRequest request = ((ChatRequest) message);
						ClientPlayer p = findPlayer(request.id);
						chatPanel.addLine(p.name + ": " + request.line);
					}
				} catch (SocketException se) {
					onEscape();
					break;
				} catch (IOException | ClassNotFoundException e) {
					System.out.println("Error while receiving message from lobbyServer");
					System.out.println(e.getMessage());
				}
			}
		}
	};

	private ListCellRenderer<ClientPlayer> playerRenderer = new ListCellRenderer<ClientPlayer>() {

		@Override
		public Component getListCellRendererComponent(JList<? extends ClientPlayer> list, final ClientPlayer value, int index, boolean isSelected,
				boolean cellHasFocus) {
			ImageIcon icon = new ImageIcon(CharacterFactory.getImage(value.type, 1));
			JLabel player = new JLabel(value.name, icon, SwingConstants.LEFT);
			if (value.active)
				player.setForeground(Color.WHITE);
			else
				player.setForeground(Color.GRAY);
			player.setFont(GUIFactory.font_s);
			return player;
		}
	};

}
