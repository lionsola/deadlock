package editor.dialogs;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
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

public class LightSourceDialog extends JDialog {
	private static final long serialVersionUID = 5917436825785813483L;
	private JButton color;
	private JSlider rangePicker;
	public LightSourceDialog (final Window owner, final JToggleButton button, int defaultColor, int defaultRange) {
		super(owner, "Light");
        JPanel panel = new JPanel(new GridBagLayout());
        //editor.setTool(tool);
        addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent arg0) {
				button.doClick();
			}});
        rangePicker = new JSlider(JSlider.HORIZONTAL, 1, 8, defaultRange);
        rangePicker.setMajorTickSpacing(1);
        rangePicker.setPaintTicks(true);
        rangePicker.setPaintLabels(true);
        rangePicker.setToolTipText("Light range (in tiles)");
        
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
	                     LightSourceDialog.this,
	                     "Choose Background Color",
	                     color.getBackground());
				color.setBackground(newColor);
				color.setText(Integer.toHexString(newColor.getRGB()));
			}
        });
        color.setToolTipText("Light color (alpha channel is ignored)");
        color.setText(Integer.toHexString(defaultColor));
        panel.add(color,c);
        
        
        getContentPane().add(panel);
        //setContentPane(panel);
        this.pack();
        Point p = button.getLocationOnScreen();
        p.x += button.getWidth();
        setLocation(p);
	}

	public int getRange() {
		return rangePicker.getValue();
	}
	
	public int getColor() {
		return color.getBackground().getRGB();
	}
}
