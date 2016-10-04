package editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import client.graphics.Renderer;
import client.gui.Camera;
import client.gui.GameWindow;
import server.world.Arena;
import shared.network.FullCharacterData;
import shared.network.GameDataPackets.InputPacket;

public class EditorPanel extends JPanel implements Runnable, KeyListener, MouseInputListener, MouseWheelListener{
	private static final long serialVersionUID = 2649458143637701147L;
	private Renderer renderer = new Renderer();
	private Arena arena;
	private Camera camera;
	private int prevCx;
	private int prevCy;
	private double zoomLevel;
	private double ppm;
	private FullCharacterData player = new FullCharacterData();
	private InputPacket input = new InputPacket();
	
	public EditorPanel () {
		openArena();
		new Thread(this).start();
		addKeyListener(this);
		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
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
	
	public void update() {
		updateCursor();
	}
	
	/**
	 * Updates the cursor
	 */
	private void updateCursor() {
		Point cursorOnScreen = MouseInfo.getPointerInfo().getLocation();
		Point gameOnScreen = getLocationOnScreen();
		input.cx = (float)Renderer.toMeter(cursorOnScreen.x - gameOnScreen.x + camera.getTopLeftXPixel(this));
		input.cy = (float)Renderer.toMeter(cursorOnScreen.y - gameOnScreen.y + camera.getTopLeftYPixel(this));
		player.direction = (float) Math.atan2(player.y-input.cy,input.cx-player.x);
	}
	
	private void setArena(Arena a) {
		arena = a;
		camera = new Camera(a);
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
		int transX = camera.getTopLeftXPixel(this);
		int transY = camera.getTopLeftYPixel(this);
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
		g.translate(transX, transY);
	}
	
	
	
	public void openArena() {
		JFileChooser fc = new JFileChooser("resource/map/");
        fc.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Map file", "map");
        fc.setFileFilter(filter);
        int returnVal = fc.showDialog(this, "Attach");
        Arena a = null;
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            a = new Arena(file,true);
        }
        if (a!=null)
        	setArena(a);
	}
	
	public void saveArena() {
		
	}
	
	public void newArena() {
		JPasswordField pwd = new JPasswordField(10);
        JTextField user = new JTextField();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Username"));
        panel.add(user);
        panel.add(new JLabel("Password"));
        panel.add(pwd);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
		ppm = Renderer.DEFAULT_PPM*(1+zoomLevel);
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		player.x += (float) Renderer.toMeter(arg0.getX()-prevCx);
		player.y += (float) Renderer.toMeter(arg0.getY()-prevCy);
		camera.update(this, player, input);
		prevCx = arg0.getX();
		prevCy = arg0.getY();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		prevCx = arg0.getX();
		prevCy = arg0.getY();
	}
}
