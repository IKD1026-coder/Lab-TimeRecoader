package jp.recoarder.ikd.gui.user;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import jp.recoarder.ikd.Usermaster.AttendanceReader;
import jp.recoarder.ikd.Usermaster.UserMaster;
import jp.recoarder.ikd.assets.Sound;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;
import library.gui.swing.IKDButton;

public class AchievementView_allUsers_oneday {

	private static final int GRID_X = 28;
	private static final int GRID_Y = 24;

	private FullscreenFrame frame;
	private JTable calendarTable;
	private JLabel monthLabel;
	private JLabel title;
	private JLabel descriptTimepanel;
	private JLabel descriptCarendar;
	private JScrollPane scroll;

	private YearMonth currentMonth;

	private JCheckBox cbEJ;
	private JCheckBox cbNE;
	private JCheckBox cbKMJ;
	private JCheckBox cbOther;

	// ★研究した日（仮データ）
	private TimePanel_allUsers_oneday recordPanel;

	private static int REMAIN_CLOSE = 600;
	private int remaintime = -1;

	public AchievementView_allUsers_oneday() {

		frame = new FullscreenFrame();
		frame.setTitle("実績確認");

		GridLayeredPane grid = new GridLayeredPane(GRID_X, GRID_Y);
		grid.setBackground(new Color(30, 40, 50));
		frame.getContentPane().add(grid);

		// =========================
		// タイトル
		// =========================
		title = new JLabel("実績確認", SwingConstants.CENTER);
		title.setText("研究時間確認");
		title.setFont(new Font("SansSerif", Font.BOLD, 40));
		title.setForeground(Color.WHITE);
		grid.add(title, 4, 1, 20, 3);

		JButton btnMenu = createButton("◀戻る", Color.GRAY);
		grid.add(btnMenu, 1, 2, 6, 2);
		btnMenu.addActionListener(e -> {
			setVisible(false);
			ini_checkbox();
		});

		new Timer(1000, e -> {
			if (remaintime == 0) {
				setVisible(false);
				ini_checkbox();
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
		scroll = new JScrollPane(calendarTable);
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
		grid.add(descriptTimepanel, 14, 21, 12, 1);

		recordPanel = new TimePanel_allUsers_oneday();
		recordPanel.setBackground(Color.DARK_GRAY);
		JScrollPane js = new JScrollPane(recordPanel);
		grid.add(js, 14, 5, 12, 16);

		cbEJ = new JCheckBox("EJ");
		cbNE = new JCheckBox("NE");
		cbKMJ = new JCheckBox("KMJ");
		cbOther = new JCheckBox("Other");

		// デフォルト選択（任意）
		ini_checkbox();
		cbEJ.setHorizontalAlignment(JCheckBox.CENTER);
		cbNE.setHorizontalAlignment(JCheckBox.CENTER);
		cbKMJ.setHorizontalAlignment(JCheckBox.CENTER);
		cbOther.setHorizontalAlignment(JCheckBox.CENTER);
		// 共通リスナー
		ActionListener listener = e -> updateFilter();

		// 配置
		grid.add(cbEJ, 14, 4, 3, 1);
		grid.add(cbNE, 17, 4, 3, 1);
		grid.add(cbKMJ, 20, 4, 3, 1);
		grid.add(cbOther, 23, 4, 3, 1);

		// イベント登録
		cbEJ.addActionListener(listener);
		cbNE.addActionListener(listener);
		cbKMJ.addActionListener(listener);
		cbOther.addActionListener(listener);

		// =========================
		// ボタン処理
		// =========================
		prevBtn.addActionListener(e -> {
			currentMonth = currentMonth.minusMonths(1);
			updateCalendar();
			Sound.touch();
		});

		nextBtn.addActionListener(e -> {
			currentMonth = currentMonth.plusMonths(1);
			updateCalendar();
			Sound.touch();
		});
	}

	private void ini_checkbox() {
		cbEJ.setSelected(false);
		cbNE.setSelected(false);
		cbKMJ.setSelected(false);
		cbOther.setSelected(false);
		updateFilter();
	}

	private void updateFilter() {
		updateCalendar();
		Sound.touch();

		List<String> selected = new ArrayList<>();
		List<String> no_selected = new ArrayList<>();

		if (cbEJ.isSelected())
			selected.add("EJ");
		else
			no_selected.add("EJ");

		if (cbNE.isSelected())
			selected.add("NE");
		else
			no_selected.add("NE");

		if (cbKMJ.isSelected())
			selected.add("KMJ");
		else
			no_selected.add("KMJ");

		if (cbOther.isSelected())//その他が選ばれた時は、選ばれていないものを除外
			recordPanel.onlyDisplayType(no_selected, true);
		else//それ以外は、選ばれたものを表示
			recordPanel.onlyDisplayType(selected, false);

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

			// ★研究日なら印追加

			List<String> selected = new ArrayList<>();
			List<String> no_selected = new ArrayList<>();

			if (cbEJ.isSelected())
				selected.add("EJ");
			else
				no_selected.add("EJ");

			if (cbNE.isSelected())
				selected.add("NE");
			else
				no_selected.add("NE");

			if (cbKMJ.isSelected())
				selected.add("KMJ");
			else
				no_selected.add("KMJ");

			List<String> users;
			if (cbOther.isSelected()) {
				users = UserMaster.getAllUserID_filtered_exclusive(no_selected);
			} else {
				users = UserMaster.getAllUserID_filtered(selected);
			}

			boolean studyed = false;
			for (String userId : users) {
				if (AttendanceReader
						.isStarted(userId, LocalDate.of(currentMonth.getYear(), currentMonth.getMonthValue(), day))) {
					studyed = true;
				}
			}
			if (studyed) {
				text += " ●";
			}

			model.setValueAt(text, row, col);

			col++;
			if (col == 7) {
				col = 0;
				row++;
			}
		}
	}

	private void setDate(int year, int month, int day) {
		descriptTimepanel.setText(year + "/" + month + "/" + day + "の研究時間");
		recordPanel.setDate(LocalDate.of(year, month, day));
		updateFilter();

		scroll.getVerticalScrollBar().setValue(0);

		SwingUtilities.invokeLater(() -> {
			scroll.getViewport().setViewPosition(new Point(0, 0));
		});
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
		currentMonth = YearMonth.now();
		updateCalendar();
		setDate(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
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