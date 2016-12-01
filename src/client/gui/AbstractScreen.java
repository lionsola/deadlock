package client.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Abstract class. AbstractScreen objects are used within a GameWindow to switch between, well, screens.
 * i.e MenuScreen, GameScreen, OptionScreen ...
 */
public abstract class AbstractScreen extends JPanel {

	private static final long serialVersionUID = 8389167884117662594L;
	protected BufferedImage background;
	protected final GameWindow game;

	/**
	 * Constructor
	 * @param game The game object.
	 */
	public AbstractScreen(GameWindow game) {
		this.game = game;
		this.setSize(game.getContentPane().getWidth(), game.getContentPane().getHeight());
		// this.setIgnoreRepaint(true);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					onEscape();
				}
			}
		});
	}

	/**
	 * Update method that can be fulfilled by subclasses.
	 */
	public void update() {};

	protected void setBackground(BufferedImage bg) {
		background = bg;
	}
	
	/**
	 * Use the default background for each screen.
	 */
	protected void useDefaultBackground() {
		try {
			background = ImageIO.read(new FileInputStream("resource/background/mansion.png"));
		} catch (Exception e) {
			System.out.println("Error load background image!");
			background = null;
		}
	}

	/**
	 * Paint the component from the client.graphics passed.
	 * 
	 * @param g
	 *            the client.graphics to be painted.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (background != null)
			g.drawImage(background, (getWidth()-background.getWidth())/2, (getHeight()-background.getHeight())/2, null);
		else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, game.getWidth(), game.getHeight());
		}
	}

	/**
	 * What to do when you click escape.
	 */
	public void onEscape() {
		game.setScreen(new MainMenuScreen(game));
	};

}
