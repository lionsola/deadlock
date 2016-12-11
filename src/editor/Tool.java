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
import server.world.Thing;
import server.world.Tile;
import server.world.Utils;
import server.world.trigger.Trigger;
import server.world.trigger.TileSwitchPreset;
import server.world.trigger.Trigger.PressurePlateTouch;
import server.world.trigger.Trigger.SwitchOnTouch;
import server.world.trigger.Trigger.SwitchOnTouchSide;
import server.world.trigger.TriggerEffect;
import server.world.trigger.TriggerEffect.TileSwitch;
import server.world.Light;
import server.world.Misc;
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
	private boolean alt = false;
	//private Point2D pointedCoor
	
	public Tool(ArenaPanel arenaPanel) {
		this.arenaPanel = arenaPanel;
	}

	public void setAlternative(boolean alt) {
		this.alt = alt;
	}
	
	public boolean isAlternative() {
		return alt;
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
	
	public static class MiscPaint extends Tool {
		private JList<Misc> list;

		public MiscPaint(ArenaPanel arenaPanel, JList<Misc> list) {
			super(arenaPanel);
			this.list = list;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if (SwingUtilities.isLeftMouseButton(e)) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().get(p.x, p.y).setMisc(list.getSelectedValue());
			} else if (SwingUtilities.isRightMouseButton(e)) {
				Point p = getPointedTile(e);
				arenaPanel.getArena().get(p.x, p.y).setMisc(null);
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
		private JList<TileSwitchPreset> list;
		private Trigger tr;
		private Point triggerLocation;
		
		public TriggerPaint(ArenaPanel arenaPanel, JList<TileSwitchPreset> list) {
			super(arenaPanel);
			this.list = list;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			Point p = getPointedTile(e);
			if (SwingUtilities.isLeftMouseButton(e)) {
				TileSwitchPreset tp = list.getSelectedValue();
				if (tr==null && tp!=null) {
					Tile t = arenaPanel.getArena().get(p);
					if (t.getTrigger()==null) {
						switch (tp.getTriggerType()) {
							case 0:
								tr = new Trigger.PressurePlateTouch();
								break;
							case 1:
								tr = new Trigger.SwitchOnTouch();
								break;
							case 2:
								tr = new Trigger.SwitchOnTouchSide();
								break;
							default:
								System.err.println("Invalid trigger type!");
								return;
						}
						tr.setLocation(p);
						t.setTrigger(tr);
					} else {
						tr = t.getTrigger();
					}
					
					this.triggerLocation = p;
				} else {
					for (TriggerEffect effect:tr.getEffects()) {
						if (effect instanceof TileSwitch) {
							TileSwitch ts = (TileSwitch) effect;
							if (ts.getTargetTile().equals(p)) {
								tr.removeEffect(ts);
								return;
							}
						}
					}
					if (tp!=null) {
						TriggerEffect.TileSwitch ts = new TriggerEffect.TileSwitch(tp.getId());
						ts.setPreset(tp);
						ts.setTargetTile(p);
						tr.addEffect(ts);
						
						this.tr = null;
						this.triggerLocation = null;
					}
				}
			} else if (SwingUtilities.isRightMouseButton(e)) {
				if (this.tr!=null) {
					tr = null;
				} else {
					arenaPanel.getArena().get(p).setTrigger(null);
				}
			} else if (SwingUtilities.isMiddleMouseButton(e)) {
				
			}
		}
		
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			Point p = getPointedTile(e);
			Trigger tr = arenaPanel.getArena().get(p).getTrigger();
			if (tr instanceof SwitchOnTouchSide) {
				SwitchOnTouchSide sots = (SwitchOnTouchSide)tr;
				if (e.getWheelRotation()<0) {
					int side = sots.getSide() - 1;
					if (side<0) {
						side += 4;
					}
					sots.setSide(side);
				} else if (e.getWheelRotation()>0){
					sots.setSide((sots.getSide()+1)%4);
				}
			}
		}
		
		@Override
		public void render(Graphics2D g2D) {
			if (tr!=null) {
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
			SpriteConfig config;
			if (!isAlternative()) {
				if (tile.getThingConfig()==null) {
					tile.setThingConfig(new SpriteConfig());
				}
				config = tile.getThingConfig();
			} else {
				if (tile.getMiscConfig()==null) {
					tile.setMiscConfig(new SpriteConfig());
				}
				config = tile.getMiscConfig();
			}
			
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
			CellRenderable item;
			SpriteConfig config;
			if (!isAlternative()) {
				item = tile.getThing();
				if (tile.getThingConfig()==null) {
					tile.setThingConfig(new SpriteConfig());
				}
				config = tile.getThingConfig();
			} else {
				item = tile.getMisc();
				if (tile.getMiscConfig()==null) {
					tile.setMiscConfig(new SpriteConfig());
				}
				config = tile.getMiscConfig();
			}
			
			if (item!=null) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					BufferedImage image = item.getImage();
					int w = image.getWidth()/32;
					int h = image.getHeight()/32;
					if (w>1 || h>1) {
						config.spriteX += 1;
						if (config.spriteX>=w) {
							config.spriteX -= w;
							config.spriteY = (config.spriteY+1)%h;
						}
					}
				} else if (SwingUtilities.isMiddleMouseButton(e)) {
					config.flip = !config.flip;
				}
			}
			
			if (SwingUtilities.isRightMouseButton(e)) {
				if (!isAlternative()) {
					tile.setThingConfig(null);
				} else {
					tile.setMiscConfig(null);
				}
			}
		}
	}
}
