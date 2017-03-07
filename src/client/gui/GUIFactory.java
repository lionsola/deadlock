package client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
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

	public static final Font font_m = new Font(Font.MONOSPACED, Font.BOLD, 22);
	public static final Font font_s = new Font(Font.MONOSPACED, Font.PLAIN, 16);
	public static final Font font_s_bold = new Font(Font.MONOSPACED, Font.BOLD, 16);
	public static final Font font_class = new Font(Font.MONOSPACED, Font.BOLD, 20);
	
	//public static final Color UICOLOR = new Color(50,100,255);
	//public static final Color UICOLOR_BG = new Color(10,20,50);
	
	public static final Color UICOLOR = new Color(150,150,150);
	public static final Color UICOLOR_BG = new Color(75,75,75);
	
	public static final Color TRANSBLACK = new Color(0,0,0,127);

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
		sep.setBackground(new Color(0,0,0,0));
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
		stylizeMenuComponent(button);
		return button;
	}

	/**
	 * Creates and returns a new styled JTextField
	 * 
	 * @return a styled JTextField
	 */
	public static JTextField getStyledTextField() {
		JTextField textField = new JTextField();
		stylizeMenuComponent(textField);
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
		JLabel label = new JLabel(text);
		stylizeMenuComponent(label);
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

		button.setFocusable(false);
		button.setContentAreaFilled(false);

		return button;
	}

	public static void stylizeMenuComponent(JComponent component) {
		component.setBackground(Color.DARK_GRAY);
		component.setForeground(Color.WHITE);
		component.setOpaque(true);
		
		component.setFont(font_m);
		int margin = 5;
		// center-align the text
		if (component instanceof ChatPanel) {
			ChatPanel chatpanel = (ChatPanel) component;
			chatpanel.getTextArea().setOpaque(false);
			chatpanel.getInputField().setOpaque(false);
			chatpanel.getInputLabel().setOpaque(false);
		} else if (component instanceof JLabel) {
			JLabel label = (JLabel) component;
			label.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setVerticalAlignment(SwingConstants.CENTER);
			component.setFocusable(false);
		} else if (component instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) component;
			button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			button.setHorizontalAlignment(SwingConstants.CENTER);
			button.setVerticalAlignment(SwingConstants.CENTER);
			component.setFocusable(false);
		} else if (component instanceof JTextField) {
			JTextField text = (JTextField) component;
			//text.setHorizontalAlignment(SwingConstants.CENTER);
			text.setEditable(true);
			margin = 2;
		}
		component.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true),
				BorderFactory.createEmptyBorder(margin,margin,margin,margin)));
		//button.setIgnoreRepaint(true);
		
	}
	
	public static void stylizeHUDComponent(JComponent component) {
		component.setOpaque(true);
		component.setForeground(UICOLOR);
		component.setBackground(TRANSBLACK);
		component.setAlignmentX(Component.CENTER_ALIGNMENT);
		component.setAlignmentY(Component.CENTER_ALIGNMENT);
		component.setFont(font_s_bold);
		int margin = 10;
		// center-align the text
		if (component instanceof ChatPanel) {
			ChatPanel chatpanel = (ChatPanel) component;
			stylizeHUDComponent(chatpanel.getTextArea());
			stylizeHUDComponent(chatpanel.getInputField());
			stylizeHUDComponent(chatpanel.getInputLabel());
			stylizeHUDComponent(chatpanel.getSeparator());
			chatpanel.getTextArea().setOpaque(false);
			chatpanel.getInputField().setOpaque(false);
			chatpanel.getInputLabel().setOpaque(false);
		} else if (component instanceof JLabel) {
			JLabel label = (JLabel) component;
			label.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setVerticalAlignment(SwingConstants.CENTER);
		} else if (component instanceof AbstractButton) {
			AbstractButton button = (AbstractButton) component;
			button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			button.setHorizontalAlignment(SwingConstants.CENTER);
			button.setVerticalAlignment(SwingConstants.CENTER);
		} else if (component instanceof JTextField) {
			JTextField text = (JTextField) component;
			text.setHorizontalAlignment(SwingConstants.CENTER);
			margin = 2;
		}
		//component.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(UICOLOR, 2, true),
		//		BorderFactory.createEmptyBorder(margin,margin,margin,margin)));
	}
	
	/**
	 * Creates and returns a new styled JTextArea.
	 * 
	 * @return a styled JTextArea.
	 */
	public static JTextArea getStyledTextArea() {
		JTextArea textArea = new JTextArea();
		stylizeMenuComponent(textArea);
		textArea.setFont(font_s_bold);
		return textArea;
	}

}
