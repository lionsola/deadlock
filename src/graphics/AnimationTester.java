package graphics;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

/**
 * Tests the animations.
 * 
 * @author Anh Pham
 */
public class AnimationTester extends JFrame implements MouseListener, Runnable {
	
	private static final long serialVersionUID = 3838059201198901134L;
	AnimationSystem as;
	private Canvas render;
	private BufferStrategy bufferstrat;
	private boolean running = true;

	public static void main(String[] args) {
		AnimationTester test = new AnimationTester(800, 600);
		new Thread(test).start();
	}

	public AnimationTester(int width, int height) {
		super();
		as = new AnimationSystem();
		setIgnoreRepaint(true);
		setResizable(true);

		render = new Canvas();
		render.setIgnoreRepaint(true);
		int nHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		int nWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		nHeight /= 2;
		nWidth /= 2;

		setBounds(nWidth - (width / 2), nHeight - (height / 2), width, height);
		render.setBounds(nWidth - (width / 2), nHeight - (height / 2), width, height);

		add(render);
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		render.createBufferStrategy(2);
		bufferstrat = render.getBufferStrategy();
		render.addMouseListener(this);
	}

	@Override
	public void run() {
		while (true) {
			if (running)
				as.update();
			render();

			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void render() {
		do {
			do {
				Graphics2D g2d = (Graphics2D) bufferstrat.getDrawGraphics();
				g2d.fillRect(0, 0, render.getWidth(), render.getHeight());

				as.render(g2d);

				g2d.dispose();
			} while (bufferstrat.contentsRestored());
			bufferstrat.show();
		} while (bufferstrat.contentsLost());
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		// as.addNoiseAnimation(e.getX(),e.getY(),50);
		as.addShotAnimation(e.getX(), e.getY(), 0);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	
}
