package status;

import character.AbstractCharacter;
import game.Game;

public abstract class StatusEffect {
    private long duration = 0;
    private long elapsedTime = 0;
    private AbstractCharacter self;
    
    public StatusEffect (AbstractCharacter self, long duration) {
        this.duration = duration;
        this.self = self;
    }
    
    public void update () {
        if (!isFinished()) {
            elapsedTime += Game.MS_PER_UPDATE;
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
    
    protected AbstractCharacter getSelf() {
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