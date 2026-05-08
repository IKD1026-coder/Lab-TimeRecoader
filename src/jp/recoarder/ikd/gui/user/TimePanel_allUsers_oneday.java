package jp.recoarder.ikd.gui.user;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.swing.JPanel;

import jp.recoarder.ikd.Usermaster.AttendanceReader;
import jp.recoarder.ikd.Usermaster.UserMaster;

public class TimePanel_allUsers_oneday extends JPanel {

	// ===== Font/ColorをstaticフィールドにしてpaintComponentのたびに生成しない =====
	private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 14);
	private static final Font MSG_FONT = new Font("SansSerif", Font.PLAIN, 18);
	private static final Color COLOR_BG = new Color(0, 0, 30);
	private static final Color COLOR_START = new Color(0, 150, 255);
	private static final Color COLOR_START_A = new Color(0, 150, 255, 200);
	private static final Color COLOR_WORKING = new Color(75, 255, 75);
	private static final Color COLOR_WORKING_A = new Color(75, 255, 75, 200);
	private static final Color COLOR_END = new Color(255, 140, 0);
	private static final Color COLOR_END_A = new Color(255, 140, 0, 200);
	private static final Color COLOR_ERROR = new Color(255, 0, 0);
	private static final Color COLOR_ERROR_A = new Color(255, 0, 0, 180);
	private static final Color COLOR_ERROR_A2 = new Color(255, 0, 0, 200);
	private static final Color COLOR_BAR = new Color(255, 0, 0, 180);

	private LocalDate targetDate;
	private List<String> selected;
	private boolean execlusiveMode;

	public TimePanel_allUsers_oneday() {
		targetDate = LocalDate.now();
	}

	public void setDate(LocalDate date) {
		this.targetDate = date;
		repaint();
	}

	@Override
	public java.awt.Dimension getPreferredSize() {
		List<String> users = execlusiveMode
				? UserMaster.getAllUserID_filtered_exclusive(selected)
				: UserMaster.getAllUserID_filtered(selected);

		int count = 0;
		for (String user : users) {
			if (AttendanceReader.getTime(user, true, targetDate) != null)
				count++;
		}

		int lineHeight = 75;
		int marginTop = 20;
		int height = marginTop + count * lineHeight + 50;
		return new java.awt.Dimension(getWidth() - 20, height);
	}

	private String formatTime(LocalTime time) {
		if (time == null)
			return "--:--";
		return String.format("%02d:%02d", time.getHour(), time.getMinute());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (targetDate == null)
			return;

		Graphics2D g2 = (Graphics2D) g;
		int width = getWidth();
		int height = getHeight();

		g2.setColor(COLOR_BG);
		g2.fillRect(0, 0, width, height);

		g2.setFont(DEFAULT_FONT);

		List<String> users = execlusiveMode
				? UserMaster.getAllUserID_filtered_exclusive(selected)
				: UserMaster.getAllUserID_filtered(selected);

		double startBase = 6 * 60;
		double endBase = 24 * 60;
		double total = endBase - startBase;

		int marginTop = 20;
		int lineHeight = 75;
		int index = 0;
		boolean allskip = true;

		for (String userid : users) {
			LocalTime startTime = AttendanceReader.getTime(userid, true, targetDate);
			LocalTime endTime = AttendanceReader.getTime(userid, false, targetDate);

			String start_str = formatTime(startTime); // 例: "09:05"
			String end_str = formatTime(endTime);

			if (startTime == null)
				continue;

			double startMin = startTime.getHour() * 60 + startTime.getMinute();
			boolean isWorking = (endTime == null);
			double endMin;

			if (isWorking) {
				endMin = LocalDate.now().isEqual(targetDate)
						? LocalTime.now().getHour() * 60 + LocalTime.now().getMinute()
						: startMin;
			} else {
				endMin = endTime.getHour() * 60 + endTime.getMinute();
			}

			int baseY = marginTop + index * lineHeight;
			int lineY = baseY + 50;
			int startX = (int) (50 + (startMin - startBase) / total * (width - 100));
			int endX = (int) (50 + (endMin - startBase) / total * (width - 100));

			// 軸
			g2.setColor(Color.WHITE);
			g2.drawLine(50, lineY, width - 50, lineY);

			// 目盛り
			g2.setColor(Color.GRAY);
			for (int h = 6; h <= 24; h += 3) {
				int x = (int) (50 + (h * 60 - startBase) / total * (width - 100));
				g2.drawLine(x, lineY - 6, x, lineY + 6);
				g2.drawString(h + ":00", x - 20, lineY + 20);
			}

			// 名前
			if (userid.contains("EJ") || userid.contains("NE")) {
				g2.setColor(Color.ORANGE);
			} else {
				g2.setColor(Color.WHITE);
			}
			g2.drawString(UserMaster.getUsername(userid), 20, lineY - 10);

			// 開始マーカー
			g2.setColor(COLOR_START_A);
			g2.fillPolygon(
					new int[] { startX, startX - 12, startX + 12 },
					new int[] { lineY - 10, lineY - 35, lineY - 35 }, 3);
			g2.setColor(COLOR_START);
			g2.drawString("開始" + start_str, startX + 10, lineY - 15);

			// 終了 / 現在 / 打刻漏れ
			if (isWorking) {
				if (!LocalDate.now().isAfter(targetDate)) {
					g2.setColor(COLOR_WORKING);
					g2.drawString("現在", endX + 15, lineY + 35);
					g2.setColor(COLOR_WORKING_A);
				} else {
					g2.setColor(COLOR_ERROR);
					g2.drawString("打刻漏 ", endX + 15, lineY + 35);

					float[] dash = { 20f, 10f };
					g2.setStroke(new java.awt.BasicStroke(
							7, java.awt.BasicStroke.CAP_BUTT,
							java.awt.BasicStroke.JOIN_BEVEL, 0, dash, 0));
					g2.setColor(COLOR_ERROR_A);
					g2.drawLine(startX, lineY,
							(int) (50 + (endBase - startBase) / total * (width - 100)), lineY);
					g2.setStroke(new java.awt.BasicStroke());
					g2.setColor(COLOR_ERROR_A2);
				}
			} else {
				g2.setColor(COLOR_END);
				g2.drawString("終了" + end_str, endX + 15, lineY + 35);
				g2.setColor(COLOR_END_A);
			}

			// 終了マーカー
			g2.fillPolygon(
					new int[] { endX, endX - 12, endX + 12 },
					new int[] { lineY + 10, lineY + 35, lineY + 35 }, 3);

			// 研究時間バー
			g2.setColor(COLOR_BAR);
			g2.fillRect(startX, lineY - 4, endX - startX, 8);

			// バー内時間表記
			if (!isWorking) {
				long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
				g2.setColor(Color.LIGHT_GRAY);
				g2.drawString(String.format("%02d:%02d", minutes / 60, minutes % 60), (startX + endX) / 2 - 20, lineY-3);
			}

			// 右端ステータス
			if (!isWorking) {
				long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
				g2.setColor(Color.WHITE);
				g2.drawString(String.format("%02d:%02d", minutes / 60, minutes % 60),
						width - 65, lineY - 10);
			} else {
				if (!LocalDate.now().isAfter(targetDate)) {
					g2.setColor(COLOR_WORKING);
					g2.drawString("研究中", width - 65, lineY - 10);
				} else {
					g2.setColor(Color.RED);
					g2.drawString("打刻漏", width - 65, lineY - 10);
				}
			}

			index++;
			allskip = false;
		}

		if (allskip) {
			g2.setColor(Color.WHITE);
			g2.setFont(MSG_FONT);
			String msg = "記録はありません";
			int strWidth = g2.getFontMetrics().stringWidth(msg);
			g2.drawString(msg, (width - strWidth) / 2, height / 2);
		}
	}

	public void onlyDisplayType(List<String> selected, boolean execlusiveMode) {
		this.selected = selected;
		this.execlusiveMode = execlusiveMode;
		revalidate();
		repaint();
	}
}
