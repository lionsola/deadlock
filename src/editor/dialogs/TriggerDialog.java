package editor.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import editor.CustomListModel;
import editor.Editor;
import server.world.Thing;
import server.world.TriggerPreset;
import server.world.Utils;

public class TriggerDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 5917436825785813483L;
	private Editor editor;
	
	private JFormattedTextField id;
	private JTextField name;
	private JLabel switchThingIcon;
	private JLabel originalThingIcon;
	private JButton chooseSwitchThing;
	private JButton chooseOriThing;
	private JButton save;
	
	TriggerPreset triggerPreset;
	Thing originalThing;
	Thing switchThing;
	private JFormattedTextField soundId;
	private JFormattedTextField soundVolume;
	
	public TriggerDialog (final Editor editor, TriggerPreset triggerPreset) {
		super(editor, "Trigger", false);
		this.editor = editor;
		
		//Create and populate the top panel.
        JPanel topPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridx = 0;
        c.gridy = 0;
        id = new JFormattedTextField();
        id.setHorizontalAlignment(JTextField.CENTER);
        id.setToolTipText("Trigger ID");
        topPanel.add(id, c);
        
        c.gridy += 1;
        name = new JTextField();
        name.setHorizontalAlignment(JTextField.CENTER);
        name.setToolTipText("Trigger's name");
        topPanel.add(name, c);
        
        c.gridy += 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        chooseSwitchThing = new JButton("Switch");
        chooseSwitchThing.addActionListener(this);
        topPanel.add(chooseSwitchThing,c);
        
        c.gridy += 1;
        switchThingIcon = new JLabel();
        switchThingIcon.setHorizontalAlignment(JTextField.CENTER);
        switchThingIcon.setToolTipText("The switch object");
        topPanel.add(switchThingIcon,c);
        
        c.gridy += 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        chooseOriThing = new JButton("Original");
        chooseOriThing.addActionListener(this);
        topPanel.add(chooseOriThing,c);
        
        c.gridy += 1;
        originalThingIcon = new JLabel();
        originalThingIcon.setHorizontalAlignment(JTextField.CENTER);
        originalThingIcon.setToolTipText("The original object");
        topPanel.add(originalThingIcon,c);
        
        c.gridy += 1;
        soundId = new JFormattedTextField();
        soundId.setHorizontalAlignment(JTextField.CENTER);
        soundId.setToolTipText("Sound ID");
        topPanel.add(soundId,c);
        
        c.gridy += 1;
        soundVolume = new JFormattedTextField();
        soundVolume.setHorizontalAlignment(JTextField.CENTER);
        soundVolume.setToolTipText("Sound volume");
        topPanel.add(soundVolume,c);
        
        c.gridy += 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        save = new JButton("Save");
        save.addActionListener(this);
        topPanel.add(save,c);
        
        if (triggerPreset!=null) {
        	id.setValue(triggerPreset.getId());
        	id.setEditable(false);
        	this.triggerPreset = triggerPreset;
        	name.setText(triggerPreset.getName());
        	switchThing = triggerPreset.getSwitchThing();
        	originalThing = triggerPreset.getOriginalThing();
        	switchThingIcon.setIcon(new ImageIcon(editor.getObjectTable().get(triggerPreset.getSwitchThingID()).getImage()));
        	originalThingIcon.setIcon(new ImageIcon(editor.getObjectTable().get(triggerPreset.getOriginalThingID()).getImage()));
        	soundId.setValue(triggerPreset.getSoundID());
        	soundVolume.setValue(triggerPreset.getSoundVolume());
        } else {
        	id.setValue(Utils.random().nextInt());
        	soundId.setValue(0);
        	soundVolume.setValue(0.0);
        }
        
        this.setContentPane(topPanel);
        this.pack();
        this.setLocationRelativeTo(editor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==chooseSwitchThing) {
			// show a list dialog and let the user choose
			CustomListModel<Thing> tlm = new CustomListModel<Thing>(editor.getObjectList());
			JButton OK = new JButton("OK");
			JButton[] buttons = {OK};
			final ListDialog<Thing> listDialog = new ListDialog<Thing>(editor, null, "Tile", buttons, tlm);
			OK.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					listDialog.setVisible(false);
					Thing t  = listDialog.getList().getSelectedValue();
					switchThing = t;
					switchThingIcon.setIcon(new ImageIcon(t.getImage()));
					TriggerDialog.this.pack();
				}
			});
			listDialog.setVisible(true);
		} else if (e.getSource()==chooseOriThing) {
			// show a list dialog and let the user choose
			CustomListModel<Thing> tlm = new CustomListModel<Thing>(editor.getObjectList());
			JButton OK = new JButton("OK");
			JButton[] buttons = {OK};
			final ListDialog<Thing> listDialog = new ListDialog<Thing>(editor, null, "Tile", buttons, tlm);
			OK.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					listDialog.setVisible(false);
					Thing t  = listDialog.getList().getSelectedValue();
					originalThing = t;
					originalThingIcon.setIcon(new ImageIcon(t.getImage()));
					TriggerDialog.this.pack();
				}
			});
			listDialog.setVisible(true);
		}
		else if (e.getSource()==save) {
			if (this.triggerPreset==null) {
				int idNumber = ((Number)id.getValue()).intValue();
				if (editor.getTriggerTable().containsKey(idNumber)) {
					JOptionPane.showMessageDialog(this,"Existing ID");
					return;
				} else if (name.getText().equals("")) {
					JOptionPane.showMessageDialog(this,"Empty name field!");
					return;
				} else if (switchThing==null || originalThing==null) {
					JOptionPane.showMessageDialog(this,"Pick an im!");
					return;				
				} else {
					this.triggerPreset = new TriggerPreset(idNumber);
					editor.getTriggerList().add(triggerPreset);
					editor.getTriggerTable().put(triggerPreset.getId(), triggerPreset);
				}
			}

			triggerPreset.setName(name.getText());
			triggerPreset.setOriginalThing(originalThing);
			triggerPreset.setSwitchThing(switchThing);
			triggerPreset.setSoundID(((Number)soundId.getValue()).intValue());
			triggerPreset.setSoundVolume(((Number)soundVolume.getValue()).doubleValue());
			editor.tileDataChanged = true;
			// Close the dialog
			this.setVisible(false);
			this.dispose();
		}
	}
	
	public TriggerPreset getTriggerPreset() {
		return triggerPreset;
	}
}
