package jp.recoarder.ikd.gui.input;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import jp.recoarder.ikd.Usermaster.AttendanceReader;
import jp.recoarder.ikd.Usermaster.UserMaster;
import jp.recoarder.ikd.assets.Sound;
import library.Greeting;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;
import library.gui.swing.IKDButton;

public class AttendanceInputGUI {

	private static final int GRID_X = 28;
	private static final int GRID_Y = 24;

	private FullscreenFrame frame;

	private JComboBox<String> startHourBox;
	private JComboBox<String> startMinBox;

	private JComboBox<String> endHourBox;
	private JComboBox<String> endMinBox;

	private JLabel messageLabel;
	private JLabel messageLabel2;
	private JButton okBtn;

	private GridLayeredPane grid;

	private static int REMAIN_CLOSE = 60;
	private int remaintime = -1;

	public AttendanceInputGUI() {

		frame = new FullscreenFrame();
		frame.setTitle("時間入力");
		LocalTime.of(16, 0);
		grid = new GridLayeredPane(GRID_X, GRID_Y);
		grid.setBackground(new Color(30, 40, 50));
		frame.getContentPane().add(grid);

		// =========================
		// タイトル
		// =========================
		JLabel title = new JLabel("開始・終了時間を入力してください", SwingConstants.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 42));
		title.setForeground(Color.WHITE);
		grid.add(title, 4, 2, 20, 1);

		JLabel descript = new JLabel("※研究時間の平均を超える終了時間は入力できません。", SwingConstants.CENTER);
		descript.setFont(new Font("SansSerif", Font.BOLD, 22));
		descript.setForeground(Color.WHITE);
		grid.add(descript, 4, 14, 20, 1);

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

		JButton btnMenu = IKDButton.createButton("◀戻る(" + REMAIN_CLOSE + ")", Color.GRAY, 32);
		grid.add(btnMenu, 1, 2, 6, 2);
		btnMenu.addActionListener(e -> {
			Sound.touch();
			setVisible(false);
		});

		new Timer(1000, e -> {
			if (remaintime == 0) {
				setVisible(false);
				remaintime = -1;
			} else if (remaintime > 0) {
				btnMenu.setText("◀戻る(" + remaintime + ")");
				remaintime--;
			} else {
				btnMenu.setText("◀戻る(" + REMAIN_CLOSE + ")");
			}
		}).start();

		// =========================
		// メッセージ
		// =========================
		messageLabel2 = new JLabel("--さん、こんにちは。", SwingConstants.CENTER);
		messageLabel2.setForeground(Color.WHITE);
		messageLabel2.setFont(new Font("SansSerif", Font.PLAIN, 45));
		grid.add(messageLabel2, 0, 4, 28, 2);

