package server.passive;

import server.character.Armor;
import server.character.InputControlledEntity;
import server.world.Geometry;
import server.world.Projectile;
import server.world.World;

public class Shield extends Passive {
	private final double MIN_START = -Math.PI*0.3;
	private final double MAX_START = Math.PI*0.25;
	
	private final double DEFAULT_START = -Math.PI*0.15;
	private final double DEFAULT_ANGLE = Math.PI*0.5;
	
	private final double RUNNING_START = Math.PI*0.2;
	
	private final double CHANGE_RATE = 0.012;
	private final Armor shield;
	public Shield(InputControlledEntity self) {
		super(self);
		shield = new Armor(self,DEFAULT_START,DEFAULT_ANGLE) {
			@Override
			protected void applyEffect(Projectile p) {
				double prevAngle = Math.atan2(p.getPrevY()-self().getY(),p.getPrevX()-self().getX());
				double curAngle = Math.atan2(p.getY()-self().getY(),p.getX()-self().getX());
				double dAngle = -Geometry.wrapAngle(curAngle - prevAngle);
				double force = p.getSize()*p.getSpeed();
				self().setDirection(self().getDirection()+dAngle*force/20);
				setStart(getStart() + dAngle*force/3);
				super.applyEffect(p);
			}};
		self.setArmor(shield);
	}

	@Override
	protected void onUpdate(World w) {
		// if player is stationary or moving slowly
		if (!self().isMoving() || self().getInput().sneaking) {
			// reposition the shield properly
			double d = DEFAULT_START - shield.getStart();
			double recovery = CHANGE_RATE*(0.5+Math.abs(d/(MAX_START - DEFAULT_START)));
			double inc = Math.copySign(Math.min(Math.abs(d),recovery), d);
			shield.setStart(Math.min(MAX_START, Math.max(MIN_START, shield.getStart()+inc)));
		} // else if he's running
		else {
			// hold the shield aside
			double d = RUNNING_START - shield.getStart();
			double recovery = CHANGE_RATE*(0.5+Math.abs(d/(DEFAULT_START - RUNNING_START)));
			double inc = Math.copySign(Math.min(Math.abs(d),recovery), d);
			shield.setStart(Math.min(MAX_START, Math.max(MIN_START, shield.getStart()+inc)));
		}
	}

	@Override
	protected double calculateActivationLevel(World w) {
		return 1-Math.abs((shield.getStart() - DEFAULT_START)/(MAX_START - DEFAULT_START));
	}
}
