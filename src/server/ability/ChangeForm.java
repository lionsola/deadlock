package server.ability;

import server.character.ControlledCharacter;
import server.weapon.WeaponFactory;
import server.world.World;

public class ChangeForm extends ToggleAbility {
	private static final double CF_BAT_FOVRANGEMOD = -0.7;
	private static final double CF_BAT_SIZEMOD = -0.7;
	private static final double CF_BAT_SPEEDMOD = 0.7;
	private static final double CF_BAT_HEARMOD = 0.5;
	private static final long CF_COOLDOWN = 3000;
	public static final int CF_BAT_WEAPON = 100;
	public static final int CF_HUMAN_WEAPON = 101;
	
	public ChangeForm(ControlledCharacter self) {
		super(self, CF_COOLDOWN);
	}

	@Override
	protected void onUpdate(World w, ControlledCharacter c) {
		
	}

	@Override
	protected void onActivate(World w, ControlledCharacter c) {
		// RELEASE BATS!!!
		
		// MORPH INTO A BAT TOO!!
		self().addFovRangeMod(CF_BAT_FOVRANGEMOD);
		self().addSizeMod(CF_BAT_SIZEMOD);
		self().addSpeedMod(CF_BAT_SPEEDMOD);
		self().addHearMod(CF_BAT_HEARMOD);
		
		self().setWeapon(WeaponFactory.createGun(CF_BAT_WEAPON,self()));
		
	}

	@Override
	protected void onDeactivate(World w, ControlledCharacter c) {
		
		
		// MORPH BACK INTO VAMPIRE
		self().addFovRangeMod(-CF_BAT_FOVRANGEMOD);
		self().addSizeMod(-CF_BAT_SIZEMOD);
		self().addSpeedMod(-CF_BAT_SPEEDMOD);
		self().addHearMod(-CF_BAT_HEARMOD);
		
		self().setWeapon(WeaponFactory.createGun(CF_HUMAN_WEAPON,self()));
	}
}
