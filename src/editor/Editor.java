package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import server.world.Thing;
import server.world.TriggerPreset;
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
    private JLabel cursorInfo;
    
    /**
	 * @return the arenaPanel
	 */
	public ArenaPanel getArenaPanel() {
		return arenaPanel;
	}

	Tool currentTool;

	List<Terrain> tiles;
	List<Thing> objects;
	List<TriggerPreset> triggers;

	HashMap<Integer,Terrain> tileTable;
	HashMap<Integer,Thing> objectTable;
	HashMap<Integer,TriggerPreset> triggerTable;
	
	public boolean tileDataChanged = false;
	
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
        this.setBackground(Color.BLACK);
		//tiles = DataManager.loadTileListOld();
		tiles = (List<Terrain>) DataManager.loadObject(DataManager.FILE_TILES);
        try {
			DataManager.loadTileGraphics(tiles);
		} catch (IOException e) {
			System.err.println("Error while loading tile images.");
			e.printStackTrace();
		}
		tileTable = DataManager.getTileMap(tiles);
        
        //objects = DataManager.loadObjectListOld();
        objects = (List<Thing>) DataManager.loadObject(DataManager.FILE_OBJECTS);
        try {
			DataManager.loadObjectGraphics(objects);
		} catch (IOException e) {
			e.printStackTrace();
		}
        objectTable = DataManager.getObjectMap(objects);
        
        triggers = (List<TriggerPreset>) DataManager.loadObject(DataManager.FILE_TRIGGERS);
        if (triggers==null) {
        	triggers = new LinkedList<TriggerPreset>();
        }
        for (TriggerPreset tp:triggers) {
        	tp.setSwitchThing(objectTable.get(tp.getSwitchThingID()));
        	tp.setOriginalThing(objectTable.get(tp.getOriginalThingID()));
        }
        
        triggerTable = DataManager.getTriggerMap(triggers);
        
        getContentPane().setLayout(new BorderLayout());
        
        this.setJMenuBar(new MenuBar(this));
        ToolMenu tools = new ToolMenu(this);
        this.getContentPane().add(tools,BorderLayout.WEST);
        cursorInfo = new JLabel();
		this.getContentPane().add(cursorInfo,BorderLayout.SOUTH);
        this.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		if (tileDataChanged) {
	        		DataManager.saveObject(tiles, DataManager.FILE_TILES);
	        		DataManager.saveObject(objects, DataManager.FILE_OBJECTS);
	        		DataManager.saveObject(triggers, DataManager.FILE_TRIGGERS);
	        		DataManager.saveTileListOld(tiles);
	        		DataManager.saveObjectListOld(objects);
	        		
        		}
        	}
        });
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        openArena();
    }

    public void backupTileList() {
    	DataManager.saveObject(tiles, DataManager.FILE_TILES+"_backup");
    	DataManager.saveObject(objects, DataManager.FILE_OBJECTS+"_backup");
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
    	arenaPanel = new ArenaPanel(this,a);
    	getContentPane().add(arenaPanel, BorderLayout.CENTER);
    	arenaPanel.addMouseMotionListener(new MouseMotionListener(){
			@Override
			public void mouseDragged(MouseEvent arg0) {
				cursorInfo.setText(currentTool.getPointedTile().toString());
			}
			@Override
			public void mouseMoved(MouseEvent arg0) {
				cursorInfo.setText(currentTool.getPointedTile().toString());
			}});
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
            a = new EditorArena((ArenaData)DataManager.loadObject(file),tileTable,objectTable,triggerTable);
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
    
    public HashMap<Integer,Thing> getObjectTable() {
    	return objectTable;
    }
    
    public HashMap<Integer,Terrain> getTerrainTable() {
    	return tileTable;
    }
    
    public static void main(String[] args) {
		try {
		    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {
		}
    	new Editor();
    }

	public void newArena(String n, int w, int h) {
		setArena(new EditorArena(n,w,h));
	}

	public List<Thing> getObjectList() {
		return objects;
	}

	public List<Terrain> getTerrainList() {
		return tiles;
	}

	/**
	 * @return the triggerTable
	 */
	public HashMap<Integer, TriggerPreset> getTriggerTable() {
		return triggerTable;
	}

	/**
	 * @param triggerTable the triggerTable to set
	 */
	public void setTriggerTable(HashMap<Integer, TriggerPreset> triggerTable) {
		this.triggerTable = triggerTable;
	}

	public List<TriggerPreset> getTriggerList() {
		return triggers;
	}
}
