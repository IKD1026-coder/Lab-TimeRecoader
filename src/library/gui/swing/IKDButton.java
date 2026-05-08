package library.gui.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;

public class IKDButton {

	public static JButton createButton(String text, Color color,int size) {
		JButton button = new JButton(text);
		button.setFont(new Font("SansSerif", Font.BOLD, size));
		button.setBackground(color);
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);
		return button;
	}

}
