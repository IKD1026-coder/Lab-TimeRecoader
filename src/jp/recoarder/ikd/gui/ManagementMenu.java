package jp.recoarder.ikd.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import jp.recoarder.ikd.assets.Sound;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;

public class ManagementMenu {

	// --- 定数 ---
	private static final int GRID_X = 15;
	private static final int GRID_Y = 12;

	private FullscreenFrame frame;

	private JButton btnUserEdit;
	private JButton btnCsv;
	private JButton btnFuture;
	private JButton btnSemi;

	private static int REMAIN_CLOSE = 60;
	private int remaintime = -1;

	public ManagementMenu() {

		frame = new FullscreenFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("管理画面");

		GridLayeredPane gridPanel = new GridLayeredPane(GRID_X, GRID_Y);
		gridPanel.setBackground(new Color(40, 45, 50));

		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(gridPanel, java.awt.BorderLayout.CENTER);

		// --- タイトル ---
		JLabel titleLabel = new JLabel("管理画面", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
		titleLabel.setForeground(Color.WHITE);
		gridPanel.add(titleLabel, 0, 0, 15, 2);

		// ソースコード
		JLabel srcQR= new JLabel();
		srcQR.setHorizontalAlignment(SwingConstants.RIGHT);
		srcQR.setIcon(new ImageIcon("./Lab_TimeRecoader/assets_custom/QR_214033.png"));
		gridPanel.add(srcQR, 13, 10, 2, 2);

		// --- 説明 ---
		JLabel descLabel = createDescriptionLabel(
				"各種管理機能を選択してください。",
				true);
		descLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		gridPanel.add(descLabel, 0, 2, 15, 1);

		// --- ボタン ---
		JButton btnback = createActionButton("戻る", Color.gray);
		gridPanel.add(btnback, 3, 3, 9, 1);
		btnback.addActionListener(e -> {
			Sound.touch();
			setVisible(false);
		});

		new Timer(1000, e -> {
			if (remaintime == 0) {
				setVisible(false);
				remaintime = -1;
			} else if (remaintime > 0) {
				btnback.setText("◀戻る(" + remaintime + ")");
				remaintime--;
			} else {
				btnback.setText("◀戻る(" + REMAIN_CLOSE + ")");
			}
		}).start();

		// ① ユーザー編集
		btnUserEdit = createActionButton("ユーザー編集", new Color(100, 200, 255));
		gridPanel.add(btnUserEdit, 3, 5, 4, 1);

		// ② CSV出力
		btnCsv = createActionButton("CSV出力", new Color(255, 200, 100));
		gridPanel.add(btnCsv, 3, 7, 9, 1);

		btnSemi = createActionButton("ゼミ打刻", new Color(200, 200, 200));
		gridPanel.add(btnSemi, 8, 5, 4, 1);

		JButton btnShutdown = createActionButton("終了", new Color(200, 100, 100));
		gridPanel.add(btnShutdown, 3, 9, 9, 1);
		btnShutdown.addActionListener(e -> {
			System.exit(1);
		});

		JButton b1 = createActionButton("効果音1", new Color(200, 200, 100));
		gridPanel.add(b1, 13, 0, 1, 1);
		b1.addActionListener(e -> {
			Sound.Chime();
		});
		JButton b2 = createActionButton("効果音2", new Color(200, 200, 100));
		gridPanel.add(b2, 14, 0, 1, 1);
		b2.addActionListener(e -> {
			Sound.Chime2();
		});
		JButton b3 = createActionButton("効果音3", new Color(200, 200, 100));
		gridPanel.add(b3, 13, 1, 1, 1);
		b3.addActionListener(e -> {
			Sound.Error();
		});
		JButton b4 = createActionButton("効果音4", new Color(200, 200, 100));
		gridPanel.add(b4, 14, 1, 1, 1);
		b4.addActionListener(e -> {
			Sound.CritricalError();
		});
		JLabel descLabel2 = createDescriptionLabel(
				"音量調整用",
				true);
		descLabel2.setFont(new Font("SansSerif", Font.PLAIN, 20));
		gridPanel.add(descLabel2, 13, 2, 2, 1);
	}

	public void setVisible(boolean b) {
		if (b)
			remaintime = REMAIN_CLOSE;
		else
			remaintime = -1;

		frame.setVisible(b);
	}

	// --- リスナー ---
	public void btnUserEditAddListener(ActionListener e) {
		btnUserEdit.addActionListener(e);
	}

	public void btnCsvAddListener(ActionListener e) {
		btnCsv.addActionListener(e);
	}

	public void btnFutureAddListener(ActionListener e) {
		btnFuture.addActionListener(e);
	}

	public void btnSemiAddListener(ActionListener e) {
		btnSemi.addActionListener(e);
	}

	// --- 共通ボタン生成 ---
	private JButton createActionButton(String text, Color bgColor) {
		JButton button = new JButton(text);
		button.setFont(new Font("SansSerif", Font.BOLD, 24));
		button.setBackground(bgColor);
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);
		button.addActionListener(e -> Sound.touch());

		return button;
	}

	// --- 説明ラベル ---
	private static JLabel createDescriptionLabel(String text, boolean centered) {
		String align = centered ? "center" : "left";
		JLabel label = new JLabel(
				"<html><div style='text-align: " + align + ";'>" + text + "</div></html>",
				centered ? SwingConstants.CENTER : SwingConstants.LEFT);
		label.setForeground(Color.WHITE);
		return label;
	}

	public int confirmDialog(String string) {
		return JOptionPane.showConfirmDialog(frame, string);
	}
}