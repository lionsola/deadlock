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
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import server.world.Thing;
import server.world.Terrain;
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
    private ArenaPanel arenaPanel;
    
    /**
	 * @return the arenaPanel
	 */
	public ArenaPanel getArenaPanel() {
		return arenaPanel;
	}

	Tool currentTool;

	Collection<Terrain> tiles;
	Collection<Thing> objects;

	HashMap<Integer,Terrain> tileTable;
	HashMap<Integer,Thing> objectTable;
    public Editor() {
        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];

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
        
        getContentPane().setLayout(new BorderLayout());
        
        this.setJMenuBar(new MenuBar(this));
        this.getContentPane().add(new ToolMenu(this),BorderLayout.WEST);
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
    
    public void setArena(EditorArena a) {
    	if (arenaPanel!=null) {
    		arenaPanel.stop();
    		getContentPane().remove(arenaPanel);
    	}
    	arenaPanel = new ArenaPanel(a);
    	getContentPane().add(arenaPanel, BorderLayout.CENTER);
    	pack();
    	setTool(new Tool.MoveTool(arenaPanel));
    	arenaPanel.start();
    }
    
    public void openArena() {
		JFileChooser fc = new JFileChooser("resource/map/");
        fc.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Arena file", "arena");
        fc.setFileFilter(filter);
        int returnVal = fc.showDialog(this, "Open");
        EditorArena a = null;
        if (returnVal==JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            a = new EditorArena((ArenaData)DataManager.loadObject(file),tileTable,objectTable);
        }
        if (a!=null) {
        	setArena(a);
        }
	}
	
    public void setTool(Tool tool) {
    	arenaPanel.removeMouseListener(currentTool);
    	arenaPanel.removeMouseMotionListener(currentTool);
    	arenaPanel.removeMouseWheelListener(currentTool);
    	arenaPanel.addMouseListener(tool);
    	arenaPanel.addMouseMotionListener(tool);
    	arenaPanel.addMouseWheelListener(tool);
    	currentTool = tool;
    }
    
    public void addTileBG(Terrain t) {
    	tiles.add(t);
    	tileTable.put(t.getId(), t);
    }
    
    public void addObject(Thing t) {
    	objects.add(t);
    	objectTable.put(t.getId(), t);
    }
    
    public static void main(String[] args) {
		try {
		    // Set System L&F
		    UIManager.setLookAndFeel(
		        UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {
		}
    	new Editor();
    }

	public void newArena(String n, int w, int h) {
		setArena(new EditorArena(n,w,h,tileTable,objectTable));
	}
}
