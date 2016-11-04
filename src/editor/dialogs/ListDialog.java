package editor.dialogs;
 
import javax.swing.*;

import editor.Editor;

import java.awt.*;
import java.awt.event.*;
 
/*
 * ListDialog is meant to be used to display a list of objects.
 * A cell renderer should be provided along with buttons. The first button given
 * is also triggered with a double click on a list item.
 */

public class ListDialog<T> extends JDialog {

	private static final long serialVersionUID = -2705330003771825132L;
    private JList<T> list;
    private Editor editor;
 
    /**
     * Set up and show the dialog.  The first Component argument
     * determines which frame the dialog depends on; it should be
     * a component in the dialog's controlling frame. The second
     * Component argument should be null if you want the dialog
     * to come up with its left corner in the center of the screen;
     * otherwise, it should be the component on top of which the
     * dialog should appear.
     */
    public ListDialog(Editor editor,
                       final JToggleButton button,
                       String title,
                       final JButton[] buttons,
                       ListModel<T> lm,
                       String initialValue,
                       String longValue) {
        super(editor, title, false);
        this.editor = editor;
        this.addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		button.doClick();
        	}
        });
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
        Point p = button.getLocationOnScreen();
        p.x += button.getWidth();
        this.setLocation(p);
    }
    
    

	public JList<T> getList() {
		return list;
	}
}