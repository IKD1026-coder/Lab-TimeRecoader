package jp.recoarder.ikd.gui.user;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JPanel;

import jp.recoarder.ikd.Usermaster.AttendanceReader;
import jp.recoarder.ikd.Usermaster.UserMaster;

public class TimePanel_allusers_oneyear extends JPanel {

	// ===== Font/ColorをstaticフィールドにしてpaintComponentのたびに生成しない =====
	private static final Font MAIN_FONT  = new Font("SansSerif", Font.PLAIN, 14);
	private static final Font SCALE_FONT = new Font("SansSerif", Font.PLAIN, 12);
	private static final Color COLOR_BG  = new Color(0, 0, 30);
	private static final Color COLOR_DIV = new Color(255, 255, 255, 80);

	private static final long BASE_MINUTES = 450 * 60;
	private int year;
	private List<String> keyword;
	private boolean rankingMode   = false;
	private boolean execlusiveMode = false;

	private static final Color[] monthColors = new Color[] {
		new Color(0, 150, 255),   // 1月
		new Color(0, 200, 150),   // 2月
		new Color(255, 120, 0),   // 3月
		new Color(255, 80, 120),  // 4月
		new Color(180, 100, 255), // 5月
		new Color(255, 200, 0),   // 6月
		new Color(0, 180, 255),   // 7月
		new Color(0, 220, 120),   // 8月
		new Color(255, 140, 60),  // 9月
		new Color(200, 80, 200),  // 10月
		new Color(120, 160, 255), // 11月
		new Color(100, 220, 200)  // 12月
	};

	public TimePanel_allusers_oneyear() {
		year = LocalDate.now().getYear();
	}

	public void setYear(int year) {
		this.year = year;
		revalidate();
		repaint();
	}

	public void onlyDisplayType(List<String> keyword, boolean execlusive) {
		this.keyword       = keyword;
		this.execlusiveMode = execlusive;
		revalidate();
		repaint();
	}

