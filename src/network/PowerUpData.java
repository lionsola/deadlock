package network;

import java.io.Serializable;

/**
 * This class will include power up data to transfer to clients.
 * 
 * @author Madyan Al-Jazaeri
 */
public class PowerUpData implements Serializable {

	private static final long serialVersionUID = -7688794491148523248L;
	public short x;
	public short y;
	public byte type;
	public byte display;
	public short pickerId;

}
