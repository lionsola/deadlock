package editor.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import editor.Editor;
import server.world.Thing;
import server.world.Utils;
import server.world.trigger.TileSwitchPreset;

public class TileSwitchDialog extends JDialog implements ActionListener, ItemListener {
	private static final long serialVersionUID = 5917436825785813483L;
	private Editor editor;
	
	private JFormattedTextField id;
	private JTextField name;
	private JLabel switchThingIcon;
	private JLabel originalThingIcon;
	private JButton chooseSwitchThing;
	private JButton chooseOriThing;
	private JComboBox<String> triggerType;
	private JComboBox<String> itemType;
	private JButton save;
	
	TileSwitchPreset triggerPreset;
	
	Thing originalThing;
	Thing switchThing;
	
	private JFormattedTextField soundId;
	private JFormattedTextField soundVolume;
	
	private String[] triggerTypes = {"On while touching", "On/off On Touch", "On/off on touch side"};
	private String[] itemTypes = {"Thing", "Misc"};
	public TileSwitchDialog (final Editor editor, TileSwitchPreset triggerPreset) {
		super(editor, "Trigger", ModalityType.APPLICATION_MODAL);
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
        switchThingIcon.setToolTipText("The switch item");
        topPanel.add(switchThingIcon,c);
        
        c.gridy += 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        chooseOriThing = new JButton("Original");
        chooseOriThing.addActionListener(this);
        topPanel.add(chooseOriThing,c);
        
        c.gridy += 1;
        originalThingIcon = new JLabel();
        originalThingIcon.setHorizontalAlignment(JTextField.CENTER);
        originalThingIcon.setToolTipText("The original item");
        topPanel.add(originalThingIcon,c);
        
        c.gridy += 1;
        triggerType = new JComboBox<String>(triggerTypes);
        topPanel.add(triggerType,c);
        
        c.gridy += 1;
        itemType = new JComboBox<String>(itemTypes );
        itemType.addItemListener(this);
        topPanel.add(itemType,c);
        
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
        	itemType.setSelectedIndex(triggerPreset.getItemType());
    		switchThing = triggerPreset.getSwitchThing();
        	originalThing = triggerPreset.getOriginalThing();
        	if (switchThing!=null) {
        		switchThingIcon.setIcon(new ImageIcon(switchThing.getImage()));
        	}
        	if (originalThing!=null) {
        		originalThingIcon.setIcon(new ImageIcon(originalThing.getImage()));
        	}
        	triggerType.setSelectedIndex(triggerPreset.getTriggerType());
        	soundId.setValue(triggerPreset.getSoundID());
        	soundVolume.setValue(triggerPreset.getSoundVolume());
        } else {
        	id.setValue(Utils.random().nextInt());
        	soundId.setValue(0);
        	soundVolume.setValue(0.0);
        	triggerType.setSelectedIndex(0);
        }
        
        this.setContentPane(topPanel);
        this.pack();
        this.setLocationRelativeTo(editor);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==chooseSwitchThing) {
			// show a list dialog and let the user choose
			switchThing = ListDialog.selectFromList(editor.getObjectTable().values(), this);
			switchThingIcon.setIcon(new ImageIcon(switchThing.getImage()));
			pack();
		} else if (e.getSource()==chooseOriThing) {
			// show a list dialog and let the user choose
			originalThing = ListDialog.selectFromList(editor.getObjectTable().values(), this);
			originalThingIcon.setIcon(new ImageIcon(originalThing.getImage()));
			pack();
		} else if (e.getSource()==save) {
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
					this.triggerPreset = new TileSwitchPreset(idNumber);
				}
			}
			triggerPreset.setItemType(itemType.getSelectedIndex());
			triggerPreset.setName(name.getText());
			triggerPreset.setOriginalThing(originalThing);
			triggerPreset.setSwitchThing(switchThing);
			triggerPreset.setSoundID(((Number)soundId.getValue()).intValue());
			triggerPreset.setSoundVolume(((Number)soundVolume.getValue()).doubleValue());
			triggerPreset.setTriggerType(triggerType.getSelectedIndex());
			editor.tileDataChanged = true;
			// Close the dialog
			this.setVisible(false);
			this.dispose();
		}
	}
	
	public TileSwitchPreset getTriggerPreset() {
		return triggerPreset;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource()==itemType) {
			switchThing = null;
			originalThing = null;
			switchThingIcon.setIcon(null);
			originalThingIcon.setIcon(null);
		}
	}
}
