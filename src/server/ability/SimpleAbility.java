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

public abstract class SimpleAbility extends Ability {

	public SimpleAbility(int id, InputControlledEntity self, long cooldown) {
		super(id, self, cooldown);
	}
	
	@Override
	public void update(World w) {
		if (!self().isDead() && isReady() && self().getInput().fire2 && !self().getInput().alt) {
			activate(w);
			startCooldown();
		}
		super.update(w);
	}
	
	abstract public void activate(World w);
	
	public static class Growl extends SimpleAbility {

		public Growl(InputControlledEntity self) {
			super(GROWL_ID, self, 8000);
		}

		@Override
		public void activate(World w) {
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
	}
	
}
