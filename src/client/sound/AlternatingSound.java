package client.sound;

import java.util.ArrayList;
import java.util.List;

import server.world.Utils;

/**
 * A sound that keeps alternating between different files.
 */
public class AlternatingSound implements Sound {
	private int next = 0;
	private List<Sound> sounds = new ArrayList<Sound>();
	
	public AlternatingSound() {
		
	}
	
	public void addSound(Sound sound) {
		sounds.add(sound);
	}
	
	@Override
	public SingleSound getSound() {
		SingleSound ss = sounds.get(next).getSound();
		int last = next;
		next = Utils.random().nextInt(sounds.size()-1);
		if (next==last) {
			next++;
		}
		return ss;
	}
}
