package library.gui.swing;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;

public class FullscreenFrame extends JFrame {

	private int fullWidth;
	private int fullHeight;

	public FullscreenFrame() {
		super();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle screenBounds = env.getMaximumWindowBounds();
		fullWidth = screenBounds.width;
		fullHeight = screenBounds.height;
		setSize(fullWidth, fullHeight);
		setLocation(0, 0);
		setResizable(false);
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			setSize(fullWidth, fullHeight);
			toFront();
		} else {
			setSize(0, 0);
		}
		super.setVisible(b);
	}
}