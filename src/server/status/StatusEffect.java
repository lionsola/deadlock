package server.status;

import client.gui.GameWindow;
import server.character.Entity;
import server.world.World;

public abstract class StatusEffect {
	public static final int TICK = 500;
	public static final int DEFAULT_DURATION = 5000;
	
    private long duration = 0;
    private long elapsedTime = 0;
    private Entity self;
    
    public StatusEffect(Entity self, long duration) {
        this.duration = duration;
        this.self = self;
    }
    
    public void update (World w) {
        if (!isFinished()) {
            elapsedTime += GameWindow.MS_PER_UPDATE;
        }
        if (isFinished()) {
            onFinish(w);
        }
    }
    
    protected Entity getSelf() {
        return self;
    }

    protected long getDuration() {
    	return duration;
    }
    
    protected void setDuration(long duration) {
    	this.duration = duration;
    }
    
    protected long getElapsed() {
    	return elapsedTime;
    }
    
    public boolean isFinished () {
        return elapsedTime>=duration;
    }
    
    public void merge(StatusEffect se) {
    	if (se.getDuration()>duration) {
    		duration = se.getDuration(); 
    	}
    }
    
    public abstract void start();
    public abstract void onFinish(World w);
}