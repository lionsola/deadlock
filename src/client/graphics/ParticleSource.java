package client.graphics;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;

import client.graphics.ParticleAnimation.GroundInteraction;
import client.gui.GameWindow;
import server.world.Terrain;
import server.world.Utils;
import shared.core.Vector3D;

/**
 * This class is used to continuously produce particle effects in-game,
 * such as fire, water or smoke. 
 */
public class ParticleSource implements Cloneable, Serializable {
	private static final long serialVersionUID = -5327521687188327899L;
	
	static final public HashMap<String,ParticleSource> presets = new HashMap<String,ParticleSource>();
	static {
		ParticleSource fountain = new ParticleSource("Fountain",1500);
		fountain.setInterval(100);
		fountain.pSpeed = 0.015;
		fountain.setSpread(0.2, 0.2, 0.2);;
		fountain.altColor = new Color(0xa0a0a0d0,true);
		ParticleAnimation pf = fountain.getBaseParticle();
		pf.setLoc(0, 0, 0.2);
		pf.setVel(0, 0, 0.06);
		pf.setAcc(0, 0, -0.002);
		pf.setColor(new Color(0x808080f0,true));
		pf.setSize(0.3);
		pf.setRotationSpeed(0.05);
		pf.setGroundInteraction(GroundInteraction.Slide);
		presets.put(fountain.name,fountain);
		
		ParticleSource waterglow = new ParticleSource("Waterglow",1500);
		waterglow.setInterval(6000);
		waterglow.pSpeed = 0.001;
		waterglow.random = false;
		waterglow.setSpread(0.5, 0.5, 0.1);;
		waterglow.altColor = new Color(0xa0a0a0d0,true);
		ParticleAnimation pw = waterglow.getBaseParticle();
		pw.setLoc(0, 0, 0.1);
		pw.setVel(0, 0, 0.003);
		pw.setAcc(0, 0, -0.0001);
		pw.setColor(new Color(0x808080f0,true));
		pw.setSize(0.2);
		//pf.setRotationSpeed(0.001);
		presets.put(waterglow.name,waterglow);
		
		ParticleSource leaves = new ParticleSource("Leaves",2500);
		leaves.setInterval(2200);
		leaves.pSpeed = 0.01;
		leaves.random = true;
		leaves.setSpread(5, 4, 0.5);
		leaves.spreadAngle = Math.PI/6;
		ParticleAnimation ps = leaves.getBaseParticle();
		ps.setLoc(0, 0, 2);
		ps.setVel(0, 0, -0.018);
		ps.setAcc(0, 0, 0);
		ps.setDirection(-Math.PI/5, 0.05);
		ps.setColor(new Color(0xc0408040,true));
		ps.setSize(0.2);
		ps.setRotationSpeed(0.02);
		ps.setGroundInteraction(GroundInteraction.Stop);
		presets.put(leaves.name,leaves);
		
		ParticleSource fire = new ParticleSource("Fire",2200);
		fire.setInterval(150);
		fire.setSpread(0.3,0.3,0.1);
		fire.pSpeed = 0.005;
		fire.random = false;
		fire.altColor = new Color(0xa0e0d040,true);
		ParticleAnimation pfr = fire.getBaseParticle();
		pfr.setLoc(0,0,-0.1);
		pfr.setVel(0, 0, 0.015);
		pfr.setAcc(0, 0, 0);
		pfr.setColor(new Color(0x80f05040,true));
		pfr.setSize(0.25);
		pfr.setRotationSpeed(0.02);
		presets.put(fire.name,fire);
		
		ParticleSource light = new ParticleSource("Light",1200);
		light.setInterval(1500);
		light.pSpeed = 0.005;
		light.setSpread(0.2,0.2,0);
		light.random = true;
		ParticleAnimation pl = light.getBaseParticle();
		pl.setLoc(0, 0, 0.5);
		pl.setVel(0, 0, 0.004);
		pl.setAcc(0, 0, -0.0001);
		pl.setColor(new Color(0xcfcf4f));
		pl.setSize(0.12);
		pl.setRotationSpeed(0.02);
		presets.put(light.name,light);
		
		ParticleSource outsideDust = new ParticleSource("Outside dust",5000);
		outsideDust.setInterval(400);
		outsideDust.pSpeed = 0.001;
		outsideDust.setSpread(8,8,2.5);
		outsideDust.random = true;
		outsideDust.spreadAngle = Math.PI/4;
		ParticleAnimation pwi = outsideDust.getBaseParticle();
		pwi.setLoc(0, 0, 3);
		pwi.setVel(0, 0, -0.001);
		pwi.setAcc(0, 0, 0);
		pwi.setColor(new Color(0x5fffffff,true));
		pwi.setDirection(-Math.PI/5, 0.5);
		pwi.setSize(0.07);
		pwi.setRotationSpeed(0.005);
		presets.put(outsideDust.name,outsideDust);
		
		ParticleSource insideDust = new ParticleSource("Inside dust",5000);
		insideDust.setInterval(400);
		insideDust.pSpeed = 0.0005;
		insideDust.setSpread(8,8,1.25);
		insideDust.random = true;
		insideDust.spreadAngle = Math.PI/4;
		ParticleAnimation pid = insideDust.getBaseParticle();
		pid.setLoc(0, 0, 1.75);
		pid.setVel(0, 0, -0.001);
		pid.setAcc(0, 0, 0);
		pid.setColor(new Color(0x5fffddbf,true));
		pid.setDirection(-Math.PI/5, 0.5);
		pid.setSize(0.06);
		pid.setRotationSpeed(0.002);
		presets.put(insideDust.name,insideDust);
	}

	public final String name;
	private ParticleAnimation baseParticle;
	private int tx, ty;
	final Vector3D spread = new Vector3D(0,0,0);
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
			double x = p.getX() + 2*(Utils.random().nextDouble()-0.5)*spread.x;
			double y = p.getY() + 2*(Utils.random().nextDouble()-0.5)*spread.y;
			
			
			ParticleAnimation pa = baseParticle.clone();
			double z = pa.getLoc().z + 2*(Utils.random().nextDouble()-0.5)*spread.z;
			pa.setLoc(x, y, z);
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
	
	public void setSpread(double x, double y, double z) {
		spread.x = x;
		spread.y = y;
		spread.z = z;
	}
}
