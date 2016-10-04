package shared.network;

import java.io.Serializable;

/**
 * The data to send to clients to draw projectiles.
 * 
 * @author Anh Pham
 */
public class ProjectileData implements Serializable {
	
	private static final long serialVersionUID = 4031643912795589086L;
	public float x;
	public float y;
	
	public float direction;
	public float speed;
	public float size;
}
