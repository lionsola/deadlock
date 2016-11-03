package editor.dialogs;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import editor.Editor;

public class LightSourceDialog extends JDialog {
	private static final long serialVersionUID = 5917436825785813483L;
	private JButton color;
	private int range = 3;
	public LightSourceDialog (final Editor editor, final JToggleButton button) {
		super(editor, "Light", false);
        JPanel panel = new JPanel(new GridBagLayout());
        //editor.setTool(tool);
        addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent arg0) {
				button.doClick();
			}});
        JSlider rangePicker = new JSlider(JSlider.HORIZONTAL, 2, 8, range);
        rangePicker.setMajorTickSpacing(1);
        rangePicker.setPaintTicks(true);
        rangePicker.setPaintLabels(true);
        rangePicker.setToolTipText("Light range (in tiles)");
        rangePicker.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
			    JSlider source = (JSlider)e.getSource();
			    if (!source.getValueIsAdjusting()) {
			        range = source.getValue();
			    }
			}});
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridx = 0;
        c.gridy = 0;
        panel.add(rangePicker);
        
        c.gridy += 1;
        color = new JButton("0xffffffff");
        color.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color newColor = JColorChooser.showDialog(
	                     editor,
	                     "Choose Background Color",
	                     color.getBackground());
				color.setBackground(newColor);
				color.setText(Integer.toHexString(newColor.getRGB()));
			}
        });
        color.setToolTipText("Light color (alpha channel is ignored)");
        panel.add(color,c);
        
        this.setContentPane(panel);
        this.pack();
        this.setLocationRelativeTo(editor);
	}

	public int getRange() {
		return range;
	}
	
	public int getColor() {
		return color.getBackground().getRGB();
	}
}
