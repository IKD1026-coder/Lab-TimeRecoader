package jp.recoarder.ikd.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import jp.recoarder.ikd.Usermaster.UserMaster;
import jp.recoarder.ikd.assets.Sound;
import library.gui.swing.FullscreenFrame;
import library.gui.swing.GridLayeredPane;

public class UserManageScreen {

	private static final int GRID_X = 16;
	private static final int GRID_Y = 16;

	private FullscreenFrame frame;

	private JTextField userIdField;
	private JTextField usernameField;
	private JTextArea userListArea;

	private static int REMAIN_CLOSE = 300;
	private int remaintime = -1;

	public UserManageScreen() {

		frame = new FullscreenFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("ユーザー管理");

		GridLayeredPane grid = new GridLayeredPane(GRID_X, GRID_Y);
		grid.setBackground(new Color(40, 45, 50));

		frame.getContentPane().add(grid);

		JButton btnMenu = createButton("◀戻る", Color.GRAY);
		grid.add(btnMenu, 1, 1, 4, 1);
		btnMenu.addActionListener(e -> {
			Sound.touch();
			userIdField.setText("");
			usernameField.setText("");
			setVisible(false);
		});

		new Timer(1000, e -> {
			if (remaintime == 0) {
				userIdField.setText("");
				usernameField.setText("");
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
		// タイトル
		// =========================
		JLabel title = new JLabel("ユーザー登録・削除", SwingConstants.CENTER);
		title.setFont(new Font("SansSerif", Font.BOLD, 40));
		title.setForeground(Color.WHITE);
		grid.add(title, 0, 0, 16, 2);

		// =========================
		// 入力欄
		// =========================

		JLabel idLabel = new JLabel("ユーザーID", SwingConstants.RIGHT);
		idLabel.setFont(new Font("SansSerif", Font.PLAIN, 40));
		idLabel.setForeground(Color.WHITE);
		grid.add(idLabel, 2, 3, 3, 1);

		userIdField = new JTextField();
		userIdField.setFont(new Font("SansSerif", Font.PLAIN, 40));
		grid.add(userIdField, 5, 3, 6, 1);

		JLabel nameLabel = new JLabel("ユーザー名", SwingConstants.RIGHT);
		nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 40));
		nameLabel.setForeground(Color.WHITE);
		grid.add(nameLabel, 2, 5, 3, 1);

		usernameField = new JTextField();
		usernameField.setFont(new Font("SansSerif", Font.PLAIN, 40));
		grid.add(usernameField, 5, 5, 6, 1);

		// =========================
		// ボタン
		// =========================

		JButton btnAdd = createButton("登録", new Color(80, 200, 120));
		grid.add(btnAdd, 12, 3, 3, 1);

		JButton btnDelete = createButton("削除", new Color(220, 80, 80));
		grid.add(btnDelete, 12, 4, 3, 1);

		JButton btnSearch = createButton("検索", new Color(100, 140, 255));
		grid.add(btnSearch, 12, 5, 3, 1);

		// =========================
		// リスト
		// =========================

		userListArea = new JTextArea();
		userListArea.setEditable(false);
		userListArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
		userListArea.setBackground(new Color(20, 20, 25));
		userListArea.setForeground(Color.WHITE);

		JScrollPane scroll = new JScrollPane(userListArea);
		grid.add(scroll, 2, 7, 12, 8);

		loadUserList();

		// =========================
		// イベント
		// =========================

		// 登録
		btnAdd.addActionListener(e -> {
			Sound.touch();

			String id = userIdField.getText().toUpperCase().trim().toUpperCase();
			String name = usernameField.getText().trim();

			if (id.isEmpty() || name.isEmpty()) {
				Sound.Error();
				JOptionPane.showMessageDialog(frame, "IDと名前を入力してください");
				return;
			}

			if (UserMaster.addUser(id, name)) {
				Sound.Complete();
				loadUserList();
				clearFields();
				JOptionPane.showMessageDialog(frame, "登録しました");
			} else {
				Sound.CritricalError();
				JOptionPane.showMessageDialog(frame, "登録失敗");
			}
		});

		// 削除
		btnDelete.addActionListener(e -> {
			Sound.touch();

			String id = userIdField.getText().toUpperCase().trim();
			if (id.isEmpty())
				return;

			String name = UserMaster.getUsername(id);

			if (name.equals("不明なユーザー")) {
				Sound.Error();
				JOptionPane.showMessageDialog(frame, "存在しません");
				return;
			}

			int confirm = JOptionPane.showConfirmDialog(
					frame,
					name + " を削除しますか？",
					"確認",
					JOptionPane.YES_NO_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				if (UserMaster.removeUser(id)) {
					Sound.Complete();
					loadUserList();
					clearFields();
				}
			}
		});

		// 検索
		btnSearch.addActionListener(e -> {
			Sound.touch();

			String id = userIdField.getText().toUpperCase().trim();
			if (id.isEmpty())
				return;

			String name = UserMaster.getUsername(id);

			if (!name.equals("不明なユーザー")) {
				usernameField.setText(name);
				Sound.Ding();
			} else {
				usernameField.setText("");
				JOptionPane.showMessageDialog(frame, "見つかりません");
			}
		});
	}

	// =========================
	// リスト更新
	// =========================
	private void loadUserList() {

		userListArea.setText("");

		userListArea.append(String.format("%-10s %-20s\n", "ID", "名前"));
		userListArea.append("----------------------------------------\n");

		List<String> list = UserMaster.getAllUsers();
		Collections.reverse(list); // [C, B, A]

		for (String line : list) {
			String[] p = line.split(",");
			if (p.length == 2) {

				String id = p[0];
				String name = p[1];

				// 横並び整形（重要）
				String row = String.format("%-10s %-20s", id, name);

				userListArea.append(row + "\n");
			}
		}
	}

	private void clearFields() {
		userIdField.setText("");
		usernameField.setText("");
		userIdField.requestFocus();
	}

	private JButton createButton(String text, Color color) {
		JButton btn = new JButton(text);
		btn.setFont(new Font("SansSerif", Font.BOLD, 18));
		btn.setBackground(color);
		btn.setForeground(Color.BLACK);
		btn.setFocusPainted(false);
		return btn;
	}

	public void setVisible(boolean b) {
		if (b)
			remaintime = REMAIN_CLOSE;
		else
			remaintime = -1;
		frame.setVisible(b);
	}
}