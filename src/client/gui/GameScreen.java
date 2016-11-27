package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import server.ability.Ability;
import server.weapon.Weapon;
import server.world.Arena;
import server.world.Thing;
import server.world.TriggerPreset;
import server.world.Terrain;
import server.world.Visibility;
import shared.network.Connection;
import shared.network.FullCharacterData;
import shared.network.CharData;
import shared.network.ProjectileData;
import shared.network.Vision;
import shared.network.GameDataPackets.InputPacket;
import shared.network.GameDataPackets.WorldStatePacket;
import shared.network.event.AnimationEvent;
import shared.network.event.GameEvent;
import shared.network.event.GameEvent.*;
import shared.network.event.SoundEvent;
import client.graphics.AnimationSystem;
import client.graphics.Renderer;
import client.image.SoftHardLightComposite;
import client.sound.AudioManager;
import editor.DataManager;

/**
 * The GUI where the match takes place, i.e. the arena with players.
 */
public class GameScreen extends JLayeredPane implements KeyListener, MouseListener, Runnable, MouseWheelListener {
	private static final long serialVersionUID = 1383374303503610168L;

	private FullCharacterData mainCharacter = new FullCharacterData();
	private GameWindow game;
	private Arena arena;
	private Camera camera;
	private final int id;
	private Connection connection;
	private InputPacket input = new InputPacket();
	private List<GameEvent> events = new LinkedList<GameEvent>();
	private WorldStatePacket currentState;
	private AnimationSystem nonvisualAnimations = new AnimationSystem();
	private AnimationSystem visualAnimations = new AnimationSystem();
	private AnimationSystem globalAnimations = new AnimationSystem();
	
	private AudioManager audioManager = new AudioManager();
	private List<ClientPlayer> team1;
	private List<ClientPlayer> team2;
	private List<ClientPlayer> players;
	
	private TeamListModel team1Model;
	private TeamListModel team2Model;
	private JPanel scoreboard;
	private JLabel teamScore;
	private ChatPanel chatPanel;
	private AbilityBar abilityBar;
	private JComponent minimap;
	private Visibility visibility = new Visibility();
	private Renderer renderer = new Renderer();
	private HashMap<Integer,Thing> objectTable;
	
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
	 * @param connection
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
	public GameScreen(GameWindow game, int id, Connection connection, String arenaName, List<ClientPlayer> team1, List<ClientPlayer> team2) throws IOException {
		super();
		
		
		setSize(game.getContentPane().getWidth(), game.getContentPane().getHeight());
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.setIgnoreRepaint(true);
		setFocusTraversalKeysEnabled(false);
		setCursor(Renderer.createCursor());
		addKeyListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		
		// Loading the arena
		Collection<Terrain> tileList = (List<Terrain>) DataManager.loadObject(DataManager.FILE_TILES);
		HashMap<Integer,Terrain> tileTable = DataManager.getTileMap(tileList);
		
		Collection<Thing> objectList = (List<Thing>) DataManager.loadObject(DataManager.FILE_OBJECTS);
		objectTable = DataManager.getObjectMap(objectList);
		
		Collection<TriggerPreset> triggerList = (Collection<TriggerPreset>) DataManager.loadObject(DataManager.FILE_TRIGGERS);
		HashMap<Integer,TriggerPreset> triggerTable = DataManager.getTriggerMap(triggerList);
		
		try {
			DataManager.loadTileGraphics(tileList);
		} catch (IOException e) {
			System.err.println("Error while loading tile images.");
			e.printStackTrace();
		}
		try {
			DataManager.loadObjectGraphics(objectList);
		} catch (IOException e) {
			System.err.println("Error while loading tile images.");
			e.printStackTrace();
		}
		this.arena = new Arena(arenaName, tileTable, objectTable,triggerTable);
		renderer.initArenaImages(arena);
		camera = new Camera(arena, this);
		
		// Initialise fields
		this.id = id;
		this.connection = connection;
		this.game = game;
		this.team1 = team1;
		this.team2 = team2;
		players = new LinkedList<ClientPlayer>();
		players.addAll(team1);
		players.addAll(team2);
		
		for (ClientPlayer p : players) {
			p.character = new CharData();
		}

		initUI();
		new Thread(this).start();
	}

