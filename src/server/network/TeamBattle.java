package server.network;

import java.util.List;

import server.world.World;
import shared.network.event.GameEvent;
import shared.network.event.GameEvent.PlayerDieEvent;
import shared.network.event.GameEvent.ScoreChangedEvent;

public class TeamBattle extends MatchServer {
	private int[] score = new int[2];
	private int maxScore = 3;
	public TeamBattle(List<ServerPlayer> players, String arenaName) {
		super(players, arenaName);
	}

	@Override
	protected boolean checkEndGame() {
		//return score[0]>=maxScore || score[1]>=maxScore;
		return score[0]>=maxScore || score[1]>=maxScore;
	}

	@Override
	protected int getWinningTeam() {
		if (score[0]>score[1]) {
			return 0;
		} else if (score[1]>score[0]) {
			return 1;
		} else {
			return -1;
		}
	}
	
	@Override
	protected void update(World world) {
		int[] remainingPlayers = new int[2];
		for (ServerPlayer p:getPlayers()) {
			if (!p.character.isDead()) {
				remainingPlayers[p.team] ++;
			}
		}
		if (remainingPlayers[0]>0 && remainingPlayers[1]>0) {
			return;
		}
		int winner = -1;
		if (remainingPlayers[0]==0 && remainingPlayers[1]>0) {
			winner = 1;
		} else if (remainingPlayers[0]>0 && remainingPlayers[1]==0) {
			winner = 0;
		}
		if (winner!=-1) {
			score[winner] += 1;
			addEvent(new ScoreChangedEvent(score[0],score[1]));
		}
		if (!checkEndGame()) {
			initializeWorld(world.getArena().getName());
			setUp(this.world,getPlayers());
			//new Thread(this).start();
		}
	}
	
	public void onEventReceived(GameEvent event) {
		if (event instanceof PlayerDieEvent) {
			PlayerDieEvent e = (PlayerDieEvent) event;
			ServerPlayer killer = findPlayer(e.killerID);
			ServerPlayer victim = findPlayer(e.killedID);

			victim.deaths++;
			if (killer.team != victim.team) {
				killer.kills++;
			} else {
				killer.kills--;
			}
		}
		addEvent(event);
	}
}
