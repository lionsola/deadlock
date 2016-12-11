package editor;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JPanel;

import client.graphics.ImageBlender;
import client.graphics.Renderer;
import client.gui.Camera;
import client.gui.ClientPlayer;
import client.gui.GameWindow;
import client.image.SoftHardLightComposite;
import server.world.Terrain;
import shared.network.FullCharacterData;
import shared.network.GameDataPackets.InputPacket;

/**
 * This panel is used in the editor to show the arena along with other
 * editor elements (light, light sources, etc.)
 */
public class ArenaPanel extends JPanel implements Runnable, KeyListener, MouseWheelListener{
	private static final long serialVersionUID = 2649458143637701147L;
	//private Renderer renderer = new Renderer();
	private EditorArena arena;
	public BufferedImage lightImage;
	
	protected boolean renderLayer[] = {true, true, true, true};
	
	protected boolean renderTerrain = true;
	protected boolean renderThing = true;
	protected boolean renderMisc = true;
	
	protected boolean renderLight = false;
	protected boolean renderHardLight = false;
	protected boolean renderLightSource = false;
	protected boolean renderGrid = false;
	protected boolean renderConfig = false;
	protected boolean renderTileSwitchTrigger = false;
	
	private Camera camera;
	//private double zoomLevel;
	private ClientPlayer playerInfo = new ClientPlayer();
	FullCharacterData player = new FullCharacterData();
	private InputPacket input = new InputPacket();
	private double FPS;
	private volatile boolean running = true;
	private Thread thread;
	private Editor editor;
	
	
	public ArenaPanel (Editor editor, EditorArena arena) {
		super();
		this.editor = editor;
		this.arena = arena;
		generateLightImage();
		camera = new Camera(arena,this);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.setIgnoreRepaint(true);
		this.setBackground(Color.DARK_GRAY);
		setFocusTraversalKeysEnabled(false);
		
		addKeyListener(this);
		addMouseWheelListener(this);
	}

	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		while (running) {
			long current = System.currentTimeMillis();
			long elapsed = current - previous;
			previous = current;
			lag += elapsed;
	
			//System.out.println("Lag: " + lag);
			while (lag >= GameWindow.MS_PER_UPDATE) {
				update();
				lag -= GameWindow.MS_PER_UPDATE;
			}
			//System.out.println("Calling repaint");
			repaint();
	
			long waitTime = GameWindow.MS_PER_UPDATE - (System.currentTimeMillis() - current);
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
		}
	}
	
	public void update() {
		updateCursor();
		camera.update(player);
	}
	
	/**
	 * Updates the cursor
	 */
	private void updateCursor() {
		Point cursorOnScreen = MouseInfo.getPointerInfo().getLocation();
		Point gameOnScreen = getLocationOnScreen();
		input.cx = (float)Renderer.toMeter(cursorOnScreen.x - gameOnScreen.x + camera.getTopLeftXPixel());
		input.cy = (float)Renderer.toMeter(cursorOnScreen.y - gameOnScreen.y + camera.getTopLeftYPixel());
		player.direction = (float) Math.atan2(player.y-input.cy,input.cx-player.x);
	}
	
	public EditorArena getArena() {
		return arena;
	}
	
	/**
	 * @return the camera
	 */
	public Camera getCamera() {
		return camera;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (arena==null)
			return;
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
		Rectangle2D window = camera.getDrawArea();
		
		for (int layer=0;layer<=4;layer++) {
			Renderer.drawArenaLayer(g2D, arena, layer, renderTerrain, renderThing, renderMisc);
		}
		
		if (renderLight) {
			Composite save = g2D.getComposite();
			//g2D.setComposite(new MultiplyComposite(0.5f));
			g2D.setComposite(new SoftHardLightComposite(1f));
			//g2D.setComposite(new LightComposite(0.5f));
			//long before = System.currentTimeMillis();
			Renderer.drawArenaImage(g2D, lightImage, window);
			//System.out.println(System.currentTimeMillis()-before);
			g2D.setComposite(save);
		}
		if (renderHardLight) {
			Renderer.renderEditorHardLight(g2D, arena.getLightmap(), window);
		}
		if (renderLightSource) {
			Renderer.renderEditorLightSource(g2D, arena, window);
		}
		if (renderGrid) {
			Renderer.renderGrid(g2D, arena, window);
		}
		if (renderConfig) {
			Renderer.renderSpriteConfig(g2D, arena, window);
		}
		if (renderTileSwitchTrigger) {
			Renderer.renderTrigger(g2D, arena, window);
		}
		editor.currentTool.render(g2D);
		//Renderer.renderMainCharacter(g2D, player, playerInfo);
		g2D.setColor(Color.WHITE);
		Renderer.renderCrosshair(g2D, player.x, player.y, 0.5f, 1.5f);
		g.translate(transX, transY);
		g2D.drawString("FPS: "+FPS, 10, 10);
	}
	
	public void generateLightImage( ) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				arena.generateLightMap();
				BufferedImage i = ImageBlender.drawLightImage(arena);
				lightImage = i;
			}}).start();
	}
	
	// TILE
	public void saveTiles(HashMap<Integer,Terrain> tileTable) throws FileNotFoundException, IOException {
		Collection<Terrain> tileList = tileTable.values();
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("resource/tile/tiles")));
		out.writeObject(tileList);
		out.close();
	}
	
	

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			default:
				keyChanged(e, false);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			default:
				keyChanged(e, false);
		}
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
		}
	}



	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
    
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		//zoomLevel = Math.max(-0.5,Math.min(0.5,zoomLevel + 0.05*e.getPreciseWheelRotation()));
		//Renderer.setPPM(Renderer.DEFAULT_PPM*(1+zoomLevel));
	}
}
