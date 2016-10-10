package client.graphics;

import java.awt.Color;

import client.gui.GameWindow;
import server.world.Utils;

public class ParticleEmitter {
	double pDirection;
	double pSpeed;
	double pSize;
	double pGrowth;
	double pRotSpeed;
	long pLife;
	Color pColor;
	
	double x, y;
	int type;
	long interval;
	long counter;
	
	public ParticleEmitter(double x, double y) {
		
	}
	
	
	
	public void update(AnimationSystem as) {
		counter += GameWindow.MS_PER_UPDATE;
		if (counter>interval) {
			double direction;
			if (type==0) {
				direction = this.pDirection + Utils.random().nextGaussian()*Math.PI/4;
			} else {
				direction = Utils.random().nextDouble()*Math.PI*2;
			}
			ParticleAnimation pa = new ParticleAnimation(x,y, direction,pSpeed,pSize,pLife,pColor);
			pa.setGrowth(pGrowth, pGrowth);
			pa.setRotationSpeed(pRotSpeed);
			as.addCustomAnimation(pa);
			
			counter = 0;
		}
	}
}
