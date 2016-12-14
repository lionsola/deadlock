package editor.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import editor.Editor;
import server.world.SoundSource;

public class SoundSourceDialog extends JDialog {
	private static final long serialVersionUID = 5917436825785813483L;
	private JFormattedTextField soundId;
	private JFormattedTextField volume;
	private JFormattedTextField frequency;
	private JCheckBox random;
	public SoundSourceDialog (final Window owner, Editor editor, final JToggleButton button, SoundSource ss) {
		super(owner, "Sound source");
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
        panel.add(new JLabel("Sound ID:"),c);
        c.gridx++;
        soundId = new JFormattedTextField();
        panel.add(soundId,c);
        
        c.gridx = 0;
        c.gridy += 1;
        panel.add(new JLabel("Sound volume:"),c);
        volume = new JFormattedTextField();
        c.gridx++;
        panel.add(volume,c);
        
        c.gridy++;
        c.gridx = 0;
        panel.add(new JLabel("Frequency:"),c);
        frequency = new JFormattedTextField();
        c.gridx++;
        panel.add(frequency,c);
        
        c.gridx = 0;
        c.gridy++;
        panel.add(new JLabel("Randomess:"),c);
        c.gridx++;
        random = new JCheckBox();
        panel.add(random,c);
        
        getContentPane().add(panel);
        
        if (ss!=null) {
        	soundId.setValue(ss.getSoundId());
        	volume.setValue(ss.getSoundVolume());
        	frequency.setValue(ss.getFrequency());
        	random.setSelected(ss.isRandom());
        } else {
        	soundId.setValue(0);
        	volume.setValue(0);
        	frequency.setValue(1000);
        }
        
        this.pack();
        Point p = button.getLocationOnScreen();
        p.x += button.getWidth();
        setLocation(p);
	}

	public int getSoundID() {
		return ((Number)soundId.getValue()).intValue();
	}
	
	public double getSoundVolume() {
		return ((Number)volume.getValue()).doubleValue();
	}
	
	public double getFrequency() {
		return ((Number)frequency.getValue()).doubleValue();
	}
	
	public boolean isRandom() {
		return random.isSelected();
	}
}
