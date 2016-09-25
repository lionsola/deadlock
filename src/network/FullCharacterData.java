package network;

import java.io.Serializable;

/**
 * Full Character data.
 * 
 * @author Anh Pham
 */
public class FullCharacterData implements Serializable {
	
	private static final long serialVersionUID = 6568364547122850939L;
	public float x = 0;
	public float y = 0;
	
	public float radius;

	public float armorStart;
	public float armorAngle;
	
	public float healthPoints;
	public float reloadPercent;
	public float viewRange;
	public float viewAngle;
	public float direction;
	
	public float crosshairSize;
}
