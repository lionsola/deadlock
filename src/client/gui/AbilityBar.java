package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;

import client.graphics.ImageBlender;
import client.graphics.Sprite;
import shared.network.FullCharacterData;

public class AbilityBar extends JPanel {
	private static final long serialVersionUID = -438149020666218325L;
	private JProgressBar hp;
	
	private AbilityIcon weapon;
	private AbilityIcon ability;
	private AbilityIcon passive;
	private int weaponId = -1;
	private int abilityId = -1;
	private int passiveId = -1;
	final int SIZE = 40;
	
	public AbilityBar(GameScreen parent, ClientPlayer p) {
		super();
		this.weaponId = p.weaponId;
		this.abilityId = p.abilityId;
		this.passiveId = p.passiveId;
		this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
		
		hp = new JProgressBar(JProgressBar.VERTICAL);
		hp.setForeground(GUIFactory.UICOLOR);
		hp.setBackground(GUIFactory.UICOLOR_BG);
		hp.setMinimum(0);
		hp.setMaximum(100);
		hp.setMaximumSize(new Dimension(10, SIZE*4/3));
		hp.setBorder(BorderFactory.createLineBorder(GUIFactory.UICOLOR,2,true));
		add(hp);
		try {
			BufferedImage image = ImageBlender.applyColor(GUIFactory.UICOLOR,ImageIO.read(new File("resource/character/"+p.type.name().toLowerCase()+"_face.png")));
			ImageIcon avatar = new ImageIcon(image.getScaledInstance(SIZE*4/3, SIZE*4/3, Image.SCALE_SMOOTH));
			JLabel avt = new JLabel(avatar);
			avt.setBorder(BorderFactory.createLineBorder(GUIFactory.UICOLOR,2,true));
			add(avt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.add(new JSeparator());
		
		weapon = new AbilityIcon(true);
		weapon.setAbility(weaponId);;
		add(weapon);
		
		ability = new AbilityIcon(true);
		ability.setAbility(abilityId);
		add(ability);
		
		passive = new AbilityIcon(false);
		passive.setAbility(passiveId);
		add(passive);
		
		setOpaque(false);
		
		//ability = new AbilityIcon();
		//add(ability);
	}
	
	public void update(FullCharacterData p, ClientPlayer cp) {
		if (weaponId!=cp.weaponId) {
			this.weaponId = cp.weaponId;
			weapon.setAbility(cp.weaponId);
			this.invalidate();
		}
		if (abilityId!=cp.abilityId) {
			this.abilityId = cp.abilityId;
			ability.setAbility(cp.abilityId);
			this.invalidate();
		}
		if (passiveId!=cp.passiveId) {
			this.passiveId = cp.passiveId;
			passive.setAbility(cp.passiveId);
			this.invalidate();
		}
		hp.setValue((int)p.healthPoints);
		weapon.update(p.weaponCooldown);
		ability.update(p.abilityCooldown);
		passive.update(p.passiveLevel);
	}
	
	public class AbilityIcon extends JLabel {
		private static final long serialVersionUID = 8228610117418905743L;
		volatile private float fill = 0;
		private final boolean active;
		
		public AbilityIcon(boolean active) {
			this.active = active;
			int BORDER = 2;
			setOpaque(false);
			setBorder(BorderFactory.createLineBorder(GUIFactory.UICOLOR,2,true));
			int w = SIZE+BORDER*2;
			setPreferredSize(new Dimension(w,w));
			setSize(new Dimension(w,w));
			//setAlignmentY(TOP_ALIGNMENT);
		}
		
		public void setAbility(int id) {
			BufferedImage icon = Sprite.getAbilityIcon(id);
			if (icon!=null) {
				setIcon(new ImageIcon(icon.getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH)));
			}
		}
		
		void update(float fill) {
			this.fill = fill;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0,0,getWidth(),getHeight());
			if (active) {
				g.setColor(GUIFactory.UICOLOR_BG);
				g.fillRect(0,(int)((1-fill)*getHeight()+0.5),getWidth(),getHeight());
				super.paintComponent(g);
				if (fill<1) {
					g.setColor(new Color(0,0,0,0x8f));
					g.fillRect(0, 0, getWidth(), getHeight());
				}
			} else {
				g.setColor(GUIFactory.UICOLOR_BG);
				g.fillRect(0,(int)((1-fill)*getHeight()+0.5),getWidth(),getHeight());
				super.paintComponent(g);
			}
		}
	}
}
