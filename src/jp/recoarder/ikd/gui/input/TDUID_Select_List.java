package jp.recoarder.ikd.gui.input;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import jp.recoarder.ikd.Usermaster.AttendanceReader;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;

public class TDUID_Select_List {
	private List<JButton> resultButtons = new ArrayList<>();
	private List<JButton> resultButtons_old = new ArrayList<>();
	private GridLayeredPane grid; // フィールド化

	private static final int GRID_X = 28;
	private static final int GRID_Y = 24;

	private FullscreenFrame frame;

	private static int REMAIN_CLOSE = 60;
	private int remaintime = -1;

	private JPanel listPanel;
	private JScrollPane scrollPane;

	private JPanel listPanel_old;
	private JScrollPane scrollPane_old;

	private ActionListener btn_listen;

	public JFrame getFrame() {
		return frame;
	}

	public TDUID_Select_List() {

		frame = new FullscreenFrame();
		frame.setTitle("学籍番号選択");

		grid = new GridLayeredPane(GRID_X, GRID_Y);
		grid.setBackground(new Color(30, 40, 50));

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

		btn_listen = e -> {
		};

		// =========================
		// タイトル
		// =========================
		JLabel title = new JLabel("学籍番号を選択してください", SwingConstants.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 40));
		title.setForeground(Color.WHITE);
		grid.add(title, 4, 1, 20, 3);

		JButton btnMenu = new JButton("◀戻る");
		btnMenu.setFont(new Font("SansSerif", Font.BOLD, 32));
		btnMenu.setBackground(Color.GRAY);
		btnMenu.addActionListener(e -> setVisible(false));
		grid.add(btnMenu, 1, 2, 6, 2);

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

		// ===== 検索結果スクロールエリア =====
		listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		listPanel.setBackground(new Color(30, 40, 50));

		scrollPane = new JScrollPane(listPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		listPanel_old = new JPanel();
		listPanel_old.setLayout(new BoxLayout(listPanel_old, BoxLayout.Y_AXIS));
		listPanel_old.setBackground(new Color(30, 40, 50));

		scrollPane_old = new JScrollPane(listPanel_old);
		scrollPane_old.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Gridに配置（サイズは調整してOK）
		grid.add(scrollPane, 12, 6, 12, 16);
		grid.add(scrollPane_old, 4, 6, 8, 16);


		JLabel oldpane_disc = new JLabel("今年度打刻なし", SwingConstants.CENTER);
		oldpane_disc.setFont(new Font("SansSerif", Font.BOLD, 20));
		oldpane_disc.setForeground(Color.WHITE);
		grid.add(oldpane_disc, 4, 5, 8, 1);

		JLabel pane_disc = new JLabel("今年度打刻あり", SwingConstants.CENTER);
		pane_disc.setFont(new Font("SansSerif", Font.BOLD, 20));
		pane_disc.setForeground(Color.WHITE);
		grid.add(pane_disc, 12, 5, 12, 1);


		frame.getContentPane().add(grid);
	}

	public void setList(List<String> list) {

		listPanel.removeAll();
		resultButtons.clear();
		listPanel_old.removeAll();
		resultButtons_old.clear();

		for (String text : list) {
			int times = AttendanceReader.getStudiedDays_nendo(text.split(",")[0], LocalDate.now().getYear(), 4);

			JButton btn = new JButton(text);
			btn.setFont(new Font("SansSerif", Font.BOLD, 24));
			btn.setBackground(new Color(80, 120, 160));
			btn.setAlignmentX(Component.CENTER_ALIGNMENT);

			btn.setPreferredSize(new Dimension(0, 60)); // 高さ指定
			btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90)); // 横いっぱい

			btn.addActionListener(btn_listen);

			if(times > 0) {
				resultButtons.add(btn);
				listPanel.add(btn);
			}else {
				resultButtons_old.add(btn);
				listPanel_old.add(btn);
			}
		}

		listPanel.revalidate();
		listPanel.repaint();
		listPanel_old.revalidate();
		listPanel_old.repaint();
	}

	public void setBtnListener(ActionListener e) {
		btn_listen = e;
	}

	public void setVisible(boolean b) {
		if (b)
			remaintime = REMAIN_CLOSE;
		else
			remaintime = -1;
		frame.setVisible(b);
	}
}
