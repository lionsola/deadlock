package editor;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import client.graphics.Renderer;

public class MoveTool implements MouseInputListener {

	private int prevCx;
	private int prevCy;
	private ArenaPanel editor;

	public MoveTool(ArenaPanel editor) {
		this.editor = editor;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

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
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		editor.player.x = editor.player.x - (float) Renderer.toMeter(arg0.getX()-prevCx);
		editor.player.x = (float)Math.max(0,editor.player.x);
		editor.player.x = (float)Math.min(editor.getArena().getWidthMeter(),editor.player.x);
		editor.player.y = editor.player.y - (float) Renderer.toMeter(arg0.getY()-prevCy);
		editor.player.y = (float)Math.max(0,editor.player.y);
		editor.player.y = (float)Math.min(editor.getArena().getHeightMeter(),editor.player.y);
		prevCx = arg0.getX();
		prevCy = arg0.getY();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		prevCx = arg0.getX();
		prevCy = arg0.getY();
	}

}
