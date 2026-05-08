package jp.recoarder.ikd.gui.input;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import jp.recoarder.ikd.Usermaster.UserMaster;
import jp.recoarder.ikd.assets.Sound;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;

public class TDUID_Select {

	private static final int GRID_X = 28;
	private static final int GRID_Y = 24;

	private FullscreenFrame frame;

	private JTextField inputField;
	private JButton okBtn;

	private static int REMAIN_CLOSE = 60;
	private int remaintime = -1;

	public JFrame getFrame() {
		return frame;
	}

	public TDUID_Select() {

		frame = new FullscreenFrame();
		frame.setTitle("学籍番号選択");

		GridLayeredPane grid = new GridLayeredPane(GRID_X, GRID_Y);
		grid.setBackground(new Color(30, 40, 50));
		frame.getContentPane().add(grid);

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
		// タイトル
		// =========================
		JLabel title = new JLabel("学籍番号を入力してください", SwingConstants.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 40));
		title.setForeground(Color.WHITE);
		grid.add(title, 4, 1, 20, 3);

		// =========================
		// 戻る
		// =========================
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

		// =========================
		// 入力フィールド（統合）
		// =========================
		inputField = new JTextField();
		inputField.setFont(new Font("SansSerif", Font.BOLD, 32));
		grid.add(inputField, 6, 5, 16, 3);

		Timer timer = new Timer(1000, e -> {
			inputField.requestFocusInWindow();
		});
		timer.start();

		// =========================
		// テンキー（共通）
		// =========================
		String[] types = UserMaster.getUserID_type();

		addKeypad(grid, 4, 10, types);

		// =========================
		// 決定
		// =========================
		okBtn = new JButton("決定");
		okBtn.setFont(new Font("SansSerif", Font.BOLD, 32));
		okBtn.setBackground(new Color(100, 200, 255));
		grid.add(okBtn, 20, 10, 4, 12);
	}

	// =========================
	// テンキー生成（数字 + 学科 + ←）
	// =========================
	private void addKeypad(GridLayeredPane grid, int x, int y, String[] List) {

		String[][] layout = {
				{ "7", "8", "9", " ", "" },
				{ "4", "5", "6", " ", "" },
				{ "1", "2", "3", " ", "" },
				{ "全消", "0", "<html>一文字<br>消去</html>", " ", "" }
		};

		for (int i = 0; i < List.length; i++) {
			if (i >= 8)
				break;
			layout[i % 4][3 + i / 4] = List[i];
		}

		for (int row = 0; row < layout.length; row++) {
			for (int col = 0; col < layout[row].length; col++) {

				String label = layout[row][col];

				JButton btn = new JButton(label);
				btn.setFont(new Font("SansSerif", Font.BOLD, 32));
				btn.setBackground(new Color(120, 150, 200));

				if (col >= 3)
					btn.setBackground(new Color(100, 200, 200));

				btn.addActionListener(e -> {
					handleInput(label);
					Sound.touch();
				});

				grid.add(btn, x + col * 3, y + row * 3, 3, 3);

				if (label.isEmpty()) {
					btn.setEnabled(false);
					continue;
				}
			}
		}
	}

	// =========================
	// 入力処理
	// =========================
	private void handleInput(String value) {

		String text = inputField.getText();

		// バックスペース
		if (value.equals("<html>一文字<br>消去</html>")) {
			if (text.isEmpty())
				return;


			// 最後が英字（学科）ならまとめて削除
			boolean removed = false;
			for(String type:UserMaster.getUserID_type()) {
				if(text.endsWith(type)) {
					inputField.setText(text.substring(0, text.length() - type.length()));
					removed = true;
				}
			}

			if (!removed) {
				// 数字は1文字削除
				inputField.setText(text.substring(0, text.length() - 1));
			}
			return;
		}

		if (value.equals("全消")) {
			inputField.setText("");
			return;
		}

		// 入力追加
		inputField.setText(text + value);
	}

	// =========================
	// 学籍番号取得
	// =========================
	public String getStudentId() {
		return inputField.getText().toUpperCase();
	}

	public void addOKListener(ActionListener e) {
		okBtn.addActionListener(e);
	}

	public void setVisible(boolean b) {
		if (b)
			remaintime = REMAIN_CLOSE;
		else
			remaintime = -1;
		inputField.setText("");
		frame.setVisible(b);
	}

	public void requestPOPUP(String string, String title, int Type) {
		JOptionPane.showMessageDialog(frame, string, title, Type);
	}

	public String getTextField() {
		return inputField.getText();
	}

	public void clearTextField() {
		inputField.setText("");
	}
}