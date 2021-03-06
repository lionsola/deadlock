package client.gui;

import java.awt.BasicStroke;
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

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import server.ability.Ability;
import server.passive.Passive;
import server.weapon.Weapon;
import server.world.Arena;
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
import shared.network.NPCData;
import shared.network.event.AnimationEvent;
import shared.network.event.GameEvent;
import shared.network.event.GameEvent.*;
import shared.network.event.SoundEvent;
import shared.network.event.VoiceEvent;
import client.graphics.AnimationSystem;
import client.graphics.BasicAnimation;
import client.graphics.ParticleSource;
import client.graphics.Renderer;
import client.graphics.VoiceAnimation;
import client.image.MultiplyComposite;
import client.sound.AudioManager;
import client.sound.MusicPlayer;
import editor.DataManager;

/**
 * The GUI where the match takes place, i.e. the arena with players.
 */
public class GameScreen extends JLayeredPane implements KeyListener, MouseListener, Runnable, MouseWheelListener {
	private static final long serialVersionUID = 1383374303503610168L;

	private FullCharacterData mainCharacter = null;
	private GameWindow game;
	private Arena arena;
	private Camera camera;
	private final ClientPlayer player;
	private Connection connection;
	private InputPacket input = new InputPacket();
	private List<GameEvent> events = new LinkedList<GameEvent>();
	private WorldStatePacket currentState;
	private AnimationSystem nonvisualAnimations = new AnimationSystem();
	private AnimationSystem visualAnimations = new AnimationSystem();
	private AnimationSystem globalAnimations = new AnimationSystem();
	private AnimationSystem persistentAnimations = new AnimationSystem();
	
	private AudioManager audioManager = new AudioManager();
	private List<ClientPlayer> players;
	
	private TeamListModel team1Model;
	private JPanel scoreboard;
	private JLabel teamScore;
	private JLabel winnerText;
	private ChatPanel chatPanel;
	private AbilityBar abilityBar;
	private JComponent minimap;
	private Visibility visibility = new Visibility();
	private Renderer renderer = new Renderer();
	private HashMap<Integer,Thing> objectTable;
	
	private boolean lightImageChanged = false;
	private boolean playing = true;
	private double zoomLevel = 0;
	
	volatile private double UPS = 0;
	
	volatile private int frameCount = 0;
	volatile private double FPS = 0;
	
	private HashMap<Integer,String> dataLines;

