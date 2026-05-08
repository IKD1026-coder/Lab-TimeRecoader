package jp.recoarder.ikd.gui.manage;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;

public class ErrorDisplay {

	// --- 定数 ---
	private static final int GRID_X = 15;
	private static final int GRID_Y = 12;

	private FullscreenFrame frame;

	public JFrame getframe() {
		return frame;
	}

	public ErrorDisplay() {

		frame = new FullscreenFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("エラー確認画面");

		GridLayeredPane gridPanel = new GridLayeredPane(GRID_X, GRID_Y);
		gridPanel.setBackground(new Color(40, 45, 50));

		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(gridPanel, java.awt.BorderLayout.CENTER);

		// --- タイトル ---
		JLabel titleLabel = new JLabel("エラー確認画面", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
		titleLabel.setForeground(Color.WHITE);
		gridPanel.add(titleLabel, 0, 0, 14, 2);
	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

}