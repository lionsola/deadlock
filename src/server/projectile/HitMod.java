package server.projectile;

import java.util.HashMap;

import server.character.Entity;
import server.status.Bleed;
import server.status.Slowed;
import server.status.Stunned;
import server.world.Tile;
import server.world.World;

public enum HitMod {
	Sharp(0) {
		@Override
		public void onHitEntity(World w, Entity e, Projectile b) {
			b.setSpeed(b.getSpeed()*0.8);
			double damage = 0;
			if (b instanceof Bullet) {
				damage = ((Bullet)b).getDamage()*0.4;
			} else {
				damage = b.getSize()*b.getSize()*b.getSpeed();
			}
			
			e.addStatusEffect(new Bleed(e, damage, 5000, b.id));
		}},
	Blunt(1) {
		@Override
		public void onHitEntity(World w, Entity e, Projectile b) {
			e.addStatusEffect(new Stunned(e, 100));
			b.setSpeed(b.getSpeed()*0.8);
		}},
	Rubber(2) {
		@Override
		public void onHitWall(World w, Tile t, Projectile p) {
			final int LAYER = 1;
			if (t.isBounceTile(LAYER)) {
				p.bounce(w,LAYER);
				p.setSpeed(p.getSpeed()*(-0.2+t.getCoverType()*0.3));
			}
		}},
	Piercing(3) {
		@Override
		public void onHitWall(World w, Tile t, Projectile p) {
			final int LAYER = 1;
			if (t.isBounceTile(LAYER)) {
				p.bounce(w,LAYER);
				p.setSpeed(p.getSpeed()*(t.getCoverType()*0.25));
			}
		}},
	Zombie(4) {
		@Override
		public void onHitEntity(World w, Entity e, Projectile b) {
			e.addStatusEffect(new Slowed(e, 500, 0.2));
			for (Entity z:w.getCharacters()) {
				if (z.id==b.id) {
					z.setHealthPoints(z.getMaxHP()*0.3);
					break;
				}
			}
		}};
	
	private static HashMap<Integer,HitMod> table;
	static {
		table = new HashMap<Integer,HitMod>();
		for (HitMod mod : HitMod.values()) {
			table.put(mod.id, mod);
		}
	}
	public static HitMod get(int id) {
		return table.get(id);
	}
	
	public final int id;
	private HitMod(int id) {
		this.id = id;
	}
	
	public void onHitEntity(World w, Entity e, Projectile b) {};
	public void onHitWall(World w, Tile t, Projectile b) {};
}