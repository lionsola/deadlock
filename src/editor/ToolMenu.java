package editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.gui.GUIFactory;
import editor.Tool.TilePaint;
import server.world.Tile;
import server.world.TileBG;

public class ToolMenu extends JPanel {
	private static final long serialVersionUID = 5669888117742429060L;
	final Editor editor;
	public ToolMenu(final Editor editor) {
		this.editor = editor;
		this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
		
		final JButton tilePaint = new JButton("Tiles");
		tilePaint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CustomListModel<TileBG> tlm = new CustomListModel<TileBG>(new LinkedList<TileBG>(editor.tiles));
				JButton add = new JButton("Add");
				JButton edit = new JButton("Edit");
				JButton[] buttons = {add,edit};
				final ListDialog<TileBG> list = new ListDialog<TileBG>(editor, tilePaint, "Tile", buttons, tlm, null,null);
				list.setVisible(true);
				list.getList().setCellRenderer(cellRenderer);
				list.getList().addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						editor.setTool(new TilePaint(editor.arenaPanel, list.getList().getSelectedValue()));
					}
				});
				add.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						new TileBGDialog(editor,null).setVisible(true);
					}
				});
				edit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						new TileBGDialog(editor,list.getList().getSelectedValue()).setVisible(true);
					}
				});
			}
		});
		this.add(tilePaint);
		
		final JButton objectPaint = new JButton("Objects");
		objectPaint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				CustomListModel<Tile> tlm = new CustomListModel<Tile>(new LinkedList<Tile>(editor.objects));
				JButton edit = new JButton("Edit");
				JButton[] buttons = {edit};
				final ListDialog<Tile> list = new ListDialog<Tile>(editor, tilePaint, "Tile", buttons, tlm, null,null);
				list.getList().setCellRenderer(cellRenderer);
				list.setVisible(true);
				//list.getList().setCellRenderer();
				list.getList().addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						editor.setTool(new Tool.ObjectPaint(editor.arenaPanel, list.getList().getSelectedValue()));
					}
				});
				edit.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
					}
				});
			}
		});
		
		this.add(objectPaint);
		
		final JButton open = new JButton("Open");
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.openArena();
			}
		});
		this.add(open);
		
		final JButton openNew = new JButton("OpenN");
		openNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.openArenaNew();
			}
		});
		this.add(openNew);
		
		final JButton save = new JButton("Export");
		open.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.saveArena();
			}
		});
		this.add(save);
		
		final JToggleButton light = new JToggleButton("Light");
		light.addActionListener(new ActionListener() {
			LightDialog dialog = new LightDialog(editor,light);
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (light.isSelected()) {
					dialog.setVisible(true);
					editor.arenaPanel.layer = ArenaPanel.Layer.LIGHT;
					editor.setTool(new Tool.LightPaint(editor.arenaPanel, dialog));
				} else {
					editor.arenaPanel.layer = ArenaPanel.Layer.ARENA;
					editor.arenaPanel.generateLightImage();
					dialog.setVisible(false);
					editor.setTool(new Tool.MoveTool(editor.arenaPanel));
				}
			}
		});
		this.add(light);
	}
	
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
