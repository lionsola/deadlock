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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import client.sound.AudioManager;
import editor.tools.Tool;
import server.world.Thing;
import server.world.Arena.ArenaData;
import server.world.trigger.TileSwitchPreset;
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
	Tool save;

	//List<Terrain> tiles;
	//List<Thing> objects;
	//List<TriggerPreset> triggers;

	HashMap<Integer,Terrain> tileTable;
	HashMap<Integer,Thing> objectTable;
	HashMap<Integer,TileSwitchPreset> triggerTable;
	private AudioManager audioManager;
	
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
        tileTable = (HashMap<Integer,Terrain>)DataManager.loadObject(DataManager.FILE_TILES);
		DataManager.loadImage(tileTable.values());
        
        objectTable = (HashMap<Integer, Thing>) DataManager.loadObject(DataManager.FILE_OBJECTS);
        DataManager.loadImage(objectTable.values());
        DataManager.updateParticleSource(objectTable.values());

        triggerTable = (HashMap<Integer, TileSwitchPreset>) DataManager.loadObject(DataManager.FILE_TRIGGERS);
        if (triggerTable==null) {
        	triggerTable = new HashMap<Integer,TileSwitchPreset>();
        }
        for (TileSwitchPreset tp:triggerTable.values()) {
        	try {
	        	tp.setSwitchThing(objectTable.get(tp.getSwitchThingID()));
	        	tp.setOriginalThing(objectTable.get(tp.getOriginalThingID()));
        	} catch (Exception e) {
        		System.err.println("Error while loading tile switch preset "+tp.getName());
        		e.printStackTrace();
        	}
        }
        
        audioManager = new AudioManager();
        
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
    	DataManager.saveObject(triggerTable, DataManager.FILE_TRIGGERS+"_backup");
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
            	a = new EditorArena((ArenaData)o,tileTable,objectTable,triggerTable);
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
			currentTool.setAlternative(!currentTool.isAlternative());
		} else if (arg0.getKeyCode()==KeyEvent.VK_CAPS_LOCK) {
			if (save==null) {
				save = currentTool;
				setTool(new Tool.MoveTool(arenaPanel));
			} else {
				setTool(save);
				save = null;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public AudioManager getAudioManager() {
		return audioManager;
	}

	public static String printAnything(Object object) {
	    if (object!=null && object.getClass().isArray()) {
	        if (object instanceof Object[]) // can we cast to Object[]
	            return Arrays.toString((Object[]) object);
	        else {  // we can't cast to Object[] - case of primitive arrays
	            int length = Array.getLength(object);
	            Object[] objArr = new Object[length];
	            for (int i=0; i<length; i++)
	                objArr[i] =  Array.get(object, i);
	            return Arrays.toString(objArr);
	        }
	    } else {
	    	return String.valueOf(object);
	    }
	}
}
