package editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import server.world.Thing;
import server.world.Arena.ArenaData;
import server.world.trigger.TileSwitchPreset;
import server.world.Misc;
import server.world.Terrain;

/**
 * The master frame that holds the game screens.
 * 
 *  @author Anh Pham
 * 
 * @author Anh Pham
 */
public class Editor extends JFrame implements KeyListener {
	public String[] layerTypes = {"Ground - 0m", "Medium - 1m", "High - 2m", "Ceiling - 4m"};
    
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

	//List<Terrain> tiles;
	//List<Thing> objects;
	//List<TriggerPreset> triggers;

	HashMap<Integer,Terrain> tileTable;
	HashMap<Integer,Thing> objectTable;
	HashMap<Integer,TileSwitchPreset> triggerTable;
	public HashMap<Integer,Misc> miscTable;
	
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
		//List<Terrain >tiles = (List<Terrain>) DataManager.loadObject(DataManager.FILE_TILES);
        //tileTable = DataManager.getTileMap(tiles);
        tileTable = (HashMap<Integer,Terrain>)DataManager.loadObject(DataManager.FILE_TILES);
		DataManager.loadImage(tileTable.values());
		
        //objects = DataManager.loadObjectListOld();
        //objects = (List<Thing>) DataManager.loadObject(DataManager.FILE_OBJECTS);
        //objectTable = DataManager.getObjectMap(objects);
        
        objectTable = (HashMap<Integer, Thing>) DataManager.loadObject(DataManager.FILE_OBJECTS);
        DataManager.loadImage(objectTable.values());
        
        miscTable = (HashMap<Integer, Misc>) DataManager.loadObject(DataManager.FILE_MISC);
        if (miscTable==null) {
        	miscTable = new HashMap<Integer,Misc>();
        }
        DataManager.loadImage(miscTable.values());
        //triggers = (List<TriggerPreset>) DataManager.loadObject(DataManager.FILE_TRIGGERS);
        
        triggerTable = (HashMap<Integer, TileSwitchPreset>) DataManager.loadObject(DataManager.FILE_TRIGGERS);
        if (triggerTable==null) {
        	triggerTable = new HashMap<Integer,TileSwitchPreset>();
        }
        for (TileSwitchPreset tp:triggerTable.values()) {
        	try {
	        	if (tp.getItemType()==TileSwitchPreset.THING) {
		        	tp.setSwitchThing(objectTable.get(tp.getSwitchThingID()));
		        	tp.setOriginalThing(objectTable.get(tp.getOriginalThingID()));
	        	} else if (tp.getItemType()==TileSwitchPreset.MISC) {
	        		tp.setSwitchThing(miscTable.get(tp.getSwitchThingID()));
	        		tp.setOriginalThing(miscTable.get(tp.getOriginalThingID()));
	        	}
        	} catch (Exception e) {
        		System.err.println("Error while loading tile switch preset "+tp.getName());
        		e.printStackTrace();
        	}
        }
        
        getContentPane().setLayout(new BorderLayout());
        
        this.setJMenuBar(new MenuBar(this));
        ToolBar tools = new ToolBar(this);
        this.getContentPane().add(tools,BorderLayout.WEST);
        cursorInfo = new JLabel();
		this.getContentPane().add(cursorInfo,BorderLayout.SOUTH);
        this.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		if (tileDataChanged) {
	        		//DataManager.saveObject(tiles, DataManager.FILE_TILES);
	        		//DataManager.saveObject(objects, DataManager.FILE_OBJECTS);
	        		//DataManager.saveObject(triggers, DataManager.FILE_TRIGGERS);
	        		
	        		DataManager.saveObject(tileTable, DataManager.FILE_TILES);
	        		DataManager.saveObject(objectTable, DataManager.FILE_OBJECTS);
	        		DataManager.saveObject(triggerTable, DataManager.FILE_TRIGGERS);
	        		DataManager.saveObject(miscTable, DataManager.FILE_MISC);
        		}
        	}
        });
        pack();
        setVisible(true);
        setLocationRelativeTo(null);
        addKeyListener(this);
        openArena();
    }

    public void backupTileList() {
    	DataManager.saveObject(tileTable, DataManager.FILE_TILES+"_backup");
    	DataManager.saveObject(objectTable, DataManager.FILE_OBJECTS+"_backup");
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
				mouseMoved(arg0);
			}
			@Override
			public void mouseMoved(MouseEvent arg0) {
				if (currentTool!=null) {
					String info = currentTool.getPointedTile().toString();
					info += ", illumination: "+ Integer.toHexString(arenaPanel.getArena().getLightAt(currentTool.getPointedCoord()));
					cursorInfo.setText(info);
				}
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
            Object o = DataManager.loadObject(file);
            if (o instanceof ArenaData) {
            	a = new EditorArena((ArenaData)o,tileTable,objectTable,triggerTable,miscTable);
            }
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

	/*
	public List<Terrain> getTerrainList() {
		return tiles;
	}*/

	/**
	 * @return the triggerTable
	 */
	public HashMap<Integer, TileSwitchPreset> getTriggerTable() {
		return triggerTable;
	}

	/**
	 * @param triggerTable the triggerTable to set
	 */
	public void setTriggerTable(HashMap<Integer, TileSwitchPreset> triggerTable) {
		this.triggerTable = triggerTable;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode()==KeyEvent.VK_ALT && currentTool!=null) {
			currentTool.setAlternative(true);
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if (arg0.getKeyCode()==KeyEvent.VK_ALT && currentTool!=null) {
			currentTool.setAlternative(false);
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
