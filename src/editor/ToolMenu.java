package editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client.gui.GUIFactory;
import server.world.Tile;

public class ToolMenu extends JPanel {
	private static final long serialVersionUID = 5669888117742429060L;
	final Editor editor;
	public ToolMenu(final Editor editor) {
		this.editor = editor;
		final JButton tilePaint = new JButton("Tile paint");
		tilePaint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				TileListModel tlm = new TileListModel(new LinkedList<Tile>(editor.tiles));
				JButton edit = new JButton("Edit");
				JButton[] buttons = {edit};
				final ListDialog<Tile> list = new ListDialog<Tile>(editor, tilePaint, "Tile", buttons, tlm, null,null);
				list.setVisible(true);
				list.getList().setCellRenderer(tileCellRenderer);
				list.getList().addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						editor.setTool(new TilePaintTool(editor.arenaPanel, list.getList().getSelectedValue()));
					}
				});
				edit.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						new TileEditDialog(editor,list.getList().getSelectedValue()).setVisible(true);
						
					}
					
				});
			}
			
		});
		this.add(tilePaint);
	}

	private ListCellRenderer<Tile> tileCellRenderer = new ListCellRenderer<Tile>() {
		@Override
		public Component getListCellRendererComponent(JList<? extends Tile> list, final Tile value, int index, boolean isSelected,
				boolean cellHasFocus) {
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
