package client.gui;

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
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
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
import javax.swing.border.EmptyBorder;

import server.network.LobbyServer;
import shared.network.Connection;
import shared.network.LobbyRequest;
import shared.network.LobbyRequest.ChangeArenaRequest;
import shared.network.LobbyRequest.ChangeCharacterRequest;
import shared.network.LobbyRequest.ChangeSpawnRequest;
import shared.network.LobbyRequest.ChatRequest;
import shared.network.LobbyRequest.GameConfig;
import shared.network.LobbyRequest.LobbyInformationPacket;
import shared.network.LobbyRequest.NewPlayerRequest;
import shared.network.LobbyRequest.PlayerLeaveRequest;
import shared.network.LobbyRequest.StartGameRequest;
import shared.network.LobbyRequest.SwitchTeamRequest;
import shared.network.LobbyRequest.ToggleReadyRequest;
import editor.SpawnPoint;
import editor.SpawnPoint.CharType;

/**
 * GUI view to show the lobby screen where the player waits before launching the game.
 * 
 * @author Anh Pham
 * @author Shobitha Shivakumar
 */
public class LobbyScreen extends AbstractScreen implements ActionListener {

	private static final long serialVersionUID = -5689724117739992298L;
	private LobbyServer lobbyServer;
	private Connection connection;
	private JButton playButton;
	private JButton readyButton;

	private GameConfig config;
	private List<ClientPlayer> players;
	private JLabel typeIcon;
	private JLabel typeName;
	private JButton left;
	private JButton right;
	private JButton switchTeam;

	// place holder, use each type's icon later
	private final ClientPlayer clientPlayer;
	//private String config = "";//"Settings shown here\nSetting1\nSetting2\nSetting3"; 

	private int currentType = 0;

	private ChatPanel chatPanel;
	private HashMap<Integer,SpawnPanel> spawnPanels = new HashMap<Integer,SpawnPanel>();
	private List<ClientPlayer> idlePlayers = new LinkedList<ClientPlayer>();
	private TeamListModel idlePlayersModel;

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
	public LobbyScreen(LobbyServer lobbyServer, Connection connection, LobbyInformationPacket lip, GameWindow game) {
		super(game);
		this.lobbyServer = lobbyServer;
		useDefaultBackground();
		this.connection = connection;
		initLobbyInfo(lip);
		clientPlayer = findPlayer(lip.id);
		initUI(lobbyServer!=null);
		new Thread(requestListener).start();
	}

