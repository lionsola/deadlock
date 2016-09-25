package network;

import java.io.Serializable;

/**
 * Partial Character Data
 * 
 * @author Anh Pham
 */
public class PartialCharacterData implements Serializable {
	
	private static final long serialVersionUID = 951232578889372942L;
	public short id;
	public byte team;
	
	public float x;
	public float y;
	
	public float armorStart;
	public float armorAngle;
	
	public float radius;
	public float healthPoints;
	public float direction;
}
