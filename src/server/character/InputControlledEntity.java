package server.character;

import server.ability.*;
import server.ai.NPCBrain;
import server.passive.*;
import server.projectile.HitMod;
import server.weapon.Weapon;
import server.weapon.WeaponFactory;
import server.world.Arena;
import server.world.Geometry;
import server.world.Utils;
import server.world.World;
import shared.network.CharData;
import shared.network.GameDataPackets.InputPacket;
import shared.network.NPCData;
import shared.network.event.GameEvent.*;
import shared.network.event.VoiceEvent;

import java.awt.geom.Point2D;

import client.gui.GameWindow;
import editor.SpawnPoint.Behaviour;
import editor.SpawnPoint.CharType;

/**
 * The <code>ControlledCharacter</code> class defines the behaviour that all types of Characters
 * must inherit.
 * 
 * A <code>ControlledCharacter</code> also uses a specific set of <code>Weapons</code>.
 */
public class InputControlledEntity extends Entity {
	
	public static final double MOVEMENT_SWAY_FACTOR = 0.004;
	public static final double ROTATION_DISPERSION_FACTOR = 0.04;
	public static final double MAX_DISPERSION_ANGLE = 0.09;
	
	private double instabilityMod = 1;
	private double recoilMod = 1;

	private double targetDirection = 0;

	private Weapon primary; // the primary server.weapon of the server.character class
	private Ability ability;
	private Passive passive;
	
	private InputPacket input = new InputPacket();
	public NPCBrain brain;

	public InputControlledEntity(int id, int team, int typeId) {
		super(id,team,typeId);
	}
	
	public void initAI(Arena a, Behaviour b) {
		brain = new NPCBrain(b,a,this);
	}
	
	/**
	 * Update the server.character in the specified world
	 * 
	 * @param world
	 *            the world in which the server.character is in.
	 */
	@Override
	public void update(World world) {
		synchronized (input) {
			if (isDead() || !isEnabled()) {
				input.up = false;
				input.down = false;
				input.left = false;
				input.right = false;
				
				input.fire1 = false;
				input.fire2 = false;
				input.reload = false;
				input.sneaking = false;
			}
			
			if (isEnabled()) {
				// translate directional input into movement vector
				processInput(world);
			}
			
			// apply collision detection to correct the movement vector
			super.updateCollision(world);
			
			super.updatePerception(world);
			
			// send ping
			if (passive!=null)
				passive.update(world);
			
			if (ability!=null && ability.isEnabled())
				ability.update(world);
				
			if (!isDead()) {
				if (primary!=null && primary.isEnabled())
					primary.update(world, this);
				
				
				super.updateStatusEffects(world);
				
				super.updatePosition(world);
				super.updateNoise(world);
				super.updateExposure(world);
				
				if (brain!=null) {
					brain.update(getPerception());
					getPerception().events.clear();
				}
			}
			
			updateCrosshair();
		}
	}
	
	private void updateCrosshair() {
		// add the direction swaying when walking / running
		//addDispersion(instaF*MOVEMENT_DISPERSION_FACTOR*getCurrentSpeed()*GameWindow.MS_PER_UPDATE);
		direction += sway*getInstaMod()*MOVEMENT_SWAY_FACTOR*getCurrentSpeed()*GameWindow.MS_PER_UPDATE;
		
		// move character direction toward the target direction slowly 
		//addDispersion(-DISPERSION_DEC*(0.5+charDispersion));
		double delta = Geometry.wrapAngle(targetDirection - direction);
		double stabFactor = (0.5+Math.abs(delta)/MAX_DISPERSION_ANGLE);
		direction += Math.copySign(0.01*MAX_DISPERSION_ANGLE*stabFactor,delta);
		
		// limit the difference
		if (Math.abs(delta)>MAX_DISPERSION_ANGLE) {
			direction = targetDirection + Math.copySign(MAX_DISPERSION_ANGLE,-delta);
		}
	}

	public double getCrosshairSize() {
		//double angle = Math.abs(gunDirection)+getWeapon().type.gunDispersion;
		double angle = getWeapon().type.getGunDispersion();
		return Math.tan(angle)*Point2D.distance(getX(),getY(),getInput().cx,getInput().cy);
	}

	/**
	 * Returns the primary server.weapon of the server.character
	 * 
	 * @return the primary server.weapon of the server.character
	 */
	public Weapon getWeapon() {
		return primary;
	}

	/**
	 * Process the input
	 * 
	 * @param input
	 *            the input to be processed
	 */
	private void processInput(World w) {
		// update direction
		if (!isDead()) {
			double newDirection = Math.atan2(getY() - getInput().cy, getInput().cx - getX());
			double dDirection = Geometry.wrapAngle(newDirection - targetDirection);
			direction += dDirection*(1-ROTATION_DISPERSION_FACTOR);
			targetDirection = newDirection;
		}
		if (getInput().down && getInput().up) {
			getInput().down = false;
			getInput().up = false;
		}
		if (getInput().left && getInput().right) {
			getInput().left = false;
			getInput().right = false;
		}
		
		double dx = 0, dy = 0;
		
		if (getInput().down)
			dy = 1;
		else if (getInput().up)
			dy = -1;
		if (getInput().right)
			dx = 1;
		else if (getInput().left)
			dx = -1;
		
		if (dx!=0 && dy!=0) {
			double SQRT2INV = 1/Math.sqrt(2);
			dx *= SQRT2INV;
			dy *= SQRT2INV;
		}
		
		if (getInput().sneaking) {
			dx *= 0.7;
			dy *= 0.7;
		}
		
		setDx(dx);
		setDy(dy);
		
		// process chat event
		if (input.chatText!=null)
			w.addSound(VoiceEvent.CUSTOM_LINE_ID, VoiceEvent.DEFAULT_VOLUME, getX(), getY(), input.chatText);
	}
	
