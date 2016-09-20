package gui;

import game.Game;
import graphics.AnimationSystem;
import graphics.Renderer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import network.FullCharacterData;
import network.GameDataPackets.InputPacket;
import network.GameDataPackets.WorldStatePacket;
import network.GameEvent;
import network.GameEvent.*;
import network.PartialCharacterData;
import network.ProjectileData;
import sound.AudioManager;
import character.CharacterFactory;
import core.Arena;
import core.LineOfSight;

/**
 * The GUI where the match takes place, i.e. the arena with players.
 * 
 * @author Anh Pham
 * @author Connor Cartwright
 */
public class GameScreen extends JLayeredPane implements KeyListener, MouseListener, Runnable, MouseWheelListener {

	private static final long serialVersionUID = 1383374303503610168L;
	// private World world;
	private FullCharacterData mainCharacter = new FullCharacterData();
	private Game game;
	private Arena arena;
	private Camera camera;
	private final int id;
	private Socket socket;
	private InputPacket input = new InputPacket();
	private WorldStatePacket wsp;
	private AnimationSystem globalAnimations;
	private AnimationSystem visualAnimations;
	private AudioManager audioManager;
	private List<ClientPlayer> team1;
	private List<ClientPlayer> team2;
	private List<ClientPlayer> players;
	private TeamListModel team1Model;
	private TeamListModel team2Model;
	private JPanel scoreboard;
	private JLabel teamScore;
	private ChatPanel chatPanel;
	private JComponent minimap;
	private LineOfSight lineOfSight = new LineOfSight();
	private boolean playing = true;
	private double zoomLevel = 0;
	
	private double FPS = 0;

	/**
	 * Creates a new gamescreen where the match will take place
	 * 
	 * @param game
	 *            The game loop which will use the game screen
	 * @param id
	 *            The id of the local player
	 * @param socket
	 *            The socket to communicate with the server
	 * @param arenaName
	 *            The name of the arena for this game
	 * @param team1
	 *            The list of players on team 1
	 * @param team2
	 *            The list of players on team 2
	 * @throws IOException
	 *             Exception thrown on gamescreen
	 */
	public GameScreen(Game game, int id, Socket socket, String arenaName, List<ClientPlayer> team1, List<ClientPlayer> team2) throws IOException {
		super();
		this.game = game;
		this.setSize(game.getWidth(), game.getHeight());
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.setIgnoreRepaint(true);
		setFocusTraversalKeysEnabled(false);
		this.arena = new Arena(arenaName, true);
		this.team1 = team1;
		this.team2 = team2;
		this.players = new LinkedList<ClientPlayer>();
		players.addAll(team1);
		players.addAll(team2);
		for (ClientPlayer p : players) {
			p.character = new PartialCharacterData();
		}

		this.id = id;
		globalAnimations = new AnimationSystem();
		visualAnimations = new AnimationSystem();
		audioManager = new AudioManager();
		input = new InputPacket();
		camera = new Camera(arena);

		// TODO fix later, this is messy - create connection in one place and
		// read from it in another

		this.socket = socket;
		socket.setSoTimeout(5);
		setCursor(Renderer.createCursor());
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		initUI();
		Renderer.init();
		new Thread(this).start();
	}

	/**
	 * The game loop.
	 */
	@Override
	public void run() {
		long previous = System.currentTimeMillis();
		double lag = 0.0;
		long totalTime = 0;
		int frameCount = 0;
		final int MAX_FRAME_COUNT = 50;

		while (playing) {

			long current = System.currentTimeMillis();
			long elapsed = current - previous;
			previous = current;
			lag += elapsed;

			//System.out.println("Lag: " + lag);
			while (lag >= Game.MS_PER_UPDATE) {
				update();
				lag -= Game.MS_PER_UPDATE;
			}
			//System.out.println("Calling repaint");
			repaint();

			long waitTime = Game.MS_PER_UPDATE - (System.currentTimeMillis() - current);
			//System.out.println("Waiting for " + waitTime + " milisecs");
			try {
				Thread.sleep(waitTime);
			} catch (Exception e) {}

			totalTime += System.currentTimeMillis() - current;

			frameCount++;
			if (frameCount == MAX_FRAME_COUNT) {
				FPS = (1000.0 * frameCount) / totalTime;
				frameCount = 0;
				totalTime = 0;
			}
			///FPS = averageFPS;
		}
	}

