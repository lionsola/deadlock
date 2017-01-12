package client.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.DefaultCaret;

/**
 * Chat panel to be used in lobby and game screen, showing chats from players.
 */
public class ChatPanel extends JPanel {

	private static final long serialVersionUID = -525120464560666172L;
	private JTextField text;
	private JTextArea textarea;
	private JScrollPane scroller;
	private JLabel label;
	private JSeparator separator;
	private final int MAX_ROWS;

	public ChatPanel(int maxRows) {
		this.setOpaque(false);
		MAX_ROWS = maxRows;
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1;
		c.weightx = 1;
		c.gridx = 0;
		c.gridy = 0;
		//add(Box.createGlue());
		//c.weighty = 0;
		//c.gridy += 1;
		textarea = new JTextArea();
		//textarea.setBackground(new Color(0,0,0,0));
		textarea.setOpaque(false);
		textarea.setEditable(false);
		textarea.setLineWrap(true);
		textarea.setWrapStyleWord(true);
		textarea.setAlignmentY(BOTTOM_ALIGNMENT);
		((DefaultCaret) textarea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		// JScrollPane scroll = new JScrollPane(textarea);
		// add(scroll);
		scroller = new JScrollPane(textarea);
		scroller.setOpaque(false);
		scroller.setBorder(null);
		// scroller.setWheelScrollingEnabled(true);
		scroller.setWheelScrollingEnabled(false);
		scroller.getViewport().setOpaque(false);
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroller, c);

		text = new JTextField();
		text.setEditable(true);
		text.setOpaque(false);
		text.setBorder(null);
		label = GUIFactory.getStyledLabel("");
		label.setOpaque(false);
		label.setBorder(null);
		JPanel inputPanel = GUIFactory.getTransparentPanel();
		inputPanel.setLayout(new BorderLayout());
		inputPanel.add(label, BorderLayout.WEST);
		inputPanel.add(text, BorderLayout.CENTER);
		c.weighty = 0;
		c.gridy++;
		separator = GUIFactory.getStyledSeparator(); 
		add(separator, c);
		c.gridy++;
		add(inputPanel, c);
	}

	public void addLine(String line) {
		textarea.append(line + "\n");
		textarea.setRows(Math.min(MAX_ROWS,textarea.getRows()+1));
		invalidate();
	}

	public String getInput() {
		return text.getText();
	}

	public void resetInput() {
		text.setText("");
	}

	public boolean isTyping() {
		return text.isFocusOwner();
	}

	public JTextField getInputField() {
		return text;
	}

	public JTextArea getTextArea() {
		return textarea;
	}

	public JScrollPane getScroller() {
		return scroller;
	}

	public JLabel getInputLabel() {
		return label;
	}

	public JSeparator getSeparator() {
		return separator;
	}

	public void startTyping() {
		text.requestFocusInWindow();
	}

	public static void main(String args[]) {
		JFrame frame = new JFrame();
		ChatPanel chat = new ChatPanel(10);
		frame.setContentPane(chat);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(400, 400);
		frame.setVisible(true);
		chat.addLine("xxx");
		chat.addLine("yyy");
	}

}
