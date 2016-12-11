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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import editor.Editor;
import server.world.Light;
import server.world.Thing;
import server.world.Utils;

/**
 * A dialog to edit a Thing type.
 */
public class ThingDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 5917436825785813483L;
	private Editor editor;
	private Thing tile;
	
	private JFormattedTextField id;
	private JTextField name;
	
	private JLabel tileImage;
	private JLabel imageName;
	private JButton loadImage;
	private JButton save;
	private JCheckBox light;
	private LightSourceDialog lightDialog;
	
	private BufferedImage curTileImage;
	private JCheckBox border;
	private JCheckBox walkable;
	private JCheckBox clear;
	private JComboBox<String> cover;
	private JFormattedTextField spriteSize;
	private JComboBox<String> layer;
	
	public ThingDialog (Editor editor, Thing tile) {
		super(editor, "Edit tile", ModalityType.APPLICATION_MODAL);
		this.editor = editor;
		//Create and populate the top panel.
        JPanel topPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        
        c.gridx = 0;
        c.gridy = 0;
        id = new JFormattedTextField();
        id.setHorizontalAlignment(JTextField.CENTER);
        id.setToolTipText("Thing's ID");
        topPanel.add(id,c);
        
        c.gridy += 1;
        name = new JTextField();
        name.setHorizontalAlignment(JTextField.CENTER);
        name.setToolTipText("Thing's name");
        topPanel.add(name,c);
        
        c.gridy += 1;
        c.fill = GridBagConstraints.BOTH;
        tileImage = new JLabel();
        tileImage.setToolTipText("Thing's sprite");
        topPanel.add(tileImage,c);
        
        c.gridy += 1;
        imageName = new JLabel();
        imageName.setToolTipText("Sprite file");
    	topPanel.add(imageName,c);
        
        c.gridy += 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        loadImage = new JButton("Load image");
        loadImage.addActionListener(this);
        topPanel.add(loadImage,c);
        
        c.gridy += 1;
        spriteSize = new JFormattedTextField();
        spriteSize.setToolTipText("Sprite size");
        topPanel.add(spriteSize,c);
        
        c.gridy += 1;
        light = new JCheckBox("Light");
        light.addActionListener(this);
        topPanel.add(light,c);
        
        c.gridy += 1;
        border = new JCheckBox("Border");
        topPanel.add(border,c);
        
        c.gridy += 1;
        walkable = new JCheckBox("Walkable");
        topPanel.add(walkable,c);
        
        c.gridy += 1;
        clear = new JCheckBox("Clear");
        topPanel.add(clear,c);
        
        c.gridy += 1;
        JPanel coverPanel = new JPanel();
        coverPanel.add(new JLabel("Cover: "));
        String[] coverTypes = {"None","Light","Medium","Heavy"};
        cover = new JComboBox<String>(coverTypes);
        coverPanel.add(cover);
        topPanel.add(coverPanel,c);
        
        c.gridy += 1;
        JPanel layerPanel = new JPanel();
        layerPanel.add(new JLabel("Height: "));
        
        layer = new JComboBox<String>(editor.layerTypes);
        layerPanel.add(layer);
        topPanel.add(layerPanel,c);
        
        c.gridy += 1;
        save = new JButton("Save");
        save.addActionListener(this);
        topPanel.add(save,c);
        
        if (tile!=null) {
        	id.setValue(tile.getId());
        	id.setEditable(false);
        	this.tile = tile;
        	name.setText(tile.getName());
        	walkable.setSelected(tile.isWalkable());
        	border.setSelected(tile.isBorderDrawn());
        	clear.setSelected(tile.isClear());
        	cover.setSelectedIndex(tile.getCoverType());
        	if (tile.getImage()!=null) {
        		tileImage.setIcon(new ImageIcon(tile.getImage()));
        	}
        	imageName.setText(tile.getImageName());
        	curTileImage = tile.getImage();
        	spriteSize.setValue(tile.getSpriteSize());
        	if (tile.getLight()!=null) {
        		light.setSelected(true);
        	}
        	layer.setSelectedIndex(Math.min(layer.getItemCount()-1,tile.getLayer()));
        } else {
        	int ID = Utils.random().nextInt();
        	//this.tile = new TileBG(ID);
        	id.setValue(ID);
        	spriteSize.setValue(1);
        }
        this.setContentPane(topPanel);
        this.setLocationRelativeTo(editor);
        this.pack();
	}

	public Thing getThing() {
		return tile;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource()==save) {
			if (this.tile==null) {
				int idNumber = ((Number)id.getValue()).intValue();
				if (editor.getObjectTable().containsKey(idNumber)) {
					JOptionPane.showMessageDialog(this,"Existing ID");
					return;
				} else if (name.getText().equals("")) {
					JOptionPane.showMessageDialog(this,"Empty name field!");
					return;
				} else if (curTileImage==null || imageName.getText().equals("")) {
					JOptionPane.showMessageDialog(this,"Pick an image!");
					return;				
				} else {
					this.tile = new Thing(idNumber);
				}
			}

			tile.setName(name.getText());
			tile.setImage(curTileImage);
			tile.setImageName(imageName.getText());
			tile.setBorder(border.isSelected());
			
			tile.setCoverType(cover.getSelectedIndex());
			tile.setClear(clear.isSelected());
			tile.setWalkable(walkable.isSelected());
			tile.setSpriteSize(((Number)spriteSize.getValue()).doubleValue());
			tile.setLayer(layer.getSelectedIndex());
			if (light.isSelected()) {
				if (lightDialog!=null) {
					tile.setLight(new Light(lightDialog.getColor(), lightDialog.getRange()));
				}
			} else {
				tile.setLight(null);
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
		} else if (arg0.getSource()==light) {
			if (light.isSelected()) {
				if (lightDialog==null) {
					Light l = tile.getLight();
					if (l!=null) {
						lightDialog = new LightSourceDialog(this, light,l.getColor(),l.getRange());
		        	} else {
		        		lightDialog = new LightSourceDialog(this,light,0xffffff,3);
		        	}
				}
				lightDialog.setModalityType(ModalityType.MODELESS);
				lightDialog.setVisible(true);
			} else {
				if (lightDialog!=null) {
					lightDialog.setVisible(false);
				}
			}
		}
	}
}
