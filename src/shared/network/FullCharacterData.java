package shared.network;

import server.character.InputControlledEntity;

/**
 * Full Character data, used to send data of the main character to
 * his respective client.
 * 
 */
public class FullCharacterData extends CharData {

	private static final long serialVersionUID = 6568364547122850939L;
	
	public float weaponCooldown;
	public float abilityCooldown;
	
	public float passiveLevel;
	
	public float viewRange;
	public float viewAngle;
	
	public byte ammo;
	public byte maxAmmo;
	
	public float crosshairSize;
	
	public FullCharacterData(InputControlledEntity e) {
		super(e);
		if (e.getWeapon()!=null)
			weaponCooldown = (float) e.getWeapon().getCooldownPercent();
		if (e.getAbility()!=null)
			abilityCooldown = (float) e.getAbility().getCooldownPercent();
		if (e.getPassive()!=null)
			passiveLevel = (float) e.getPassive().getActivationLevel();
		
		viewRange = (float) e.getVision().range;
		viewAngle = (float) e.getVision().angle;
		
		crosshairSize = (float) e.getCrosshairSize();
		ammo = (byte) e.getWeapon().getAmmo();
		maxAmmo = (byte) e.getWeapon().type.getMagSize();
	}
	
	public FullCharacterData() {}
}
