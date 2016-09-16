package gui;

import game.Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import sound.AudioManager;
import sound.MusicPlayer;

/**
 * Load the game, meanwhile show name / logo.
 * 
 * @author Anh Pham
 * @author Connor Cartwright
 */
public class SplashScreen extends AbstractScreen {

	private static final long serialVersionUID = 244142930942747767L;
	private Image background;

	public SplashScreen(final Game game) {
		super(game);
		try {
			background = ImageIO.read(new FileInputStream("resource/background/splash.png"));
		} catch (Exception e) {
			System.out.println("Error load background image!");
		}
		AudioManager.playMusic("menumusic.wav", MusicPlayer.DEFAULT_VOLUME);
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
				// move to a new gui
				// TEST CODE create a local network, connect to it and start the
				// game right away
				game.setScreen(new MainMenuScreen(game));
			}
		});
		// TODO Auto-generated constructor stub
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (background != null)
			g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), null);
		else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, game.getWidth(), game.getHeight());
		}
	}

}
