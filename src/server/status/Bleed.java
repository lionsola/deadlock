package server.status;

import client.gui.GameWindow;
import server.character.Entity;
import server.world.World;
import shared.network.event.AnimationEvent;

public class Bleed extends StatusEffect {
	private int counter = 0;
	private int sourceId;
	private double dmgPerTick;
	public Bleed(Entity self, double damage, long duration, int sourceId) {
		super(self, duration);
		this.sourceId = sourceId;
		this.dmgPerTick = damage/(duration/StatusEffect.TICK);
	}

	@Override
	public void onFinish(World w) {
		
	}

	@Override
	public void update(World w) {
		counter += GameWindow.MS_PER_UPDATE;
		if (counter>=StatusEffect.TICK) {
			counter -= StatusEffect.TICK;
			getSelf().onHit(w, dmgPerTick, sourceId);
			AnimationEvent a = new AnimationEvent(AnimationEvent.BLOOD,getSelf().team,
					getSelf().getX(),getSelf().getY(),getSelf().getDirection()+Math.PI);
			a.value = (float) dmgPerTick;
			w.addGlobalAnimation(a);
		}
		super.update(w);
	}

	@Override
	public void start() {
	}

}
