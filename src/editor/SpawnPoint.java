package editor;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SpawnPoint implements Serializable {
	public enum SpawnType {PlayerOnly,NPCOnly,Both}
	public enum Behaviour {Dummy,NPCPatroller,NPCWatcher,NPCAttacker,NPCRandom,Patrol,NPCWerewolf,NPCWolf,Custom}
	public enum CharType {Alpha(0x03b1), Beta(0x03b2), Gamma(0x03b3), Pi(0x03c0), Ju(0x006a), Nu(0x03bd), Officer(0x0061), Agent(0x006f),
		MOfficer(0x0062),Bat(0x0063),Vampire(0x0064),Werewolf(0x0065),Wolf(0x0066),WolfBig(0x0067),Ghoul(0x0068);
		public final int id;
		private CharType(int id) {
			this.id = id;
		}
		
		private final static HashMap<Integer,CharType> types;
		static {
			types = new HashMap<Integer,CharType>();
			for (CharType type:CharType.values()) {
				types.put(type.id, type);
			}
		}
		
		public static CharType valueOf(int id) {
			return types.get(id);
		}
	}
	
	private static final long serialVersionUID = -3445372971670642349L;
	public int x,y;
	public float direction;
	
	public int level;
	public int team;
	public int players;
	
	public List<CharType> setups = new LinkedList<CharType>();
	public Behaviour behaviour;
	public SpawnType type;
	
	public List<Point2D> patrolLocations = new LinkedList<Point2D>();
	private int id;
	
	@Override
	public String toString() {
		String s = "Spawn: lev"+level+", "+players+" players, ";
		return s;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}