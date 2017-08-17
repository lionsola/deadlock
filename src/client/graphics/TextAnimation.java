package client.graphics;

import java.awt.Graphics2D;

public class TextAnimation extends BasicAnimation {
	private String text;
	
	public TextAnimation(long life, String s) {
		super(life);
		text = s;
	}

	@Override
	public void render(Graphics2D g2D) {
		
	}

}
