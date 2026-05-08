package jp.recoarder.ikd.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import jp.recoarder.ikd.assets.Sound;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;

public class ProcessingMenu {

	public ProcessingMenu() {
	}

	public static void startAnimation(int time_milli, String message) {

		int GRID_X = 28;
		int GRID_Y = 24;

		FullscreenFrame frame;
		JLabel title;
		frame = new FullscreenFrame();
		frame.setTitle("実績確認");

		GridLayeredPane grid = new GridLayeredPane(GRID_X, GRID_Y);
		grid.setBackground(new Color(30, 40, 50));
		frame.getContentPane().add(grid);

		// =========================
		// タイトル
		// =========================
		title = new JLabel("実績確認", SwingConstants.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 40));
		title.setForeground(Color.WHITE);
		grid.add(title, 4, 1, 20, 3);
		// IKDロゴ
		JLabel ikdLogo = new JLabel();
		ikdLogo.setHorizontalAlignment(SwingConstants.RIGHT);
		ikdLogo.setIcon(new ImageIcon("./Lab_TimeRecoader/assets/logo-mini.jpg"));
		grid.add(ikdLogo, 22, 0, 4, 4);

		// 技研ロゴ
		JLabel gikenLogo = new JLabel();
		gikenLogo.setHorizontalAlignment(SwingConstants.LEFT);
		gikenLogo.setIcon(new ImageIcon("./Lab_TimeRecoader/assets/giken.png"));
		grid.add(gikenLogo, 24, 0, 4, 4);

		JLabel[] dots = new JLabel[8];
		JLabel loadingText;

		// =========================
		// ○○○○○○○○（中央）
		// =========================// =========================
		// ○を円形に配置
		// =========================
		JPanel dotPanel = new JPanel();
		dotPanel.setBackground(new Color(30, 40, 50));
		dotPanel.setLayout(null);

		// 円の設定
		int centerX = frame.getWidth() / 2;
		int centerY = frame.getHeight() * 13 / GRID_Y / 2;
		int radius = 120;

		for (int i = 0; i < 8; i++) {

			double angle = 2 * Math.PI * i / 8;

			int x = (int) (centerX + radius * Math.cos(angle));
			int y = (int) (centerY + radius * Math.sin(angle));

			dots[i] = new JLabel("●");
			dots[i].setFont(new Font("SansSerif", Font.BOLD, 30));
			dots[i].setForeground(Color.GRAY);

			// サイズ指定（これ重要）
			dots[i].setSize(40, 40);

			// 中央基準にする
			dots[i].setLocation(x - 20, y - 20);

			dotPanel.add(dots[i]);
		}

		// =========================
		// 処理中テキスト（下）
		// =========================
		loadingText = new JLabel(message, SwingConstants.CENTER);
		loadingText.setFont(new Font("SansSerif", Font.BOLD, 60));
		loadingText.setForeground(Color.WHITE);

		grid.add(dotPanel, 0, 5, 28, 13);
		grid.add(loadingText, 4, 18, 20, 2);

		// ●のアニメーション（ぐるぐる）
		Timer dotTimer = new Timer(100, new ActionListener() {
			int activeIndex = 0;

			@Override
			public void actionPerformed(ActionEvent e) {

				for (int i = 0; i < dots.length; i++) {
					dots[i].setForeground(Color.GRAY);
				}

				dots[activeIndex].setForeground(Color.WHITE);

				activeIndex++;
				if (activeIndex >= dots.length) {
					activeIndex = 0;
				}
			}
		});

		dotTimer.start();

		Timer t = new Timer(time_milli, e -> {
			frame.dispose();
			Sound.Chime();
		});
		t.setRepeats(false);
		t.start();
		frame.setVisible(true);
	}
}