package client.sound;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.ability.TimedGrenade;
import server.weapon.Bullet;
import server.weapon.WeaponFactory;
import server.character.Character;
import server.character.PlayerCharacter;

/**
 * Manages the audio in game.
 */
public class AudioManager implements Runnable {
	
	// volume gain in decibel
	public static final float GAIN_MINVOLUME = -30;
	public static final float GAIN_RANGE = 25;

	/** Upper bound of in-game sounds' volume.
	 * Sounds will be from 170db (flash-bang grenade) to 0db (silence).
	 */
	public static final float GAME_RANGE = 170;

	static MusicPlayer currentMusic = null;

	private ExecutorService threadPool;
	private ConcurrentLinkedQueue<PlayMessage> pending;
	
	private HashMap<Integer, Sound> soundMap;
	
	private static final String SOUND_DIR = "resource/audio/sounds/";
	private static final String MUSIC_DIR = "resource/audio/music/";
	
	public static final int DOOR_OPEN_ID = 36;

	/**
	 * Creates a new Audio Manager, where the location of all the sounds used for the game are
	 * located
	 */
	public AudioManager() {
		threadPool = Executors.newFixedThreadPool(10);
		pending = new ConcurrentLinkedQueue<PlayMessage>();
		soundMap = new HashMap<Integer, Sound>();
		
		WeaponFactory.initWeapons();

		// initialize sound files
		soundMap.put(0, new SingleSound(SOUND_DIR + "gunshot_assault.wav"));
		soundMap.put(1, new SingleSound(SOUND_DIR + "gunshot_shotgun.wav"));
		soundMap.put(2, new SingleSound(SOUND_DIR + "gunshot_pistol.wav"));
		soundMap.put(3, new SingleSound(SOUND_DIR + "gunshot_silent_pistol.wav"));
		soundMap.put(4, new SingleSound(SOUND_DIR + "gunshot_sniper.wav"));
		
		

		AlternatingSound footsteps = new AlternatingSound();
		footsteps.addSound(new SingleSound(SOUND_DIR + "footstep1.wav"));
		footsteps.addSound(new SingleSound(SOUND_DIR + "footstep2.wav"));
		footsteps.addSound(new SingleSound(SOUND_DIR + "footstep3.wav"));
		footsteps.addSound(new SingleSound(SOUND_DIR + "footstep4.wav"));
		soundMap.put(Character.FOOTSTEP_SOUND_ID, footsteps);
		
		AlternatingSound bulletwall = new AlternatingSound();
		bulletwall.addSound(new SingleSound(SOUND_DIR + "bulletwall0.wav"));
		bulletwall.addSound(new SingleSound(SOUND_DIR + "bulletwall1.wav"));
		soundMap.put(Bullet.BULLET_WALL_SOUND_ID,bulletwall);
		
		soundMap.put(TimedGrenade.GRENADE_EXPLODE_SOUND_ID,new SingleSound(SOUND_DIR + "grenade_explode.wav"));
		
		soundMap.put(DOOR_OPEN_ID, new SingleSound(SOUND_DIR + "door_open.wav"));
		
		soundMap.put(PlayerCharacter.PING_SOUND_ID,new SingleSound(SOUND_DIR + "ping.wav"));
	}

	/**
	 * Plays a client.sound in the audio manager
	 * 
	 * @param client.sound
	 *            The client.sound to be played
	 * @param volume
	 *            The volume of the client.sound to be played
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

	public void playSound(int id, float volume) {
		if (soundMap.containsKey(id))
			playSound(soundMap.get(id),volume);
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
		Sound test = new SingleSound(SOUND_DIR + "test.wav");
		am.playSound(test, 100);
		am.update();
	}
}
