package editor.dialogs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;

import editor.SpawnPoint;
import editor.SpawnPoint.Behaviour;
import editor.SpawnPoint.CharType;
import editor.SpawnPoint.SpawnType;

public class SpawnDialog extends JDialog {
	private static final long serialVersionUID = 5917436825785813483L;
	private JLabel id;
	private JComboBox<Integer> player;
	private JComboBox<Behaviour> behaviour;
	private JComboBox<String> level;
	private JComboBox<String> team;
	private JComboBox<SpawnType> spawnType;
	private JList<CharType> characters;
	
	public SpawnDialog (final Window owner, final JToggleButton button) {
		super(owner, "Spawn");
        JPanel panel = new JPanel(new GridBagLayout());
        //editor.setTool(tool);
        addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent arg0) {
				button.doClick();
			}});
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        
        id = new JLabel("?");
        String[] levels = {"0", "1", "2"};
        level = new JComboBox<String>(levels);
        
        characters = new JList<SpawnPoint.CharType>(CharType.values());
        characters.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        characters.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        characters.setSelectedIndex(0);
        characters.setVisibleRowCount(-1);
        
        behaviour = new JComboBox<SpawnPoint.Behaviour>(SpawnPoint.Behaviour.values());
        
        Integer[] players = {1,2,3,4};
        player = new JComboBox<Integer>(players);
        
        String[] teams = {"Runners","Chasers"};
        team = new JComboBox<String>(teams);
        
        spawnType = new JComboBox<SpawnType>(SpawnType.values());
        
        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("ID: "));
        c.gridy++;
        panel.add(new JLabel("Level: "),c);
        c.gridy++;
        panel.add(new JLabel("Team: "),c);
        c.gridy++;
        panel.add(new JLabel("Players: "),c);
        c.gridy++;
        panel.add(new JLabel("Spawn type: "),c);
        c.gridy++;
        panel.add(new JLabel("Behaviour: "),c);
        c.gridy++;
        panel.add(new JLabel("Character: "),c);
        
        c.gridx = 1;
        c.gridy = 0;
        panel.add(id);
        c.gridy++;
        panel.add(level,c);
        c.gridy++;
        panel.add(team,c);
        c.gridy++;
        panel.add(player,c);
        c.gridy++;
        panel.add(spawnType,c);
        c.gridy++;
        panel.add(behaviour,c);
        c.gridy++;
        panel.add(new JScrollPane(characters),c);
        
        getContentPane().add(panel);
        //setContentPane(panel);
        this.pack();
        if (button!=null) {
	        Point p = button.getLocationOnScreen();
	        p.x += button.getWidth();
	        setLocation(p);
        } else {
        	setLocationRelativeTo(owner);
        }
	}
	
	public SpawnPoint getSpawn() {
		SpawnPoint s = new SpawnPoint();
		s.level = level.getSelectedIndex();
		s.team = team.getSelectedIndex();
		s.players = (int) player.getSelectedItem();
		s.setups = characters.getSelectedValuesList();
		s.type = (SpawnType) spawnType.getSelectedItem();
		s.behaviour = (Behaviour) behaviour.getSelectedItem();
		
		return s;
	}
	
	public void setSpawn(SpawnPoint s) {
		if (s!=null) {
			id.setText(String.valueOf(s.getId()));
			level.setSelectedIndex(s.level);
			team.setSelectedIndex(s.team);
			player.setSelectedItem(s.players);
			characters.clearSelection();
			for (CharType c:s.setups) {
				characters.setSelectedValue(c, false);
			}
			spawnType.setSelectedItem(s.type);
			behaviour.setSelectedItem(s.behaviour);
		} else {
			id.setText("?");
		}
	}
}
