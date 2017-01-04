package editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import client.graphics.Sprite;
import client.gui.GUIFactory;
import editor.dialogs.ListDialog;
import editor.dialogs.MiscDialog;
import editor.dialogs.SpawnDialog;
import editor.dialogs.TerrainDialog;
import editor.dialogs.ThingDialog;
import editor.dialogs.TileSwitchDialog;
import editor.tools.Tool;
import editor.tools.Tool.*;
import server.world.Thing;
import server.world.trigger.TileSwitchPreset;
import server.world.Misc;
import server.world.Terrain;

public class ToolBar extends JPanel {
	private static final long serialVersionUID = 5669888117742429060L;
	final Editor editor;
	
	JToggleButton activeButton; 
	
	public ToolBar(final Editor editor) {
		this.editor = editor;
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		final JButton move = new JButton();
		stylizeToolButton(move);
		try {
			move.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/hand.png"))));
		} catch (IOException e1) {
			move.setText("H");
		}
		move.setToolTipText("Hand Tool - drag to move map.");
		move.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.setTool(new MoveTool(editor.getArenaPanel()));
			}});
		this.add(move);
		
		
		final JToggleButton tilePaint = new JToggleButton();
		stylizeToolButton(tilePaint);
		try {
			tilePaint.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/terrain.png"))));
		} catch (IOException e1) {
			tilePaint.setText("Terrain");
		}
		tilePaint.setToolTipText("Terrain Paint - left to paint, right to remove");
		tilePaint.addItemListener(new ItemListener() {
			ListDialog<Terrain> list = null;
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (list==null) {
						final CustomListModel<Terrain> tlm = new CustomListModel<Terrain>(new ArrayList<Terrain>(editor.tileTable.values()));
						JButton add = new JButton("Add");
						JButton edit = new JButton("Edit");
						JButton delete = new JButton("Delete");
						JButton[] buttons = {edit,add,delete};
						list = new ListDialog<Terrain>(editor, tilePaint, "Terrain", buttons, tlm);
						
						list.getList().setCellRenderer(cellRenderer);
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								TerrainDialog dialog = new TerrainDialog(editor,null);
								dialog.setVisible(true);
								Terrain t = dialog.getTile();
								if (t!=null) {
									editor.getTerrainTable().put(t.getId(), t);
									editor.tileDataChanged = true;
									tlm.getList().add(t);
									tlm.invalidate();
								}
							}
						});
						edit.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TerrainDialog(editor,list.getList().getSelectedValue()).setVisible(true);
								editor.tileDataChanged = true;
								tlm.invalidate();
							}
						});
						delete.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								int result = JOptionPane.showConfirmDialog(editor,
										"Deleting an object will affect all maps that use it."
										+ "\nRemove this object from those maps first."
										+ "\nProceed?",
										"WARNING", JOptionPane.OK_CANCEL_OPTION);
								if (result==JOptionPane.OK_OPTION) {
									String s = JOptionPane.showInputDialog(editor,
											"Just to double check, are you sure?");
									if (s.equals("yup")) {
										Terrain t = list.getList().getSelectedValue();
										//editor.tiles.remove(t);
										editor.tileTable.remove(t.getId());
										editor.tileDataChanged = true;
										tlm.getList().remove(t);
										tlm.invalidate();
									}
								}
							}
						});
					}
					list.setVisible(true);
					editor.setTool(new Tool.TilePaint(editor.getArenaPanel(), list.getList()));
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {
					list.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
				}
			}
		});
		tilePaint.addItemListener(toggleButtonSwitch);
		this.add(tilePaint);
		
		final JToggleButton objectPaint = new JToggleButton();
		stylizeToolButton(objectPaint);
		try {
			objectPaint.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/thing.png"))));
		} catch (IOException e1) {
			objectPaint.setText("Thing");
		}
		objectPaint.setToolTipText("Thing Paint - left to paint, right to remove");
		objectPaint.addItemListener(new ItemListener() {
			ListDialog<Thing> list = null;
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (list==null) {
						final CustomListModel<Thing> tlm = new CustomListModel<Thing>(new ArrayList<Thing>(editor.objectTable.values()));
						JButton add = new JButton("Add");
						JButton edit = new JButton("Edit");
						JButton delete = new JButton("Delete");
						JButton[] buttons = {edit,add,delete};
						list = new ListDialog<Thing>(editor, objectPaint, "Thing", buttons, tlm);
						list.getList().setCellRenderer(cellRenderer);
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								ThingDialog dialog = new ThingDialog(editor,null);
								dialog.setVisible(true);
								
								Thing t = dialog.getThing();
								if (t!=null) {
									editor.getObjectTable().put(t.getId(), t);
									editor.tileDataChanged = true;
									tlm.getList().add(t);
									tlm.invalidate();
								}
							}
						});
						edit.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new ThingDialog(editor,list.getList().getSelectedValue()).setVisible(true);
								editor.tileDataChanged = true;
								tlm.invalidate();
							}
						});
						delete.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								int result = JOptionPane.showConfirmDialog(editor,
										"Deleting an object will affect all maps that use it."
										+ "\nRemove this object from those maps first."
										+ "\nProceed?",
										"WARNING", JOptionPane.OK_CANCEL_OPTION);
								if (result==JOptionPane.OK_OPTION) {
									String s = JOptionPane.showInputDialog(editor,
											"Just to double check, are you sure?");
									if (s.equals("yup")) {
										Thing t = list.getList().getSelectedValue();
										editor.objectTable.remove(t.getId());
										editor.tileDataChanged = true;
										tlm.getList().remove(t);
										tlm.invalidate();
									}
								}
							}
						});
					}
					list.setVisible(true);
					editor.setTool(new Tool.ObjectPaint(editor.getArenaPanel(), list.getList()));
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {
					list.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
				}
			}
		});
		objectPaint.addItemListener(toggleButtonSwitch);
		this.add(objectPaint);
		
		final JToggleButton miscPaint = new JToggleButton();
		stylizeToolButton(miscPaint);
		try {
			miscPaint.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/misc.png"))));
		} catch (IOException e1) {
			miscPaint.setText("Misc");
		}
		miscPaint.setToolTipText("Thing Paint - left to paint, right to remove");
		miscPaint.addItemListener(new ItemListener() {
			ListDialog<Misc> list = null;
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (list==null) {
						final CustomListModel<Misc> tlm = new CustomListModel<Misc>(new ArrayList<Misc>(editor.miscTable.values()));
						JButton add = new JButton("Add");
						JButton edit = new JButton("Edit");
						JButton delete = new JButton("Delete");
						JButton[] buttons = {edit,add,delete};
						list = new ListDialog<Misc>(editor, miscPaint, "Misc", buttons, tlm);
						list.getList().setCellRenderer(cellRenderer);
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								MiscDialog dialog = new MiscDialog(editor,null);
								dialog.setVisible(true);
								
								Misc m = dialog.getItem();
								if (m!=null) {
									editor.miscTable.put(m.getId(), m);
									editor.tileDataChanged = true;
									tlm.getList().add(m);
									tlm.invalidate();
								}
							}
						});
						edit.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new MiscDialog(editor,list.getList().getSelectedValue()).setVisible(true);
								editor.tileDataChanged = true;
								tlm.invalidate();
							}
						});
						delete.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								int result = JOptionPane.showConfirmDialog(editor,
										"Deleting an object will affect all maps that use it."
										+ "\nRemove this object from those maps first."
										+ "\nProceed?",
										"WARNING", JOptionPane.OK_CANCEL_OPTION);
								if (result==JOptionPane.OK_OPTION) {
									String s = JOptionPane.showInputDialog(editor,
											"Just to double check, are you sure?");
									if (s.equals("yup")) {
										Misc m = list.getList().getSelectedValue();
										editor.miscTable.remove(m.getId());
										editor.tileDataChanged = true;
										tlm.getList().remove(m);
										tlm.invalidate();
									}
								}
							}
						});
					}
					list.setVisible(true);
					editor.setTool(new Tool.MiscPaint(editor.getArenaPanel(), list.getList()));
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {
					list.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
				}
			}
		});
		miscPaint.addItemListener(toggleButtonSwitch);
		this.add(miscPaint);
		
		final JToggleButton editSprite = new JToggleButton();
		stylizeToolButton(editSprite);
		try {
			editSprite.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/rotate.png"))));
		} catch (IOException e1) {
			editSprite.setText("Sprite");
		}
		editSprite.setToolTipText("Edit Sprite - left to switch sprite, scroll to rotate, mid to flip, right to clear");
		editSprite.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					editor.setTool(new Tool.SpriteSwitcher(editor.getArenaPanel()));
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
				}
			}
		});
		editSprite.addItemListener(toggleButtonSwitch);
		this.add(editSprite);
		
		final JButton light = new JButton();
		stylizeToolButton(light);
		try {
			light.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/light.png"))));
		} catch (IOException e1) {
			light.setText("Light");
		}
		light.setToolTipText("Update light map");
		light.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.getArenaPanel().generateLightImage();
			}
		});
		light.addItemListener(toggleButtonSwitch);
		this.add(light);
		
		final JToggleButton trigger = new JToggleButton();
		stylizeToolButton(trigger);
		try {
			trigger.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/switch.png"))));
		} catch (IOException e1) {
			trigger.setText("Trigger");
		}
		trigger.setToolTipText("Trigger - left to add / select / set target tile, right to remove");
		trigger.addItemListener(new ItemListener() {
			ListDialog<TileSwitchPreset> list = null;
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (list==null) {
						final CustomListModel<TileSwitchPreset> tlm = new CustomListModel<TileSwitchPreset>(new ArrayList<TileSwitchPreset>(editor.getTriggerTable().values()));
						JButton add = new JButton("Add");
						JButton edit = new JButton("Edit");
						JButton delete = new JButton("Delete");
						JButton[] buttons = {edit,add,delete};
						list = new ListDialog<TileSwitchPreset>(editor, trigger, "Trigger", buttons, tlm);
						list.getList().setCellRenderer(triggerRenderer);
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								TileSwitchDialog dialog = new TileSwitchDialog(editor,null);
								dialog.setVisible(true);
								TileSwitchPreset t = dialog.getTriggerPreset();
								if (t!=null) {
									editor.getTriggerTable().put(t.getId(), t);
									editor.tileDataChanged = true;
									tlm.getList().add(t);
									tlm.invalidate();
								}
							}
						});
						edit.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TileSwitchDialog(editor,list.getList().getSelectedValue()).setVisible(true);
								editor.tileDataChanged = true;
								tlm.invalidate();
							}
						});
						delete.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								int result = JOptionPane.showConfirmDialog(editor,
										"Deleting a trigger will affect all maps that use it."
										+ "\nRemove this trigger from those maps first."
										+ "\nProceed?",
										"WARNING", JOptionPane.OK_CANCEL_OPTION);
								if (result==JOptionPane.OK_OPTION) {
									String s = JOptionPane.showInputDialog(editor,
											"Just to double check, are you sure?");
									if (s.equals("yup")) {
										TileSwitchPreset t = list.getList().getSelectedValue();
										editor.triggerTable.remove(t.getId());
										editor.tileDataChanged = true;
										tlm.getList().remove(t);
										tlm.invalidate();
									}
								}
							}
						});
					}
					list.setVisible(true);
					editor.setTool(new Tool.TriggerPaint(editor.getArenaPanel(), list.getList()));
					editor.getArenaPanel().renderTileSwitchTrigger = true;
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {
					list.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
					editor.getArenaPanel().renderTileSwitchTrigger = false;
				}
			}
		});
		trigger.addItemListener(toggleButtonSwitch);
		this.add(trigger);
		
		final JToggleButton spawn = new JToggleButton();
		stylizeToolButton(spawn);
		try {
			spawn.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/light.png"))));
		} catch (IOException e1) {
			spawn.setText("NPC");
		}
		spawn.setToolTipText("Add spawn points");
		spawn.addItemListener(new ItemListener() {
			SpawnDialog dialog = null;
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (arg0.getStateChange()==ItemEvent.SELECTED) {
					if (dialog==null) {
						dialog = new SpawnDialog(editor,spawn);
						dialog.setVisible(true);
					}
					editor.setTool(new Tool.SpawnPaint(editor.getArenaPanel(), dialog));
				} else if (arg0.getStateChange()==ItemEvent.DESELECTED) {
					dialog.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
				}
			}
		});
		spawn.addItemListener(toggleButtonSwitch);
		this.add(spawn);
	}
	
	private static void stylizeToolButton(AbstractButton button) {
		//button.setOpaque(true);
		button.setFocusable(false);
		//button.setBackground(Color.BLACK);
		button.setBorderPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	}
	
	private ItemListener toggleButtonSwitch = new ItemListener() {
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange()==ItemEvent.SELECTED) {
				if (e.getSource()!=activeButton) {
					// deactivate the current active button & dialog first
					if (activeButton!=null)
						activeButton.doClick();
					
					// set this one to be the one
					activeButton = (JToggleButton) e.getSource();
				}
			} else if (e.getStateChange()==ItemEvent.DESELECTED) {
				if (e.getSource()==activeButton) {
					activeButton = null;
				}
			}
		}};
	
	private ListCellRenderer<CellRenderable> cellRenderer = new ListCellRenderer<CellRenderable>() {
		@Override
		public Component getListCellRendererComponent(JList<? extends CellRenderable> list,
				final CellRenderable value, int index, boolean isSelected, boolean cellHasFocus) {
			BufferedImage iconImage = value.getImage();
			if (iconImage.getHeight() > Sprite.TILE_SPRITE_SIZE ||
					iconImage.getWidth() > Sprite.TILE_SPRITE_SIZE) {
				iconImage = iconImage.getSubimage(0, 0, Sprite.TILE_SPRITE_SIZE, Sprite.TILE_SPRITE_SIZE);
			}
			ImageIcon icon = new ImageIcon(iconImage);
			JLabel tile = new JLabel(icon, SwingConstants.LEFT);
			tile.setOpaque(true);
			tile.setFont(GUIFactory.font_s);
			tile.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			if (isSelected) {
				tile.setBackground(list.getSelectionBackground());
				tile.setForeground(list.getSelectionForeground());
			}
			else {
				tile.setBackground(list.getBackground());
				tile.setForeground(list.getForeground());
			}
			tile.setToolTipText(value.getName());
			return tile;
		}
	};
	
	private ListCellRenderer<TileSwitchPreset> triggerRenderer = new ListCellRenderer<TileSwitchPreset>() {
		@Override
		public Component getListCellRendererComponent(JList<? extends TileSwitchPreset> list,
				final TileSwitchPreset value, int index, boolean isSelected, boolean cellHasFocus) {
			try {
				BufferedImage oriImage = null;
				oriImage = value.getOriginalThing().getImage();
				if (oriImage.getHeight() > Sprite.TILE_SPRITE_SIZE ||
						oriImage.getWidth() > Sprite.TILE_SPRITE_SIZE) {
					oriImage = oriImage.getSubimage(0, 0, Sprite.TILE_SPRITE_SIZE, Sprite.TILE_SPRITE_SIZE);
				}
				BufferedImage switchImage = null;
				switchImage = value.getSwitchThing().getImage();
				if (switchImage.getHeight() > Sprite.TILE_SPRITE_SIZE ||
						switchImage.getWidth() > Sprite.TILE_SPRITE_SIZE) {
					switchImage = switchImage.getSubimage(0, 0, Sprite.TILE_SPRITE_SIZE, Sprite.TILE_SPRITE_SIZE);
				}
				
				
				ImageIcon icon = new ImageIcon(oriImage);
				JLabel tile = new JLabel(icon, SwingConstants.LEFT);
				tile.setOpaque(true);
				tile.setFont(GUIFactory.font_s);
				tile.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
				tile.setToolTipText(value.getOriginalThing().getName());
				
				ImageIcon sicon = new ImageIcon(switchImage);
				JLabel stile = new JLabel(sicon, SwingConstants.LEFT);
				stile.setOpaque(true);
				stile.setFont(GUIFactory.font_s);
				stile.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
				stile.setToolTipText(value.getSwitchThing().getName());
				
				JLabel triggerName = new JLabel(value.getName());
				triggerName.setFont(GUIFactory.font_s);
				
				JPanel panel = new JPanel();
				panel.add(tile);
				panel.add(stile);
				panel.add(triggerName);
				
				if (isSelected) {
					panel.setBackground(list.getSelectionBackground());
					panel.setForeground(list.getSelectionForeground());
				}
				else {
					panel.setBackground(list.getBackground());
					panel.setForeground(list.getForeground());
				}
				return panel;
			}
			catch (Exception e) {
				return new JPanel();
			}
		}
	};
}