	/**
	 * Creates a new gamescreen where the match will take place
	 * 
	 * @param game
	 *            The game loop which will use the game screen
	 * @param id
	 *            The id of the local player
	 * @param connection
	 *            The socket to communicate with the server
	 * @param mId
	 *            The name of the arena for this game
	 * @param team1
	 *            The list of players on team 1
	 * @throws IOException
	 *             Exception thrown on gamescreen
	 */
	public GameScreen(GameWindow game, ClientPlayer player, Connection connection, int mId, List<ClientPlayer> players) throws IOException {
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
		HashMap<Integer,Terrain> tileTable = (HashMap<Integer, Terrain>) DataManager.loadInternalObject(DataManager.FILE_TILES);
		
		objectTable = (HashMap<Integer, Thing>) DataManager.loadInternalObject(DataManager.FILE_OBJECTS);
		
		HashMap<Integer,TileSwitchPreset> triggerTable = (HashMap<Integer, TileSwitchPreset>) DataManager.loadInternalObject(DataManager.FILE_TRIGGERS);
		
		DataManager.loadImages(tileTable.values());
		DataManager.loadImages(objectTable.values());
		DataManager.updateParticleSource(objectTable.values());
		
		arena = new Arena(mId, tileTable, objectTable,triggerTable);
		renderer.initArenaImages(arena);
		camera = new Camera(arena, this, 10);
		
		// Initialise fields
		this.player = player;
		this.connection = connection;
		this.game = game;
		this.players = players;
		
		for (ClientPlayer p : players) {
			p.character = new CharData();
		}

		initUI();
		// load data lines
		initLines(mId);
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
		team1Model = new TeamListModel(players);

		JList<ClientPlayer> team1List = new JList<ClientPlayer>(team1Model);
		team1List.setCellRenderer(playerRenderer);
		team1List.setOpaque(false);

		teamPanel.add(team1List);
		JSeparator line = new JSeparator();
		line.setOrientation(JSeparator.VERTICAL);
		GUIFactory.stylizeHUDComponent(line);
		teamPanel.add(line);
		line.setPreferredSize(new Dimension(5, scoreboard.getPreferredSize().height));
		
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
		
		minimap = new Minimap(arena, player.spawnId, players);
		GUIFactory.stylizeHUDComponent(minimap);
		add(minimap);
		minimap.setLocation(20, getHeight()-20-minimap.getHeight());
		
		chatPanel = new ChatPanel(10);
		GUIFactory.stylizeHUDComponent(chatPanel);
		chatPanel.setVisible(false);
		//chatPanel.getTextArea().setRows(15);
		chatPanel.setMaximumSize(new Dimension(800, 300));
		chatPanel.getInputLabel().setText(player.name + ": ");
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
				case Alpha:
					wId = Weapon.ASSAULT_RIFLE_ID;
					aId = Ability.FRAG_ID;
					pId = Passive.ASSAULT_ID;
					break;
					
				// markman
				case Gamma:
					wId = Weapon.MARKMAN_RIFLE_ID;
					aId = Ability.SCOPE_ID;
					pId = Passive.OVERWATCH_ID;
					break;
					
				// scout
				case Pi:
					wId = Weapon.SHOTGUN_ID;
					aId = Ability.BINO_ID;
					pId = Passive.MARK_ID;
					break;
					
				// shield
				case Beta:
					wId = Weapon.MP7_ID;
					aId = Ability.FLASH_ID;
					pId = Passive.SHIELD_ID;
					break;
					
				// agent
				case Ju:
					wId = Weapon.SILENT_PISTOL_ID;
					aId = Ability.AMP_ID;
					pId = Passive.BACKSTAB_ID;
					break;
					
				// werewolf
				case Werewolf:
					wId = Weapon.BITE_ID;
					aId = Ability.GROWL_ID;
					pId = Passive.ASSAULT_ID;
					break;
					
				default:
					break;
			}
			cp.weaponId = wId;
			cp.abilityId = aId;
			cp.passiveId = pId;
		}
		
		abilityBar = new AbilityBar(this,player);
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
		
		// update particle sources
		for (ParticleSource ps:arena.getParticleSources()) {
			ps.update(visualAnimations);
		}
		//
		nonvisualAnimations.update();
		visualAnimations.update();
		globalAnimations.update();
		persistentAnimations.update();
		//persistentAnimations.render(renderer.bloodImage.createGraphics(),camera,null);
		audioManager.update();
		
		if (wsp != null) {
			this.currentState = wsp;
			mainCharacter = wsp.player;
			for (ClientPlayer p : players) {
				if (p.spawnId!=mainCharacter.id) {
					p.active = false;
				}
			}
			ClientPlayer targetPlayer = Utils.findPlayer(players, mainCharacter.id);
			if (targetPlayer!=null) {
				targetPlayer.character = wsp.player;
				targetPlayer.active = true;
			}
			
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
					onEventReceived(e,wsp);
				}
				events.clear();
			}
			for (String s : wsp.chatTexts) {
				chatPanel.addLine(s);
			}
			//if (!wsp.dynamicLights.isEmpty()) {
			
			if (!wsp.dynamicLights.isEmpty() || lightImageChanged) {
				arena.updateLightMap(wsp.dynamicLights);
				lightImageChanged = true;
			}
			//}
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
		if (input.alt && input.fire1) {
			input.reload = true;
			input.fire1 = false;
		} else {
			input.reload = false;
		}
		if (input.alt && input.fire2) {
			input.ping = true;
			input.fire2 = false;
		} else {
			input.ping = false;
		}
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
		if (c==null) {
			return;
		}
		if (currentState != null) {
			// render the region outside of both vision and hearing
			Renderer.drawArenaImage(g2D, renderer.getDarkArenaImage(),camera.getDrawArea());
			
			/*
			// render the hearing region
			Area a = new Area();
			for (Vision v:currentState.visions) {
				a.add(new Area(new Ellipse2D.Double(Renderer.toPixel(v.x - v.hearRange),Renderer.toPixel(v.y - v.hearRange),
					Renderer.toPixel(v.hearRange*2),Renderer.toPixel(v.hearRange*2))));
			
				//Rectangle2D hearBox = getCharacterVisionBox(c.x,c.y,c.hearRange);
			}
			g2D.setClip(a);
			Renderer.drawArenaImage(g2D, renderer.getArenaImage(),camera.getDrawArea());
			*/
			
			
			
			// create the vision region
			Area losPixel = new Area();
			Area losMeter = new Area();

			for (Vision v:currentState.visions) {
				losPixel.add(visibility.generateLoS(v, arena));
				losMeter.add(visibility.genLOSAreaMeter(v.x, v.y, v.range, v.angle, v.direction, arena));
			}
			Area nonvisual = new Area(camera.getDrawAreaPixel());
			nonvisual.subtract(losPixel);
			
			g2D.setClip(nonvisual);
			
			nonvisualAnimations.render(g2D, camera, camera.getDrawAreaPixel());
			g2D.setClip(camera.getDrawAreaPixel());
			
			Rectangle2D viewBox = losMeter.getBounds2D();
			if (arena.isReal()) {
				g2D.setClip(losPixel);
				//Rectangle2D viewBox = getCharacterVisionBox(c.x,c.y,c.viewRange);
				Renderer.drawArenaImage(g2D, renderer.lowImage, viewBox);
				//Renderer.drawArenaImage(g2D, renderer.bloodImage, viewBox);
			} else {
				g2D.setColor(GUIFactory.UICOLOR);
				g2D.setStroke(new BasicStroke(2));
				g2D.draw(losPixel);
				g2D.setClip(losPixel);
			}
			for (CharData ch : currentState.characters) {
				renderer.renderOtherCharacter(g2D, ch);
			}
			
			for (CharData data : currentState.characters) {
				if (data instanceof NPCData) {
					renderer.renderNPC(g2D, (NPCData)data);
				}
			}
			
			// render projectiles
			visualAnimations.render(g2D, camera, viewBox);
			for (ProjectileData data : currentState.projectiles) {
				Renderer.renderProjectile(g2D,data);
			}
			
			// render the main character
			//Renderer.renderLOS(g2D, los);
			ClientPlayer currentTarget = Utils.findPlayer(players, c.id);
			renderer.renderMainCharacter(g2D, c, currentTarget);
			
			if (arena.isReal()) {
				Renderer.drawArenaImage(g2D, renderer.highImage, viewBox);
				// Render lighting & shadow
				if (lightImageChanged) {
					//renderer.redrawLightImage(arena);
					lightImageChanged = false;
				}
				
				Composite save = g2D.getComposite();
				g2D.setComposite(new MultiplyComposite(1f));
				
				//Renderer.drawArenaImage(g2D,renderer.getLightMap(),viewBox);
				g2D.setComposite(save);
			} else {
				//Renderer.drawArenaImage(g2D,renderer.getLightMap(),viewBox);
			}
			
			renderer.saveCharData(mainCharacter, currentState.characters);
		}
		g2D.setClip(camera.getDrawAreaPixel());
		globalAnimations.render(g2D, camera, null);
		int tileX = (int)(input.cx/Terrain.tileSize);
		int tileY = (int)(input.cy/Terrain.tileSize);
		if (arena.get(tileX,tileY).getCoverType()>0)
			Renderer.renderProtection(g2D,tileX,tileY,arena.get(tileX,tileY).getCoverType());
		renderer.renderCharacterUI(g2D,c);
		Renderer.renderData(g2D, arena, camera.getDrawArea());
		g2D.setColor(Color.WHITE);
		Renderer.renderCrosshair(g2D,input.cx,input.cy,c.crosshairSize,1.5f);
		g2D.translate(transX, transY);
		g2D.drawString("UPS: "+UPS +", FPS: "+FPS+", draw time: "+(System.currentTimeMillis()-tick)+"ms", 10, 10);
		
		frameCount += 1;
	}

	private static Rectangle2D getCharacterVisionBox(double x, double y, double viewRange) {
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

	public void onEventReceived(GameEvent event, WorldStatePacket wsp) {
		if (event instanceof PlayerDieEvent) {
			PlayerDieEvent e = (PlayerDieEvent) event;
			ClientPlayer killer = Utils.findPlayer(players, e.killerId);
			ClientPlayer victim = Utils.findPlayer(players, e.killedId);

			if (victim!=null) {
				victim.deaths++;
			}
			if (killer!=null) {
				if (victim!=null && killer.team == victim.team) {
					killer.kills--;
				} else {
					killer.kills++;
				}
			}
			team1Model.invalidate();
		} else if (event instanceof Headshot) {
			final Headshot e = (Headshot) event;
			ClientPlayer attacker = Utils.findPlayer(players,e.attacker);
			if (attacker!=null)
				attacker.headshots++;
			globalAnimations.addCustomAnimation(new BasicAnimation(1000) {
				@Override
				public void render(Graphics2D g2D) {
					int alpha = (int)Math.min(255,255*2*life/duration);
					g2D.setColor(new Color(150,150,150,alpha));
					Renderer.renderCrosshair(g2D, e.x, e.y, 1.5f, 2);
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
				visualAnimations.addVisualNoiseAnimation(e.x, e.y, e.volume);
			}
			if (e instanceof VoiceEvent) {
				globalAnimations.addCustomAnimation(new VoiceAnimation((VoiceEvent)e));
			}
		} else if (event instanceof AnimationEvent) {
			AnimationEvent e = (AnimationEvent) event;
			if (e.id==AnimationEvent.BLOOD) {
				//persistentAnimations.addPersistentBloodAnimation(e.x, e.y, e.direction, e.team);
			}
			
			if (!e.global) {
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
				renderer.redrawArenaImage(arena,e.tx,e.ty,arena.get(e.tx,e.ty).getThing().getLayer());
			} else if (e.itemType==TileSwitchPreset.MISC) {
				arena.get(e.tx, e.ty).setMisc(objectTable.get(e.switchThingID));
				renderer.redrawArenaImage(arena,e.tx,e.ty,arena.get(e.tx,e.ty).getMisc().getLayer());
			}
			arena.recalculateStaticLights();
			lightImageChanged = true;
		} else if (event instanceof DataObtained) {
			final DataObtained e = (DataObtained) event;
			System.out.println("Data "+e.dataId+" obtained!");
			arena.setData(e.dataId);
			VoiceEvent ve = new VoiceEvent(e.charType, 15, e.x, e.y, dataLines.containsKey(e.dataId)?dataLines.get(e.dataId):"");
			globalAnimations.addCustomAnimation(new VoiceAnimation(ve));
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
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomLevel = Math.max(-0.5,Math.min(0.5,zoomLevel + 0.05*e.getPreciseWheelRotation()));
		Renderer.setPPM(Renderer.DEFAULT_PPM*(1+zoomLevel));
	}
	
	public void initLines(int missionId) {
        dataLines = new HashMap<Integer,String>();
        InputStream is = GameScreen.class.getResourceAsStream("/data.xlsx");
        Workbook workbook = null;
        try {
        	if (is!=null) {
        		workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(missionId);
                for (int i=1;i<arena.getNoData()+1;i++) {
                    int dId = (int)sheet.getRow(i).getCell(0).getNumericCellValue();
                    String s = sheet.getRow(i).getCell(1).getStringCellValue();
                    dataLines.put(dId, s);
                }
        	}
            
        } catch (Exception e) {
            System.err.println("Error while loading mission data.");
            e.printStackTrace();
        } finally {
            if (workbook!=null) {
                try {
					workbook.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
        }
    }
}
