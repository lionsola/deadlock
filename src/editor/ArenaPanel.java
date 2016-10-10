package editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JPanel;
import client.graphics.Renderer;
import client.gui.Camera;
import client.gui.ClientPlayer;
import client.gui.GameWindow;
import server.world.Arena;
import server.world.Tile;
import shared.network.FullCharacterData;
import shared.network.GameDataPackets.InputPacket;

public class ArenaPanel extends JPanel implements Runnable, KeyListener, MouseWheelListener{
	private static final long serialVersionUID = 2649458143637701147L;
	private Renderer renderer = new Renderer();
	private Arena arena;

	private Camera camera;
	private double zoomLevel;
	private ClientPlayer playerInfo = new ClientPlayer();
	FullCharacterData player = new FullCharacterData();
	private InputPacket input = new InputPacket();
	public ArenaPanel (Arena arena) {
		super();
		this.arena = arena;
		camera = new Camera(arena,this);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.setIgnoreRepaint(true);
		setFocusTraversalKeysEnabled(false);
		
		addKeyListener(this);
		addMouseWheelListener(this);
		
		//addMouseListener(this);
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
		while (true) {
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
	
	public void setArena(Arena a) {
		arena = a;
		camera = new Camera(a,this);
	}
	
	public Arena getArena() {
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
		int layer = 0;
		switch (layer) {
			case 0:
				Renderer.renderArena(g2D, arena, camera.getDrawArea(this));
				break;
			case 1:
				renderer.render(g2D, camera.getDrawArea(this));
				break;
			case 2:
				renderer.renderForeground(g2D, camera.getDrawArea(this));
				break;
		}
		Renderer.renderMainCharacter(g2D, player, playerInfo);
		g.translate(transX, transY);
	}
	
	
	
	// TILE
	public void saveTiles(HashMap<Integer,Tile> tileTable) throws FileNotFoundException, IOException {
		Collection<Tile> tileList = tileTable.values();
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
		}
	}



	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
    
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		zoomLevel = Math.max(-0.5,Math.min(0.5,zoomLevel + 0.05*e.getPreciseWheelRotation()));
		Renderer.ppm = Renderer.DEFAULT_PPM*(1+zoomLevel);
	}
}
