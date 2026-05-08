package jp.recoarder.ikd.gui.user;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JPanel;

public class TimePanel extends JPanel {

	private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

	private static final Font LABEL_FONT  = new Font("SansSerif", Font.BOLD, 22);
	private static final Font SCALE_FONT  = new Font("SansSerif", Font.PLAIN, 18);
	private static final Font CENTER_FONT = new Font("SansSerif", Font.BOLD, 28);
	private static final Font BOTTOM_FONT = new Font("SansSerif", Font.BOLD, 20);
	private static final Font MSG_FONT    = new Font("SansSerif", Font.PLAIN, 18);

	private static final Color COLOR_BG        = new Color(0, 0, 30);
	private static final Color COLOR_START      = new Color(0, 150, 255);
	private static final Color COLOR_WORKING    = new Color(125, 255, 125);
	private static final Color COLOR_END        = new Color(255, 140, 0);
	private static final Color COLOR_ERROR      = new Color(255, 0, 0);
	private static final Color COLOR_BAR        = new Color(255, 0, 0, 180);
	private static final Color COLOR_ERROR_LINE = new Color(255, 0, 0, 180);

	// ===== BasicStrokeをstaticフィールドに移動（paintComponentのたびに生成しない） =====
	private static final BasicStroke STROKE_DEFAULT = new BasicStroke();
	private static final BasicStroke STROKE_DASH    = new BasicStroke(
		6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{ 20f, 10f }, 0);

	private LocalTime startTime;
	private LocalTime currentTime;
	private LocalDate targetDate = LocalDate.now();
	private boolean isworking = false;

	public void setTime(LocalDate ld, LocalTime start, LocalTime end) {
		this.startTime = (start != null) ? start : LocalTime.of(0, 0);

		if (end != null) {
			this.currentTime = end;
		} else if (ld.isEqual(LocalDate.now())) {
			this.currentTime = (start != null) ? LocalTime.now() : LocalTime.of(0, 0);
		} else {
			this.currentTime = (start != null) ? start.plusSeconds(1) : LocalTime.of(0, 0);
		}

		this.isworking  = (start != null && end == null);
		this.targetDate = ld;
		repaint();
	}

	/** 研究中の場合に現在時刻を更新する（Timerから呼ぶ用） */
	public void updateCurrentTime() {
		if (isworking && !LocalDate.now().isAfter(targetDate)) {
			this.currentTime = LocalTime.now();
			repaint();
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (startTime == null || currentTime == null)
			return;

		Graphics2D g2 = (Graphics2D) g;

		int width  = getWidth();
		int height = getHeight();
		int lineY  = height / 2;

		g2.setColor(COLOR_BG);
		g2.fillRect(0, 0, width, height);

		double startBase = 6 * 60;
		double endBase   = 24 * 60;
		double total     = endBase - startBase;

		int startX = (int) (50 + (startTime.getHour() * 60 + startTime.getMinute() - startBase) / total * (width - 100));
		int nowX   = (int) (50 + (currentTime.getHour() * 60 + currentTime.getMinute() - startBase) / total * (width - 100));

		// 軸線
		g2.setColor(Color.WHITE);
		g2.drawLine(50, lineY, width - 50, lineY);

		// 目盛り
		g2.setFont(SCALE_FONT);
		g2.setColor(Color.GRAY);
		for (int h = 6; h <= 24; h += 3) {
			int x = (int) (50 + (h * 60 - startBase) / total * (width - 100));
			g2.drawLine(x, lineY - 6, x, lineY + 6);
			g2.drawString(h + ":00", x - 20, lineY + 30);
		}

		// 開始マーカー
		g2.setFont(LABEL_FONT);
		g2.setColor(COLOR_START);
		g2.fillPolygon(
			new int[]{ startX, startX - 12, startX + 12 },
			new int[]{ lineY - 10, lineY - 35, lineY - 35 }, 3);
		g2.drawString("開始 " + startTime.format(TIME_FMT), startX - 20, lineY - 45);

		// 現在 / 打刻漏れ / 終了
		if (isworking) {
			if (!LocalDate.now().isAfter(targetDate)) {
				g2.setColor(COLOR_WORKING);
				g2.drawString("現在 " + currentTime.format(TIME_FMT), nowX - 80, lineY + 60);
			} else {
				g2.setColor(COLOR_ERROR);
				g2.drawString("打刻漏 ", nowX - 35, lineY + 60);
				g2.setStroke(STROKE_DASH);
				g2.setColor(COLOR_ERROR_LINE);
				g2.drawLine(startX, lineY, (int)(50 + (endBase - startBase) / total * (width - 100)), lineY);
				g2.setStroke(STROKE_DEFAULT);
			}
		} else {
			g2.setColor(COLOR_END);
			g2.drawString("終了 " + currentTime.format(TIME_FMT), nowX - 80, lineY + 60);
		}

		g2.fillPolygon(
			new int[]{ nowX, nowX - 12, nowX + 12 },
			new int[]{ lineY + 10, lineY + 35, lineY + 35 }, 3);

		// 経過バー
		g2.setColor(COLOR_BAR);
		g2.fillRect(startX, lineY - 4, nowX - startX, 8);

		// 経過時間（中央）
		if (currentTime.isAfter(startTime)) {
			long minutes = java.time.Duration.between(startTime, currentTime).toMinutes();
			String text = String.format("研究時間：%02d時間%02d分", minutes / 60, minutes % 60);
			g2.setFont(CENTER_FONT);
			g2.setColor(Color.WHITE);
			int textWidth = g2.getFontMetrics().stringWidth(text);
			g2.drawString(text, (width - textWidth) / 2, lineY - 100);
		} else {
			g2.setColor(Color.WHITE);
			g2.setFont(MSG_FONT);
			String msg = "記録はありません";
			int strWidth = g2.getFontMetrics().stringWidth(msg);
			g2.drawString(msg, (width - strWidth) / 2, height / 3);
		}

		// 下部：開始・終了
		g2.setFont(BOTTOM_FONT);
		String startText = "開始: " + startTime.format(TIME_FMT);
		String endText;
		int y = height - 20;

		g2.setColor(COLOR_START);
		g2.drawString(startText, 60, y);

		if (isworking) {
			if (LocalDate.now().isAfter(targetDate)) {
				endText = "終了: 打刻漏";
				g2.setColor(COLOR_ERROR);
			} else {
				endText = "現在: " + currentTime.format(TIME_FMT);
				g2.setColor(COLOR_WORKING);
			}
		} else {
			endText = "終了: " + currentTime.format(TIME_FMT);
			g2.setColor(COLOR_END);
		}
		int endWidth = g2.getFontMetrics().stringWidth(endText);
		g2.drawString(endText, width - endWidth - 60, y);
	}
}
