package client.sound;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.weapon.WeaponFactory;
import shared.network.event.SoundEvent;

/**
 * Manages the audio in game.
 */
public class AudioManager implements Runnable {
	
	// volume gain in decibel
	public static final float GAIN_MINVOLUME = -35;
	public static final float GAIN_RANGE = 45;

	/** Upper bound of in-game sounds' volume.
	 * Sounds will be from 170db (flash-bang grenade) to 0db (silence).
	 */
	public static final float GAME_RANGE = 170;

	static MusicPlayer currentMusic = null;

	private ExecutorService threadPool;
	private ConcurrentLinkedQueue<PlayMessage> pending;
	
	private HashMap<Integer, Sound> soundMap;
	
	private static final String SOUND_DIR = "/audio/sounds/";
	private static final String MUSIC_DIR = "/audio/music/";
	
	private static final String FOOTSTEP_DIR = SOUND_DIR + "footstep/";
	
	/**
	 * Creates a new Audio Manager, where all the sounds used for the game are loaded
	 */
	public AudioManager() {
		threadPool = Executors.newFixedThreadPool(10);
		pending = new ConcurrentLinkedQueue<PlayMessage>();
		soundMap = new HashMap<Integer, Sound>();
		
		WeaponFactory.initWeapons();

		// initialize sound files
		soundMap.put(0, new SingleSound(SOUND_DIR + "weapon/" + "gunshot_assault.wav"));
		soundMap.put(1, new SingleSound(SOUND_DIR + "weapon/" + "gunshot_shotgun.wav"));
		soundMap.put(2, new SingleSound(SOUND_DIR + "weapon/" + "gunshot_pistol.wav"));
		soundMap.put(3, new SingleSound(SOUND_DIR + "weapon/" + "gunshot_silent_pistol.wav"));
		soundMap.put(4, new SingleSound(SOUND_DIR + "weapon/" + "weapon_whoosh.wav"));
		
		AlternatingSound footstepDirt = new AlternatingSound();
		footstepDirt.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_dirt0.wav"));
		footstepDirt.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_dirt1.wav"));
		footstepDirt.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_dirt2.wav"));
		footstepDirt.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_dirt3.wav"));
		soundMap.put(SoundEvent.FOOTSTEP_DIRT_ID, footstepDirt);
		soundMap.put(SoundEvent.FOOTSTEP_DEFAULT_ID, footstepDirt);
		
		AlternatingSound footstepGrass = new AlternatingSound();
		footstepGrass.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_grass0.wav"));
		footstepGrass.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_grass1.wav"));
		footstepGrass.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_grass2.wav"));
		footstepGrass.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_grass3.wav"));
		soundMap.put(SoundEvent.FOOTSTEP_GRASS_ID, footstepGrass);
		
		AlternatingSound footstepHard = new AlternatingSound();
		footstepHard.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_hard0.wav"));
		footstepHard.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_hard1.wav"));
		footstepHard.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_hard2.wav"));
		footstepHard.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_hard3.wav"));
		soundMap.put(SoundEvent.FOOTSTEP_HARD_ID, footstepHard);
		
		AlternatingSound footstepWater = new AlternatingSound();
		footstepWater.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_water0.wav"));
		footstepWater.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_water1.wav"));
		footstepWater.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_water2.wav"));
		footstepWater.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_water3.wav"));
		soundMap.put(SoundEvent.FOOTSTEP_WATER_ID, footstepWater);
		
		AlternatingSound footstepCarpet = new AlternatingSound();
		footstepCarpet.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_carpet0.wav"));
		footstepCarpet.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_carpet1.wav"));
		footstepCarpet.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_carpet2.wav"));
		footstepCarpet.addSound(new SingleSound(FOOTSTEP_DIR + "footstep_carpet3.wav"));
		soundMap.put(SoundEvent.FOOTSTEP_CARPET_ID, footstepCarpet);
		
		AlternatingSound bulletwall = new AlternatingSound();
		bulletwall.addSound(new SingleSound(SOUND_DIR + "environment/" + "bulletwall0.wav"));
		bulletwall.addSound(new SingleSound(SOUND_DIR + "environment/" + "bulletwall1.wav"));
		soundMap.put(SoundEvent.BULLET_WALL_SOUND_ID,bulletwall);
		
		AlternatingSound cricket = new AlternatingSound();
		cricket.addSound(new SingleSound(SOUND_DIR + "environment/" + "cricket0.wav"));
		cricket.addSound(new SingleSound(SOUND_DIR + "environment/" + "cricket1.wav"));
		cricket.addSound(new SingleSound(SOUND_DIR + "environment/" + "cricket2.wav"));
		cricket.addSound(new SingleSound(SOUND_DIR + "environment/" + "cricket3.wav"));
		cricket.addSound(new SingleSound(SOUND_DIR + "environment/" + "cricket4.wav"));
		cricket.addSound(new SingleSound(SOUND_DIR + "environment/" + "cricket5.wav"));
		
		AlternatingSound critter = new AlternatingSound();
		critter.addSound(cricket);
		critter.addSound(new SingleSound(SOUND_DIR + "environment/" + "scuttling.wav"));
		soundMap.put(SoundEvent.CRITTER_NOISE_ID,critter);
		
		soundMap.put(SoundEvent.GRENADE_EXPLODE_SOUND_ID,new SingleSound(SOUND_DIR + "weapon/" + "grenade_explode.wav"));
		
		soundMap.put(SoundEvent.DOOR_OPEN_ID, new SingleSound(SOUND_DIR + "environment/" + "door_open.wav"));
		
		Sound ping = new SingleSound(SOUND_DIR + "ping.wav");
		soundMap.put(SoundEvent.PING_SOUND_ID,ping);
		Sound staticNoise = new SingleSound(SOUND_DIR + "environment/" + "tv_static.wav");
		soundMap.put(SoundEvent.STATIC_NOISE_ID,staticNoise);
		
		AlternatingSound swi = new AlternatingSound();
		swi.addSound(new SingleSound(SOUND_DIR + "environment/" + "switch0.wav"));
		swi.addSound(new SingleSound(SOUND_DIR + "environment/" + "switch1.wav"));
		soundMap.put(SoundEvent.SWITCH_ID,swi);
		
		AlternatingSound tick = new AlternatingSound();
		tick.addSound(new SingleSound(SOUND_DIR + "environment/" + "tick0.wav"));
		tick.addSound(new SingleSound(SOUND_DIR + "environment/" + "tick1.wav"));
		soundMap.put(SoundEvent.TICK_ID,tick);
		
		soundMap.put(SoundEvent.MELEE_HIT_ID, new SingleSound(SOUND_DIR + "weapon/" + "melee_hit.wav"));
		
		AlternatingSound tv = new AlternatingSound();
		tv.addSound(footstepHard);
		tv.addSound(footstepHard);
		tv.addSound(footstepHard);
		tv.addSound(staticNoise);
		tv.addSound(soundMap.get(SoundEvent.DOOR_OPEN_ID));
		tv.addSound(soundMap.get(3));
		tv.addSound(soundMap.get(3));
		
		soundMap.put(SoundEvent.TV_NOISE_ID, tv);
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
		//threadPool.execute(this);
		run();
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

	public HashMap<Integer,Sound> getSoundMap() {
		return soundMap;
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
