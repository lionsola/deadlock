package client.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

/**
 * Load the game, meanwhile show name / logo.
 * 
 * @author Anh Pham
 * @author Connor Cartwright
 */
public class SplashScreen extends AbstractScreen {

	private static final long serialVersionUID = 244142930942747767L;

	public SplashScreen(final GameWindow game) {
		super(game);
		//AudioManager.playMusic("menumusic.wav", MusicPlayer.DEFAULT_VOLUME);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				e.consume();
			}

			@Override
			public void keyPressed(KeyEvent e) {
				e.consume();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				e.consume();
				// destruct yourself, leave no attachments behind
				removeKeyListener(this);
				// move to a new client.gui
				// TEST CODE create a local network, connect to it and start the
				// game right away
				game.setScreen(new MainMenuScreen(game));
			}
		});
		// TODO Auto-generated constructor stub
	}
}
