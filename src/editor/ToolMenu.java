package editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.graphics.Sprite;
import client.gui.GUIFactory;
import editor.Tool.*;
import editor.dialogs.ListDialog;
import editor.dialogs.LightSourceDialog;
import editor.dialogs.TerrainDialog;
import editor.dialogs.ThingDialog;
import editor.dialogs.TriggerDialog;
import server.world.Thing;
import server.world.TriggerPreset;
import server.world.Terrain;

public class ToolMenu extends JPanel {
	private static final long serialVersionUID = 5669888117742429060L;
	final Editor editor;
	
	JToggleButton activeButton; 
	
	public ToolMenu(final Editor editor) {
		this.editor = editor;
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		final JButton move = new JButton();
		stylizeToolButton(move);
		try {
			move.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/hand.png"))));
		} catch (IOException e1) {
			move.setText("H");
		}
		move.setToolTipText("Hand Tool");
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
		tilePaint.setToolTipText("Terrain Paint");
		tilePaint.addItemListener(new ItemListener() {
			ListDialog<Terrain> list = null;
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (list==null) {
						final CustomListModel<Terrain> tlm = new CustomListModel<Terrain>(editor.tiles);
						JButton add = new JButton("Add");
						JButton edit = new JButton("Edit");
						JButton delete = new JButton("Delete");
						JButton[] buttons = {edit,add,delete};
						list = new ListDialog<Terrain>(editor, tilePaint, "Terrain", buttons, tlm);
						
						list.getList().setCellRenderer(cellRenderer);
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TerrainDialog(editor,null).setVisible(true);
								tlm.invalidate();
							}
						});
						edit.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TerrainDialog(editor,list.getList().getSelectedValue()).setVisible(true);
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
										editor.tiles.remove(t);
										editor.tileTable.remove(t.getId());
										editor.tileDataChanged = true;
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
		objectPaint.setToolTipText("Thing Paint");
		objectPaint.addItemListener(new ItemListener() {
			ListDialog<Thing> list = null;
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (list==null) {
						final CustomListModel<Thing> tlm = new CustomListModel<Thing>(editor.objects);
						JButton add = new JButton("Add");
						JButton edit = new JButton("Edit");
						JButton delete = new JButton("Delete");
						JButton[] buttons = {edit,add,delete};
						list = new ListDialog<Thing>(editor, objectPaint, "Thing", buttons, tlm);
						list.getList().setCellRenderer(cellRenderer);
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new ThingDialog(editor,null).setVisible(true);
								tlm.invalidate();
							}
						});
						edit.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new ThingDialog(editor,list.getList().getSelectedValue()).setVisible(true);
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
										editor.objects.remove(t);
										editor.objectTable.remove(t.getId());
										editor.tileDataChanged = true;
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
		
		final JToggleButton editSprite = new JToggleButton();
		stylizeToolButton(editSprite);
		try {
			editSprite.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/rotate.png"))));
		} catch (IOException e1) {
			editSprite.setText("Sprite");
		}
		editSprite.setToolTipText("Edit Sprite");
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
		
		final JToggleButton light = new JToggleButton();
		stylizeToolButton(light);
		try {
			light.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/light.png"))));
		} catch (IOException e1) {
			light.setText("Light");
		}
		light.setToolTipText("Light Source");
		light.addActionListener(new ActionListener() {
			LightSourceDialog dialog = null;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (light.isSelected()) {
					if (dialog==null) {
						dialog = new LightSourceDialog(editor,light);
					}
					dialog.setVisible(true);
					editor.getArenaPanel().renderLightSource = true;
					editor.setTool(new Tool.LightPaint(editor.getArenaPanel(), dialog));
				} else {
					editor.getArenaPanel().renderLightSource = false;
					editor.getArenaPanel().generateLightImage();
					dialog.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
				}
			}
		});
		light.addItemListener(toggleButtonSwitch);
		this.add(light);
		
		final JToggleButton trigger = new JToggleButton();
		stylizeToolButton(trigger);
		try {
			trigger.setIcon(new ImageIcon(ImageIO.read(new File("resource/editor/switch.png"))));
		} catch (IOException e1) {
			trigger.setText("Light");
		}
		trigger.setToolTipText("Light Source");
		trigger.addItemListener(new ItemListener() {
			ListDialog<TriggerPreset> list = null;
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange()==ItemEvent.SELECTED) {
					if (list==null) {
						final CustomListModel<TriggerPreset> tlm = new CustomListModel<TriggerPreset>(editor.triggers);
						JButton add = new JButton("Add");
						JButton edit = new JButton("Edit");
						JButton delete = new JButton("Delete");
						JButton[] buttons = {edit,add,delete};
						list = new ListDialog<TriggerPreset>(editor, trigger, "Trigger", buttons, tlm);
						list.getList().setCellRenderer(triggerRenderer);
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TriggerDialog(editor,null).setVisible(true);
								tlm.invalidate();
							}
						});
						edit.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TriggerDialog(editor,list.getList().getSelectedValue()).setVisible(true);
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
										TriggerPreset t = list.getList().getSelectedValue();
										editor.triggers.remove(t);
										editor.triggerTable.remove(t.getId());
										editor.tileDataChanged = true;
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
	
	private ListCellRenderer<TriggerPreset> triggerRenderer = new ListCellRenderer<TriggerPreset>() {
		@Override
		public Component getListCellRendererComponent(JList<? extends TriggerPreset> list,
				final TriggerPreset value, int index, boolean isSelected, boolean cellHasFocus) {
			BufferedImage oriImage = value.getOriginalThing().getImage();
			
			if (oriImage.getHeight() > Sprite.TILE_SPRITE_SIZE ||
					oriImage.getWidth() > Sprite.TILE_SPRITE_SIZE) {
				oriImage = oriImage.getSubimage(0, 0, Sprite.TILE_SPRITE_SIZE, Sprite.TILE_SPRITE_SIZE);
			}
			
			BufferedImage switchImage = value.getSwitchThing().getImage();
			if (switchImage.getHeight() > Sprite.TILE_SPRITE_SIZE ||
					switchImage.getWidth() > Sprite.TILE_SPRITE_SIZE) {
				switchImage = switchImage.getSubimage(0, 0, Sprite.TILE_SPRITE_SIZE, Sprite.TILE_SPRITE_SIZE);
			}
			
			JPanel panel = new JPanel();
			
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
	};
}