	/**
	 * Update the game
	 */
	public void update() {
		//System.out.println("Game update");
		sendInput();
		input.chatText = null;
		WorldStatePacket wsp = receiveState();

		// update state
		// TODO this queue should be used for interpolation
		// at the moment it's just a placeholder and keeps exactly 5 past states
		// (around 100ms delay)
		// while (wsps.size() > 0) {
		// wsp = wsps.poll();
		// }
		updateCursor();
		globalAnimations.update();
		visualAnimations.update();
		audioManager.update();
		if (wsp != null) {
			this.wsp = wsp;
			for (ClientPlayer p : players) {
				p.active = false;
			}
			for (PartialCharacterData cdata : wsp.characters) {
				ClientPlayer p = Utils.findPlayer(players, cdata.id);
				if (p != null) {
					p.character = cdata;
					p.active = true;
				}
			}
			mainCharacter = wsp.player;
			mainCharacter.direction = (float) Math.atan2(mainCharacter.y - input.cy, input.cx - mainCharacter.x);

			camera.update(this, mainCharacter,input);
			for (GameEvent e : wsp.events) {
				listener.onEventReceived(e);
			}
			for (String s : wsp.chatTexts) {
				chatPanel.addLine(s);
			}
		}

	}

	/**
	 * Initialises the UI
	 */
	private void initUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		teamScore = GUIFactory.getStyledLabel("0 - 0");
		teamScore.setHorizontalAlignment(SwingConstants.CENTER);
		teamScore.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(teamScore);
		scoreboard = GUIFactory.getTransparentPanel();
		team1Model = new TeamListModel(team1);
		team2Model = new TeamListModel(team2);

		JList<ClientPlayer> team1List = new JList<ClientPlayer>(team1Model);
		JList<ClientPlayer> team2List = new JList<ClientPlayer>(team2Model);
		team1List.setCellRenderer(playerRenderer);
		team2List.setCellRenderer(playerRenderer);
		team1List.setOpaque(false);
		team2List.setOpaque(false);

		scoreboard.add(team1List);
		scoreboard.add(team2List);
		this.add(scoreboard);
		scoreboard.setVisible(false);

