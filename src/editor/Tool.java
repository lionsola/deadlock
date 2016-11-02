package editor;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.event.MouseInputAdapter;
import client.graphics.Renderer;
import editor.dialogs.NewLightDialog;
import server.world.Thing;
import server.world.Tile;
import server.world.Light;
import server.world.SpriteConfig;
import server.world.Terrain;

public class Tool extends MouseInputAdapter {
	protected final ArenaPanel arenaPanel;
	public Tool(ArenaPanel arenaPanel) {
		this.arenaPanel = arenaPanel;
	}

	protected Point getPointedTile(MouseEvent e) {
		int tx = (int) (Renderer.toMeter(e.getX()+arenaPanel.getCamera().getTopLeftXPixel())/Terrain.tileSize);
		int ty = (int) (Renderer.toMeter(e.getY()+arenaPanel.getCamera().getTopLeftYPixel())/Terrain.tileSize);
		return new Point(tx,ty);
	}
	
	public static class TilePaint extends Tool {
		private Terrain tile;

		public TilePaint(ArenaPanel arenaPanel, Terrain tile) {
			super(arenaPanel);
			this.tile = tile;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton()==MouseEvent.BUTTON1) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().setTerrain(p.x, p.y, tile);
			} else if (e.getButton()==MouseEvent.BUTTON2) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().setTerrain(p.x, p.y, null);
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			mousePressed(e);
		}
	}
	
	public static class ObjectPaint extends Tool {
		private Thing object;

		public ObjectPaint(ArenaPanel arenaPanel, Thing object) {
			super(arenaPanel);
			this.object = object;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton()==MouseEvent.BUTTON1) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().setThing(p.x, p.y, object);
			} else if (e.getButton()==MouseEvent.BUTTON2) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().setThing(p.x, p.y, null);
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			mousePressed(e);
		}
	}
	
	public static class NewLightPaint extends Tool {
		NewLightDialog colorPicker;
		public NewLightPaint(ArenaPanel arenaPanel, NewLightDialog lightDialog) {
			super(arenaPanel);
			this.colorPicker = lightDialog;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton()==MouseEvent.BUTTON1) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().addLight(new Light(p.x,p.y,colorPicker.getColor(),colorPicker.getRange()));
				arenaPanel.generateLightImage();
			} else if (e.getButton()==MouseEvent.BUTTON2) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().clearLight(p.x, p.y);
				arenaPanel.generateLightImage();
			}
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
	
	public static class SpriteSwitcher extends Tool {

		public SpriteSwitcher(ArenaPanel arenaPanel) {
			super(arenaPanel);
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			Point p = getPointedTile(e);
			Tile tile = arenaPanel.getArena().get(p); 
			Thing thing = tile.getThing();
			if (thing!=null) {
				if (e.getButton()==MouseEvent.BUTTON1) {
					BufferedImage image = thing.getImage();
					int w = image.getWidth()/32;
					int h = image.getHeight()/32;
					if (w>1 && h>1) {
						if (tile.getSpriteConfig()==null) {
							tile.setSpriteConfig(new SpriteConfig());
						}
						SpriteConfig config = tile.getSpriteConfig();
						config.spriteX += 1;
						if (config.spriteX>=w) {
							config.spriteX -= w;
							config.spriteY = (config.spriteY+1)%h;
						}
					}
				} else if (e.getButton()==MouseEvent.BUTTON3) {
					if (tile.getSpriteConfig()==null) {
						tile.setSpriteConfig(new SpriteConfig());
					}
					SpriteConfig config = tile.getSpriteConfig();
					config.rotation = (config.rotation+1)%4;
				} else if (e.getButton()==MouseEvent.BUTTON2) {
					if (tile.getSpriteConfig()==null) {
						tile.setSpriteConfig(new SpriteConfig());
					}
					SpriteConfig config = tile.getSpriteConfig();
					config.flip = !config.flip;
				}
			}
		}
	}
}
