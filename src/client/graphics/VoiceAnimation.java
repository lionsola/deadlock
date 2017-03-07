package client.graphics;

import java.awt.BasicStroke;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import client.gui.GUIFactory;
import shared.network.event.VoiceEvent;

public class VoiceAnimation extends BasicAnimation {
	private static final long serialVersionUID = 4176514316270729539L;
	VoiceEvent ve;

	public VoiceAnimation(VoiceEvent ve) {
		super(1500+50*ve.line.length());
		this.ve = ve;
	}

	@Override
	public void render(Graphics2D g2D) {
		// Get the FontMetrics
	    FontMetrics metrics = g2D.getFontMetrics(GUIFactory.font_s_bold);
	    String s = ' ' + ve.line + ' ';
	    double DIST = 1.5;
	    // Determine the X coordinate for the text
	    int px = Renderer.toPixel(ve.x) - metrics.stringWidth(s) / 2 - Renderer.toPixel(DIST);
	    // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
	    int py = Renderer.toPixel(ve.y) - metrics.getHeight() / 2 + metrics.getAscent() - Renderer.toPixel(DIST);
	    
	    g2D.setColor(GUIFactory.UICOLOR);
	    g2D.setStroke(new BasicStroke(2));
		Renderer.drawLine(g2D, ve.x-0.5, ve.y-0.5, ve.x-DIST, ve.y-DIST);
		
		g2D.setColor(Renderer.BACKGROUND_COLOR);
	    g2D.fillRect(px, py-metrics.getAscent(), metrics.stringWidth(s), metrics.getHeight());
	    
	    g2D.setColor(GUIFactory.UICOLOR);
	    g2D.drawRect(px, py-metrics.getAscent(), metrics.stringWidth(s), metrics.getHeight());
	    
	    // Set the font
	    g2D.setFont(GUIFactory.font_s_bold);
	    
	    // Draw the String
	    g2D.drawString(s, px, py);
	}
}
