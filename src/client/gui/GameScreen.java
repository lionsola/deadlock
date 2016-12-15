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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import server.ability.Ability;
import server.passive.Passive;
import server.weapon.Weapon;
import server.world.Arena;
import server.world.Misc;
import server.world.Thing;
import server.world.Terrain;
import server.world.Visibility;
import server.world.trigger.TileSwitchPreset;
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
import client.graphics.BasicAnimation;
import client.graphics.Renderer;
import client.image.SoftHardLightComposite;
import client.sound.AudioManager;
import client.sound.MusicPlayer;
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
	private AnimationSystem persistentAnimations = new AnimationSystem();
	
	private AudioManager audioManager = new AudioManager();
	private List<ClientPlayer> team1;
	private List<ClientPlayer> team2;
	private List<ClientPlayer> players;
	
	private TeamListModel team1Model;
	private TeamListModel team2Model;
	private JPanel scoreboard;
	private JLabel teamScore;
	private JLabel winnerText;
	private ChatPanel chatPanel;
	private AbilityBar abilityBar;
	private JComponent minimap;
	private Visibility visibility = new Visibility();
	private Renderer renderer = new Renderer();
	private HashMap<Integer,Thing> objectTable;
	private HashMap<Integer, Misc> miscTable;
	
	private boolean playing = true;
	private double zoomLevel = 0;
	
	volatile private double UPS = 0;
	
	volatile private int frameCount = 0;
	volatile private double FPS = 0;

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
		
		AudioManager.stopMusic();
		AudioManager.playMusic("winds.wav", MusicPlayer.DEFAULT_VOLUME - 20);
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
		HashMap<Integer,Terrain> tileTable = (HashMap<Integer, Terrain>) DataManager.loadObject(DataManager.FILE_TILES);
		
		objectTable = (HashMap<Integer, Thing>) DataManager.loadObject(DataManager.FILE_OBJECTS);
		
		HashMap<Integer,TileSwitchPreset> triggerTable = (HashMap<Integer, TileSwitchPreset>) DataManager.loadObject(DataManager.FILE_TRIGGERS);
		miscTable = (HashMap<Integer,Misc>) DataManager.loadObject(DataManager.FILE_MISC);
		if (miscTable==null) {
			miscTable = new HashMap<Integer,Misc>();
		}
		
		DataManager.loadImage(tileTable.values());
		DataManager.loadImage(objectTable.values());
		DataManager.loadImage(miscTable.values());
		
		arena = new Arena(arenaName, tileTable, objectTable,triggerTable,miscTable);
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
	 * Initialises the UI components
	 */
	private void initUI() {
		//this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		teamScore = new JLabel("0 - 0");
		GUIFactory.stylizeHUDComponent(teamScore);
		teamScore.setFont(GUIFactory.font_m);
		
		scoreboard = new JPanel();
		scoreboard.setLayout(new BoxLayout(scoreboard, BoxLayout.Y_AXIS));
		scoreboard.add(teamScore);
		String labels = String.format("%8s %4s %4s %4s  ","Name","K","D","HS");
		JLabel cols = new JLabel(labels+" "+labels);
		GUIFactory.stylizeHUDComponent(cols);
		scoreboard.add(cols);
		JSeparator line2 = new JSeparator();
		GUIFactory.stylizeHUDComponent(line2);
		scoreboard.add(line2);
		
		
		JPanel teamPanel = GUIFactory.getTransparentPanel();
		team1Model = new TeamListModel(team1);
		team2Model = new TeamListModel(team2);

		JList<ClientPlayer> team1List = new JList<ClientPlayer>(team1Model);
		JList<ClientPlayer> team2List = new JList<ClientPlayer>(team2Model);
		team1List.setCellRenderer(playerRenderer);
		team2List.setCellRenderer(playerRenderer);
		team1List.setOpaque(false);
		team2List.setOpaque(false);

		teamPanel.add(team1List);
		JSeparator line = new JSeparator();
		line.setOrientation(JSeparator.VERTICAL);
		GUIFactory.stylizeHUDComponent(line);
		teamPanel.add(line);
		line.setPreferredSize(new Dimension(5, scoreboard.getPreferredSize().height));
		teamPanel.add(team2List);
		
		scoreboard.add(teamPanel);
		scoreboard.setSize(scoreboard.getPreferredSize());
		scoreboard.setBackground(GUIFactory.TRANSBLACK);
		scoreboard.setOpaque(true);
		
		add(scoreboard,new Integer(1));
		Utils.setLocationCenterOf(scoreboard, this);
		scoreboard.setVisible(false);

		winnerText = new JLabel(" ");
		GUIFactory.stylizeHUDComponent(winnerText);
		
		winnerText.setVisible(false);
		winnerText.setFont(GUIFactory.font_m);
		winnerText.setSize(200, 50);
		winnerText.setLocation(0, scoreboard.getY()-winnerText.getHeight()-20);
		Utils.alignCenterHorizontally(winnerText, this);
		add(winnerText);
		
		minimap = new Minimap(arena, id, players);
		GUIFactory.stylizeHUDComponent(minimap);
		add(minimap);
		minimap.setLocation(20, getHeight()-20-minimap.getHeight());
		
		chatPanel = new ChatPanel(10);
		GUIFactory.stylizeHUDComponent(chatPanel);
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
		for (ClientPlayer cp:players) {
			switch(cp.type) {
				// grenadier
				case 0:
					wId = Weapon.ASSAULT_RIFLE_ID;
					aId = Ability.FRAG_ID;
					pId = Passive.ASSAULT_ID;
					break;
					
				// markman
				case 1:
					wId = Weapon.MARKMAN_RIFLE_ID;
					aId = Ability.SCOPE_ID;
					pId = Passive.OVERWATCH_ID;
					break;
					
				// scout
				case 2:
					wId = Weapon.SHOTGUN_ID;
					aId = Ability.BINO_ID;
					pId = Passive.MARK_ID;
					break;
					
				// shield
				case 3:
					wId = Weapon.MP7_ID;
					aId = Ability.FLASH_ID;
					pId = Passive.SHIELD_ID;
					break;
					
				// agent
				case 4:
					wId = Weapon.SILENT_PISTOL_ID;
					aId = Ability.AMP_ID;
					pId = Passive.BACKSTAB_ID;
					break;
			}
			cp.weaponId = wId;
			cp.abilityId = aId;
			cp.passiveId = pId;
		}
		
		ClientPlayer p = Utils.findPlayer(players, id);
		abilityBar = new AbilityBar(this,p.weaponId,p.abilityId,p.passiveId);
		GUIFactory.stylizeHUDComponent(abilityBar);
		abilityBar.setSize(abilityBar.getPreferredSize());
		abilityBar.setLocation(0, getHeight() - abilityBar.getHeight() - 20);
		add(abilityBar);
		Utils.alignCenterHorizontally(abilityBar, this);
	}
	
	/**
	 * The main game loop.
	 */
	@Override
	public void run() {
		long previous = System.currentTimeMillis();
		double lag = 0.0;
		long totalTime = 0;
		int updateCount = 0;
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

			updateCount++;
			if (updateCount == MAX_FRAME_COUNT) {
				UPS = (1000.0 * updateCount) / totalTime;
				updateCount = 0;
				
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
		persistentAnimations.update();
		persistentAnimations.render(renderer.bloodImage.createGraphics());
		audioManager.update();
		if (wsp != null) {
			this.currentState = wsp;
			mainCharacter = wsp.player;
			for (ClientPlayer p : players) {
				if (p.id!=mainCharacter.id) {
					p.active = false;
				}
			}
			ClientPlayer targetPlayer = Utils.findPlayer(players, mainCharacter.id);
			targetPlayer.character = wsp.player;
			targetPlayer.active = true;
			
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
			
			abilityBar.update(mainCharacter,targetPlayer);
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
			while (in.available() > 2000) {
				wsp = (WorldStatePacket) connection.receive();
				events.addAll(wsp.events);
			}
		} catch (IOException e) {
			System.out.println("Timeout when reading data from network");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wsp;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//System.out.println("GameWindow paint");
		// render background
		long tick = System.currentTimeMillis();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		// render HUD
		// render world
		double transX = camera.getTopLeftXMeter()*Renderer.getPPM();
		double transY = camera.getTopLeftYMeter()*Renderer.getPPM();
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.translate(-transX, -transY);
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2D.setRenderingHints(rh);
		FullCharacterData c = mainCharacter;
		if (currentState != null) {
			// render the region outside of both vision and hearing
			Renderer.drawArenaImage(g2D, renderer.getDarkArenaImage(),camera.getDrawArea());
			
			// render the hearing region
			Area a = new Area();
			for (Vision v:currentState.visions) {
				a.add(new Area(new Ellipse2D.Double(Renderer.toPixel(v.x - v.hearRange),Renderer.toPixel(v.y - v.hearRange),
					Renderer.toPixel(v.hearRange*2),Renderer.toPixel(v.hearRange*2))));
			
				//Rectangle2D hearBox = getCharacterVisionBox(c.x,c.y,c.hearRange);
			}
			g2D.setClip(a);
			Renderer.drawArenaImage(g2D, renderer.getArenaImage(),camera.getDrawArea());
			
			
			g2D.setClip(null);
			nonvisualAnimations.render(g2D);
			
			// create the vision region
			Area los = new Area();

			for (Vision v:currentState.visions) {
				los.add(visibility.generateLoS(v, arena));
				double r = v.radius*1.5;
				los.add(new Area(new Ellipse2D.Double(Renderer.toPixel(v.x - r),Renderer.toPixel(v.y - r),
						Renderer.toPixel(r*2),Renderer.toPixel(r*2))));
			}
			
			g2D.setClip(los);
			Renderer.drawArenaImage(g2D, renderer.lowImage, camera.getDrawArea());
			Renderer.drawArenaImage(g2D, renderer.bloodImage, camera.getDrawArea());

			for (ClientPlayer data : players) {
				if (data.id != c.id && data.active) {
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
			ClientPlayer currentTarget = Utils.findPlayer(players, c.id);
			Renderer.renderMainCharacter(g2D, c, currentTarget);
			
			Renderer.drawArenaImage(g2D, renderer.highImage, camera.getDrawArea());
			
			
			// Render lighting & shadow
			Composite save = g2D.getComposite();
			g2D.setComposite(new SoftHardLightComposite(1f));
			Rectangle2D viewBox = getCharacterVisionBox(c.x,c.y,c.viewRange);
			Renderer.drawArenaImage(g2D,renderer.getLightMap(),viewBox);
			g2D.setComposite(save);
			
		}
		g2D.setClip(null);
		globalAnimations.render(g2D);
		int tileX = (int)(input.cx/Terrain.tileSize);
		int tileY = (int)(input.cy/Terrain.tileSize);
		if (arena.get(tileX,tileY).coverType()>0)
			Renderer.renderProtection(g2D,tileX,tileY,arena.get(tileX,tileY).coverType());
		renderer.renderCharacterUI(g2D,c);
		
		g2D.setColor(Color.WHITE);
		Renderer.renderCrosshair(g2D,input.cx,input.cy,c.crosshairSize,1.5f);
		g2D.translate(transX, transY);
		g2D.drawString("UPS: "+UPS +", FPS: "+FPS+", draw time: "+(System.currentTimeMillis()-tick)+"ms", 10, 10);
		frameCount += 1;
		
		
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
			String text = String.format("%8s %4d %4d %4d  ",value.name, value.kills, value.deaths, value.headshots);
			//ClientPlayer localPlayer = Utils.findPlayer(players, id);
			JLabel player = new JLabel(text/*, new ImageIcon(Sprite.getImage(value.type, value.team == localPlayer.team ? 1 : 0))*/,
					SwingConstants.LEFT);
			GUIFactory.stylizeHUDComponent(player);
			return player;
		}
	};


	private Listener listener = new Listener() {
		@Override
		public void onEventReceived(GameEvent event) {
			if (event instanceof PlayerDieEvent) {
				PlayerDieEvent e = (PlayerDieEvent) event;
				ClientPlayer killer = Utils.findPlayer(players, e.killerId);
				ClientPlayer killed = Utils.findPlayer(players, e.killedId);

				killed.deaths++;
				if (killer.team != killed.team) {
					killer.kills++;
				} else {
					killer.kills--;
				}
				team1Model.invalidate();
				team2Model.invalidate();
			} else if (event instanceof Headshot) {
				final Headshot e = (Headshot) event;
				ClientPlayer attacker = Utils.findPlayer(players,e.attacker);
				attacker.headshots++;
				globalAnimations.addCustomAnimation(new BasicAnimation(1500) {
					@Override
					public void render(Graphics2D g2D) {
						int alpha = (int)Math.min(255,255*2*life/duration);
						g2D.setColor(new Color(50,100,255,alpha));
						Renderer.renderCrosshair(g2D, e.x, e.y, 2, 3);
					}});;
				audioManager.playSound(SoundEvent.PING_SOUND_ID, SoundEvent.PING_SOUND_VOLUME);
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
				
				if (e.id!=SoundEvent.PING_SOUND_ID && e.volume>0) {
					nonvisualAnimations.addNoiseAnimation(e.x, e.y, e.volume);
				}
			} else if (event instanceof AnimationEvent) {
				AnimationEvent e = (AnimationEvent) event;
				if (e.id==AnimationEvent.BLOOD) {
					persistentAnimations.addBloodAnimation(e.x, e.y, e.direction, Utils.findPlayer(players, e.charId).team);
				}
				else if (!e.global) {
					visualAnimations.addAnimation(e);
				} else {
					globalAnimations.addAnimation(e);
				}
				
			} else if (event instanceof GameEndEvent) {
				playing = false;
				try {
					connection.getSocket().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// delay a bit, need another even to change back to lobby screen
				game.setScreen(new MainMenuScreen(game));
			} else if (event instanceof EnemyInfoEvent) {
				EnemyInfoEvent e = (EnemyInfoEvent) event;
				nonvisualAnimations.addAnimation(new AnimationEvent(AnimationEvent.ENEMYMARK,e.x,e.y,0));
			} else if (event instanceof TileChanged) {
				TileChanged e = (TileChanged) event;
				if (e.itemType==TileSwitchPreset.THING) {
					arena.get(e.tx, e.ty).setThing(objectTable.get(e.switchThingID));
				} else if (e.itemType==TileSwitchPreset.MISC) {
					arena.get(e.tx, e.ty).setMisc(miscTable.get(e.switchThingID));
				}
				
				arena.generateLightMap();
				renderer.redrawLightImage(arena);
				
				renderer.redrawArenaImage(arena,e.tx,e.ty,arena.get(e.tx,e.ty).getThing().getLayer());
			} else if (event instanceof RoundEnd) {
				RoundEnd e = (RoundEnd) event;
				winnerText.setText("Team "+e.winner+" won!");
				winnerText.setVisible(true);
				scoreboard.setVisible(true);
			} else if (event instanceof RoundStart) {
				scoreboard.setVisible(false);
				winnerText.setVisible(false);
			}
		}
	};
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomLevel = Math.max(-0.5,Math.min(0.5,zoomLevel + 0.05*e.getPreciseWheelRotation()));
		Renderer.setPPM(Renderer.DEFAULT_PPM*(1+zoomLevel));
	}
}
