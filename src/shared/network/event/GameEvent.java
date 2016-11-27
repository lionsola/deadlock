package shared.network.event;

import java.io.Serializable;

/**
 * Models a game event.
 * 
 * @author Anh Pham
 */
public abstract class GameEvent implements Serializable {
	
	private static final long serialVersionUID = 5351736924124300703L;

	public static interface GameEventListener {
		public void onEventReceived(GameEvent event);
	}

	public static class PlayerDieEvent extends GameEvent {
		private static final long serialVersionUID = 7455242055782855036L;
		public final int killerID;
		public final int killedID;

		public PlayerDieEvent(int killerID, int killedID) {
			this.killerID = killerID;
			this.killedID = killedID;
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
		public TileChanged(int tx, int ty, int switchThingID) {
			this.tx = (short)tx;
			this.ty = (short)ty;
			this.switchThingID = switchThingID;
		}
	}
	
	public static class GameEndEvent extends GameEvent {
		private static final long serialVersionUID = 4030097780765059510L;
	}
}