	/**
	 * The main game loop.
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

			while (lag >= GameWindow.MS_PER_UPDATE) {
				update();
				lag -= GameWindow.MS_PER_UPDATE;
			}
			
			repaint();

			long waitTime = GameWindow.MS_PER_UPDATE - (System.currentTimeMillis() - current);
			
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
			
		}
	}

	/**
	 * Update the game
	 */
	public void update() {
		//System.out.println("GameWindow update");
		sendInput();
		input.chatText = null;
		WorldStatePacket wsp = receiveState();

		// update state
		// TODO NETWORK this queue should be used for interpolation
		// at the moment it's just a placeholder and keeps exactly 5 past states
		// (around 100ms delay)
		// while (wsps.size() > 0) {
		// wsp = wsps.poll();
		// }
		updateCursor();
		nonvisualAnimations.update();
		visualAnimations.update();
		globalAnimations.update();
		audioManager.update();
		if (wsp != null) {
			this.currentState = wsp;
			for (ClientPlayer p : players) {
				if (p.id!=id) {
					p.active = false;
				}
			}
			Utils.findPlayer(players, id).character = wsp.player;
			for (CharData cdata : wsp.characters) {
				ClientPlayer p = Utils.findPlayer(players, cdata.id);
				if (p != null) {
					p.character = cdata;
					p.active = true;
				}
			}
			for (ProjectileData data : wsp.projectiles) {
				visualAnimations.addProjectileTrail(data.x, data.y, data.prevX, data.prevY ,data.size);
			}
			mainCharacter = wsp.player;
			abilityBar.update(mainCharacter);
			//mainCharacter.direction = (float) Math.atan2(mainCharacter.y - input.cy, input.cx - mainCharacter.x);

			camera.update(mainCharacter);
			synchronized (events) {
				for (GameEvent e : events) {
					listener.onEventReceived(e);
				}
				events.clear();
			}
			for (String s : wsp.chatTexts) {
				chatPanel.addLine(s);
			}
		}

	}