	@Override
	public Dimension getPreferredSize() {
		List<String> users = execlusiveMode
				? UserMaster.getAllUserID_filtered_exclusive(keyword)
				: UserMaster.getAllUserID_filtered(keyword);

		int count = 0;
		for (String userid : users) {
			if (AttendanceReader.getTotalStudyMinutes_nendo(userid, year, 4) > 0)
				count++;
		}

		int marginTop  = 40;
		int lineHeight = 40;
		int height = marginTop + count * lineHeight;
		int width  = getWidth() - 30;
		return new Dimension(width, height);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		int width  = getWidth();
		int height = getHeight();

		g2.setColor(COLOR_BG);
		g2.fillRect(0, 0, width, height);

		List<String> users = execlusiveMode
				? UserMaster.getAllUserID_filtered_exclusive(keyword)
				: UserMaster.getAllUserID_filtered(keyword);

		if (rankingMode) {
			users.sort((u1, u2) -> {
				long t1 = AttendanceReader.getTotalStudyMinutes_nendo(u1, year, 4);
				long t2 = AttendanceReader.getTotalStudyMinutes_nendo(u2, year, 4);
				return Long.compare(t2, t1);
			});
		}

		int marginTop   = 40;
		int lineHeight  = 40;
		int leftMargin  = 200;
		int rightMargin = 150;
		int usableWidth = width - leftMargin - rightMargin;
		int graphTop    = 30;
		int graphBottom = height - 10;

		// 最大値計算 + 平均用の集計
		long maxMinutes = BASE_MINUTES;
		long[] monthlySum = new long[12]; // 4月→翌3月の順で月ごと合計
		int activeCount = 0;              // 学習時間>0の人数

		for (String userId : users) {
		    long total = AttendanceReader.getTotalStudyMinutes_nendo(userId, year, 4);
		    if (total > maxMinutes) maxMinutes = total;

		    if (total > 0) {
		        activeCount++;
		        for (int i = 4; i < 12 + 4; i++) {
		            int month = i > 12 ? i - 12 : i;
		            int y     = i > 12 ? year + 1 : year;
		            monthlySum[i - 4] += AttendanceReader.getTotalStudyMinutes(userId, y, month);
		        }
		    }
		}

		// 個人バー描画
		g2.setFont(MAIN_FONT);

		// ===== 平均バー（最上段） =====
		if (activeCount > 0) {
		    int barY = marginTop + 0 * lineHeight + 15;

		    g2.setColor(Color.YELLOW);
		    g2.drawString("平均", 20, barY);

		    double scale = (double) usableWidth / maxMinutes;
		    int xCursor  = leftMargin;
		    long avgYearTotal = 0;

		    for (int i = 4; i < 12 + 4; i++) {
		        long avgMinutes = monthlySum[i - 4] / activeCount; // 月ごとの平均
		        avgYearTotal += avgMinutes;
		        int w = (int) (avgMinutes * scale);

		        g2.setColor(monthColors[(i - 4) % 12]);
		        g2.fillRect(xCursor + 1, barY - 12, w - 2, 20);
		        xCursor += w;

		        g2.setColor(COLOR_DIV);
		        g2.drawLine(xCursor, barY - 12, xCursor, barY + 8);
		    }

		    long hours = avgYearTotal / 60;
		    long mins  = avgYearTotal % 60;
		    g2.setColor(Color.WHITE);
		    g2.drawString(String.format("平均 %d時間%d分", hours, mins), xCursor + 10, barY + 2);
		}

		int index = 1; // ← 0 から 1 に変更（平均バーの分だけ個人バーを下にずらす）

		for (String userId : users) {
			if (AttendanceReader.getTotalStudyMinutes_nendo(userId, year, 4) == 0)
				continue;

			int barY = marginTop + index * lineHeight + 15;

			if (userId.contains("EJ") || userId.contains("NE")) {
				g2.setColor(Color.ORANGE);
			} else {
				g2.setColor(Color.WHITE);
			}
			g2.drawString(UserMaster.getUsername(userId), 20, barY);

			long yearTotal = AttendanceReader.getTotalStudyMinutes_nendo(userId, year, 4);
			double scale   = (double) usableWidth / maxMinutes;
			int xCursor    = leftMargin;

			for (int i = 4; i < 12 + 4; i++) {
				int month = i > 12 ? i - 12 : i;
				int y     = i > 12 ? year + 1 : year;

				long minutes = AttendanceReader.getTotalStudyMinutes(userId, y, month);
				int w = (int) (minutes * scale);

				g2.setColor(monthColors[(i - 4) % 12]);
				g2.fillRect(xCursor + 1, barY - 12, w - 2, 20);
				xCursor += w;

				g2.setColor(COLOR_DIV);
				g2.drawLine(xCursor, barY - 12, xCursor, barY + 8);
			}

			long hours = yearTotal / 60;
			long mins  = yearTotal % 60;
			g2.setColor(Color.WHITE);
			g2.drawString(String.format("%d時間%d分", hours, mins), xCursor + 10, barY + 2);

			index++;
		}

		// 450hライン
		int goalX = leftMargin + (int) (usableWidth * ((double) BASE_MINUTES / maxMinutes));
		g2.setColor(Color.YELLOW);
		g2.drawLine(goalX, 20, goalX, height - 20);
		g2.drawString("450h", goalX - 20, 20);

		// 横軸目盛り
		g2.setColor(Color.GRAY);
		g2.setFont(SCALE_FONT);
		for (int i = 0; i <= 9; i++) {
			double ratio = i / 10.0;
			long value   = (long) (maxMinutes * ratio);
			int x = leftMargin + (int) (usableWidth * ratio);
			g2.drawLine(x, graphTop - 5, x, 35);
			g2.drawString(value / 60 + "h", x - 10, 20);
		}

		// 0hライン
		g2.setColor(Color.GRAY);
		g2.drawLine(leftMargin, graphTop, leftMargin, graphBottom);

		// 凡例
		int legendX = width - 60;
		int legendY = 40;
		int boxSize = 12;
		int gapY    = 20;
		g2.setFont(SCALE_FONT);

		for (int i = 0; i < 12; i++) {
			g2.setColor(monthColors[i]);
			g2.fillRect(legendX, legendY + i * gapY, boxSize, boxSize);

			g2.setColor(Color.WHITE);
			g2.drawRect(legendX, legendY + i * gapY, boxSize, boxSize);

			int month = (i + 4) % 12;
			if (month == 0) month = 12;
			g2.drawString(month + "月", legendX + boxSize + 8, legendY + i * gapY + 10);
		}
	}

	public void setRankingMode(boolean ranking) {
		this.rankingMode = ranking;
		repaint();
	}
}
