package jp.recoarder.ikd.gui.user;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import jp.recoarder.ikd.LoginListener;
import jp.recoarder.ikd.Usermaster.AttendanceReader;
import jp.recoarder.ikd.Usermaster.UserMaster;

public class TimePanel_allStudent extends JPanel {

	private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
	private static final Font BAR_FONT = new Font("SansSerif", Font.BOLD, 14);

	private LocalDate targetMonth;
	private List<String> keyword;
	private long rankingFirst = 0;

	private boolean allBlack = false;
	private String addtion_script = "";

	// ===== 追加: バーの当たり判定リスト =====
	private final List<BarHitArea> barHitAreas = new ArrayList<>();

	private LoginListener tL = e->{};

	public TimePanel_allStudent(boolean alb) {
		allBlack = alb;

		// ===== 追加: マウスクリックでバーの番号を出力 =====
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				for (BarHitArea hit : barHitAreas) {
					if (hit.rect.contains(e.getPoint())) {
						tL.actionPerformed(hit.userId);
						break;
					}
				}
			}
		});
	}

	public void setLoginListener(LoginListener tl2) {
		tL = tl2;
	}

	public void setMonth(LocalDate ld) {
		this.targetMonth = ld;
		repaint();
	}

	public void onlyDisplayType(List<String> keyword) {
		this.keyword = keyword;
		repaint();
	}

	@Override
	public java.awt.Dimension getPreferredSize() {
		if (targetMonth == null) {
			return new java.awt.Dimension(800, 600);
		}
		int barWidth = 30;
		int gap = 20;
		int width = UserMaster.getAllUserID_filtered(keyword).size() * (barWidth + gap);
		return new java.awt.Dimension(width, getHeight());
	}

	public long getRankingFirstMinute() {
		List<RankData> ranking = new ArrayList<>();
		List<String> users = UserMaster.getAllUserID_filtered(keyword);

		for (String userId : users) {
			long minutes = AttendanceReader.getTotalStudyMinutes(
					userId, targetMonth.getYear(), targetMonth.getMonthValue());

			if (AttendanceReader.getTotalStudyMinutes_nendo(
					userId, targetMonth.getYear(), targetMonth.getMonthValue()) == 0)
				continue;

			ranking.add(new RankData(userId, UserMaster.getUsername(userId), minutes));
		}

		ranking.sort((a, b) -> Long.compare(b.minutes, a.minutes));

		return ranking.stream().mapToLong(r -> r.minutes).max().orElse(1);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (targetMonth == null)
			return;

		int panelWidth = getWidth();
		int panelHeight = getHeight();

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, panelWidth, panelHeight);

		List<RankData> ranking = new ArrayList<>();
		List<String> users = UserMaster.getAllUserID_filtered(keyword);

		for (String userId : users) {
			long minutes = AttendanceReader.getTotalStudyMinutes(
					userId, targetMonth.getYear(), targetMonth.getMonthValue());

			if (AttendanceReader.getTotalStudyMinutes_nendo(
					userId, targetMonth.getYear(), targetMonth.getMonthValue()) == 0)
				continue;

			ranking.add(new RankData(userId, UserMaster.getUsername(userId), minutes));
		}

		ranking.sort((a, b) -> Long.compare(b.minutes, a.minutes));

		long maxMinutes = ranking.stream().mapToLong(r -> r.minutes).max().orElse(1);

		if (rankingFirst != 0) {
			maxMinutes = rankingFirst;
		}

		int titleHeight = 50;
		int bottomMargin = 50;
		int topMargin = 20;
		int chartBottom = panelHeight - bottomMargin;
		int chartHeight = chartBottom - (titleHeight + topMargin);
		int barWidth = 30;
		int gap = 20;
		int x = 30;

		g.setFont(TITLE_FONT);
		g.setColor(Color.BLACK);
		g.drawString("月間研究時間ランキング("
				+ targetMonth.getYear() + "年"
				+ targetMonth.getMonthValue() + "月)" + addtion_script, 20, 30);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setFont(BAR_FONT);
		int rank = 1;

		// ===== 追加: 描画前にリストをクリア =====
		barHitAreas.clear();

		for (RankData r : ranking) {
			int barHeight = (int) ((double) r.minutes / maxMinutes * chartHeight);
			int barX = x;
			int barY = chartBottom - barHeight;

			if ((r.userId.contains("EJ") || r.userId.contains("NE")) && !allBlack) {
				g.setColor(Color.BLUE);
			} else {
				g.setColor(Color.BLACK);
			}
			g.fillRect(barX, barY, barWidth, barHeight);

			// ===== 追加: 当たり判定を記録 =====
			barHitAreas.add(new BarHitArea(new Rectangle(barX, barY - 40, barWidth, barHeight+40), r.userId));

			double angle = Math.toRadians(20);
			g2d.rotate(angle, barX, chartBottom + 20);
			g2d.drawString(r.name, barX, chartBottom + 20);
			g2d.rotate(-angle, barX, chartBottom + 20);

			g.drawString(r.minutes / 60 + "h", barX, barY - 5);
			g.drawString(rank + "位", barX, barY - 20);

			x += barWidth + gap;
			rank++;
		}
	}

	public void setRankingFirst(long time1) {
		rankingFirst = time1;
		repaint();
		revalidate();
	}

	class RankData {
		String userId;
		String name;
		long minutes;

		public RankData(String userId, String name, long minutes) {
			this.userId = userId;
			this.name = name;
			this.minutes = minutes;
		}

		public long getMinutes() {
			return minutes;
		}
	}

	// ===== 追加: 当たり判定データクラス =====
	private static class BarHitArea {
		Rectangle rect;
		String userId;

		BarHitArea(Rectangle rect, String userId) {
			this.rect = rect;
			this.userId = userId;
		}
	}

	public void setName_addtion(String string) {
		addtion_script = string;
	}
}