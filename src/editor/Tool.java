package editor;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import client.graphics.Renderer;
import editor.dialogs.LightSourceDialog;
import editor.dialogs.ListDialog;
import editor.dialogs.TriggerDialog;
import server.world.Thing;
import server.world.Tile;
import server.world.Trigger;
import server.world.Trigger.TileSwitchTrigger;
import server.world.TriggerPreset;
import server.world.Utils;
import server.world.Light;
import server.world.SpriteConfig;
import server.world.Terrain;

/**
 * Tools are used to modify an arena. This abstract class provides convenient methods
 * for all tools to use.
 */
public abstract class Tool extends MouseInputAdapter {
	protected final ArenaPanel arenaPanel;
	private Point pointedTile = new Point();
	private Point2D pointedCoord = new Point2D.Double();
	//private Point2D pointedCoor
	
	public Tool(ArenaPanel arenaPanel) {
		this.arenaPanel = arenaPanel;
	}

	public Tile getPointedTile() {
		return arenaPanel.getArena().get(pointedTile);
	}
	
	public Point2D getPointedCoord() {
		return pointedCoord;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		pointedTile = getPointedTile(e);
		pointedCoord = getPointedCoord(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		pointedTile = getPointedTile(e);
		pointedCoord = getPointedCoord(e);
	}
	
	protected Point getPointedTile(MouseEvent e) {
		int tx = (int) (Renderer.toMeter(e.getX()+arenaPanel.getCamera().getTopLeftXPixel())/Terrain.tileSize);
		int ty = (int) (Renderer.toMeter(e.getY()+arenaPanel.getCamera().getTopLeftYPixel())/Terrain.tileSize);
		return new Point(tx,ty);
	}
	
	protected Point2D getPointedCoord(MouseEvent e) {
		double x = Renderer.toMeter(e.getX()+arenaPanel.getCamera().getTopLeftXPixel());
		double y = Renderer.toMeter(e.getY()+arenaPanel.getCamera().getTopLeftYPixel());
		return new Point2D.Double(x, y);
	}
	
	public void render(Graphics2D g2D) {
		
	}
	
	public static class TilePaint extends Tool {
		private JList<Terrain> list;

		public TilePaint(ArenaPanel arenaPanel, JList<Terrain> list) {
			super(arenaPanel);
			this.list = list;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().setTerrain(p.x, p.y, list.getSelectedValue());
			} else if (SwingUtilities.isRightMouseButton(e)) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().setTerrain(p.x, p.y, null);
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			mousePressed(e);
		}
	}
	
	public static class ObjectPaint extends Tool {
		private JList<Thing> list;

		public ObjectPaint(ArenaPanel arenaPanel, JList<Thing> list) {
			super(arenaPanel);
			this.list = list;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().setThing(p.x, p.y, list.getSelectedValue());
			} else if (SwingUtilities.isRightMouseButton(e)) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().setThing(p.x, p.y, null);
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			mousePressed(e);
		}
	}
	
	public static class LightPaint extends Tool {
		LightSourceDialog colorPicker;
		public LightPaint(ArenaPanel arenaPanel, LightSourceDialog lightDialog) {
			super(arenaPanel);
			this.colorPicker = lightDialog;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().addLight(new Light(p.x,p.y,colorPicker.getColor(),colorPicker.getRange()));
				arenaPanel.generateLightImage();
			} else if (SwingUtilities.isRightMouseButton(e)) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().clearLight(p.x, p.y);
				arenaPanel.generateLightImage();
			}
		}
	}
	
	public static class TriggerPaint extends Tool {
		JList<TriggerPreset> list;
		Trigger.TileSwitchTrigger trigger;
		Point triggerLocation;
		public TriggerPaint(ArenaPanel arenaPanel, JList<TriggerPreset> list) {
			super(arenaPanel);
			this.list = list;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point p = getPointedTile(e);
				TriggerPreset tp = list.getSelectedValue();
				if (this.trigger==null && tp!=null) {
					Tile t = arenaPanel.getArena().get(p);
					if (t.getTrigger()==null) {
						Trigger.TileSwitchTrigger tr = new Trigger.TileSwitchTrigger(tp.getId());
						tr.setPreset(tp);
						tr.setTargetTile(p);
						
						t.setTrigger(tr);
						this.trigger = tr;
						this.triggerLocation = p;
					} else if (t.getTrigger() instanceof TileSwitchTrigger){
						this.trigger = (TileSwitchTrigger)t.getTrigger();
						this.triggerLocation = p;
					}
				} else {
					this.trigger.setTargetTile(p);
					this.trigger = null;
				}
			} else if (SwingUtilities.isRightMouseButton(e)) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().get(p).setTrigger(null);
			}
		}
		
		@Override
		public void render(Graphics2D g2D) {
			if (this.trigger!=null) {
				g2D.setStroke(new BasicStroke(1));
				Renderer.drawLine(g2D,Utils.tileToMeter(triggerLocation),getPointedCoord());
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
			super.mouseDragged(arg0);
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
			super.mouseMoved(arg0);
			prevCx = arg0.getX();
			prevCy = arg0.getY();
		}
	}
	
	public static class SpriteSwitcher extends Tool {

		public SpriteSwitcher(ArenaPanel arenaPanel) {
			super(arenaPanel);
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			super.mouseWheelMoved(e);
			Point p = getPointedTile(e);
			Tile tile = arenaPanel.getArena().get(p); 
			Thing thing = tile.getThing();
			if (tile.getSpriteConfig()==null) {
				tile.setSpriteConfig(new SpriteConfig());
			}
			SpriteConfig config = tile.getSpriteConfig();
			
			if (e.getWheelRotation()<0) {
				config.rotation -= 1;
				if (config.rotation<0) {
					config.rotation += 4;
				}
			} else if (e.getWheelRotation()>0){
				config.rotation = (config.rotation+1)%4;
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			Point p = getPointedTile(e);
			Tile tile = arenaPanel.getArena().get(p); 
			Thing thing = tile.getThing();
			if (thing!=null) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					BufferedImage image = thing.getImage();
					int w = image.getWidth()/32;
					int h = image.getHeight()/32;
					if (w>1 || h>1) {
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
				} else if (SwingUtilities.isRightMouseButton(e)) {
					tile.setSpriteConfig(null);
				} else if (SwingUtilities.isMiddleMouseButton(e)) {
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
