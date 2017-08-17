package server.status;

import server.character.Entity;
import server.projectile.Bullet;
import server.world.World;

public class Leaping extends StatusEffect {
	private final double maxSpeed = 3;
	
	private double originalSpeed;
	private double direction;
	
	private double increasedSpeed = 0;
	
	public Leaping(Entity self) {
		super(self, 800);
	}

	@Override
	public void onFinish(World w) {
		getSelf().setEnabled(true);
		getSelf().addSpeedMod(-increasedSpeed);
		
		Entity c = getSelf();
		w.addProjectile(new Bullet(c,c.getX(),c.getY(),direction, 0.1, 3, 10, 1));
		w.addProjectile(new Bullet(c,c.getX(),c.getY(),direction+Math.PI/4, 0.1, 3, 10, 1));
		w.addProjectile(new Bullet(c,c.getX(),c.getY(),direction-Math.PI/4, 0.1, 3, 10, 1));
	}

	@Override
	public void start() {
		getSelf().setEnabled(false);
		
		direction = getSelf().getDirection();
		
		double dx = Math.cos(direction);
		double dy = -Math.sin(direction);
		
		getSelf().setDx(dx);
		getSelf().setDy(dy);
		
		originalSpeed = getSelf().getSpeedF();
	}
	
	@Override
	public void update(World w) {
		if (!isFinished()) {
			double targetSpeed = 0;
			Entity hit = null;
			for (Entity e:w.getCharacters()) {
				if (e.team!=getSelf().team && 
						getSelf().getPosition().distance(e.getPosition()) < 0.8*(e.getRadius()+getSelf().getRadius())) {
					hit = e;
				}
			}
			if (hit!=null) {
				this.setDuration(getElapsed());
				targetSpeed = 0;
			}
			else if (getElapsed()<getDuration()*0.3) {
				targetSpeed = -originalSpeed*0.6;
			} else if (getElapsed()<getDuration()*0.8){
				targetSpeed = maxSpeed;
			} else {
				targetSpeed = -originalSpeed*0.8;
			}
			getSelf().addSpeedMod(targetSpeed-increasedSpeed);
			increasedSpeed = targetSpeed;
		}
		super.update(w);
	}
}
