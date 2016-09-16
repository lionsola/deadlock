package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * A factory class to provide a unified look-and-feel to the game's GUI elements.
 * 
 * @author Anh Pham
 */
public class GUIFactory {

	public static final Font font_m = new Font(Font.MONOSPACED, Font.PLAIN, 22);
	public static final Font font_s = new Font(Font.MONOSPACED, Font.PLAIN, 16);
	public static final Font font_class = new Font(Font.MONOSPACED, Font.BOLD, 20);

	public static JButton getStyledButton(String text) {
		// TODO add default button image and styling
		JButton button = new JButton();
		button.setText(text);

		button.setSize(106, 29);
		// button.setBorder(null);
		// button.setBackground(Color.BLACK);
		button.setOpaque(false);
		button.setEnabled(true);
		button.setContentAreaFilled(false);
		button.setForeground(Color.WHITE);
		// center-align the text
		button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		button.setFont(font_s);
		button.setIgnoreRepaint(true);
		button.setFocusable(false);
		// button.setContentAreaFilled(false);

		return button;
	}

	/**
	 * Creates and returns a new transparent JPanel
	 * 
	 * @return a transparent JPanel
	 */
	public static JPanel getTransparentPanel() {
		JPanel panel = new JPanel();
		panel.setOpaque(false);
		return panel;
	}

	/**
	 * Creates and returns a new styled JSeparator
	 * 
	 * @return a styled JSeparator
	 */
	public static JSeparator getStyledSeparator() {
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setBackground(Color.BLACK);
		sep.setForeground(Color.GRAY);
		return sep;
	}

	/**
	 * Creatrs and returns a new styled Styled JButton with the specified text
	 * 
	 * @param text
	 *            the text that should be on the button
	 * @return a new JButton with the specified text
	 */
	public static JButton getStyledFunctionButton(String text) {
		JButton button = new JButton();
		button.setText(text);

		button.setSize(196, 53);
		// button.setBorder(null);
		// button.setBackground(Color.BLACK);
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		// button.setBorderPainted(false);

		button.setForeground(Color.WHITE);
		// center-align the text
		button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		button.setFont(font_m);
		button.setIgnoreRepaint(true);
		button.setFocusable(false);
		// button.setContentAreaFilled(false);

		return button;
	}

	/**
	 * Creates and returns a new styled JTextField
	 * 
	 * @return a styled JTextField
	 */
	public static JTextField getStyledTextField() {
		JTextField textField = new JTextField();

		// button.setBorder(null);
		textField.setBackground(Color.BLACK);
		textField.setForeground(Color.WHITE);
		textField.setEditable(true);
		// center-align the text
		textField.setAlignmentX(Component.CENTER_ALIGNMENT);
		textField.setFont(font_s);
		textField.setIgnoreRepaint(true);
		textField.setOpaque(false);

		return textField;
	}

	/**
	 * 
	 * @Title: getStyledLable
	 * @Description: get a style label with iamge background
	 * @param @param text
	 * @param @return
	 * @return JLabel
	 * @throws
	 */
	public static JLabel getStyledLabel(String text) {
		JLabel label = new JLabel();
		label.setText(text);

		label.setSize(216, 29);
		label.setBorder(null);
		label.setBackground(Color.BLACK);
		label.setForeground(Color.WHITE);
		// made the text in the center
		label.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		label.setFont(font_m);
		label.setIgnoreRepaint(true);
		label.setFocusable(false);
		return label;
	}

	/**
	 * Creates and returned a new styled RadioButton with the specified text
	 * 
	 * @param text
	 *            the text that should be by the radio button
	 * @return a new JRadio button
	 */
	public static JRadioButtonMenuItem getStyledRadioButton(String text) {
		JRadioButtonMenuItem button = new JRadioButtonMenuItem();
		button.setText(text);

		button.setSize(136, 29);
		button.setBorder(null);
		button.setBackground(Color.BLACK);
		button.setForeground(Color.WHITE);
		button.setOpaque(false);
		// center-align the text
		button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

		button.setIgnoreRepaint(true);
		button.setFocusable(false);
		button.setContentAreaFilled(false);

		return button;
	}

	/**
	 * Returns a new styled check box with the specified text and whether or not it is ticked
	 * 
	 * @param text
	 *            the text to go by the checkbox
	 * @param isSelected
	 *            whether or not the box is checked to begin with
	 * @return a new styled check box
	 */
	public static JCheckBox getStyledCheckBox(String text, boolean isSelected) {
		JCheckBox button = new JCheckBox();
		button.setText(text);

		button.setSize(142, 36);
		button.setBorder(null);
		button.setBackground(Color.BLACK);
		button.setForeground(Color.WHITE);
		// center-align the text
		button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

		button.setIgnoreRepaint(true);
		button.setFocusable(false);
		button.setContentAreaFilled(false);

		return button;
	}

	/**
	 * Creates and returns a new styled JTextArea.
	 * 
	 * @return a styled JTextArea.
	 */
	public static JTextArea getStyledTextArea() {
		JTextArea textField = new JTextArea();

		// button.setBorder(null);
		textField.setBackground(Color.BLACK);
		textField.setForeground(Color.WHITE);
		textField.setEditable(true);
		// center-align the text
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);
		textField.setFont(font_s);
		textField.setIgnoreRepaint(true);
		textField.setOpaque(false);

		return textField;
	}

}
