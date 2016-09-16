package status;

import character.AbstractCharacter;

public class Farsighted extends StatusEffect {

	public Farsighted(AbstractCharacter self, long duration) {
		super(self, duration);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate() {
		//if (getElapsed()<getDuration()/5)
	}

}
