package editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import editor.dialogs.MissionDialog;
import editor.tools.Tool;;

public class MenuBar extends JMenuBar {
	private static final long serialVersionUID = 1984099892818541571L;
	private final Editor editor;
	public MenuBar (final Editor editor) {
		this.editor = editor;
		JMenu file = new JMenu("File");
		this.add(file);
		
		final JMenuItem newArena = new JMenuItem("New Arena");
		newArena.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (editor.getArenaPanel()!=null) {
					int dialogResult = JOptionPane.showConfirmDialog (editor, "Save the current map?");
					if(dialogResult == JOptionPane.CANCEL_OPTION){
						return;
					} else if (dialogResult == JOptionPane.YES_OPTION) {
						DataManager.exportArenaData(editor.getArenaPanel().getArena());
					}
				}
				
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				panel.add(new JLabel("Name: "));
				JTextField name = new JTextField(10);
				panel.add(name);
				panel.add(new JLabel("Size"));
				
				JPanel sizePanel = new JPanel();
				JTextField width = new JTextField(5);
				JTextField height = new JTextField(5);
				sizePanel.add(width);
				sizePanel.add(new JLabel(" x "));
				sizePanel.add(height);
				
				panel.add(sizePanel);
				JOptionPane.showMessageDialog(editor,panel,"New Arena",JOptionPane.PLAIN_MESSAGE);
				
				try {
					String n = name.getText();
					if (n.equals(""))
						throw new Exception();
					int w = Integer.parseInt(width.getText());
					int h = Integer.parseInt(height.getText());
					editor.newArena(n,w,h);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(editor, "Invalid inputs!");
				}
			}
		});
		file.add(newArena);
		file.addSeparator();
		
		final JMenuItem openNew = new JMenuItem("Open");
		openNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.openArena();
			}
		});
		file.add(openNew);
		file.addSeparator();
		
		final JMenuItem save = new JMenuItem("Save");
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DataManager.exportArenaData(editor.getArenaPanel().getArena());
			}
		});
		file.add(save);
		
		final JMenuItem saveImages = new JMenuItem("Export Images");
		saveImages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							DataManager.exportImages(editor.getArenaPanel().getArena());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}}).start();
			}
		});
		file.add(saveImages);
		file.addSeparator();
		
		final JMenuItem backup = new JMenuItem("Back-up Tile Data");
		backup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				editor.backupTileList();
			}
		});
		file.add(backup);
		
		JMenu edit = new JMenu("Edit");
		add(edit);
		final JMenuItem editSize = new JMenuItem("Map");
		editSize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				EditorArena a = editor.getArenaPanel().getArena();
				
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
				
				JPanel namePanel = new JPanel();
				namePanel.add(new JLabel("Name: "));
				JTextField name = new JTextField(editor.getArenaPanel().getArena().getName());
				name.setColumns(15);
				name.setEditable(true);
				namePanel.add(name);
				panel.add(namePanel);
				
				JPanel sizePanel = new JPanel();
				JFormattedTextField width = new JFormattedTextField(a.getWidth());
				JFormattedTextField height = new JFormattedTextField(a.getHeight());
				
				sizePanel.add(new JLabel("Size: "));
				sizePanel.add(width);
				sizePanel.add(new JLabel(" x "));
				sizePanel.add(height);
				
				panel.add(sizePanel);
				
				JPanel hAPanel = new JPanel();
				hAPanel.add(new JLabel("Horizontal alignment: "));
				String[] hAligns = { "Left", "Right" };
				JComboBox<String> hAlign = new JComboBox<String>(hAligns);
				hAlign.setMaximumSize(hAlign.getPreferredSize());
				hAPanel.add(hAlign);
				panel.add(hAPanel);
				
				JPanel vAPanel = new JPanel();
				vAPanel.add(new JLabel("Vertical alignment: "));
				String[] vAligns = { "Top", "Bottom" };
				JComboBox<String> vAlign = new JComboBox<String>(vAligns);
				vAlign.setMaximumSize(vAlign.getPreferredSize());
				vAPanel.add(vAlign);
				panel.add(vAPanel);
				
				int result = JOptionPane.showConfirmDialog(editor,panel,"Edit map",JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (result==JOptionPane.OK_OPTION) {
					int w = Integer.parseInt(width.getText());
					int h = Integer.parseInt(height.getText());
					if (w>0 && h>0 && !name.getText().equals("")) {
						a.setName(name.getText());
						a.changeSize(w,h, hAlign.getSelectedIndex(), vAlign.getSelectedIndex());
					}
				}
			}});
		edit.add(editSize);
		
		final JMenuItem editMission = new JMenuItem("Mission");
		editMission.addActionListener(new ActionListener() {
			MissionDialog dialog = null;
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (dialog==null) {
					dialog = new MissionDialog(editor);
				}
				if (!dialog.isVisible()) {
					dialog.setVisible(true);
				}
				editor.setTool(new Tool.MissionVarChooser(editor.getArenaPanel(), dialog));
				dialog.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						editor.setTool(new Tool.MoveTool(editor.getArenaPanel()));
					}
				});
			}});
		edit.add(editMission);
		
		JMenu view = new JMenu("View");
		add(view);
		JCheckBoxMenuItem terrain = new JCheckBoxMenuItem("Terrain");
		terrain.setSelected(true);
		terrain.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					editor.getArenaPanel().renderTerrain = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					editor.getArenaPanel().renderTerrain = false;
				}
			}
		});
		view.add(terrain);
		
		JCheckBoxMenuItem things = new JCheckBoxMenuItem("Things");
		things.setSelected(true);
		things.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					editor.getArenaPanel().renderThing = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					editor.getArenaPanel().renderThing = false;
				}
			}
		});
		view.add(things);
		
		JCheckBoxMenuItem misc = new JCheckBoxMenuItem("Misc");
		misc.setSelected(true);
		misc.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					editor.getArenaPanel().renderMisc = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					editor.getArenaPanel().renderMisc = false;
				}
			}
		});
		view.add(misc);
		
		view.addSeparator();
		for (int layer=0;layer<editor.layerTypes.length;layer++) {
			JCheckBoxMenuItem viewLayer = new JCheckBoxMenuItem(editor.layerTypes[layer]);
			viewLayer.setSelected(true);
			final int l = layer;
			viewLayer.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						editor.getArenaPanel().renderLayer[l] = true;
					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
						editor.getArenaPanel().renderLayer[l] = false;
					}
				}
			});
			view.add(viewLayer);
		}
		view.addSeparator();
		
		JCheckBoxMenuItem light = new JCheckBoxMenuItem("Soft light (ingame)");
		light.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					editor.getArenaPanel().renderLight = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					editor.getArenaPanel().renderLight = false;
				}
			}
		});
		view.add(light);
		
		JCheckBoxMenuItem hardLight = new JCheckBoxMenuItem("Hard light");
		hardLight.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					editor.getArenaPanel().renderHardLight = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					editor.getArenaPanel().renderHardLight = false;
				}
			}
		});
		view.add(hardLight);
		
		JCheckBoxMenuItem grid = new JCheckBoxMenuItem("Grid");
		grid.setSelected(true);
		grid.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					editor.getArenaPanel().renderGrid = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					editor.getArenaPanel().renderGrid = false;
				}
			}
		});
		view.add(grid);
		
		JCheckBoxMenuItem config = new JCheckBoxMenuItem("Thing Sprite Config.");
		config.setSelected(false);
		config.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					editor.getArenaPanel().renderConfig = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					editor.getArenaPanel().renderConfig = false;
				}
			}
		});
		view.add(config);
		
		JCheckBoxMenuItem mconfig = new JCheckBoxMenuItem("Misc Sprite Config.");
		mconfig.setSelected(false);
		mconfig.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					editor.getArenaPanel().renderMiscConfig = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					editor.getArenaPanel().renderMiscConfig = false;
				}
			}
		});
		view.add(mconfig);
		
		view.addSeparator();
		JCheckBoxMenuItem spawns = new JCheckBoxMenuItem("Spawn locations");
		spawns.setSelected(false);
		spawns.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					editor.getArenaPanel().renderSpawns = true;
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					editor.getArenaPanel().renderSpawns = false;
				}
			}
		});
		view.add(spawns);
	}
}
