package client.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import client.graphics.Sprite;
import shared.network.FullCharacterData;

public class AbilityBar extends JPanel {
	private static final long serialVersionUID = -438149020666218325L;
	private AbilityIcon weapon;
	private AbilityIcon ability;
	private AbilityIcon passive;
	
	public AbilityBar(GameScreen parent, int weaponId, int abilityId, int passiveId) {
		super();
		int SIZE = 40;
		weapon = new AbilityIcon(Sprite.getAbilityIcon(weaponId).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH));
		add(weapon);
		ability = new AbilityIcon(Sprite.getAbilityIcon(abilityId).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH));
		add(ability);
		setOpaque(false);
		
		//ability = new AbilityIcon();
		//add(ability);
	}
	
	public void update(FullCharacterData p) {
		if (weapon!=null)
			weapon.update(p.weaponCooldown);
		if (ability!=null)
			ability.update(p.abilityCooldown);
		//ability.update(p.abilityCooldown);
	}
	
	public class AbilityIcon extends JLabel {
		private static final long serialVersionUID = 8228610117418905743L;
		volatile private float fill = 0;
		
		public AbilityIcon(Image image) {
			super(new ImageIcon(image));
			setBackground(new Color(0,0,0,0x3f));
			setOpaque(false);
			setBorder(BorderFactory.createLineBorder(new Color(50,100,255)));
		}
		
		void update(float fill) {
			this.fill = fill;
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(new Color(255,255,255,0x3f));
			g.fillRect(0,(int)(fill*getHeight()+0.5),getWidth(),getHeight());
		}
	}
}
