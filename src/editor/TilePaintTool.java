package editor;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import client.graphics.Renderer;
import server.world.Arena;
import server.world.Tile;

public class TilePaintTool implements MouseInputListener {
	private Tile tile;
	private ArenaPanel arenaPanel;

	public TilePaintTool(ArenaPanel arenaPanel, Tile tile) {
		this.arenaPanel = arenaPanel;
		this.tile = tile;
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
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		int tx = (int) (Renderer.toMeter(arg0.getX()+arenaPanel.getCamera().getTopLeftXPixel())/Tile.tileSize);
		int ty = (int) (Renderer.toMeter(arg0.getY()+arenaPanel.getCamera().getTopLeftYPixel())/Tile.tileSize);
		
		arenaPanel.getArena().setTile(tx, ty, tile);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
	}

}
