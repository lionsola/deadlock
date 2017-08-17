package server.ability;

import client.gui.GameWindow;
import server.character.Entity;
import server.character.InputControlledEntity;
import server.status.Courage;
import server.status.Fear;
import server.status.StatusEffect;
import server.status.Stunned;
import server.world.Utils;
import server.world.World;
import shared.network.event.SoundEvent;

public class Leap extends SimpleAbility {
	private final double MAX_SPEED;
	private final int DURATION;
	private final int STUN_DURATION;
	
	private double originalSpeed;
	private double direction;
	
	private double increasedSpeed = 0;
	
	boolean leaping = false;
	int elapsed = 0;
	
	

	/*
	public void onFinish(World w) {
		self().setEnabled(true);
		self().addSpeedMod(-increasedSpeed);
		
		Entity c = self();
		w.addProjectile(new Bullet(c,c.getX(),c.getY(),direction, 0.1, 3, 10, 1));
		w.addProjectile(new Bullet(c,c.getX(),c.getY(),direction+Math.PI/4, 0.1, 3, 10, 1));
		w.addProjectile(new Bullet(c,c.getX(),c.getY(),direction-Math.PI/4, 0.1, 3, 10, 1));
	}
*/
	
	public void onFinish(World w) {
		leaping = false;
		self().setEnabled(true);
		self().addSpeedMod(-increasedSpeed);
	};
	
	@Override
	public void update(World w) {
		super.update(w);
		if (leaping) {
			elapsed += GameWindow.MS_PER_UPDATE;
			if (elapsed >= DURATION) {
				leaping = false;
				onFinish(w);
				return;
			}
			
			Entity hit = null;
			for (Entity e:w.getCharacters()) {
				if (e.team!=self().team && 
						self().getPosition().distance(e.getPosition()) < 0.8*(e.getRadius()+self().getRadius())) {
					hit = e;
				}
			}
			if (hit!=null) {
				hit.addStatusEffect(new Stunned(hit,STUN_DURATION));
				onFinish(w);
				return;
			}
			double targetSpeed = 0;
			
			if (elapsed<DURATION*0.3) {
				targetSpeed = -originalSpeed*0.6;
			} else if (elapsed<DURATION*0.8){
				targetSpeed = MAX_SPEED;
			} else {
				targetSpeed = -originalSpeed*0.8;
			}
		
			self().addSpeedMod(targetSpeed-increasedSpeed);
			increasedSpeed = targetSpeed;
		}
		
	}
	public Leap(int id, InputControlledEntity self, int cooldown, int dur, int stunDur, double maxSpeed) {
		super(LEAP_ID, self, 5000);
		STUN_DURATION = stunDur;
		DURATION = dur;
		this.MAX_SPEED = maxSpeed;
	}

	@Override
	public void activate(World w) {
		final double GROWL_VOL = 20;
		w.addSound(SoundEvent.GRENADE_EXPLODE_SOUND_ID, GROWL_VOL, self().getX(), self().getY(), "GRRRHHHHH!!");

		leaping = true;
		elapsed = 0;
		
		self().setEnabled(false);
		
		direction = self().getDirection();
		
		double dx = Math.cos(direction);
		double dy = -Math.sin(direction);
		
		self().setDx(dx);
		self().setDy(dy);
		
		originalSpeed = self().getSpeedF();
	}
	
	public static Leap wolf(InputControlledEntity e) {
		return new Leap(LEAP_ID,e,5000,800,500,2.5);
	}
	
	public static Leap wolfBig(InputControlledEntity e) {
		return new Leap(LEAP_ID,e,5000,800,500,3) {
			@Override
			public void onFinish(World w) {
				super.onFinish(w);
				final double GROWL_VOL = 50;
				w.addSound(SoundEvent.GRENADE_EXPLODE_SOUND_ID, GROWL_VOL, self().getX(), self().getY(), "GRRRHHHHH!!");
				for (InputControlledEntity e:w.getCharacters()) {
					double vol = Utils.getVolumeAtDistance(GROWL_VOL, e.getPosition().distance(self().getPosition()), e.getHearF());
					if (vol > 0 && !e.isDead()) { 
						if (e.team==self().team) {
							e.addStatusEffect(new Courage(e, StatusEffect.DEFAULT_DURATION));
						} else {
							e.addStatusEffect(new Fear(e, StatusEffect.DEFAULT_DURATION));
						}
					}
				}
			}
		};
	}
}