	/**
	 * Initialises the UI components
	 */
	private void initUI() {
		//this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		teamScore = GUIFactory.getStyledLabel("0 - 0");
		teamScore.setHorizontalAlignment(SwingConstants.CENTER);
		teamScore.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.add(teamScore);
		teamScore.setLocation(10,10);
		Utils.alignCenterHorizontally(teamScore, this);
		
		scoreboard = new JPanel();
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
		scoreboard.setSize(scoreboard.getPreferredSize());
		scoreboard.setBackground(new Color(0,0,0,100));
		scoreboard.setOpaque(true);
		this.add(scoreboard,new Integer(1));
		Utils.setLocationCenterOf(scoreboard, this);
		scoreboard.setVisible(false);

		minimap = new Minimap(arena, id, players);
		this.add(minimap);
		minimap.setLocation(20, getHeight()-20-minimap.getHeight());
		
		chatPanel = new ChatPanel(10);
		chatPanel.setVisible(false);
		//chatPanel.getTextArea().setRows(15);
		chatPanel.setMaximumSize(new Dimension(800, 300));
		chatPanel.getInputLabel().setText(Utils.findPlayer(players, id).name + ": ");
		chatPanel.setSize(chatPanel.getMaximumSize());
		this.add(chatPanel);
		Utils.setLocationCenterOf(chatPanel, this);
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "triggerChat");
		this.getActionMap().put("triggerChat", new AbstractAction() {
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
		int wId = 0, aId = 0, pId = 0;
		switch (Utils.findPlayer(players, id).type) {
			// grenadier
			case 0:
				wId = Weapon.ASSAULT_RIFLE_ID;
				aId = Ability.FRAG_ID;
				break;
				
			// markman
			case 1:
				wId = Weapon.MARKMAN_RIFLE_ID;
				aId = Ability.SCOPE_ID;
				break;
				
			// scout
			case 2:
				wId = Weapon.SHOTGUN_ID;
				aId = Ability.BINO_ID;
				break;
				
			// shield
			case 3:
				wId = Weapon.SHOTGUN_ID;
				aId = Ability.FLASH_ID;
				break;
				
			// agent
			case 4:
				wId = Weapon.SILENT_PISTOL_ID;
				aId = Ability.AMP_ID;
				break;
		}
		abilityBar = new AbilityBar(this,wId,aId,pId);
		abilityBar.setSize(abilityBar.getPreferredSize());
		abilityBar.setLocation(0, getHeight() - abilityBar.getHeight() - 20);
		add(abilityBar);
		Utils.alignCenterHorizontally(abilityBar, this);
	}

	/**
	 * Updates the cursor
	 */
	private void updateCursor() {
		Point cursorOnScreen = MouseInfo.getPointerInfo().getLocation();
		Point gameOnScreen = getLocationOnScreen();
		input.cx = (float)Renderer.toMeter(cursorOnScreen.x - gameOnScreen.x + camera.getTopLeftXPixel());
		input.cy = (float)Renderer.toMeter(cursorOnScreen.y - gameOnScreen.y + camera.getTopLeftYPixel());
	}

	/**
	 * Send an input of a player to gamescreen
	 */
	private void sendInput() {
		if (!connection.getSocket().isClosed()) {
			connection.send(input);
		}
	}

	/**
	 * Receive the world state from server
	 * 
	 * @return Returns the WorldStatePacket of the gamescreen
	 */
	private WorldStatePacket receiveState() {
		// receive state
		WorldStatePacket wsp = null;

		try {
			InputStream in = connection.getSocket().getInputStream();
			while (in.available() > 1000) {
				wsp = (WorldStatePacket) connection.receive();
				events.addAll(wsp.events);
			}
		} catch (IOException e) {
			System.out.println("Timeout when reading data from network");
		}
		return wsp;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//System.out.println("GameWindow paint");
		// render background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		// render HUD
		// render world
		int transX = camera.getTopLeftXPixel();
		int transY = camera.getTopLeftYPixel();
		g.translate(-transX, -transY);

		Graphics2D g2D = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHints(rh);
		FullCharacterData c = mainCharacter;
		if (currentState != null) {
			// render the region outside of both vision and hearing
			Renderer.drawArenaImage(g2D, renderer.getDarkArenaImage(),camera.getDrawArea());
			
			// render the hearing region
			/*
			Shape clip = new Ellipse2D.Double(Renderer.toPixel(c.x - c.hearRange),Renderer.toPixel(c.y - c.hearRange),
					Renderer.toPixel(c.hearRange*2),Renderer.toPixel(c.hearRange*2));
			g2D.setClip(clip);
			Rectangle2D hearBox = getCharacterVisionBox(c.x,c.y,c.hearRange);
			Renderer.drawArenaImage(g2D, renderer.getArenaImage(),hearBox);
			*/
			
			g2D.setClip(null);
			nonvisualAnimations.render(g2D);
			
			// create the vision region
			Area los = new Area();
			
			Vision v = new Vision();
			v.x = c.x;
			v.y = c.y;
			v.angle = c.viewAngle;
			v.direction = c.direction;
			v.radius = c.radius;
			v.range = c.viewRange;

			los.add(visibility.generateLoS(v, arena));
			double r = v.radius*1.5;
			los.add(new Area(new Ellipse2D.Double(Renderer.toPixel(v.x - r),Renderer.toPixel(v.y - r),
					Renderer.toPixel(r*2),Renderer.toPixel(r*2))));
			
			/*
			for (Vision v:currentState.visions) {
				los.add(visibility.generateLoS(v, arena));
				double r = v.radius*1.5;
				los.add(new Area(new Ellipse2D.Double(Renderer.toPixel(v.x - r),Renderer.toPixel(v.y - r),
						Renderer.toPixel(r*2),Renderer.toPixel(r*2))));
			}
			*/
			
			g2D.setClip(los);
			//Renderer.drawArenaImage(g2D,renderer.getLightArenaImage(),viewBox);
			Renderer.drawArenaImage(g2D,renderer.getLightArenaImage(),camera.getDrawArea());


			for (ClientPlayer data : players) {
				if (data.id != id && data.active) {
					CharData ch = data.character;
					Renderer.renderOtherCharacter(g2D, ch, data.type);
				}
			}

			// render projectiles
			visualAnimations.render(g2D);
			for (ProjectileData data : currentState.projectiles) {
				Renderer.renderProjectile(g2D,data);
			}
			
			// render the main character
			//Renderer.renderLOS(g2D, los);
			ClientPlayer localPlayer = Utils.findPlayer(players, id);
			Renderer.renderMainCharacter(g2D, c, localPlayer);
			
			// Render lighting & shadow
			Composite save = g2D.getComposite();
			g2D.setComposite(new SoftHardLightComposite(1f));
			
			Rectangle2D viewBox = getCharacterVisionBox(c.x,c.y,c.viewRange);
			//Renderer.drawArenaImage(g2D,renderer.getLightMap(),viewBox);
			g2D.setComposite(save);
			
		}
		g2D.setClip(null);
		globalAnimations.render(g2D);
		int tileX = (int)(input.cx/Terrain.tileSize);
		int tileY = (int)(input.cy/Terrain.tileSize);
		if (arena.get(tileX,tileY).coverType()>0)
			Renderer.renderProtection(g2D,tileX,tileY,arena.get(tileX,tileY).coverType());
		renderer.renderCharacterUI(g2D,c);
		Renderer.renderCrosshair(g2D,input.cx,input.cy,c.crosshairSize);
		g.translate(transX, transY);
		g2D.drawString("FPS: "+FPS, 10, 10);
	}

	private Rectangle2D getCharacterVisionBox(double x, double y, double viewRange) {
		return new Rectangle2D.Double(x - viewRange, y - viewRange, viewRange * 2, viewRange * 2);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// if ESC key show option menu or sth

		// if tab/some other key show detailed scoreboard
		if (e.getKeyCode() == KeyEvent.VK_TAB)
			scoreboard.setVisible(true);

		// if enter enable chat


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
			case KeyEvent.VK_ALT:
				// run
				input.alt = b;
				break;

			case KeyEvent.VK_C:
				// use server.ability
				input.fire2 = b;
				break;
				
			case KeyEvent.VK_W:
				// move up
				input.up = b;
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
			case KeyEvent.VK_SHIFT:
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
			//ClientPlayer localPlayer = Utils.findPlayer(players, id);
			JLabel player = new JLabel(text/*, new ImageIcon(Sprite.getImage(value.type, value.team == localPlayer.team ? 1 : 0))*/,
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
			} else if (event instanceof SoundEvent) {
				SoundEvent e = (SoundEvent) event;
				audioManager.playSound(e.id,e.volume);
				
				if (e.id!=SoundEvent.PING_SOUND_ID)
					nonvisualAnimations.addNoiseAnimation(e.x, e.y, e.volume);
			} else if (event instanceof AnimationEvent) {
				AnimationEvent e = (AnimationEvent) event;
				if (!e.global) {
					visualAnimations.addAnimation(e);
				} else {
					globalAnimations.addAnimation(e);
				}
				if (e.id==AnimationEvent.BLOOD) {
					renderer.addBloodToArena(e.x, e.y, e.direction);
				}
			} else if (event instanceof GameEndEvent) {
				playing = false;
				game.setScreen(new MainMenuScreen(game));
				try {
					connection.getSocket().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (event instanceof EnemyInfoEvent) {
				EnemyInfoEvent e = (EnemyInfoEvent) event;
				nonvisualAnimations.addAnimation(new AnimationEvent(AnimationEvent.ENEMYMARK,e.x,e.y,0));
			} else if (event instanceof TileChanged) {
				TileChanged e = (TileChanged) event;
				arena.get(e.tx, e.ty).setThing(objectTable.get(e.switchThingID));
				arena.generateLightMap();
				renderer.redrawLightImage(arena);
				
				renderer.redrawArenaImage(arena,e.tx,e.ty);
			}
		}
	};
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomLevel = Math.max(-0.5,Math.min(0.5,zoomLevel + 0.05*e.getPreciseWheelRotation()));
		Renderer.setPPM(Renderer.DEFAULT_PPM*(1+zoomLevel));
	}
}
