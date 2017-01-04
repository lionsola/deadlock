package server.network;

import java.util.List;

import editor.SpawnPoint;
import editor.SpawnPoint.SpawnType;
import server.character.ClassStats;
import server.character.NPC;
import server.weapon.Weapon;
import server.weapon.WeaponFactory;
import server.world.World;

public class MissionServer extends MatchServer {

	public MissionServer(List<ServerPlayer> players, String arenaName) {
		super(players, arenaName);
	}

	@Override
	public void setUp(World w, List<ServerPlayer> players) {
		super.setUp(w, players);
		int npcId = 1000;
		for (SpawnPoint p:w.getArena().getSpawns()) {
			if (p.type==SpawnType.NPCOnly) {
				NPC npc = new NPC(npcId, 1, ClassStats.classStats.get(p.setups.get(0)), w.getArena(), p.behaviour);
				w.addCharacter(npc,p);
				npc.setWeapon(WeaponFactory.createGun(Weapon.SILENT_PISTOL_ID, npc));
				npc.brain.setPatrolLocations(p.patrolLocations);
			}
		}
	}
	
	@Override
	public int getRoundWinner() {
		return UNDECIDED;
	}
}
