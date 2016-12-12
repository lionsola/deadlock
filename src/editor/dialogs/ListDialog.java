package editor.dialogs;
 
import javax.swing.*;

import editor.CellRenderable;
import editor.CustomListModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collection;
 
/*
 * ListDialog is meant to be used to display a list of objects.
 * A cell renderer should be provided along with buttons. The first button given
 * is also triggered with a double click on a list item.
 */

public class ListDialog<T> extends JDialog {
	private static final long serialVersionUID = -2705330003771825132L;
	
	public static <I extends CellRenderable> I selectFromList(Collection<I> items,Window owner) {
		CustomListModel<I> tlm = new CustomListModel<I>(new ArrayList<I>(items));
		JButton OK = new JButton("OK");
		JButton[] buttons = {OK};
		final ListDialog<I> listDialog = new ListDialog<I>(owner, null, "Tile", buttons, tlm);
		OK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				listDialog.setVisible(false);
			}
		});
		listDialog.setModalityType(ModalityType.APPLICATION_MODAL);
		listDialog.setVisible(true);
		
		return listDialog.getList().getSelectedValue();
	}
	
    private JList<T> list;
 
    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public ListDialog(Window owner,
                       final JToggleButton button,
                       String title,
                       final JButton[] buttons,
                       ListModel<T> lm) {
        super(owner, title, ModalityType.MODELESS);
        if (button!=null) {
	        this.addWindowListener(new WindowAdapter() {
	        	@Override
	        	public void windowClosing(WindowEvent e) {
	        		button.doClick();
	        	}
	        });
        }
        JPanel buttonPanel = new JPanel();
        for (JButton b:buttons) {
        	buttonPanel.add(b);
        }
        if (buttons.length>0)
        	getRootPane().setDefaultButton(buttons[0]);
 
        //main part of the dialog
        list = new JList<T>(lm);
        
        list.setSelectionBackground(Color.RED);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && buttons.length>0) {
                    buttons[0].doClick(); //emulate button click
                }
            }
        });
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(350, 120));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);
 
        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the
        //scroll pane because its background would be white.
        //Lay out the label and scroll pane from top to bottom.
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
        //JLabel label = new JLabel(labelText);
        //label.setLabelFor(list);
        //listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
 
        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
 
        
        pack();
        if (button!=null) {
        	Point p = button.getLocationOnScreen();
        	p.x += button.getWidth();
            this.setLocation(p);
        } else {
        	this.setLocationRelativeTo(owner);
        }
    }
    
    

	public JList<T> getList() {
		return list;
	}
}