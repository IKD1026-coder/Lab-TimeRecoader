package library.gui.swing;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class Panel_hausing extends JPanel {

	private Image backgroundImage;

	public Panel_hausing(ImageIcon ii) {
		backgroundImage = ii.getImage();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(backgroundImage, 0, 0, this);
	}
}
