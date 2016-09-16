package character;

import java.awt.geom.Point2D;
import java.util.List;

import core.World;

/**
 * Defines the behaviour of a Hostage character.
 * 
 * @author Anh Pham
 *
 */
public class Hostage extends AbstractCharacter {
	
	// private static final double SEARCHTHRESHOLD = 150;
	private static final double STOPTHRESHOLD = 30;
	private Point2D.Double dest = null;
	private int friendlyTeam;

	/**
	 * Creating a new hostage character.
	 * 
	 * @param team
	 *            the team the hostage is on.
	 */
	public Hostage(int team) {
		super(ClassStats.classStats.get(0));
		friendlyTeam = team;
	}

	/**
	 * Updates the Hostage.
	 * 
	 * @param world
	 *            the world in which the character is in
	 */
	@Override
	public void update(World world) {
		// if (dest==null || dest.distance(getX(), getY())<SEARCHTHRESHOLD) {
		List<AbstractCharacter> list = world.generateVisibleCharacters(this);
		for (AbstractCharacter ch : list) {
			if (ch instanceof ControlledCharacter) {
				ControlledCharacter cc = (ControlledCharacter) ch;
				if (cc.team == friendlyTeam) {
					System.out.println("New destination at " + cc.getX() + "," + cc.getY());
					dest = new Point2D.Double(cc.getX(), cc.getY());
					break;
				}
			}
		}
		// }

		if (dest != null && dest.distance(getX(), getY()) < STOPTHRESHOLD) {
			dest = null;
			this.setDx(0);
			this.setDy(0);
		}
		// go there
		if (dest != null) {
			double direction = Math.atan2(this.getY() - dest.getY(), dest.getX() - this.getX());
			this.setDirection(direction);
			this.setDx(Math.cos(direction) * getSpeed());
			this.setDy(-Math.sin(direction) * getSpeed());
		}
		super.updateCoordinate(world);
		super.updateNoise(world);
	}
	
}
