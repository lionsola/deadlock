package editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

import client.gui.GUIFactory;
import editor.Tool.*;
import editor.dialogs.ListDialog;
import editor.dialogs.NewLightDialog;
import editor.dialogs.TileBGDialog;
import editor.dialogs.TileDialog;
import server.world.Thing;
import server.world.Terrain;

public class ToolMenu extends JPanel {
	private static final long serialVersionUID = 5669888117742429060L;
	final Editor editor;
	
	JToggleButton activeButton; 
	
	public ToolMenu(final Editor editor) {
		this.editor = editor;
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		final JButton move = new JButton("Move");
		move.addActionListener(new ActionListener() { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.setTool(new MoveTool(editor.getArenaPanel()));
			}});
		this.add(move);
		
		final JToggleButton tilePaint = new JToggleButton("Tiles");
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
						list = new ListDialog<Terrain>(editor, tilePaint, "Tile", buttons, tlm, null,null);
						
						list.getList().setCellRenderer(cellRenderer);
						list.getList().addListSelectionListener(new ListSelectionListener() {
							@Override
							public void valueChanged(ListSelectionEvent e) {
								editor.setTool(new TilePaint(editor.getArenaPanel(), list.getList().getSelectedValue()));
							}
						});
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TileBGDialog(editor,null).setVisible(true);
								tlm.invalidate();
							}
						});
						edit.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TileBGDialog(editor,list.getList().getSelectedValue()).setVisible(true);
							}
						});
						delete.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								int result = JOptionPane.showConfirmDialog(editor,
										"Deleting an object will affect all maps that use it.\nProceed?",
										"WARNING", JOptionPane.OK_CANCEL_OPTION);
								if (result==JOptionPane.OK_OPTION) {
									String s = JOptionPane.showInputDialog(editor,
											"Just to double check, are you sure?");
									if (s.equals("yup")) {
										Terrain t = list.getList().getSelectedValue();
										editor.tiles.remove(t);
										editor.tileTable.remove(t.getId());
										tlm.invalidate();
									}
								}
							}
						});
					}
					list.setVisible(true);
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {
					list.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
				}
			}
		});
		tilePaint.addItemListener(toggleButtonSwitch);
		this.add(tilePaint);
		
		final JToggleButton objectPaint = new JToggleButton("Objects");
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
						list = new ListDialog<Thing>(editor, objectPaint, "Tile", buttons, tlm, null,null);
						list.getList().setCellRenderer(cellRenderer);
						list.getList().addListSelectionListener(new ListSelectionListener() {
							@Override
							public void valueChanged(ListSelectionEvent e) {
								editor.setTool(new Tool.ObjectPaint(editor.getArenaPanel(), list.getList().getSelectedValue()));
							}
						});
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TileDialog(editor,null).setVisible(true);
								tlm.invalidate();
							}
						});
						edit.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								new TileDialog(editor,list.getList().getSelectedValue()).setVisible(true);
							}
						});
						delete.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								int result = JOptionPane.showConfirmDialog(editor,
										"Deleting an object will affect all maps that use it.\nProceed?",
										"WARNING", JOptionPane.OK_CANCEL_OPTION);
								if (result==JOptionPane.OK_OPTION) {
									String s = JOptionPane.showInputDialog(editor,
											"Just to double check, are you sure?");
									if (s.equals("yup")) {
										Thing t = list.getList().getSelectedValue();
										editor.objects.remove(t);
										editor.objectTable.remove(t.getId());
										tlm.invalidate();
									}
								}
							}
						});
					}
					list.setVisible(true);
				} else if (e.getStateChange()==ItemEvent.DESELECTED) {
					list.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
				}
			}
		});
		objectPaint.addItemListener(toggleButtonSwitch);
		this.add(objectPaint);
		
		final JToggleButton editSprite = new JToggleButton("Edit sprite");
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
		
		final JToggleButton nLight = new JToggleButton("LightSource");
		nLight.addActionListener(new ActionListener() {
			NewLightDialog dialog = new NewLightDialog(editor,nLight);
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (nLight.isSelected()) {
					dialog.setVisible(true);
					editor.getArenaPanel().renderLightSource = true;
					editor.setTool(new Tool.NewLightPaint(editor.getArenaPanel(), dialog));
				} else {
					editor.getArenaPanel().renderLightSource = false;
					editor.getArenaPanel().generateLightImage();
					dialog.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
				}
			}
		});
		nLight.addItemListener(toggleButtonSwitch);
		this.add(nLight);
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
			ImageIcon icon = new ImageIcon(value.getImage());
			JLabel tile = new JLabel(value.getName(), icon, SwingConstants.LEFT);
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
			return tile;
		}
	};
	
}