	public void setInput(InputPacket input) {
		this.input = input;
	}

	public InputPacket getInput() {
		return input;
	}
	
	public void setWeapon(Weapon w) {
		primary = w;
	}
	
	public void setAbilty(Ability a) {
		ability = a;
	}
	
	public void setPassive(Passive p) {
		passive = p;
	}
	
	public double getInstaMod() {
		return instabilityMod;
	}
	
	public void addInstaMod(double instaMod) {
		this.instabilityMod += instaMod;
	}
	
	@Override
	public CharData generatePartial() {
		if (brain!=null) {
			return new NPCData(this);
		} else {
			return super.generatePartial();
		}
	}
	
	@Override
	public void reset(World w) {
		super.reset(w);
		if (primary!=null) {
			primary.reset();
		}
		if (ability!=null) {
			ability.reset();
		}
		if (brain!=null) {
			initAI(w.getArena(), brain.getBehaviour());
		}
	}
	
	@Override
	public double getNoiseF() {
		return super.getNoiseF()*(getInput().sneaking?0.5:1);
	}
	
	@Override
	public double getSizeF() {
		return super.getSizeF()*(getInput().sneaking?0.9:1);
	}
	
	@Override
	public void onHit(World w, double damage, int sourceId) {
		if (isDead())
			return;
		super.onHit(w, damage, sourceId);
		if (brain!=null) {
			brain.addAlertness(damage/100);
		}
		if (getHealthPoints()<=0) {
			w.getEventListener().onEventReceived(new PlayerDieEvent(sourceId, id));
		}
	}
	
	public Point2D getHead() {
		Point2D head = Geometry.PolarToCartesian(getRadius()*0.4, getDirection());
		head.setLocation(head.getX()+getX(),head.getY()+getY());
		return head;
	}

	public static void initAbilitySet(InputControlledEntity entity, int weaponMod, int hitMod) {
		switch (CharType.valueOf(entity.typeId)) {
			case Beta:
				entity.setWeapon(WeaponFactory.createGun(3,weaponMod,hitMod,entity));
				entity.setAbilty(new ChargedAbility.ThrowFlashGrenade(entity));
				entity.setPassive(new Shield(entity));
				break;
			case Pi:
				entity.setWeapon(WeaponFactory.createGun(2,weaponMod,hitMod,entity));
				entity.setAbilty(new Optics.Binocular(entity));
				entity.setPassive(new Mark(entity));
				break;
			case Gamma:
				entity.setWeapon(WeaponFactory.createGun(1,weaponMod,hitMod,entity));
				entity.setAbilty(new Optics.Scope(entity));
				entity.setPassive(new Overwatch(entity));
				break;
			case Ju:
				entity.setWeapon(WeaponFactory.createGun(4,weaponMod,hitMod,entity));
				entity.setAbilty(new HearingAmplifier(entity));
				entity.setPassive(new Backstab(entity));
				break;
			case Alpha:
				entity.setWeapon(WeaponFactory.createGun(0,weaponMod,hitMod,entity));
				entity.setAbilty(new ChargedAbility.ThrowFragGrenade(entity));
				entity.setPassive(new Assault(entity));
				break;
			case Officer:
				entity.setWeapon(WeaponFactory.createGun(7,weaponMod,hitMod,entity));
				entity.setAbilty(new Flashlight(entity));
				entity.setPassive(new Assault(entity));
				break;
			case MOfficer:
				entity.setWeapon(WeaponFactory.createGun(Weapon.MELEE_ID,weaponMod,hitMod,entity));
				entity.setAbilty(new Flashlight(entity));
				entity.setPassive(new Assault(entity));
				break;
			case Werewolf:
				entity.setWeapon(WeaponFactory.createGun(Weapon.BITE_ID,weaponMod,HitMod.Sharp.id,entity));
				entity.setAbilty(Leap.wolfBig(entity));
				entity.setPassive(new SummonWolf(entity));
				break;
			case Wolf:
				entity.setWeapon(WeaponFactory.createGun(Weapon.BITE_ID,weaponMod,HitMod.Sharp.id,entity));
				entity.setAbilty(Leap.wolf(entity));
				if (Utils.random().nextBoolean()) {
					entity.setPassive(new Growth(entity));
				} else {
					entity.setPassive(new Stealth(entity,1000,1000));
				}
				break;
			case WolfBig:
				entity.setWeapon(WeaponFactory.createGun(Weapon.BITE_ID,weaponMod,HitMod.Sharp.id,entity));
				entity.setAbilty(Leap.wolfBig(entity));
				entity.setPassive(new Assault(entity));
				break;
			case Ghoul:
				entity.setWeapon(WeaponFactory.createGun(Weapon.BITE_ID,weaponMod,HitMod.Zombie.id,entity));
				//entity.setAbilty(new SimpleAbility.Leap(entity));
				entity.setPassive(new FastRegen(entity));
				break;
			default:
				System.out.println("Error: Unsupported char type");
				System.exit(-1);
				break;
		}
	}
	
	public double getTargetDirection() {
		return targetDirection;
	}

	public Ability getAbility() {
		return ability;
	}

	public Passive getPassive() {
		return passive;
	}

	public double getRecoilMod() {
		return recoilMod;
	}
	
	public void addRecoilMod(double recoilMod) {
		this.recoilMod += recoilMod;
	}
}
