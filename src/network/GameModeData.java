package network;

public class GameModeData {
	public static final GameModeData FIVE_ON_FIVE = new GameModeData();
	public static final GameModeData[] modes = {FIVE_ON_FIVE};
	
	public int id;
	public String name;
	public String descriptions;
	public String[] maps;
	
	public static void initialize() {
		FIVE_ON_FIVE.id = 0;
		FIVE_ON_FIVE.name = "Five vs Five";
		FIVE_ON_FIVE.descriptions = "idk";
		String[] fofMaps = {"maze"};
		FIVE_ON_FIVE.maps = fofMaps;
	}
}
