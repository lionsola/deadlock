package client.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JComponent;

import server.world.Arena;
import server.world.Terrain;

/**
 * The minimap will draw all characters received by the server,
 * even if it doesn't show on the screen.
 */
public class Minimap extends JComponent {

	private static final long serialVersionUID = -1715530638861052738L;

	private static final int TILE_SIZE = 2; // tile size
	private static final int RADIUS = 2; // character radius

	private static final int charTransparency = 180;
	private static final int transparency = 255;
	private static final Color BORDER_COLOR = new Color(0, 0, 0, transparency * 2 / 3);
	private static final Color UNWALKABLE_TILE_COLOR = new Color(50, 100, 255, transparency);

	private static final Color PLAYER_COLOR = new Color(255, 255, 255, charTransparency);
	private static final Color TEAM_COLOR = new Color(0, 255, 0, charTransparency);
	private static final Color ENEMY_COLOR = new Color(255, 0, 0, charTransparency);

	private Arena arena;
	private ClientPlayer mainPlayer;
	private List<ClientPlayer> players;

	/**
	 * Creates a new minimap in a specified arena with a list of players.
	 * 
	 * @param arena
	 *            the arena the minimap should model
	 * @param id
	 *            the id
	 * @param players
	 *            the list of players in the arena
	 */
	public Minimap(Arena arena, int id, List<ClientPlayer> players) {
		super();
		this.arena = arena;
		this.players = players;
		this.mainPlayer = Utils.findPlayer(players, id);
		this.setSize(arena.getWidth()*TILE_SIZE, arena.getHeight()*TILE_SIZE);
		this.setPreferredSize(new Dimension(arena.getWidth()*TILE_SIZE, arena.getHeight()*TILE_SIZE));
		this.setMaximumSize(new Dimension(arena.getWidth()*TILE_SIZE, arena.getHeight()*TILE_SIZE));
	}

	/**
	 * Paint the component
	 * 
	 * @param g
	 *            the client.graphics
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// render border
		g.setColor(BORDER_COLOR);
		int borderWidth = arena.getWidth() * TILE_SIZE;
		int borderHeight = arena.getHeight() * TILE_SIZE;
		g.fillRect(0, 0, borderWidth, borderHeight);

		// render minimap
		for (int i = 0; i < arena.getWidth(); i++) {
			for (int j = 0; j < arena.getHeight(); j++) {
				boolean walkable = arena.get(i, j).isTraversable();
				if (!walkable) {
					g.setColor(UNWALKABLE_TILE_COLOR);
					int x = i * TILE_SIZE;
					int y = j * TILE_SIZE;
					g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
				}
			}
		}

		// render teams
		for (ClientPlayer p : players) {
			if (!p.active)
				continue;
			if (p.id != mainPlayer.id) {
				if (p.team == mainPlayer.team) { // if same team
					g.setColor(TEAM_COLOR);
				} else {
					g.setColor(ENEMY_COLOR);
				}
			} else {
				g.setColor(PLAYER_COLOR);
			}

			int characterX = (int)((p.character.x * TILE_SIZE / Terrain.tileSize) - RADIUS);
			int characterY = (int) ((p.character.y * TILE_SIZE / Terrain.tileSize) - RADIUS);
			int characterSize = 2 * RADIUS;
			g.fillOval(characterX, characterY, characterSize, characterSize);
		}
		g.setColor(PLAYER_COLOR);
		ClientPlayer p = mainPlayer;
		int characterX = (int)((p.character.x * TILE_SIZE / Terrain.tileSize) - RADIUS);
		int characterY = (int) ((p.character.y * TILE_SIZE / Terrain.tileSize) - RADIUS);
		int characterSize = 2 * RADIUS;
		g.fillOval(characterX, characterY, characterSize, characterSize);
	}

}
