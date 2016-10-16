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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import server.world.Arena;
import server.world.Tile;
import server.world.TileBG;
import editor.DataManager.ArenaData;

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

	Collection<TileBG> tiles;
	Collection<Tile> objects;

	HashMap<Integer,TileBG> tileTable;
	HashMap<Integer,Tile> objectTable;
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
        
        
		tiles = DataManager.loadTileListOld();
        try {
			DataManager.loadTileGraphics(tiles);
		} catch (IOException e) {
			System.err.println("Error while loading tile images.");
			e.printStackTrace();
		}
		tileTable = DataManager.getTileMap(tiles);
        
        objects = DataManager.loadObjectListOld();
        try {
			DataManager.loadObjectGraphics(objects);
		} catch (IOException e) {
			e.printStackTrace();
		}
        objectTable = DataManager.getObjectMap(objects);
        
        this.add(new ToolMenu(this),BorderLayout.WEST);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        openArenaNew();
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
        int returnVal = fc.showDialog(this, "Open");
        Arena a = null;
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            a = new Arena(file,tileTable,objectTable);
        }
        if (a!=null) {
        	if (arenaPanel!=null) {
        		getContentPane().remove(arenaPanel);
        	}
        	arenaPanel = new ArenaPanel(a);
        	getContentPane().add(arenaPanel, BorderLayout.CENTER);
        	pack();
        	setTool(new Tool.MoveTool(arenaPanel));
        }
	}
	
    
    public void openArenaNew() {
		JFileChooser fc = new JFileChooser("resource/map/");
        fc.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Arena file", "arena");
        fc.setFileFilter(filter);
        int returnVal = fc.showDialog(this, "Open");
        Arena a = null;
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            a = new Arena((ArenaData)DataManager.loadObject(file),tileTable,objectTable);
        }
        if (a!=null) {
        	if (arenaPanel!=null) {
        		arenaPanel.stop();
        		getContentPane().remove(arenaPanel);
        	}
        	arenaPanel = new ArenaPanel(a);
        	getContentPane().add(arenaPanel, BorderLayout.CENTER);
        	pack();
        	setTool(new Tool.MoveTool(arenaPanel));
        }
	}
	
    public void setTool(MouseInputListener tool) {
    	arenaPanel.removeMouseListener(currentTool);
    	arenaPanel.removeMouseMotionListener(currentTool);
    	arenaPanel.addMouseListener(tool);
    	arenaPanel.addMouseMotionListener(tool);
    	currentTool = tool;
    }
    
    public void addTileBG(TileBG t) {
    	tiles.add(t);
    	tileTable.put(t.getId(), t);
    }
    
    public void addObject(Tile t) {
    	objects.add(t);
    	objectTable.put(t.getId(), t);
    }
	
	public void saveArena() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					DataManager.exportImages(arenaPanel.getArena());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
    
    public static void main(String[] args) {
    	new Editor();
    }
}
