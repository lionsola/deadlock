package editor.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import client.graphics.ParticleSource;
import editor.Editor;

public class ParticleSourceDialog extends JDialog {
	private static final long serialVersionUID = 5917436825785813483L;
	
	/*
	private JFormattedTextField position;
	
	private JFormattedTextField direction;
	private JFormattedTextField angle;
	private JFormattedTextField speed;
	
	private JFormattedTextField zSpeed;
	private JFormattedTextField zAcc;
	
	private JFormattedTextField rotSpeed;
	
	private JFormattedTextField size;
	private JButton color;
	
	private JFormattedTextField frequency;
	private JCheckBox random;
	*/
	
	JComboBox<String> presets;
	public ParticleSourceDialog (final Window owner, Editor editor, final JToggleButton button, ParticleSource ps) {
		super(owner, "Particle source");
        JPanel panel = new JPanel(new GridBagLayout());
        //editor.setTool(tool);
        addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent arg0) {
				button.doClick();
			}});
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        
        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Presets: "),c);
        c.gridx++;
        String[] keys = new String[ParticleSource.presets.size()];
        keys = ParticleSource.presets.keySet().toArray(keys);
        presets = new JComboBox<String>(keys);
        
        panel.add(presets,c);
        
        this.setContentPane(panel);
        this.pack();
        Point p = button.getLocationOnScreen();
        p.x += button.getWidth();
        setLocation(p);
	}

	public ParticleSource getPreset() {
		return ParticleSource.presets.get(presets.getSelectedItem()).clone();
	}
}
