package editor.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import editor.Editor;
import server.world.Terrain;
import server.world.Utils;

public class TileBGDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 5917436825785813483L;
	private Editor editor;
	private Terrain tile;
	
	private JFormattedTextField id;
	private JTextField name;
	private JLabel imageName;
	private JButton loadImage;
	private JButton save;
	private JLabel tileImage;
	private BufferedImage curTileImage;
	
	public TileBGDialog (Editor editor, Terrain tile) {
		super(editor, "Edit tile", true);
		this.editor = editor;
		this.tile = tile;
		//Create and populate the top panel.
        JPanel topPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridx = 0;
        c.gridy = 0;
        id = new JFormattedTextField();
        id.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(id,c);
        
        c.gridy += 1;
        name = new JTextField();
        name.setHorizontalAlignment(JTextField.CENTER);
        topPanel.add(name,c);
        
        c.gridy += 1;
        c.fill = GridBagConstraints.BOTH;
        tileImage = new JLabel();
    	topPanel.add(tileImage,c);
        
    	c.gridy += 1;
        imageName = new JLabel();
    	topPanel.add(imageName,c);
    	
        c.gridy += 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        loadImage = new JButton("Load image");
        loadImage.addActionListener(this);
        topPanel.add(loadImage,c);
        
        c.gridy += 1;
        save = new JButton("Save");
        save.addActionListener(this);
        topPanel.add(save,c);
        
        if (tile!=null) {
        	id.setValue(tile.getId());
        	id.setEditable(false);
        	this.tile = tile;
        	name.setText(tile.getName());
        	if (tile.getImage()!=null) {
        		tileImage.setIcon(new ImageIcon(tile.getImage()));
        	}
        	imageName.setText(tile.getImageName());
        } else {
        	int ID = Utils.random().nextInt();
        	//this.tile = new TileBG(ID);
        	id.setValue(ID);
        }
        
        this.setContentPane(topPanel);
        this.pack();
        this.setLocationRelativeTo(editor);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==save) {
			if (this.tile!=null) {
				tile.setName(name.getText());
				tile.setImage(curTileImage);
				tile.setImageName(imageName.getText());
			}
			
			// Close the dialog
			this.setVisible(false);
			this.dispose();
		} else if (arg0.getSource()==loadImage) {
			JFileChooser fc = new JFileChooser("resource/tile/");
	        fc.setMultiSelectionEnabled(false);
	        fc.setFileFilter(new FileNameExtensionFilter("Tile image", "png"));
	        int returnVal = fc.showDialog(this, "Attach");
	        if (returnVal==JFileChooser.APPROVE_OPTION) {
	            File file = fc.getSelectedFile();
	            try {
	            	curTileImage = ImageIO.read(file);
					tileImage.setIcon(new ImageIcon(curTileImage));
					imageName.setText(file.getName());
					this.pack();
				} catch (IOException e) {
					System.err.println("Error while reading tile image");
					e.printStackTrace();
				}
	        }
		}
	}
}
