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
	
	public float gunDir;
	
	public float faceDir;
	
	public float bodyDir;
	
	public boolean invi;
	
	public CharData(InputControlledEntity e) {
		id = e.id;
		team = (byte) e.team;
		if (e.getWeapon()!=null) {
			weapon = (byte) e.getWeapon().getId();
		}
		
		x = (float) e.getX();
		y = (float) e.getY();
		
		if (e.getArmor()!=null) {
			armorStart = (float) e.getArmor().getStart();
			armorAngle = (float) e.getArmor().getAngle();
		}
		
		radius = (float) e.getRadius();
		healthPoints = (float) e.getHealthPoints();
		gunDir = (float) e.getDirection();
		bodyDir = (float) e.getMovingDirection();
		invi = e.isInvi();
		typeId = e.typeId;
		faceDir = (float) e.getTargetDirection();
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
		gunDir = (float) e.getDirection();
		bodyDir = (float) e.getMovingDirection();
		invi = e.isInvi();
		typeId = e.typeId;
	}
	
	public CharData() {
		
	}
}
