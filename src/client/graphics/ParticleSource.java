package client.graphics;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;
import client.gui.GameWindow;
import server.world.Terrain;
import server.world.Utils;

/**
 * This class is used to continuously produce particle effects in-game,
 * such as fire, water or smoke. 
 */
public class ParticleSource implements Cloneable, Serializable {
	private static final long serialVersionUID = -5327521687188327899L;
	
	static final public HashMap<String,ParticleSource> presets = new HashMap<String,ParticleSource>();
	static {
		ParticleSource fountain = new ParticleSource("Fountain",1500);
		fountain.setInterval(80);
		fountain.pSpeed = 0.018;
		fountain.spreadDistance = 0.2;
		fountain.altColor = new Color(0x80a0a0d0,true);
		ParticleAnimation pf = fountain.getBaseParticle();
		pf.setLoc(0, 0, 0.8);
		pf.setVel(0, 0, 0.02);
		pf.setAcc(0, 0, -0.0012);
		pf.setColor(new Color(0x608080f0,true));
		pf.setSize(0.3);
		pf.setRotationSpeed(0.05);
		presets.put(fountain.name,fountain);
		
		ParticleSource waterglow = new ParticleSource("Waterglow",1500);
		waterglow.setInterval(4000);
		waterglow.pSpeed = 0.001;
		waterglow.random = false;
		waterglow.spreadDistance = 0.5;
		waterglow.altColor = new Color(0xa0a0a0d0,true);
		ParticleAnimation pw = waterglow.getBaseParticle();
		pw.setLoc(0, 0, 0);
		pw.setVel(0, 0, 0.003);
		pw.setAcc(0, 0, -0.0001);
		pw.setColor(new Color(0x808080f0,true));
		pw.setSize(0.2);
		//pf.setRotationSpeed(0.001);
		presets.put(waterglow.name,waterglow);
		
		ParticleSource smoke = new ParticleSource("Smoke",1000);
		smoke.setInterval(200);
		smoke.pSpeed = 0.1;
		ParticleAnimation ps = smoke.getBaseParticle();
		ps.setVel(0, 0, 0.01);
		ps.setColor(new Color(0x323232));
		ps.setSize(0.2);
		ps.setRotationSpeed(0.1);
		presets.put(smoke.name,smoke);
		
		ParticleSource fire = new ParticleSource("Fire",2000);
		fire.setInterval(150);
		fire.spreadDistance = 0.1;
		fire.pSpeed = 0.005;
		fire.random = false;
		fire.altColor = new Color(0xa0e0d040,true);
		ParticleAnimation pfr = fire.getBaseParticle();
		pfr.setVel(0, 0, 0.02);
		pfr.setAcc(0, 0, 0);
		pfr.setColor(new Color(0x80f06040,true));
		pfr.setSize(0.15);
		pfr.setRotationSpeed(0.02);
		presets.put(fire.name,fire);
		
		ParticleSource light = new ParticleSource("Light",1200);
		light.setInterval(1200);
		light.pSpeed = 0.005;
		light.spreadDistance = 0.5;
		light.random = true;
		ParticleAnimation pl = light.getBaseParticle();
		pl.setVel(0, 0, 0.01);
		pl.setAcc(0, 0, -0.0001);
		pl.setColor(new Color(0xcfcf4f));
		pl.setSize(0.12);
		pl.setRotationSpeed(0.02);
		presets.put(light.name,light);
	}

	public final String name;
	private ParticleAnimation baseParticle;
	private int tx, ty;
	double spreadDistance = 0;
	double spreadAngle = 2*Math.PI;
	double pSpeed = 0.01;
	private double interval = 1000;
	boolean random = false;
	long counter;
	Color altColor = null;
	
	private ParticleSource(String name, long life) {
		this.name = name;
		baseParticle = new ParticleAnimation(life);
	}
	
	public void update(AnimationSystem as) {
		boolean add = false;
		if (random) {
			double chance = 1.0*GameWindow.MS_PER_UPDATE/interval;
			if (Utils.random().nextDouble()<chance) {
				add = true;
			}
		} else {
			int offset = (tx*59 + 71*ty)*GameWindow.MS_PER_UPDATE;
			int mod = (int) ((System.currentTimeMillis()+offset)%interval);
			if (mod<GameWindow.MS_PER_UPDATE) {
				add = true;
			}
			/*
			counter += GameWindow.MS_PER_UPDATE;
			if (counter>interval) {
				add = true;
				counter -= interval;
			}
			*/
		}
		
		if (add) {
			double direction = baseParticle.getDirection() + Utils.random().nextDouble()*spreadAngle;
			Point2D p = Utils.tileToMeter(tx, ty);
			double x = p.getX() + 2*(Utils.random().nextDouble()-0.5)*spreadDistance;
			double y = p.getY() + 2*(Utils.random().nextDouble()-0.5)*spreadDistance;
			
			ParticleAnimation pa = baseParticle.clone();
			pa.set2DLoc(x, y);
			pa.setDirection(direction, pSpeed);
			if (altColor!=null) {
				float r = Utils.random().nextFloat();
				Color oc = pa.getColor();
				
				int na = Math.round(oc.getAlpha() + r*(altColor.getAlpha()-oc.getAlpha()));
				int nr = Math.round(oc.getRed() + r*(altColor.getRed()-oc.getRed()));
				int ng = Math.round(oc.getGreen() + r*(altColor.getGreen()-oc.getGreen()));
				int nb = Math.round(oc.getBlue() + r*(altColor.getBlue()-oc.getBlue()));
				
				Color newColor = new Color(nr,ng,nb,na);
				pa.setColor(newColor);
			}
			as.addCustomAnimation(pa);
			
			counter -= interval;
		}
	}
	
	public ParticleAnimation getBaseParticle() {
		return baseParticle;
	}
	
	@Override
	public ParticleSource clone() {
		ParticleSource ps;
		try {
			ps = (ParticleSource) super.clone();
			ps.baseParticle = ps.baseParticle.clone();
			ps.counter = Utils.random().nextInt((int)interval);
			return ps;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public void setInterval(double interval) {
		this.interval = interval;
		counter = Utils.random().nextInt((int)interval);
	}
	
	public int getTx() {
		return tx;
	}
	
	public int getTy() {
		return ty;
	}
	
	public void setLocation(int x, int y) {
		tx = x;
		ty = y;
		baseParticle.set2DLoc((x+0.5)*Terrain.tileSize, (y+0.5)*Terrain.tileSize);
	}
}
