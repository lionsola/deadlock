package editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import server.world.Arena;
import server.world.Tile;

/**
 * The master frame that holds the game screens.
 * 
 *  @author Anh Pham
 * 
 * @author Anh Pham
 */
public class Editor extends JFrame {
    
    private static final long serialVersionUID = 5913371417037613515L;

    private int width;
    private int height;
    // private int scale;
    ArenaPanel arenaPanel;
    MouseInputListener currentTool;

	Collection<Tile> tiles;

	HashMap<Integer,Tile> tileTable;
    public Editor() {
        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        setIgnoreRepaint(true);
        // Should change to load from save file
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        width = (int) screenSize.getWidth();
        height = (int) screenSize.getHeight();

        setTitle("Map Editor");
        // setIgnoreRepaint(true);
        //setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        if (screen.isFullScreenSupported()) {
            //setUndecorated(true);
        }
        
        getContentPane().setLayout(new BorderLayout());
        
        try {
			tiles = DataManager.loadTileListOld();
		} catch (ClassNotFoundException | IOException e) {
			tiles = new LinkedList<Tile>();
		}
		try {
			DataManager.loadTileGraphics(tiles);
		} catch (IOException e) {
			System.err.println("Error while loading tile images.");
			e.printStackTrace();
		}
		tileTable = DataManager.getTileMap(tiles);
        
        this.add(new ToolMenu(this),BorderLayout.WEST);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        openArena();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
    
    public void openArena() {
		JFileChooser fc = new JFileChooser("resource/map/");
        fc.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Map file", "map");
        fc.setFileFilter(filter);
        int returnVal = fc.showDialog(this, "Attach");
        Arena a = null;
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            a = new Arena(file,true);
        }
        if (a!=null) {
        	arenaPanel = new ArenaPanel(a);
        	getContentPane().add(arenaPanel, BorderLayout.CENTER);
        	pack();
        	setTool(new MoveTool(arenaPanel));
        }
	}
	
    public void setTool(MouseInputListener tool) {
    	arenaPanel.removeMouseListener(currentTool);
    	arenaPanel.removeMouseMotionListener(currentTool);
    	arenaPanel.addMouseListener(tool);
    	arenaPanel.addMouseMotionListener(tool);
    	currentTool = tool;
    }
    
	public void saveArena() {
		
	}
	
	public void newArena() {

	}
    
    public static void main(String[] args) {
    	new Editor();
    }
}
