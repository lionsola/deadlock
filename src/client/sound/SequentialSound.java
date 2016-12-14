package client.sound;

import java.util.ArrayList;
import java.util.List;

/**
 * A sound that keeps alternating between different files.
 */
public class SequentialSound implements Sound {
	private int next = 0;
	private List<Sound> sounds = new ArrayList<Sound>();
	
	public SequentialSound() {
	}
	
	public void addSound(Sound sound) {
		sounds.add(sound);
	}

	@Override
	public SingleSound getSound() {
		SingleSound ss = sounds.get(next).getSound();
		next = (next+1)%sounds.size();
		return ss;
	}
}
