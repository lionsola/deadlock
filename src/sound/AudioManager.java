package sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import weapon.WeaponFactory;

/**
 * Manages the audio in game.
 * 
 * @author Anh Pham
 */
public class AudioManager implements Runnable {
	
	// volume gain in decibel
	public static final float GAIN_MINVOLUME = -30;
	public static final float GAIN_RANGE = 20;

	// artificial volume of noise in game
	// noises will be from 130db (shotgun shot) to 0db (silence)
	public static final float GAME_RANGE = 120;

	// noise constants in game
	public static final float BULLETHITNOISE = 30;
	public static final float BULLETWALLNOISE = 50;

	static MusicPlayer currentMusic = null;

	ExecutorService threadPool;
	ConcurrentLinkedQueue<PlayMessage> pending;
	HashMap<Integer, Sound> weaponSoundMap;
	ArrayList<Sound> footstepSounds;
	Sound powerUpSound;

	private static final String SOUND_DIR = "resource/audio/sounds/";
	private static final String MUSIC_DIR = "resource/audio/music/";

	/**
	 * Creates a new Audio Manager, where the location of all the sounds used for the game are
	 * located
	 */
	public AudioManager() {
		threadPool = Executors.newFixedThreadPool(10);
		pending = new ConcurrentLinkedQueue<PlayMessage>();
		weaponSoundMap = new HashMap<Integer, Sound>();
		
		footstepSounds = new ArrayList<Sound>(4);
		powerUpSound = new Sound(SOUND_DIR + "power_up.wav");
		WeaponFactory.initWeapons();

		// initialize sound files
		weaponSoundMap.put(0, new Sound(SOUND_DIR + "gunshot_assault.wav"));
		weaponSoundMap.put(1, new Sound(SOUND_DIR + "gunshot_sniper.wav"));
		weaponSoundMap.put(2, new Sound(SOUND_DIR + "gunshot_shotgun.wav"));
		weaponSoundMap.put(3, new Sound(SOUND_DIR + "gunshot_pistol.wav"));
		weaponSoundMap.put(4, weaponSoundMap.get(3));
		weaponSoundMap.put(5, new Sound(SOUND_DIR + "gunshot_smg.wav"));

		footstepSounds.add(new Sound(SOUND_DIR + "footstep1.wav"));
		footstepSounds.add(new Sound(SOUND_DIR + "footstep2.wav"));
		footstepSounds.add(new Sound(SOUND_DIR + "footstep3.wav"));
		footstepSounds.add(new Sound(SOUND_DIR + "footstep4.wav"));
	}

	/**
	 * Plays a sound in the audio manager
	 * 
	 * @param sound
	 *            The sound to be played
	 * @param volume
	 *            The volume of the sound to be played
	 */
	private void playSound(Sound sound, float volume) {
		if (volume >= 0) {
			// normalize from real-life decibel scale to in-game gain scale
			volume = GAIN_RANGE * Math.min(GAME_RANGE, volume) / GAME_RANGE + GAIN_MINVOLUME;
			for (PlayMessage pm : pending) {
				if (pm.sound == sound) {
					pm.volume = Math.max(pm.volume, volume);
					return;
				}
			}
			pending.add(new PlayMessage(sound, volume));
		}
	}

	/**
	 * Plays a weapon sound in the audio manager
	 * 
	 * @param weaponId
	 *            The weapon id of the sound to be played
	 * @param volume
	 *            The volume of the weapon sound to be played
	 */
	public void playWeaponSound(int weaponId, float volume) {
		playSound(weaponSoundMap.get(weaponId), volume);
	}

	/**
	 * Plays footsteps
	 * 
	 * @param volume
	 *            The volume of the footsteps
	 */
	public void playFootstepSound(float volume) {
		playSound(footstepSounds.get(core.Utils.random().nextInt(footstepSounds.size())), volume);
	}

	/**
	 * Plays power up pick up sound
	 * 
	 * @param volume
	 *            The volume of the footsteps
	 */
	public void playPowerUpSound(float volume) {
		playSound(powerUpSound, volume);
	}

	/**
	 * Plays specified music in the audio manager
	 * 
	 * @param name
	 *            The name of the music to be played
	 */
	public static void playMusic(String name, float volume) {
		stopMusic(); // TODO if possible, fade out / in
		currentMusic = new MusicPlayer(MUSIC_DIR + name, volume, true);
		new Thread(currentMusic).start();
	}

	/**
	 * Stops music from the audio manager
	 */
	public static void stopMusic() {
		if (currentMusic != null) {
			currentMusic.setMusicStop();
			currentMusic = null;
		}
	}

	/**
	 * Update the thread
	 */
	public void update() {
		threadPool.execute(this);
	}

    @Override
    public void run() {
        for (PlayMessage pm:pending) {
            SoundPlayer sp = new SoundPlayer(pm.sound,pm.volume,threadPool);
            sp.play();
        }
        pending.clear();
    }

	/**
	 * Get the weapon noise from the specified weapon ID.
	 * 
	 * @param weaponId
	 *            a specific weapon from which to get noise.
	 * @return the noise from the specified weapon.
	 */
	public float getWeaponNoise(int weaponId) {
		return WeaponFactory.getWeaponData(weaponId).noise;
	}

	/**
	 * Small class used to play a message.
	 * 
	 * @author Anh pham
	 */
	private static class PlayMessage {
		Sound sound;
		float volume;

		public PlayMessage(Sound sound, float volume) {
			this.sound = sound;
			this.volume = volume;
		}
	}

	/**
	 * Main function of the AudioManager.
	 * 
	 * @param args
	 *            string args
	 */
	public static void main(String[] args) {
		AudioManager am = new AudioManager();
		Sound test = new Sound(SOUND_DIR + "test.wav");
		am.playSound(test, 100);
		am.update();
	}
}
