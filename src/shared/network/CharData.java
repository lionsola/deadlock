package shared.network;

import java.io.Serializable;

import server.character.Entity;
import server.character.InputControlledEntity;

/**
 * Partial Character Data
 * 
 * @author Anh Pham
 */
public class CharData implements Serializable {
	
	private static final long serialVersionUID = 951232578889372942L;
	public int id;
	public byte team;
	public byte weapon;
	
	public float x;
	public float y;
	
	public float armorStart;
	public float armorAngle;
	
	public int typeId;
	
	public float radius;
	public float healthPoints;
	public float direction;
	public float exposure;
	
	public CharData(InputControlledEntity e) {
		id = e.id;
		team = (byte) e.team;
		weapon = (byte) e.getWeapon().getId();
		
		x = (float) e.getX();
		y = (float) e.getY();
		
		if (e.getArmor()!=null) {
			armorStart = (float) e.getArmor().getStart();
			armorAngle = (float) e.getArmor().getAngle();
		}
		
		radius = (float) e.getRadius();
		healthPoints = (float) e.getHealthPoints();
		direction = (float) e.getDirection();
		exposure = (float) e.getExposure();
		typeId = e.typeId;
	}
	
	public CharData(Entity e) {
		id = e.id;
		team = (byte) e.team;
		
		x = (float) e.getX();
		y = (float) e.getY();
		
		if (e.getArmor()!=null) {
			armorStart = (float) e.getArmor().getStart();
			armorAngle = (float) e.getArmor().getAngle();
		}
		
		radius = (float) e.getRadius();
		healthPoints = (float) e.getHealthPoints();
		direction = (float) e.getDirection();
		exposure = (float) e.getExposure();
		typeId = e.typeId;
	}
	
	public CharData() {
		
	}
}
