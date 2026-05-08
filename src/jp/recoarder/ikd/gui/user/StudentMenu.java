package jp.recoarder.ikd.gui.user;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import jp.recoarder.ikd.Usermaster.AttendanceReader;
import jp.recoarder.ikd.Usermaster.UserMaster;
import jp.recoarder.ikd.assets.Sound;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;

public class StudentMenu {

	private static final int GRID_X = 28;
	private static final int GRID_Y = 24;

	// フォントをstaticフィールド化
	private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 42);
	private static final Font USER_FONT = new Font("SansSerif", Font.BOLD, 36);
	private static final Font GUIDE_FONT = new Font("SansSerif", Font.PLAIN, 24);
	private static final Font BTN_FONT = new Font("SansSerif", Font.BOLD, 32);

	private FullscreenFrame frame;
	private JLabel userLabel;
	private JLabel guideTime;
	private JButton btnStart;
	private JButton btnEnd;
	private JButton btnConfirm;
	private JButton btnMenu;

	private static int REMAIN_CLOSE = 60;
	private int remaintime = -1;
	private TimePanel recordPanel;

	// recordPanel更新Timer
	private Timer repaintTimer;

	public JFrame getFrame() {
		return frame;
	}

	public StudentMenu() {

		frame = new FullscreenFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("勤怠管理システム");

		GridLayeredPane gridPanel = new GridLayeredPane(GRID_X, GRID_Y);
		gridPanel.setBackground(new Color(30, 40, 50));

		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(gridPanel, java.awt.BorderLayout.CENTER);

		// タイトル
		JLabel titleLabel = new JLabel("研究室 勤怠管理システム", SwingConstants.CENTER);
		titleLabel.setFont(TITLE_FONT);
		titleLabel.setForeground(Color.WHITE);
		gridPanel.add(titleLabel, 4, 0, 20, 4);

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

		ikdLogo.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {
				if (btnStart.isEnabled())
					for (ActionListener e2 : btnStart.getActionListeners())
						e2.actionPerformed(null);
				else
					for (ActionListener e2 : btnEnd.getActionListeners())
						e2.actionPerformed(null);
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
		// ユーザー表示
		userLabel = new JLabel("-----さん、こんばんは。", SwingConstants.CENTER);
		userLabel.setFont(USER_FONT);
		userLabel.setForeground(Color.WHITE);
		gridPanel.add(userLabel, 4, 3, 20, 3);

		// 説明ラベル（時計兼用）
		JLabel guideLabel = new JLabel("各種操作を選択してください。", SwingConstants.CENTER);
		guideLabel.setFont(GUIDE_FONT);
		guideLabel.setForeground(Color.LIGHT_GRAY);
		gridPanel.add(guideLabel, 4, 5, 20, 2);

		// メインメニューボタン
		btnMenu = createButton("◀メインメニュー", Color.GRAY);
		gridPanel.add(btnMenu, 1, 3, 6, 3);

		btnMenu.addActionListener(e -> {
			setVisible(false);
		});

		new Timer(1000, e -> {
			if (remaintime == 0) {
				setVisible(false);
				remaintime = -1;
			} else if (remaintime > 0) {
				btnMenu.setText("◀メインメニュー(" + remaintime + ")");
				remaintime--;
			} else {
				btnMenu.setText("◀メインメニュー(" + REMAIN_CLOSE + ")");
			}
		}).start();

		btnStart = createButton("<html>研究開始<br>➤ホーム画面に戻ります</html>", new Color(100, 200, 255));
		gridPanel.add(btnStart, 1, 7, 12, 7);

		btnEnd = createButton("<html>研究終了<br>➤ホーム画面に戻ります</html>", new Color(255, 150, 150));
		gridPanel.add(btnEnd, 1, 15, 12, 7);

		btnConfirm = createButton("実績確認", new Color(255, 240, 120));
		gridPanel.add(btnConfirm, 14, 18, 12, 4);

		// 研究実績パネル
		recordPanel = new TimePanel();
		recordPanel.setBackground(Color.DARK_GRAY);
		gridPanel.add(recordPanel, 14, 7, 12, 9);

		// 研究中の場合のみ現在時刻を30秒ごとに更新
		repaintTimer = new Timer(1000 * 30, e -> {
			recordPanel.updateCurrentTime();
		});
		repaintTimer.start();

		// 本日の研究時間ラベル
		guideTime = new JLabel("本日の研究時間", SwingConstants.CENTER);
		guideTime.setFont(GUIDE_FONT);
		guideTime.setForeground(Color.LIGHT_GRAY);
		gridPanel.add(guideTime, 14, 16, 12, 1);
	}

	public static String getGreeting() {
		int hour = LocalTime.now().getHour();
		if (hour >= 5 && hour < 12)
			return "おはようございます";
		if (hour >= 12 && hour < 18)
			return "こんにちは";
		if (hour >= 18 && hour < 21)
			return "こんばんは";
		return "遅くまでお疲れ様です";
	}

	public void setName(String id) {
		userLabel.setText(UserMaster.getUsername(id) + "さん、" + getGreeting());

		// AttendanceReaderを最小限の呼び出しにまとめる
		String activeId = UserMaster.getActiveUserId();
		LocalTime start = AttendanceReader.getLastStartTime(activeId);
		LocalTime end = AttendanceReader.getLastEndTime(activeId);
		boolean working = AttendanceReader.isWorking(activeId);

		if (working) {
			btnEnd.setText("<html>研究終了<br>➤ホーム画面に戻ります</html>");
			btnStart.setEnabled(false);
			btnEnd.setEnabled(true);
		} else if (end == null) {
			btnEnd.setText("<html>研究終了<br>➤ホーム画面に戻ります</html>");
			btnStart.setEnabled(true);
			btnEnd.setEnabled(false);
		} else {
			btnEnd.setText("<html>研究終了(修正打刻)<br>➤ホーム画面に戻ります</html>");
			btnStart.setEnabled(false);
			btnEnd.setEnabled(true);
		}

		recordPanel.setTime(LocalDate.now(), start, end);
		// setTime()内でrepaint()が呼ばれているので追加のrepaintは不要
	}

	private JButton createButton(String text, Color color) {
		JButton button = new JButton(text);
		button.setFont(BTN_FONT);
		button.setBackground(color);
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);
		button.addActionListener(e -> Sound.touch());
		return button;
	}

	private boolean isShowing = false;

	public void setVisible(boolean b) {
		if (b)
			remaintime = REMAIN_CLOSE;
		else
			remaintime = -1;

		if (b && isShowing)
			return; // ← 既に表示中なら何もしない
		isShowing = b;
		frame.setVisible(b);
	}

	public void addBtnStartListerner(ActionListener e) {
		btnStart.addActionListener(e);
	}

	public void addBtnEndListerner(ActionListener e) {
		btnEnd.addActionListener(e);
	}

	public void addAchivementViewListener(ActionListener e) {
		btnConfirm.addActionListener(e);
	}
}
