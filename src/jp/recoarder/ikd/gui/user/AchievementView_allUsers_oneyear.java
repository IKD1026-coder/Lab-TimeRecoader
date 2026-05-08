package jp.recoarder.ikd.gui.user;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import jp.recoarder.ikd.Usermaster.AttendanceReader;
import jp.recoarder.ikd.assets.Sound;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;
import library.gui.swing.IKDButton;

public class AchievementView_allUsers_oneyear {

	private static final int GRID_X = 28;
	private static final int GRID_Y = 24;

	private FullscreenFrame frame;
	private JLabel title;
	private JLabel nendo;
	private int currentyear = 2026;
	private TimePanel_allusers_oneyear tay;
	private JCheckBox cbEJ;
	private JCheckBox cbNE;
	private JCheckBox cbKMJ;
	private JCheckBox cbOther;
	private JCheckBox cbRanking;

	private static int REMAIN_CLOSE = 600;
	private int remaintime = -1;

	public AchievementView_allUsers_oneyear() {

		frame = new FullscreenFrame();
		frame.setTitle("実績確認");

		GridLayeredPane grid = new GridLayeredPane(GRID_X, GRID_Y);
		grid.setBackground(new Color(30, 40, 50));
		frame.getContentPane().add(grid);

		// =========================
		// タイトル
		// =========================
		title = new JLabel("実績確認", SwingConstants.CENTER);
		title.setText("研究時間確認(年間)");
		title.setFont(new Font("SansSerif", Font.BOLD, 40));
		title.setForeground(Color.WHITE);
		grid.add(title, 4, 1, 20, 3);

		JButton btnMenu = IKDButton.createButton("◀戻る", Color.GRAY, 32);
		grid.add(btnMenu, 1, 2, 6, 2);
		btnMenu.addActionListener(e -> {
			Sound.touch();
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

		JButton prevBtn = IKDButton.createButton("◀", Color.gray, 32);
		JButton nextBtn = IKDButton.createButton("▶", Color.gray, 32);
		grid.add(prevBtn, 15, 4, 2, 1);
		grid.add(nextBtn, 22, 4, 2, 1);
		prevBtn.addActionListener(e -> {
			currentyear -= 1;
			setnendo(currentyear);
			Sound.touch();
		});
		nextBtn.addActionListener(e -> {
			currentyear += 1;
			setnendo(currentyear);
			Sound.touch();
		});

		nendo = new JLabel("実績確認", SwingConstants.CENTER);
		nendo.setText("2026年度");
		nendo.setFont(new Font("SansSerif", Font.BOLD, 40));
		nendo.setForeground(Color.WHITE);
		grid.add(nendo, 17, 4, 5, 1);

		tay = new TimePanel_allusers_oneyear();
		String[] ar1 = { "EJ", "NE" };
		tay.onlyDisplayType(new ArrayList<>(Arrays.asList(ar1)), false);
		currentyear = LocalDate.now().getYear();
		tay.setYear(currentyear);

		JScrollPane js = new JScrollPane(tay);
		grid.add(js, 1, 5, 26, 18);

		cbEJ = new JCheckBox("EJ");
		cbNE = new JCheckBox("NE");
		cbKMJ = new JCheckBox("KMJ");
		cbOther = new JCheckBox("Other");
		cbRanking = new JCheckBox("ランキング順");

		// デフォルト選択（任意）
		ini_checkbox();

		cbEJ.setHorizontalAlignment(JCheckBox.CENTER);
		cbNE.setHorizontalAlignment(JCheckBox.CENTER);
		cbKMJ.setHorizontalAlignment(JCheckBox.CENTER);
		cbOther.setHorizontalAlignment(JCheckBox.CENTER);
		cbRanking.setHorizontalAlignment(JCheckBox.CENTER);

		// 共通リスナー
		ActionListener listener = e -> updateFilter();

		// 配置
		grid.add(cbEJ, 1, 4, 2, 1);
		grid.add(cbNE, 3, 4, 2, 1);
		grid.add(cbKMJ, 5, 4, 2, 1);
		grid.add(cbOther, 7, 4, 2, 1);
		grid.add(cbRanking, 9, 4, 3, 1);

		// イベント登録
		cbEJ.addActionListener(listener);
		cbNE.addActionListener(listener);
		cbKMJ.addActionListener(listener);
		cbOther.addActionListener(listener);
		cbRanking.addActionListener(listener);
		updateFilter();

	}

	private void ini_checkbox() {
		cbEJ.setSelected(false);
		cbNE.setSelected(false);
		cbKMJ.setSelected(false);
		cbOther.setSelected(false);
		cbRanking.setSelected(true); // デフォルトOFF（通常並び）
		updateFilter();
	}

	private void setnendo(int year) {
		nendo.setText(year + "年度");
		tay.setYear(year);
	}

	private void updateFilter() {
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

		if (cbRanking.isSelected()) {
			tay.setRankingMode(true); // ランキング順
		} else {
			tay.setRankingMode(false); // 通常順
		}

		if (cbOther.isSelected())//その他が選ばれた時は、選ばれていないものを除外
			tay.onlyDisplayType(no_selected, true);
		else//それ以外は、選ばれたものを表示
			tay.onlyDisplayType(selected, false);

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
		setnendo(AttendanceReader.getnendo(LocalDate.now().getYear(), LocalDate.now().getMonthValue()));
		frame.setVisible(b);
	}

}