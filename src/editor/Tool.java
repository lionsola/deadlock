package editor;

import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

import client.graphics.Renderer;
import server.world.Tile;
import server.world.TileBG;

public class Tool implements MouseInputListener {
	protected final ArenaPanel arenaPanel;
	public Tool(ArenaPanel arenaPanel) {
		this.arenaPanel = arenaPanel;
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
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public static class TilePaint extends Tool {
		private TileBG tile;

		public TilePaint(ArenaPanel arenaPanel, TileBG tile) {
			super(arenaPanel);
			this.tile = tile;
		}
		
		@Override
		public void mouseDragged(MouseEvent arg0) {
			int tx = (int) (Renderer.toMeter(arg0.getX()+arenaPanel.getCamera().getTopLeftXPixel())/TileBG.tileSize);
			int ty = (int) (Renderer.toMeter(arg0.getY()+arenaPanel.getCamera().getTopLeftYPixel())/TileBG.tileSize);
			
			arenaPanel.getArena().setTile(tx, ty, tile);
		}
	}
	
	public static class ObjectPaint extends Tool {
		private Tile object;

		public ObjectPaint(ArenaPanel arenaPanel, Tile object) {
			super(arenaPanel);
			this.object = object;
		}
		
		@Override
		public void mouseDragged(MouseEvent arg0) {
			int tx = (int) (Renderer.toMeter(arg0.getX()+arenaPanel.getCamera().getTopLeftXPixel())/TileBG.tileSize);
			int ty = (int) (Renderer.toMeter(arg0.getY()+arenaPanel.getCamera().getTopLeftYPixel())/TileBG.tileSize);
			
			arenaPanel.getArena().setTileObject(tx,ty,object);
		}
	}
	
	public static class LightPaint extends Tool {
		LightDialog colorPicker;
		public LightPaint(ArenaPanel arenaPanel, LightDialog colorPicker) {
			super(arenaPanel);
			this.colorPicker = colorPicker;
		}
		
		@Override
		public void mouseDragged(MouseEvent arg0) {
			int tx = (int) (Renderer.toMeter(arg0.getX()+arenaPanel.getCamera().getTopLeftXPixel())/TileBG.tileSize);
			int ty = (int) (Renderer.toMeter(arg0.getY()+arenaPanel.getCamera().getTopLeftYPixel())/TileBG.tileSize);
			arenaPanel.getArena().getLightmap()[tx][ty] = colorPicker.getColor();
		}
	}
	
	public static class MoveTool extends Tool {

		private int prevCx;
		private int prevCy;

		public MoveTool(ArenaPanel arenaPanel) {
			super(arenaPanel);
		}

		@Override
		public void mouseDragged(MouseEvent arg0) {
			arenaPanel.player.x = arenaPanel.player.x - (float) Renderer.toMeter(arg0.getX()-prevCx);
			arenaPanel.player.x = (float)Math.max(0,arenaPanel.player.x);
			arenaPanel.player.x = (float)Math.min(arenaPanel.getArena().getWidthMeter(),arenaPanel.player.x);
			arenaPanel.player.y = arenaPanel.player.y - (float) Renderer.toMeter(arg0.getY()-prevCy);
			arenaPanel.player.y = (float)Math.max(0,arenaPanel.player.y);
			arenaPanel.player.y = (float)Math.min(arenaPanel.getArena().getHeightMeter(),arenaPanel.player.y);
			prevCx = arg0.getX();
			prevCy = arg0.getY();
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			prevCx = arg0.getX();
			prevCy = arg0.getY();
		}
	}
}