	/**
	 * Initialise the lobby info
	 * 
	 * @param lip
	 *            The lobby information packet of the lobby
	 */
	private void initLobbyInfo(LobbyInformationPacket lip) {
		config = lip.gameConfig;
		players = lip.clientPlayers;
		for (ClientPlayer p:players) {
			if (p.spawnId==-1) {
				idlePlayers.add(p);
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
		int bw = game.getHeight() / 6;
		this.setBorder(new EmptyBorder(bw, bw, bw / 2, bw));
		// team panels
		JPanel teamPanel = GUIFactory.getTransparentPanel();
		teamPanel.setLayout(new GridBagLayout());
		teamPanel.setMaximumSize(new Dimension(game.getWidth() * 7 / 10, game.getHeight() * 8 / 10));
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 5, 5, 5);
		c.gridwidth = 2;
		idlePlayersModel = new TeamListModel(idlePlayers);
		JList<ClientPlayer> idlePlayersList = new JList<ClientPlayer>(idlePlayersModel);
		idlePlayersList.setCellRenderer(new ListCellRenderer<ClientPlayer>() {
			@Override
			public Component getListCellRendererComponent(JList<? extends ClientPlayer> arg0, ClientPlayer arg1, int arg2, boolean arg3,
					boolean arg4) {
				JLabel label = new JLabel(arg1.name);
				GUIFactory.stylizeMenuComponent(label);
				label.setBorder(null);
				return label;
			}});
		JPanel idlePlayersPanel = GUIFactory.getTransparentPanel();
		JLabel up = new JLabel("Unassigned players: ");
		GUIFactory.stylizeMenuComponent(up);
		up.setBorder(null);
		idlePlayersPanel.add(up);
		idlePlayersPanel.add(idlePlayersList);
		idlePlayersPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				sendRequest(new LobbyRequest.ChangeSpawnRequest(clientPlayer.id, -1, false));
			}
		});
		GUIFactory.stylizeMenuComponent(idlePlayersPanel);
		teamPanel.add(idlePlayersPanel, c);
		
		c.gridy = 1;
		c.gridx = 0;
		teamPanel.add(GUIFactory.getStyledSeparator(), c);
		c.gridy++;
		c.weighty = 1;

		JPanel playerList = GUIFactory.getTransparentPanel();
		playerList.setLayout(new BoxLayout(playerList, BoxLayout.Y_AXIS));
		for(SpawnPoint sp:config.playableSpawns) {
			SpawnPanel panel = new SpawnPanel(sp);
			GUIFactory.stylizeMenuComponent(panel);
			spawnPanels.put(sp.getId(), panel);
			playerList.add(panel);
		}
		teamPanel.add(playerList, c);

		c.gridx = 0;
		c.weighty = 0;
		c.gridy++;
		c.gridwidth = 2;
		teamPanel.add(GUIFactory.getStyledSeparator(), c);

		JPanel settingPanel = GUIFactory.getTransparentPanel();
		settingPanel.setLayout(new BoxLayout(settingPanel, BoxLayout.Y_AXIS));
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
		characterPanel.add(selectedCharacter);
		characterPanel.add(right);

		settingPanel.add(characterPanel);
		settingPanel.add(Box.createHorizontalStrut(50));
		// settingPanel.add(GUIFactory.getStyledLabel("(Map goes here)"));
		// place holder for map
		JLabel map = null;
		try {
			BufferedImage mapImage = ImageIO.read(new FileInputStream("resource/map/" + HostScreen.MAP_LIST[config.arena] + ".png"));
			float ratio = 150f/Math.max(mapImage.getWidth(),mapImage.getHeight());
			map = new JLabel(new ImageIcon(mapImage.getScaledInstance((int)(mapImage.getWidth()*ratio),(int)(mapImage.getHeight()*ratio), Image.SCALE_SMOOTH)),JLabel.CENTER);
		} catch (IOException e) {
			System.out.println("Error loading map image");
			e.printStackTrace();
			map = new JLabel("Error loading map");
		}
		map.setAlignmentX(Component.CENTER_ALIGNMENT);
		settingPanel.add(map);
		GUIFactory.stylizeMenuComponent(settingPanel);
		//c.gridy++;
		//teamPanel.add(GUIFactory.getStyledSeparator(), c);

		chatPanel = new ChatPanel(5);
		GUIFactory.stylizeMenuComponent(chatPanel);
		chatPanel.getInputLabel().setText(clientPlayer.name + ": ");
		
		JPanel lower = GUIFactory.getTransparentPanel();
		lower.setLayout(new BoxLayout(lower,BoxLayout.X_AXIS));
		lower.add(chatPanel);
		lower.add(settingPanel);
		
		c.fill = GridBagConstraints.BOTH;
		c.gridy++;
		teamPanel.add(lower, c);
		
		c.gridwidth = 2;
		c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		c.gridy++;
		teamPanel.add(GUIFactory.getStyledSeparator(), c);

		c.gridy++;

		JPanel buttonPanel = GUIFactory.getTransparentPanel();
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
	}

	@Override
	public void update() {}

	/**
	 * Sets the Type selection
	 * 
	 * @param type
	 *            The type
	 */
	private void setTypeSelection(CharType type) {
		//typeIcon.setIcon(new ImageIcon(Sprite.getImage(type, clientPlayer.team)));
		typeName.setText(String.valueOf((char)type.id));
		sendRequest(new ChangeCharacterRequest(clientPlayer.id, type));
	}

	/**
	 * Finds a player on the lobby
	 * 
	 * @param i
	 *            The id of the player
	 * @return Returns the ClientPlayer of the player to be found
	 */
	private ClientPlayer findPlayer(int i) {
		for (ClientPlayer p : players) {
			if (p.id == i) {
				return p;
			}
		}
		return null;
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
		} else if (arg0.getSource() == readyButton && clientPlayer.spawnId!=-1) {
			sendRequest(new ToggleReadyRequest(clientPlayer.id, !clientPlayer.active));
		} else if (arg0.getSource() == left && clientPlayer.spawnId!=-1) {
			List<CharType> setups = spawnPanels.get(clientPlayer.spawnId).spawn.setups;
			currentType--;
			if (currentType < 0)
				currentType = setups.size()-1;
			setTypeSelection(setups.get(currentType));
		} else if (arg0.getSource() == right && clientPlayer.spawnId!=-1) {
			List<CharType> setups = spawnPanels.get(clientPlayer.spawnId).spawn.setups;
			currentType++;
			if (currentType >= setups.size())
				currentType = 0;
			setTypeSelection(setups.get(currentType));
		}
	}

	/**
	 * Sends a request to the lobby
	 * 
	 * @param request
	 *            The request to be sent
	 */
	private void sendRequest(LobbyRequest request) {
		connection.send(request);
	}

	@Override
	public void onEscape() {
		super.onEscape();
		try {
			connection.getSocket().close();
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
			while (!connection.getSocket().isClosed()) {
				try {
					//System.out.println("Message stream available: " + ois.available());
					Object message = connection.receive();
					if (message instanceof NewPlayerRequest) {
						NewPlayerRequest request = ((NewPlayerRequest) message);
						players.add(request.newPlayer);
						idlePlayers.add(request.newPlayer);
						idlePlayersModel.invalidate();
					} else if (message instanceof PlayerLeaveRequest) {
						PlayerLeaveRequest request = ((PlayerLeaveRequest) message);
						if (request.id == clientPlayer.id) {
							onEscape();
							break;
						}
						ClientPlayer p = findPlayer(request.id);
						players.remove(p);
					} else if (message instanceof StartGameRequest) {
						game.setScreen(new GameScreen(game, clientPlayer, connection, config.arena, players));
						break;
					} else if (message instanceof ChangeSpawnRequest) {
						ChangeSpawnRequest request = ((ChangeSpawnRequest) message);
						ClientPlayer p = findPlayer(request.playerId);
						
						if (request.successful) {
							// update the source
							if (p.spawnId==-1) {
								idlePlayers.remove(p);
								idlePlayersModel.invalidate();
							} else {
								SpawnPanel source = spawnPanels.get(p.spawnId);
								if (source.clientPlayer==p) {
									source.setPlayer(null);
								}
							}
							
							// update the destination
							if (request.spawnId==-1) {
								idlePlayers.add(p);
								idlePlayersModel.invalidate();
								p.team = -1;
								p.active = false;
								typeName.setText("?");
							} else {
								SpawnPanel target = spawnPanels.get(request.spawnId);
								target.setPlayer(p);
								p.team = target.spawn.team;
								p.type = target.spawn.setups.get(0);
								
								if (p==clientPlayer) {
									currentType = 0;
									setTypeSelection(target.spawn.setups.get(0));
								}
							}
							p.spawnId = request.spawnId;
							
						} else if (request.spawnId!=-1){
							chatPanel.addLine(p.name + " wants to switch place with " +
									spawnPanels.get(request.spawnId).clientPlayer.name);
						}
					} else if (message instanceof ToggleReadyRequest) {
						ToggleReadyRequest request = ((ToggleReadyRequest) message);
						ClientPlayer p = findPlayer(request.id);
						p.active = request.ready;
						spawnPanels.get(p.spawnId).update();
						// INVALIDATE
					} else if (message instanceof ChangeCharacterRequest) {
						ChangeCharacterRequest request = ((ChangeCharacterRequest) message);
						ClientPlayer p = findPlayer(request.playerId);
						p.type = request.typeId;
						// INVALIDATE
					} else if (message instanceof ChatRequest) {
						ChatRequest request = ((ChatRequest) message);
						ClientPlayer p = findPlayer(request.id);
						chatPanel.addLine(p.name + ": " + request.line);
					}
				} catch (SocketException se) {
					onEscape();
					break;
				} catch (IOException e) {
					System.out.println("Error while receiving message from lobbyServer");
					System.out.println(e.getMessage());
				}
			}
		}
	};

	class SpawnPanel extends JPanel {
		private static final long serialVersionUID = -7928714079976455722L;
		JLabel type;
		JLabel player;
		ClientPlayer clientPlayer;
		SpawnPoint spawn;

		public SpawnPanel(SpawnPoint sp) {
			spawn = sp;
			
			type = new JLabel("?");
			player = new JLabel(" ");
			GUIFactory.stylizeMenuComponent(type);
			GUIFactory.stylizeMenuComponent(player);
			
			add(type);
			add(player);
			
			if (sp.setups.size()==1) {
				setType(sp.setups.get(0));
			}
			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					ClientPlayer p = LobbyScreen.this.clientPlayer;
					if (p != clientPlayer) {
						sendRequest(new LobbyRequest.ChangeSpawnRequest(p.id, spawn.getId(), false));
					}
				}
			});
		}
		
		public void setType(CharType ct) {
			type.setText(String.valueOf((char)ct.id));
		}
		
		public void setPlayer(ClientPlayer p) {
			clientPlayer = p;
			if (p!=null) {
				player.setText(p.name);
				update();
			} else {
				player.setText(" ");
			}
		}
		
		public void update() {
			if (clientPlayer.active) {
				player.setForeground(Color.WHITE);
			} else {
				player.setForeground(Color.GRAY);
			}
		}
	}
}
