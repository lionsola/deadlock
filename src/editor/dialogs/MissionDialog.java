package editor.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import editor.CustomListModel;
import editor.Editor;
import editor.EditorArena;
import server.network.MissionVar;

public class MissionDialog extends JDialog {
	private static final long serialVersionUID = 4408113214790963813L;
	public enum MissionType {ReachTarget,EliminateAll,KillOne,DefendOne,LocationSequence,LocationSet}
	public enum DataType implements Serializable {Location,Character,Time,Locations}
	EditorArena arena;
	CustomListModel<MissionVar> clm;
	JComboBox<MissionType> type;
	JList<MissionVar> list;
	JSlider dataNo;
	public MissionDialog (Editor editor) {
		super(editor);
		this.arena = editor.getArenaPanel().getArena();
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panel.add(new JLabel("Mission type:"));
		type = new JComboBox<MissionType>(MissionType.values());
		type.setEditable(true);
		type.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				arena.objectiveType = String.valueOf(type.getSelectedItem());
			}});
		type.setSelectedItem(arena.objectiveType);
		panel.add(type);
		
		dataNo = new JSlider(SwingConstants.HORIZONTAL,0,9,5);
		dataNo.setSnapToTicks(true);
		dataNo.setMajorTickSpacing(1);
		dataNo.setPaintTicks(true);
		dataNo.setPaintLabels(true);
		dataNo.setValue(arena.getNoData());
		dataNo.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				arena.setNoData(dataNo.getValue());
			}});
		panel.add(dataNo);
		
		clm = new CustomListModel<MissionVar>(arena.objectiveData);
		list = new JList<MissionVar>(clm);
		list.setFixedCellWidth(300);
		list.setCellRenderer(missionDataRenderer);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					list.clearSelection();
				}
			}
		});
		list.setMinimumSize(new Dimension(300,100));
		panel.add(list);
		
		JPanel newVar = new JPanel();
		final JTextField varName = new JTextField("");
		varName.setColumns(10);
		final JComboBox<DataType> varType = new JComboBox<DataType>(DataType.values());
		final JTextField varValue = new JTextField();
		varValue.setColumns(4);
		
		varType.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (varType.getSelectedItem()==DataType.Time) {
					varValue.setVisible(true);
				} else {
					varValue.setVisible(false);
				}
			}
		});
		
		JButton add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				MissionVar mv = new MissionVar(varName.getText(),varType.getItemAt(varType.getSelectedIndex()));
				if (varType.getSelectedItem()==DataType.Time) {
					mv.setValue(Integer.parseInt(varValue.getText()));
				}
				arena.objectiveData.add(mv);
				list.setSelectedValue(mv, true);
				invalidateList();
			}});
		
		JButton remove = new JButton("Remove");
		remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				arena.objectiveData.remove(list.getSelectedValue());
				invalidateList();
			}});
		
		newVar.add(varName);
		newVar.add(varType);
		newVar.add(varValue);
		newVar.add(add);
		newVar.add(remove);
		panel.add(newVar);
		this.setContentPane(panel);
		pack();
		varValue.setVisible(false);
	}

	public JList<MissionVar> getVarList() {
		return list;
	}

	public void invalidateList() {
		clm.invalidate();
		list.invalidate();
		pack();
	}

	public ListCellRenderer<MissionVar> missionDataRenderer = new ListCellRenderer<MissionVar>() {
		@Override
		public Component getListCellRendererComponent(JList<? extends MissionVar> list, final MissionVar value, int index, boolean selected, boolean focus) {
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
			JLabel label = new JLabel(value.name+" ("+value.type+"): ");
			panel.add(label);
			final JTextField v = new JTextField(Editor.printAnything(value.getValue()));
			v.setHorizontalAlignment(JTextField.RIGHT);
			panel.add(v);
			
			panel.setOpaque(true);
			if (selected) {
				panel.setBackground(list.getSelectionBackground());
				panel.setForeground(list.getSelectionForeground());
			} else {
				panel.setBackground(list.getBackground());
				panel.setForeground(list.getForeground());
			}
			return panel;
		}
	};
}
