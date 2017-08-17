package server.weapon;

import java.util.HashMap;

public enum WeaponMod {
	Supressor(0){
		@Override
		public WeaponType getModifiedType(WeaponType w) {
			return new WeaponType(w.weaponId, w.weaponType, w.getGunDispersion(),
					w.getInstability()*0.9, w.getDamage(), w.getSize(), w.getProjectileSpeed(),
					w.getCooldown(), w.getNoise()-Math.max(10,w.getNoise()/4), w.getReloadTime(),
					w.getMagSize(), w.getBulletsNo(), w.getSoundId(), w.getLength()+0.2);
		}
	},
	
	XPowder(1){
		@Override
		public WeaponType getModifiedType(WeaponType w) {
			return new WeaponType(w.weaponId, w.weaponType, w.getGunDispersion(),
					w.getInstability()+0.1,w.getDamage()*1.1,w.getSize(),w.getProjectileSpeed()+0.1,
					w.getCooldown(), w.getNoise()+10, w.getReloadTime(), w.getMagSize(),
					w.getBulletsNo(), w.getSoundId(), w.getLength());
		}
	},
	
	XMag(2){
		@Override
		public WeaponType getModifiedType(WeaponType w) {
			return new WeaponType(w.weaponId, w.weaponType, w.getGunDispersion(),
					w.getInstability(),w.getDamage(),w.getSize(),w.getProjectileSpeed(),
					w.getCooldown(),w.getNoise(),w.getReloadTime()*6/5,w.getMagSize()*4/3,
					w.getBulletsNo(), w.getSoundId(), w.getLength());
		}
	},
	
	MuzzleBrake(3){
		@Override
		public WeaponType getModifiedType(WeaponType w) {
			return new WeaponType(w.weaponId, w.weaponType, w.getGunDispersion(),
					w.getInstability()*3/4,w.getDamage(),w.getSize(),w.getProjectileSpeed(),
					w.getCooldown(),w.getNoise()+Math.max(10,w.getNoise()/5),w.getReloadTime(),w.getMagSize(),
					w.getBulletsNo(), w.getSoundId(), w.getLength());
		}
	};
	private static HashMap<Integer,WeaponMod> table;
	static {
		table = new HashMap<Integer,WeaponMod>();
		for (WeaponMod mod : WeaponMod.values()) {
			table.put(mod.id, mod);
		}
	}
	
	public static WeaponMod get(int id) {
		return table.get(id);
	}
	
	public final int id;
	private WeaponMod(int id) {
		this.id = id;
	}
	
	abstract public WeaponType getModifiedType(WeaponType w);
}
