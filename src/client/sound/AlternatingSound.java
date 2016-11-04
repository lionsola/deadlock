package client.sound;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import server.world.Utils;

/**
 * A sound that keeps alternating between different files.
 */
public class AlternatingSound implements Sound {
	private int lastPlayed = 0;
	private List<SingleSound> sounds = new ArrayList<SingleSound>();
	
	public AlternatingSound() {
		
	}
	
	public void addSound(SingleSound s) {
		sounds.add(s);
	}
	
	@Override
	public byte[] getSamples() {
		int s = Utils.random().nextInt(sounds.size()-1);
		if (s>=lastPlayed)
			s++;
		lastPlayed = s;
		return sounds.get(s).getSamples();
	}

	@Override
	public AudioFormat getFormat() {
		return sounds.get(0).getFormat();
	}
	
}
