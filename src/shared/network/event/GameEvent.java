package shared.network.event;

import java.io.Serializable;

/**
 * Models a game event.
 * 
 * @author Anh Pham
 */
public abstract class GameEvent implements Serializable {
	
	public static class DataObtained extends GameEvent {
		private static final long serialVersionUID = 3360696584653021384L;
		public final int dataId;
		public final float x;
		public final float y;
		public final int charType;
		public DataObtained(int dataId, double x, double y, int typeId) {
			this.dataId = dataId;
			this.x = (float)x;
			this.y = (float)y;
			this.charType = typeId;
		}
	}

	private static final long serialVersionUID = 5351736924124300703L;

	public static interface Listener {
		public void onEventReceived(GameEvent event);
	}

	public static class PlayerDieEvent extends GameEvent {
		private static final long serialVersionUID = 7455242055782855036L;
		public final int killerId;
		public final int killedId;

		public PlayerDieEvent(int killerId, int killedId) {
			this.killerId = killerId;
			this.killedId = killedId;
		}
	}
	
	public static class Headshot extends GameEvent {
		private static final long serialVersionUID = 8019912159558651525L;
		public final int victim;
		public final int attacker;
		public final float x;
		public final float y;
		
		public Headshot(int attackerId, int victim, double x, double y) {
			this.attacker = attackerId;
			this.victim = victim;
			this.x = (float)x;
			this.y = (float)y;
		}
	}
	
	public static class ScoreChangedEvent extends GameEvent {
		private static final long serialVersionUID = -6702972540422715379L;
		public final int team1Score;
		public final int team2Score;

		public ScoreChangedEvent(int team1Score, int team2Score) {
			this.team1Score = team1Score;
			this.team2Score = team2Score;
		}
	}

	public static class EnemyInfoEvent extends GameEvent {
		private static final long serialVersionUID = 6938635895835139027L;
		public final float x;
		public final float y;
		public final byte id;

		public EnemyInfoEvent(double x, double y, int id) {
			this.x = (float)x;
			this.y = (float)y;
			this.id = (byte) id;
		}
	}

	public static class TileChanged extends GameEvent {
		private static final long serialVersionUID = 8597074729245066489L;
		public final short tx;
		public final short ty;
		public final int switchThingID;
		public final byte itemType;
		public TileChanged(int tx, int ty, int switchThingID, int itemType) {
			this.tx = (short)tx;
			this.ty = (short)ty;
			this.switchThingID = switchThingID;
			this.itemType = (byte)itemType;
		}
	}
	
	public static class GameEndEvent extends GameEvent {
		private static final long serialVersionUID = 4030097780765059510L;
		public final byte winner;
		
		public GameEndEvent(int winner) {
			this.winner = (byte) winner;
		}
	}
	
	public static class RoundEnd extends GameEvent {
		private static final long serialVersionUID = 4887212456315473164L;
		public final byte winner;
		
		public RoundEnd(int winner) {
			this.winner = (byte) winner;
		}
	}
	
	public static class RoundStart extends GameEvent {
		private static final long serialVersionUID = 8082493004855338971L;
	}
}
