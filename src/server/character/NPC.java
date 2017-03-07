package server.character;

import editor.SpawnPoint.Behaviour;
import editor.SpawnPoint.CharType;
import server.ai.NPCBrain;
import server.passive.Assault;
import server.weapon.Weapon;
import server.weapon.WeaponFactory;
import server.world.Arena;
import server.world.World;
import shared.network.NPCData;

public class NPC extends InputControlledEntity {
	public NPC(int id, int team, int typeId) {
		super(id, team, typeId);
	}

	public NPCBrain brain;
	
	public void init(Arena a, Behaviour b) {
		brain = new NPCBrain(b);
		brain.init(a, this); 
	}
	
	@Override
	public void update(World w) {
		super.update(w);
		if (!isDead()) {
			brain.update(getPerception());
			getPerception().events.clear();
		}
	}
	
	@Override
	public NPCData generatePartial() {
		NPCData data = new NPCData(this);
		return data;
	}
	
	public static NPC newNPC(int id, int team, CharType type) {
		switch(type) {
			case Officer:
				NPC npc = new NPC(id,team,type.id);
				npc.setWeapon(WeaponFactory.createGun(7, npc));
				return npc;
			case MOfficer:
				NPC mofficer = new NPC(id,team,type.id);
				mofficer.setWeapon(WeaponFactory.createGun(Weapon.MELEE_ID, mofficer));
				mofficer.setPassive(new Assault(mofficer));
				return mofficer;
			default:
				return null;
		}
	}
}
