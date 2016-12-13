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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import editor.Editor;
import server.world.Terrain;
import server.world.Utils;
import shared.network.event.SoundEvent;

/**
 * A dialog to edit a Terrain type.
 */
public class TerrainDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 5917436825785813483L;
	private Editor editor;
	private Terrain tile;
	
	private JFormattedTextField id;
	private JTextField name;
	private JLabel imageName;
	private JButton loadImage;
	private JButton save;
	private JLabel tileImage;
	private JFormattedTextField soundId;
	private JFormattedTextField soundVolume;
	private BufferedImage curTileImage;
	
	public TerrainDialog (Editor editor, Terrain tile) {
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
        id.setToolTipText("Terrain's ID");
        topPanel.add(id,c);
        
        c.gridy += 1;
        name = new JTextField();
        name.setHorizontalAlignment(JTextField.CENTER);
        name.setToolTipText("Terrain's name");
        topPanel.add(name,c);
        
        c.gridy += 1;
        c.fill = GridBagConstraints.BOTH;
        tileImage = new JLabel();
        tileImage.setToolTipText("Terrain Sprite");
    	topPanel.add(tileImage,c);
        
    	c.gridy += 1;
    	c.fill = GridBagConstraints.HORIZONTAL;
        imageName = new JLabel();
        imageName.setToolTipText("Sprite File");
    	topPanel.add(imageName,c);
    	
        c.gridy += 1;
        loadImage = new JButton("Load image");
        loadImage.addActionListener(this);
        topPanel.add(loadImage,c);
        
        c.gridy += 1;
        soundId = new JFormattedTextField();
        topPanel.add(soundId,c);
        
        c.gridy += 1;
        soundVolume = new JFormattedTextField();
        topPanel.add(soundVolume,c);
        
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
        	curTileImage = tile.getImage();
        	soundId.setValue(tile.getSoundId());
        	soundVolume.setValue(tile.getVolume());
        } else {
        	int ID = Utils.random().nextInt();
        	//this.tile = new TileBG(ID);
        	id.setValue(ID);
        	soundId.setValue(SoundEvent.FOOTSTEP_DEFAULT_ID);
        	soundVolume.setValue(SoundEvent.FOOTSTEP_SOUND_VOLUME);
        }
        
        this.setContentPane(topPanel);
        this.pack();
        this.setLocationRelativeTo(editor);
	}

	public Terrain getTile() {
		return tile;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==save) {
			if (this.tile==null) {
				int idNumber = ((Number)id.getValue()).intValue();
				if (editor.getTerrainTable().containsKey(idNumber)) {
					JOptionPane.showMessageDialog(this,"This ID already exists!");
					return;
				} else if (name.getText().equals("")) {
					JOptionPane.showMessageDialog(this,"Empty name field!");
					return;
				} else if (curTileImage==null || imageName.getText().equals("")) {
					JOptionPane.showMessageDialog(this,"Pick an image!");
					return;
				} else if (!editor.getAudioManager().getSoundMap().containsKey(((Number)soundId.getValue()).intValue())) {
					JOptionPane.showMessageDialog(this,"Non-existent sound!");
					return;
				} else {
					this.tile = new Terrain(idNumber);
				}
			}
			tile.setName(name.getText());
			tile.setImage(curTileImage);
			tile.setImageName(imageName.getText());
			tile.setSoundId(((Number)soundId.getValue()).intValue());
			tile.setVolume(((Number)soundVolume.getValue()).doubleValue());

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
