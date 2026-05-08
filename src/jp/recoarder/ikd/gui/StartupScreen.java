package jp.recoarder.ikd.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;

public class StartupScreen {

    private FullscreenFrame frame;

    private static final int GRID_X = 16;
    private static final int GRID_Y = 12;

    public StartupScreen() {

        frame = new FullscreenFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Launching Lab_TimeRecoader.");

        GridLayeredPane grid = new GridLayeredPane(GRID_X, GRID_Y);
        grid.setBackground(new Color(20, 20, 20));

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(grid, BorderLayout.CENTER);

        createUI(grid);
    }


	private boolean isShowing = false;
	public void setVisible(boolean b) {
	    if (b && isShowing) return;  // ← 既に表示中なら何もしない
	    isShowing = b;
        frame.setVisible(b);
    }

    public void dispose() {
    	frame.dispose();
    }

    private void createUI(GridLayeredPane grid) {

        // --- 中央パネル ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new java.awt.GridLayout(5, 1));
        centerPanel.setBackground(new Color(20, 20, 20));

        // グリッド中央に配置
        grid.add(centerPanel, 4, 3, 8, 6);

        // --- IKDロゴ ---
        JLabel ikdLogo = new JLabel();
        ikdLogo.setHorizontalAlignment(SwingConstants.CENTER);
        ikdLogo.setIcon(new ImageIcon("./Lab_TimeRecoader/assets/logo-mini.jpg"));

        // --- 技研ロゴ ---
        JLabel gikenLogo = new JLabel();
        gikenLogo.setHorizontalAlignment(SwingConstants.CENTER);
        gikenLogo.setIcon(new ImageIcon("./Lab_TimeRecoader/assets/giken.png"));

        // --- システム名 ---
        JLabel title = new JLabel("Lab_TimeRecoader");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setForeground(Color.WHITE);

        // --- サブタイトル ---
        JLabel subTitle = new JLabel("Presented by IKD-Consulting");
        subTitle.setHorizontalAlignment(SwingConstants.CENTER);
        subTitle.setFont(new Font("SansSerif", Font.PLAIN, 22));
        subTitle.setForeground(Color.LIGHT_GRAY);

        // --- 起動中表示 ---
        JLabel loading = new JLabel("起動中...");
        loading.setHorizontalAlignment(SwingConstants.CENTER);
        loading.setFont(new Font("SansSerif", Font.PLAIN, 20));
        loading.setForeground(Color.GREEN);

        // 追加
        centerPanel.add(ikdLogo);
        centerPanel.add(gikenLogo);
        centerPanel.add(title);
        centerPanel.add(subTitle);
        centerPanel.add(loading);
    }
}