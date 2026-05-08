package library.gui.swing;

import java.awt.Container;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class Drager extends MouseAdapter implements MouseMotionListener {

	Container p;
	Container repainter;
	Running r;

	int x, y;

	public Drager(Container Drager, Container Repainter) {//Dragerは移動させる方
		// TODO 自動生成されたコンストラクター・スタブ
		p = Drager;
		repainter = Repainter;
	}

	public Drager(Container Drager, Running r) {
		// TODO 自動生成されたコンストラクター・スタブ
		p = Drager;
		this.r = r;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		x = e.getXOnScreen() - p.getX();
		y = e.getYOnScreen() - p.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO 自動生成されたメソッド・スタブ

		if (repainter == null) {
			r.actioned();
		} else {
			repainter.repaint();
		}
		Point pointerInfo = MouseInfo.getPointerInfo().getLocation();

		p.setBounds(((int) (pointerInfo.getX() - x)), (int) (pointerInfo
				.getY()) - y, p.getWidth(), p.getHeight());
	}

	public interface Running {
		void actioned();
	}

	public Rectangle getBounds() {
		// TODO 自動生成されたメソッド・スタブ
		return p.getBounds();
	}

}
