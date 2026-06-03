package jp.recoarder.ikd.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import jp.recoarder.ikd.LoginListener;
import jp.recoarder.ikd.TimeRecoader;
import jp.recoarder.ikd.assets.Sound;
import jp.recoarder.ikd.gui.user.TimePanel_allStudent;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;
import library.gui.swing.IKDButton;

public class AttendanceMenu {

	private static final int GRID_X = 28;
	private static final int GRID_Y = 24;

	private FullscreenFrame frame;

	// 管理者ボタン
	private JButton btnAdmin;

	// 管理者ボタン
	private JButton btnManual;

	// 管理者ボタン
	private JButton btnAchive1;
	private JButton btnAchive3;

	private JTextField textField;

	public JFrame getFrame() {
		return frame;
	}

	private LocalDate DispMonth;
	private LoginListener EnterAction;

	private TimePanel_allStudent recordPanel, recordPanel2;

	private static int REMAIN_INI = 300;
	private int remaintime = -1;

	public AttendanceMenu() {

		frame = new FullscreenFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("勤怠管理システム");

		GridLayeredPane gridPanel = new GridLayeredPane(GRID_X, GRID_Y);
		gridPanel.setBackground(new Color(30, 40, 50));

		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(gridPanel, java.awt.BorderLayout.CENTER);

		// =========================
		// タイトル + ロゴ（横並び）
		// =========================
		JLabel titleLabel = new JLabel("研究室 勤怠管理システム Ver1.8", SwingConstants.CENTER);
		titleLabel.setFont(new Font("SansSerif", Font.BOLD, 42));
		titleLabel.setForeground(Color.WHITE);
		gridPanel.add(titleLabel, 4, 0, 20, 4);

		JLabel descript = new JLabel("2026/06/03更新", SwingConstants.RIGHT);
		descript.setFont(new Font("SansSerif", Font.BOLD, 18));
		descript.setForeground(Color.WHITE);
		gridPanel.add(descript, 8, 23, 14, 1);

		// IKDロゴ
		JLabel ikdLogo = new JLabel();
		ikdLogo.setHorizontalAlignment(SwingConstants.RIGHT);
		ikdLogo.setIcon(new ImageIcon("./Lab_TimeRecoader/assets/logo-mini.jpg"));
		gridPanel.add(ikdLogo, 25, 0, 1, 4);

		// 技研ロゴ
		JLabel gikenLogo = new JLabel();
		gikenLogo.setHorizontalAlignment(SwingConstants.LEFT);
		gikenLogo.setIcon(new ImageIcon("./Lab_TimeRecoader/assets/giken.png"));
		gridPanel.add(gikenLogo, 24, 0, 1, 4);

		JLabel srcQR = new JLabel();
		srcQR.setHorizontalAlignment(SwingConstants.LEFT);
		srcQR.setIcon(new ImageIcon("./Lab_TimeRecoader/assets_custom/QR_214033.png"));
		gridPanel.add(srcQR, 0, 21, 3, 3);

		ikdLogo.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				EnterAction.actionPerformed("26KMJ02");
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseClicked(MouseEvent e) {
			}
		});

		// =========================
		// タッチ画像（中央メイン）
		// =========================
		JLabel touch = new JLabel();
		touch.setHorizontalAlignment(SwingConstants.CENTER);
		touch.setIcon(new ImageIcon("./Lab_TimeRecoader/assets_custom/touch.png"));
		gridPanel.add(touch, 2, 4, 12, 10);

		//学生証をタッチ
		JLabel touchLabel = new JLabel("学生証をタッチしてください", SwingConstants.CENTER);
		touchLabel.setFont(new Font("SansSerif", Font.BOLD, 34));
		touchLabel.setForeground(Color.WHITE);
		gridPanel.add(touchLabel, 2, 17, 12, 2);

		JLabel subLabel = new JLabel("棒グラフ押下でログインも可能→", SwingConstants.CENTER);
		subLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		subLabel.setForeground(Color.LIGHT_GRAY);
		gridPanel.add(subLabel, 2, 21, 12, 2);

		//読み取れない場合
		btnManual = createButton("➤読み取れない場合はこちらから", new Color(255, 180, 80));
		gridPanel.add(btnManual, 8, 14, 5, 2);

		JLabel dateLabel = new JLabel("----", SwingConstants.CENTER);
		dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
		dateLabel.setForeground(Color.WHITE);
		gridPanel.add(dateLabel, 2, 19, 12, 2);

		// フォーマット定義
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd (E) HH:mm");

		// タイマーで毎秒更新
		Timer timer = new Timer(1000 * 1, e -> {
			LocalDateTime now = LocalDateTime.now();
			dateLabel.setText(now.format(formatter) + (TimeRecoader.isRain_today() ? " 雨" : " 晴れ"));
		});
		dateLabel.setText(LocalDateTime.now().format(formatter));
		timer.start();

		// =========================
		// 右下：研究実績パネル（白）
		// =========================
		recordPanel = new TimePanel_allStudent(false);
		recordPanel.setBackground(Color.gray);
		String[] ar1 = {};
		recordPanel.onlyDisplayType(new ArrayList<>(Arrays.asList(ar1)));
		JScrollPane js1 = new JScrollPane(recordPanel);
		recordPanel.setName_addtion(" - 総合ランキング");

		recordPanel2 = new TimePanel_allStudent(true);
		String[] ar2 = { "EJ", "NE" };
		recordPanel2.onlyDisplayType(new ArrayList<>(Arrays.asList(ar2)));
		recordPanel2.setBackground(Color.gray);
		JScrollPane js2 = new JScrollPane(recordPanel2);
		recordPanel2.setName_addtion(" - 学部生");

		gridPanel.add(js1, 14, 13, 12, 8);
		gridPanel.add(js2, 14, 5, 12, 8);

		DispMonth = LocalDate.now();
		recordPanel.setMonth(DispMonth);
		recordPanel2.setMonth(DispMonth);

		// =========================
		JButton prevBtn = IKDButton.createButton("◀", Color.gray, 32);
		JButton nextBtn = IKDButton.createButton("▶", Color.gray, 32);
		gridPanel.add(prevBtn, 14, 4, 6, 1);
		gridPanel.add(nextBtn, 20, 4, 6, 1);
		prevBtn.addActionListener(e -> {
			remaintime = REMAIN_INI;
			DispMonth = DispMonth.minusMonths(1);
			recordPanel.setMonth(DispMonth);
			recordPanel2.setMonth(DispMonth);
			Sound.touch();
		});
		nextBtn.addActionListener(e -> {
			remaintime = REMAIN_INI;
			DispMonth = DispMonth.plusMonths(1);
			recordPanel.setMonth(DispMonth);
			recordPanel2.setMonth(DispMonth);
			Sound.touch();
		});

		new Timer(1000, e -> {
			if (remaintime == 0) {
				DispMonth = LocalDate.now();
				recordPanel.setMonth(DispMonth);
				recordPanel2.setMonth(DispMonth);
				remaintime = -1;
			} else if (remaintime > 0) {
				remaintime--;
			} else {//初期
			}
		}).start();

		btnAchive1 = createButton("研究時間確認(1日)", new Color(255, 180, 100));
		gridPanel.add(btnAchive1, 14, 21, 6, 1);
		btnAchive3 = createButton("研究時間確認(年間)", new Color(255, 180, 100));
		gridPanel.add(btnAchive3, 20, 21, 6, 1);

		// =========================
		// 管理者ボタン（右下）
		// =========================
		btnAdmin = createButton("管理画面", new Color(200, 200, 200));
		gridPanel.add(btnAdmin, 23, 23, 5, 1);

		// =========================
		// 左上のテキストボックス
		// =========================
		textField = new JTextField();
		textField.setFont(new Font("SansSerif", Font.PLAIN, 16));
		textField.setBackground(Color.WHITE);
		textField.setForeground(Color.BLACK);
		gridPanel.add(textField, 0, 0, 4, 1);

		setupKeyInput();

	}

	public void setVisible(boolean b) {
		frame.setVisible(b);
	}

	// =========================
	// リスナー
	// =========================

	public void addAdminListener(ActionListener e) {
		btnAdmin.addActionListener(e);
	}

	public void btnManualListener(ActionListener e) {
		btnManual.addActionListener(e);
	}

	public void btnAchive1Listener(ActionListener e) {
		btnAchive1.addActionListener(e);
	}

	public void btnAchive3Listener(ActionListener e) {
		btnAchive3.addActionListener(e);
	}

	public void setLoginListener(LoginListener tl) {
		EnterAction = tl;
		recordPanel.setLoginListener(tl);
		recordPanel2.setLoginListener(tl);
	}

	private void setupKeyInput() {

		InputMap inputMap = frame.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = frame.getRootPane().getActionMap();

		String loginActionKey = "loginByEnter";

		// 1. Enterキーの処理: ログインアクションにバインド
		inputMap.put(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), loginActionKey);
		actionMap.put(loginActionKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EnterAction.actionPerformed(textField.getText().toUpperCase());
				textField.setText("");
			}
		});

		frame.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char keyChar = e.getKeyChar();
				// 制御文字やEnterでない場合に処理
				if (!textField.hasFocus() && Character.isDefined(keyChar) && !Character.isISOControl(keyChar)) {
					// 入力をJTextFieldに送り込む
					textField.setText(textField.getText() + keyChar);
					Sound.touch();
					e.consume(); // イベントを消費
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// Backspace処理 (EnterはInputMapで処理済み)
				if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && !textField.hasFocus()) {
					String currentText = textField.getText();
					if (currentText.length() > 0) {
						textField.setText(currentText.substring(0, currentText.length() - 1));
					}
					e.consume();
				}
			}
		});
		frame.setFocusable(true);
		frame.requestFocusInWindow();
		new Timer(1000, e -> {
			frame.requestFocusInWindow();
		}).start();

		// 必要ならサイズ調整
		textField.setColumns(10);
	}

	// =========================
	// ボタン生成
	// =========================

	private JButton createButton(String text, Color color) {
		JButton button = new JButton(text);
		button.setFont(new Font("SansSerif", Font.BOLD, 18));
		button.setBackground(color);
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);

		button.addActionListener(e -> Sound.touch());

		return button;
	}
}