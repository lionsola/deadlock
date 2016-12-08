package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
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
	private int weaponId = -1;
	private int abilityId = -1;
	private int passiveId = -1;
	final int SIZE = 40;
	
	public AbilityBar(GameScreen parent) {
		super();
		
		weapon = new AbilityIcon();
		add(weapon);
		ability = new AbilityIcon();
		add(ability);
		setOpaque(false);
		
		//ability = new AbilityIcon();
		//add(ability);
	}
	
	public AbilityBar(GameScreen parent, int weaponId, int abilityId, int passiveId) {
		super();
		this.weaponId = weaponId;
		this.abilityId = abilityId;
		this.passiveId = passiveId;
		
		weapon = new AbilityIcon();
		weapon.setAbility(weaponId);;
		add(weapon);
		ability = new AbilityIcon();
		ability.setAbility(abilityId);
		add(ability);
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
		weapon.update(p.weaponCooldown);
		ability.update(p.abilityCooldown);
	}
	
	public class AbilityIcon extends JLabel {
		private static final long serialVersionUID = 8228610117418905743L;
		volatile private float fill = 0;
		
		public AbilityIcon() {
			int BORDER = 2;
			setBackground(GUIFactory.TRANSBLACK);
			setOpaque(true);
			setBorder(BorderFactory.createLineBorder(GUIFactory.UICOLOR,2,true));
			int w = SIZE+BORDER*2;
			setPreferredSize(new Dimension(w,w));
			setSize(new Dimension(w,w));
		}
		
		public void setAbility(int id) {
			setIcon(new ImageIcon(Sprite.getAbilityIcon(id).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH)));
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