		messageLabel = new JLabel("", SwingConstants.CENTER);
		messageLabel.setForeground(Color.YELLOW);
		messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 45));
		grid.add(messageLabel, 0, 7, 28, 2);

		// =========================
		// START
		// =========================
		JLabel startLabel = new JLabel("開始時間(編集不可)", SwingConstants.CENTER);
		startLabel.setForeground(Color.WHITE);
		startLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));
		grid.add(startLabel, 6, 9, 8, 2);

		startHourBox = createHourBox();
		startMinBox = createMinuteBox();
		startHourBox.setFont(new Font("SansSerif", Font.PLAIN, 32));
		startMinBox.setFont(new Font("SansSerif", Font.PLAIN, 32));
		startHourBox.setEnabled(false);
		startMinBox.setEnabled(false);

		grid.add(startHourBox, 6, 11, 4, 2);
		grid.add(startMinBox, 10, 11, 4, 2);

		// =========================
		// END
		// =========================
		JLabel endLabel = new JLabel("終了時間", SwingConstants.CENTER);
		endLabel.setForeground(Color.WHITE);
		endLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));
		grid.add(endLabel, 16, 9, 8, 2);

		endHourBox = createHourBox();
		endMinBox = createMinuteBox_10min();
		endHourBox.setFont(new Font("SansSerif", Font.PLAIN, 32));
		endMinBox.setFont(new Font("SansSerif", Font.PLAIN, 32));

		grid.add(endHourBox, 16, 11, 4, 2);
		grid.add(endMinBox, 20, 11, 4, 2);

		// =========================
		// 決定ボタン
		// =========================
		okBtn = new JButton("決定");
		okBtn.setFont(new Font("SansSerif", Font.BOLD, 32));
		okBtn.setBackground(new Color(100, 200, 255));
		grid.add(okBtn, 10, 17, 8, 3);
	}

	private void rebuildEndTimeCombo(LocalTime start, LocalTime maxEnd) {
		endHourBox.removeAllItems();
		int startHour = start.getHour();
		int endHour = maxEnd.plusMinutes(60).getHour();

		if (maxEnd.plusMinutes(60).isBefore(maxEnd))
			endHour = 23;

		for (int h = startHour; h <= endHour; h++) {
			endHourBox.addItem(String.format("%02d", h));
		}

		// 既存リスナー削除（重要）
		for (ActionListener al : endHourBox.getActionListeners()) {
			endHourBox.removeActionListener(al);
		}

		endHourBox.setSelectedIndex(endHourBox.getItemCount() - 1);
	}

	public void selectCombo(LocalTime lt) {
		startHourBox.setSelectedIndex(lt.getHour());
		startMinBox.setSelectedIndex(lt.getMinute());
		endHourBox.setSelectedIndex(0);
		endMinBox.setSelectedIndex(5);

		int plusminute = (int) (AttendanceReader.getAverageStudyMinutes_nendo(
				UserMaster.getActiveUserId(),
				LocalDate.now().getYear(), 4));

		LocalTime maxEnd = lt.plusMinutes(plusminute);

		if (plusminute == 0)
			maxEnd = LocalTime.of(23, 59);

		if (maxEnd.isBefore(lt)) {
			maxEnd = LocalTime.of(23, 59);
		}

		rebuildEndTimeCombo(lt, maxEnd);
	}

	// =========================
	// 共通コンポーネント
	// =========================
	private JComboBox<String> createHourBox() {
		String[] hours = new String[24];
		for (int i = 0; i < 24; i++) {
			hours[i] = String.format("%02d", i);
		}
		return new JComboBox<>(hours);
	}

	private JComboBox<String> createMinuteBox() {
		String[] mins = new String[60];
		for (int i = 0; i < 60; i++) {
			mins[i] = String.format("%02d", i);
		}
		return new JComboBox<>(mins);
	}

	private JComboBox<String> createMinuteBox_10min() {
		String[] mins = new String[6];
		for (int i = 0; i < 6; i++) {
			mins[i] = String.format("%02d", i * 10);
		}
		return new JComboBox<>(mins);
	}

	// =========================
	// 値取得
	// =========================
	public LocalTime getStartTime() {
		int hour = Integer.parseInt((String) startHourBox.getSelectedItem());
		int minute = Integer.parseInt((String) startMinBox.getSelectedItem());
		return LocalTime.of(hour, minute, 0);
	}

	public LocalTime getEndTime() {
		int hour = Integer.parseInt((String) endHourBox.getSelectedItem());
		int minute = Integer.parseInt((String) endMinBox.getSelectedItem());
		return LocalTime.of(hour, minute, 0);
	}

	public void showDialog(String message, String title, int type) {
		JOptionPane.showMessageDialog(frame, message, title, type);
	}

	// =========================
	// メッセージ設定（関数）
	// =========================
	public void setMessage(String message) {
		messageLabel.setText(message);
	}

	public void setBackgroundColor(Color c) {
		grid.setBackground(c);
	}

	// =========================
	// イベント登録
	// =========================
	public void addOKListener(ActionListener e) {
		okBtn.addActionListener(e);
	}

	// =========================
	// 表示制御
	// =========================
	public void setVisible(boolean b) {
		if (b)
			remaintime = REMAIN_CLOSE;
		else
			remaintime = -1;
		messageLabel2.setText(UserMaster.getUsername(UserMaster.getActiveUserId()) + "さん、" + Greeting.getGreeting());
		frame.setVisible(b);
	}

	public JFrame getFrame() {
		return frame;
	}

	public void popup(String message, String title, int type) {
		JOptionPane.showMessageDialog(frame, message, title, type);
	}

	public void dispose() {
		frame.dispose();
	}
}