		minimap = new Minimap(arena, id, players);
		this.add(minimap);
		chatPanel = new ChatPanel();
		chatPanel.setVisible(false);
		chatPanel.getTextArea().setRows(15);
		chatPanel.setMaximumSize(new Dimension(800, 300));
		chatPanel.getInputLabel().setText(Utils.findPlayer(players, id).name + ": ");
		this.add(chatPanel);
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "doSomething");
		this.getActionMap().put("doSomething", new AbstractAction() {
			private static final long serialVersionUID = -6566354847061381139L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (chatPanel.isTyping()) {
					if (!chatPanel.getInput().equals("")) {
						input.chatText = chatPanel.getInput();
						chatPanel.resetInput();
					}
					chatPanel.setVisible(false);
				} else {
					chatPanel.setVisible(true);
					chatPanel.startTyping();
				}
				chatPanel.getScroller().getVerticalScrollBar().setValue(chatPanel.getTextArea().getHeight());
			}
		});

	}

	/**
	 * Updates the cursor
	 */
	private void updateCursor() {
		Point cursorOnScreen = MouseInfo.getPointerInfo().getLocation();
		Point gameOnScreen = getLocationOnScreen();
		input.cx = (float)Renderer.toMeter(cursorOnScreen.x - gameOnScreen.x + camera.getTopLeftXPixel(this));
		input.cy = (float)Renderer.toMeter(cursorOnScreen.y - gameOnScreen.y + camera.getTopLeftYPixel(this));
	}

	/**
	 * Send an input of a player to gamescreen
	 */
	private void sendInput() {
		// send input
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			input.time = System.currentTimeMillis();
			out.writeObject(input);
		} catch (IOException e) {
			System.out.println("Exception when trying to send input to network");
			e.printStackTrace();
		}
	}

	/**
	 * Receives the state of the gamescreen
	 * 
	 * @return Returns the WorldStatePacket of the gamescreen
	 */
	private WorldStatePacket receiveState() {
		// receive state
		WorldStatePacket wsp = null;

		try {
			InputStream in = socket.getInputStream();
			while (in.available() > 1000) {
				wsp = (WorldStatePacket) new ObjectInputStream(in).readObject();
			}
		} catch (IOException e) {
			System.out.println("Timeout when reading data from network");
		} catch (ClassNotFoundException e) {}

		return wsp;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//System.out.println("Game paint");
		// render background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		// render HUD
		// render world
		int transX = camera.getTopLeftXPixel(this);
		int transY = camera.getTopLeftYPixel(this);
		g.translate(-transX, -transY);

		Graphics2D g2d = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHints(rh);

		if (wsp != null) {
			// render the dark part
			Shape los = lineOfSight.generateLoS(mainCharacter, arena);
			
			Renderer.renderDark(g2d,arena,camera.getDrawArea(this));
			Renderer.renderLOS(g2d, los);
			// Renderer.renderArenaBackground(g2d, camera.getDrawArea(this));
			globalAnimations.render(g2d);

			// render the light part
			g2d.clip(los);

			//Renderer.render(g2d, arena,getCharacterVisionBox(mainCharacter.x, mainCharacter.y, (int)(mainCharacter.viewRange+0.5)));

			ClientPlayer localPlayer = Utils.findPlayer(players, id);

			for (ClientPlayer data : players) {
				if (data.id != id && data.active) {
					PartialCharacterData ch = data.character;
					Renderer.renderCharacter(g2d,ch.x, ch.y, ch.direction, ch.radius, data.type, data.team == localPlayer.team);
				}
			}

			for (ProjectileData data : wsp.projectiles) {
				Renderer.renderProjectile(g2d,data);
			}

			visualAnimations.render(g2d);
			// g2d.drawImage(light,mainCharacter.x-mainCharacter.viewRange,mainCharacter.y-mainCharacter.viewRange,mainCharacter.viewRange*2,mainCharacter.viewRange*2,
			// null);
			g2d.setClip(null);

			Renderer.renderMainCharacter(g2d, mainCharacter, localPlayer.type);
		}
		Renderer.renderCrosshair(g2d,input.cx,input.cy,mainCharacter.crosshairSize);
		g.translate(transX, transY);
		g2d.drawString("FPS: "+FPS, 10, 10);
	}

	private Rectangle2D getCharacterVisionBox(double x, double y, double viewRange) {
		return new Rectangle2D.Double(x - viewRange, y - viewRange, viewRange * 2, viewRange * 2);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// if ESC key show option menu or sth

		// if tab/some other key show detailed scoreboard
		if (e.getKeyCode() == KeyEvent.VK_TAB)
			this.scoreboard.setVisible(true);

		// if enter enable chat

		// else pass the event to the mainCharacter
		// TODO CLIENT: send event to network
		// TODO SERVER: receive event and process
		keyChanged(e, true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// if tab/some other key hide detailed scoreboard
		if (e.getKeyCode() == KeyEvent.VK_TAB)
			this.scoreboard.setVisible(false);

		// else pass the event to the mainCharacter
		keyChanged(e, false);
	}

	/**
	 * Records when a key event happens
	 * 
	 * @param e
	 *            The key pressed
	 * @param b
	 *            The boolean of the key event
	 */
	private void keyChanged(KeyEvent e, boolean b) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_SHIFT:
				// run
				input.running = b;
				break;

			case KeyEvent.VK_C:
				// move up
				input.fire2 = b;
				break;
				
			case KeyEvent.VK_W:
				// move up
				input.top = b;
				break;

			case KeyEvent.VK_S:
				// move down
				input.down = b;
				break;

			case KeyEvent.VK_A:
				// move left
				input.left = b;
				break;

			case KeyEvent.VK_D:
				// move right
				input.right = b;
				break;
			case KeyEvent.VK_CONTROL:
				input.sneaking = b;
				break;
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent event) {}

	@Override
	public void mousePressed(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			input.fire1 = true;
		}/* else if (SwingUtilities.isRightMouseButton(event)) {
			input.fire2 = true;
		}*/
	}


	@Override
	public void mouseReleased(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			input.fire1 = false;
		}/* else if (SwingUtilities.isRightMouseButton(event)) {
			input.fire2 = false;
		}*/
	}
	
	private ListCellRenderer<ClientPlayer> playerRenderer = new ListCellRenderer<ClientPlayer>() {

		@Override
		public Component getListCellRendererComponent(JList<? extends ClientPlayer> list, ClientPlayer value, int index, boolean isSelected,
				boolean cellHasFocus) {
			// TODO Auto-generated method stub
			String text = value.name + "     " + value.kills + "     " + value.deaths;
			ClientPlayer localPlayer = Utils.findPlayer(players, id);
			JLabel player = new JLabel(text, new ImageIcon(CharacterFactory.getImage(value.type, value.team == localPlayer.team ? 1 : 0)),
					SwingConstants.LEFT);
			player.setForeground(Color.WHITE);
			player.setFont(GUIFactory.font_s);
			return player;
		}
	};


	private GameEventListener listener = new GameEventListener() {
		@Override
		public void onEventReceived(GameEvent event) {
			if (event instanceof PlayerDieEvent) {
				PlayerDieEvent e = (PlayerDieEvent) event;
				ClientPlayer killer = Utils.findPlayer(players, e.killerID);
				ClientPlayer killed = Utils.findPlayer(players, e.killedID);

				killed.deaths++;
				if (killer.team != killed.team) {
					killer.kills++;
				} else {
					killer.kills--;
				}
				team1Model.invalidate();
				team2Model.invalidate();
			} else if (event instanceof ScoreChangedEvent) {
				final ScoreChangedEvent e = (ScoreChangedEvent) event;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						teamScore.setText(e.team1Score + " - " + e.team2Score);
					}
				});
			} else if (event instanceof GunShotEvent) {
				GunShotEvent e = (GunShotEvent) event;
				float noise = audioManager.getWeaponNoise(e.weaponId);
				//System.out.println("A gunshot (noise:" + noise + ") at " + e.x + "," + e.y);
				visualAnimations.addShotAnimation(e.x, e.y, e.direction);

				float volume = core.Utils.getVolumeAtDistance(noise, Point2D.distance(e.x, e.y, mainCharacter.x, mainCharacter.y));
				if (volume >= 0) {
					globalAnimations.addNoiseAnimation(e.x, e.y, noise);
					audioManager.playWeaponSound(e.weaponId, volume);
				}
			} else if (event instanceof BulletHitWallEvent) {
				BulletHitWallEvent e = (BulletHitWallEvent) event;
				//System.out.println("A bullet hits wall at " + e.x + "," + e.y);
				globalAnimations.addNoiseAnimation(e.x, e.y, 20);
				visualAnimations.addBulletWallAnimation(e.x, e.y);
			} else if (event instanceof BulletHitPlayerEvent) {
				BulletHitPlayerEvent e = (BulletHitPlayerEvent) event;
				//System.out.println("A bullet hits a character at " + e.x + "," + e.y);
				globalAnimations.addNoiseAnimation(e.x, e.y, 10);
				visualAnimations.addBloodAnimation(e.x, e.y);
			} else if (event instanceof FootStepEvent) {
				FootStepEvent e = (FootStepEvent) event;
				//System.out.println("A footstep at " + e.x + "," + e.y);
				float volume = core.Utils.getVolumeAtDistance(e.noise, Point2D.distance(e.x, e.y, mainCharacter.x, mainCharacter.y));
				globalAnimations.addNoiseAnimation(e.x, e.y, e.noise);
				audioManager.playFootstepSound(volume);
			} else if (event instanceof PowerUpPickedUpEvent) {
				//PowerUpPickedUpEvent e = (PowerUpPickedUpEvent) event;
				//System.out.println("Picked up power up at " + e.x + "," + e.y);
				audioManager.playPowerUpSound(30);
			} else if (event instanceof GameEndEvent) {
				// TODO back to lobby screen
				playing = false;
				game.setScreen(new MainMenuScreen(game));
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (event instanceof GrenadeExplodeEvent) {
				GrenadeExplodeEvent e = (GrenadeExplodeEvent) event;
				//System.out.println("A footstep at " + e.x + "," + e.y);
				globalAnimations.addNoiseAnimation(e.x, e.y, 100);
			} else if (event instanceof EnemyInfoEvent) {
				
			}
		}
	};
	public static final double DEFAULT_PPM = 12;
	public static double ppm = GameScreen.DEFAULT_PPM;

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomLevel = Math.max(-0.5,Math.min(0.5,zoomLevel + 0.05*e.getPreciseWheelRotation()));
		ppm = DEFAULT_PPM*(1+zoomLevel);
	}
}
