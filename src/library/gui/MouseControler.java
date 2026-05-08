package library.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;

public class MouseControler {
	private Robot r;

	public MouseControler() {
		try {
			r = new Robot();
		} catch (AWTException e) {
			// TODO ©“®¶¬‚³‚ê‚½ catch ƒuƒƒbƒN
			e.printStackTrace();
		}
	}

	public void move_cursor(Point p) {
		r.mouseMove((int) p.getX(), (int) p.getY());
	}

	public void click(Point p) {
		move_cursor(p);
		click();
	}

	@SuppressWarnings("deprecation")
	public void click() {
		r.mousePress(InputEvent.BUTTON1_MASK);
		wait_milli(10);
		r.mouseRelease(InputEvent.BUTTON1_MASK);
		wait_milli(10);
	}

	public Point getPoint() {
		PointerInfo pi = MouseInfo.getPointerInfo();
		return pi.getLocation();
	}

	public Color getColorOnpoint(Point p) {
		BufferedImage image = r.createScreenCapture(
				new Rectangle((int) p.getX(), (int) p.getY(), (int) p.getX(), (int) p.getY()));
		return new Color(image.getRGB(0, 0));
	}

	public void wait_milli(int millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void wait_sec(int sec) {
		wait_milli(sec * 1000);
	}

	public void wait_minu(int minu) {
		wait_sec(minu * 60);
	}

	public boolean isnearColorOnScreen(Color a, Point p) {
		return isnearColor(a, getColorOnpoint(p));
	}

	private static boolean isnearColor(Color a, Color b) {
		return isnearColor(a, b, 30);
	}

	private static boolean isnearColor(Color a, Color b, int error) {
		if (Math.abs(a.getRed() - b.getRed()) > error)
			return false;
		if (Math.abs(a.getGreen() - b.getGreen()) > error)
			return false;
		if (Math.abs(a.getBlue() - b.getBlue()) > error)
			return false;
		return true;
	}
}
