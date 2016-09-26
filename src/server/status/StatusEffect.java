package server.status;

import client.gui.GameWindow;
import server.character.Character;

public abstract class StatusEffect {
    private long duration = 0;
    private long elapsedTime = 0;
    private Character self;
    
    public StatusEffect (Character self, long duration) {
        this.duration = duration;
        this.self = self;
    }
    
    public void update () {
        if (!isFinished()) {
            elapsedTime += GameWindow.MS_PER_UPDATE;
            if (isFinished()) {
                onUpdate();
                onFinish();
            }
            else {
                onUpdate();
            }
        } else {
            System.err.println("Updating finished action");
        }
    }
    
    protected Character getSelf() {
        return self;
    }

    protected long getDuration() {
    	return duration;
    }
    
    protected long getElapsed() {
    	return elapsedTime;
    }
    
    public boolean isFinished () {
        return elapsedTime>=duration;
    }
    
    public abstract void onFinish();
    public abstract void onUpdate();
}