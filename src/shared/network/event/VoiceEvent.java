package shared.network.event;

public class VoiceEvent extends SoundEvent {
	private static final long serialVersionUID = -4490555625433873400L;
	public static final float DEFAULT_VOLUME = 15;
	public static final int CUSTOM_LINE_ID = 150; 
	
	public final String line;
	
	public VoiceEvent(int id, double volume, double x, double y, String line) {
		super(id, volume, x, y);
		this.line = line;
	}
}
