package jp.recoarder.ikd.gui.user;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDate;
import java.time.YearMonth;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import jp.recoarder.ikd.Usermaster.AttendanceReader;
import jp.recoarder.ikd.Usermaster.UserMaster;
import jp.recoarder.ikd.assets.Sound;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;
import library.gui.swing.IKDButton;

public class AchievementView {

	private static final int GRID_X = 28;
	private static final int GRID_Y = 24;

	private FullscreenFrame frame;
	private JTable calendarTable;
	private JLabel monthLabel;
	private JLabel title;
	private JLabel descriptTimepanel;
	private JLabel descriptCarendar;
	private JLabel LabTimeNow1;
	private JLabel LabTimeNow2;
	private JLabel LabTimeNow3;

	private YearMonth currentMonth;

	// ★研究した日（仮データ）
	private TimePanel recordPanel;

	private static int REMAIN_CLOSE = 300;
	private int remaintime = -1;

	public AchievementView() {

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

		JButton btnMenu = createButton("◀戻る", Color.GRAY);
		grid.add(btnMenu, 1, 2, 6, 2);
		btnMenu.addActionListener(e -> {
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
		// =========================
		// 月切り替え
		// =========================
		JButton prevBtn = IKDButton.createButton("◀", Color.gray, 32);
		JButton nextBtn = IKDButton.createButton("▶", Color.gray, 32);

		monthLabel = new JLabel("", SwingConstants.CENTER);
		monthLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
		monthLabel.setForeground(Color.WHITE);

		grid.add(prevBtn, 3, 5, 2, 2);
		grid.add(monthLabel, 5, 5, 6, 2);
		grid.add(nextBtn, 11, 5, 2, 2);

		// =========================
		// カレンダー
		// =========================
		String[] cols = { "日", "月", "火", "水", "木", "金", "土" };
		DefaultTableModel model = new DefaultTableModel(cols, 6);

		calendarTable = new JTable(model);
		calendarTable.setFont(new Font("SansSerif", Font.BOLD, 20));
		calendarTable.setEnabled(false);
		// ★カスタムレンダラー
		calendarTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int col) {
				JLabel label = (JLabel) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, col);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				// 初期色
				label.setForeground(Color.BLACK);
				if (col == 0) {
					label.setForeground(Color.RED);
				} else if (col == 6) {
					label.setForeground(Color.BLUE);
				}

				return label;
			}
		});

		calendarTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {

				int row = calendarTable.rowAtPoint(e.getPoint());
				int col = calendarTable.columnAtPoint(e.getPoint());

				Object value = calendarTable.getValueAt(row, col);

				if (value != null && !value.toString().isEmpty()) {

					// "10 ●" → "10"だけ取り出す
					String text = value.toString().split(" ")[0];

					int day = Integer.parseInt(text);
					setDate(currentMonth.getYear(), currentMonth.getMonthValue(), day);
				}
				Sound.touch();
			}
		});
		int tableWidth = frame.getWidth() * 10 / GRID_X;
		int tableHeight = frame.getHeight() * 12 / GRID_Y - 30;
		calendarTable.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));
		calendarTable.setFillsViewportHeight(true);
		for (int i = 0; i < 7; i++) {
			calendarTable.getColumnModel().getColumn(i).setPreferredWidth(tableWidth / 7);
		}
		calendarTable.setRowHeight(tableHeight / 6);
		JScrollPane scroll = new JScrollPane(calendarTable);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		grid.add(scroll, 3, 8, 10, 12);

		descriptCarendar = new JLabel("カレンダーの日付を押下すると研究時間が確認できます", SwingConstants.CENTER);
		descriptCarendar.setFont(new Font("SansSerif", Font.BOLD, 23));
		descriptCarendar.setForeground(Color.WHITE);
		grid.add(descriptCarendar, 1, 20, 14, 1);

		// =========================
		// 初期化
		// =========================
		currentMonth = YearMonth.now();

		descriptTimepanel = new JLabel("1/1の研究時間", SwingConstants.CENTER);
		descriptTimepanel.setFont(new Font("SansSerif", Font.BOLD, 28));
		descriptTimepanel.setForeground(Color.WHITE);
		grid.add(descriptTimepanel, 14, 15, 12, 1);

		recordPanel = new TimePanel();
		recordPanel.setBackground(Color.DARK_GRAY);

		grid.add(recordPanel, 14, 6, 12, 9);

		// =========================
		// ボタン処理
		// =========================
		prevBtn.addActionListener(e -> {
			currentMonth = currentMonth.minusMonths(1);
			Sound.touch();
			updateCalendar();
		});

		nextBtn.addActionListener(e -> {
			currentMonth = currentMonth.plusMonths(1);
			Sound.touch();
			updateCalendar();
		});

		LabTimeNow1 = new JLabel("", SwingConstants.CENTER);
		LabTimeNow1.setFont(new Font("SansSerif", Font.BOLD, 28));
		LabTimeNow1.setForeground(Color.WHITE);
		grid.add(LabTimeNow1, 14, 17, 12, 1);

		LabTimeNow2 = new JLabel("", SwingConstants.CENTER);
		LabTimeNow2.setFont(new Font("SansSerif", Font.BOLD, 28));
		LabTimeNow2.setForeground(Color.WHITE);
		grid.add(LabTimeNow2, 14, 18, 12, 1);

		LabTimeNow3 = new JLabel("", SwingConstants.CENTER);
		LabTimeNow3.setFont(new Font("SansSerif", Font.BOLD, 32));
		LabTimeNow3.setForeground(Color.WHITE);
		grid.add(LabTimeNow3, 14, 20, 12, 1);
	}

	// =========================
	// カレンダー更新
	// =========================
	private void updateCalendar() {

		monthLabel.setText(currentMonth.getYear() + "年 " + currentMonth.getMonthValue() + "月");

		DefaultTableModel model = (DefaultTableModel) calendarTable.getModel();

		// 初期化
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 7; c++) {
				model.setValueAt("", r, c);
			}
		}

		LocalDate firstDay = currentMonth.atDay(1);
		int startDay = firstDay.getDayOfWeek().getValue() % 7; // 日曜開始

		int daysInMonth = currentMonth.lengthOfMonth();

		int row = 0;
		int col = startDay;

		for (int day = 1; day <= daysInMonth; day++) {

			String text = String.valueOf(day);

			LocalDate tod = LocalDate.of(currentMonth.getYear(), currentMonth.getMonthValue(), day);
			// ★研究日なら印追加

			if (AttendanceReader.isMissed(UserMaster.getActiveUserId(), tod) && LocalDate.now().isAfter(tod)) {
				text += " ×";
			} else if (AttendanceReader.isStarted(UserMaster.getActiveUserId(), tod)) {
				text += " ●";
			} else {

			}

			model.setValueAt(text, row, col);

			col++;
			if (col == 7) {
				col = 0;
				row++;
			}
		}
		int year = currentMonth.getYear();
		int month = currentMonth.getMonthValue();
		int year_prev = currentMonth.minusMonths(1).getYear();
		int month_prev = currentMonth.minusMonths(1).getMonthValue();
		long min_now3 = AttendanceReader.getTotalStudyMinutes_nendo(UserMaster.getActiveUserId(), year, month);
		long min_now2 = AttendanceReader.getTotalStudyMinutes(UserMaster.getActiveUserId(), year_prev, month_prev);
		long min_now1 = AttendanceReader.getTotalStudyMinutes(UserMaster.getActiveUserId(), year, month);

		LabTimeNow3.setText(
				AttendanceReader.getnendo(year, month) + "年度の研究時間：" + min_now3 / 60 + "時間" + min_now3 % 60 + "分");
		LabTimeNow2.setText(year_prev + "/" + month_prev + "の研究時間：" + min_now2 / 60 + "時間" + min_now2 % 60 + "分");
		LabTimeNow1.setText(year + "/" + month + "の研究時間：" + min_now1 / 60 + "時間" + min_now1 % 60 + "分");
	}

	private void setDate(int year, int month, int day) {
		descriptTimepanel.setText(year + "/" + month + "/" + day + "の研究時間");
		LocalDate date = LocalDate.of(year, month, day);
		recordPanel.setTime(date, AttendanceReader.getTime(UserMaster.getActiveUserId(), true, date),
				AttendanceReader.getTime(UserMaster.getActiveUserId(), false, date));
	}
	public void setVisible(boolean b) {
		if (b)
			remaintime = REMAIN_CLOSE;
		else
			remaintime = -1;

		currentMonth = YearMonth.now();
		title.setText(UserMaster.getUsername() + "さんの研究時間確認");
		setDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
		updateCalendar();
		frame.setVisible(b);
	}

	private JButton createButton(String text, Color color) {
		JButton button = new JButton(text);
		button.setFont(new Font("SansSerif", Font.BOLD, 32));
		button.setBackground(color);
		button.setForeground(Color.BLACK);
		button.setFocusPainted(false);

		button.addActionListener(e -> Sound.touch());

		return button;
	}

}