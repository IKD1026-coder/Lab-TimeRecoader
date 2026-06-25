package jp.recoarder.ikd;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.UIManager;

import jp.recoarder.ikd.Usermaster.AttendanceCSV_Writer;
import jp.recoarder.ikd.Usermaster.AttendanceLogger;
import jp.recoarder.ikd.Usermaster.AttendanceReader;
import jp.recoarder.ikd.Usermaster.UserMaster;
import jp.recoarder.ikd.assets.Sound;
import jp.recoarder.ikd.gui.AttendanceMenu;
import jp.recoarder.ikd.gui.ManagementMenu;
import jp.recoarder.ikd.gui.ProcessingMenu;
import jp.recoarder.ikd.gui.StartupScreen;
import jp.recoarder.ikd.gui.UserManageScreen;
import jp.recoarder.ikd.gui.input.AttendanceInputGUI;
import jp.recoarder.ikd.gui.input.TDUID_Select;
import jp.recoarder.ikd.gui.input.TDUID_Select_List;
import jp.recoarder.ikd.gui.user.AchievementView;
import jp.recoarder.ikd.gui.user.AchievementView_allUsers_oneday;
import jp.recoarder.ikd.gui.user.AchievementView_allUsers_oneyear;
import jp.recoarder.ikd.gui.user.StudentMenu;
import library.Weather;

public class TimeRecoader {

	public static StartupScreen ss;
	static {
		ss = new StartupScreen();
		ss.setVisible(true);
		Sound.touch();
	}

	private static boolean sisRainToday = false;

	public static void main(String[] args) {
		UIManager.put("ScrollBar.width", 35);
		UIManager.put("ScrollBar.height", 35);
		Font newFont = new Font("SansSerif", Font.BOLD, 18);
		UIManager.put("CheckBox.font", newFont);
		UIManager.put("Button.font", newFont);
		UIManager.put("Label.font", newFont);
		//----------------------
		//個人画面
		//----------------------
		StudentMenu SM = new StudentMenu();
		SM.addBtnStartListerner(e -> {
			//Sound.Chime();
			ProcessingMenu.startAnimation(1500, "研究開始処理中…", true);
			AttendanceLogger.logStart();
			System.gc();

			Delayer(new DelayListener() {
				public void invoke() {
					SM.setVisible(false);
				}
			});
		});
		SM.addBtnEndListerner(e -> {
			//Sound.Chime();
			ProcessingMenu.startAnimation(1500, "研究終了処理中…", !sisRainToday);
			AttendanceLogger.logEnd();
			System.gc();

			Delayer(new DelayListener() {
				public void invoke() {
					Umbrella();
				}
			});

			Delayer(new DelayListener() {
				public void invoke() {
					SM.setVisible(false);
				}
			});
		});

		//実績確認画面(個人)
		AchievementView AV = new AchievementView();
		SM.addAchivementViewListener(e -> {
			AV.setVisible(true);
			Delayer(new DelayListener() {
				public void invoke() {
					SM.setVisible(false);
				}
			});
		});

		//----------------------
		//管理画面作成
		//----------------------
		ManagementMenu MM = new ManagementMenu();
		//ユーザー追加
		UserManageScreen UMS = new UserManageScreen();
		MM.btnUserEditAddListener(e -> {
			UMS.setVisible(true);
			Sound.touch();
		});

		//CSVファイル
		MM.btnCsvAddListener(e -> {
			Sound.touch();
			int month = LocalDate.now().minusMonths(1).getMonthValue();
			if (MM.confirmDialog(month + "月分の月報を作成しますか?") == JOptionPane.YES_OPTION) {
				AttendanceCSV_Writer.write(LocalDate.now().minusMonths(1));
				Sound.Complete();
			}
		});

		MM.btnSemiAddListener(e -> {
			Sound.touch();
			int in = MM.confirmDialog("一括打刻(13:40-18:00)しますか？");
			if (in == JOptionPane.YES_OPTION) {
				for (String userid : UserMaster.getAllUserIDs()) {
					System.out.println(userid);
					UserMaster.Login(userid);

					long tim = AttendanceReader.getTotalStudyMinutes_nendo(userid, LocalDate.now().getYear(),
							LocalDate.now().getMonthValue());

					System.out.println(tim);
					if (tim > 0) {
						if (!AttendanceReader.isWorking())
							AttendanceLogger.write(true, LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 40)));
						if (!AttendanceReader.isEnded(userid, LocalDate.now()))
							AttendanceLogger.write(false, LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 00)));
					}

				}
			}

		});

		//----------------------
		//待機画面作成
		//----------------------
		AttendanceMenu AM = new AttendanceMenu();
		AM.addAdminListener(e -> {
			MM.setVisible(true);
			Sound.touch();
		});

		//研究時間確認
		AchievementView_allUsers_oneday AMV1 = new AchievementView_allUsers_oneday();
		AM.btnAchive1Listener(e -> {
			AMV1.setVisible(true);
		});

		//年間研究時間確認
		AchievementView_allUsers_oneyear AMV3 = new AchievementView_allUsers_oneyear();
		AM.btnAchive3Listener(e -> {
			AMV3.setVisible(true);
		});

		//===================学籍番号自動入力===================
		AM.setLoginListener(userId -> {
			if (userId != null && userId.length() >= 9) {
				userId = userId.substring(2, 9);
			}
			if (UserMaster.Login(userId)) {
				SM.setName(UserMaster.getActiveUserId());
				SM.setVisible(true);
				processLogin();
			} else {
				Sound.Error();
			}
		});

		//===================学生証タッチ認証===================
		FelicaDetector fd = new FelicaDetector(userId -> {
			if (UserMaster.Login(userId)) {
				SM.setName(UserMaster.getActiveUserId());
				SM.setVisible(true);
				processLogin();
			} else {
				Sound.Error();
			}

		}, "./Lab_TimeRecoader/Felica", "id_reader.csv");
		fd.start();

		//===================学籍番号手動入力===================
		TDUID_Select_List tss = new TDUID_Select_List();

		TDUID_Select TIS = new TDUID_Select();
		AM.btnManualListener(e -> {
			//学籍番号入力
			TIS.setVisible(true);
		});

		TIS.addOKListener(e2 -> {
			Sound.touch();
			List<String> ar = UserMaster.getAllUsers_filtered(List.of(TIS.getTextField().toUpperCase()));
			if (ar.size() == 1) {
				if (UserMaster.Login(ar.get(0).split(",")[0])) {
					SM.setName(UserMaster.getActiveUserId());
					SM.setVisible(true);
					Delayer(new DelayListener() {
						public void invoke() {
							TIS.setVisible(false);
						}
					});
					processLogin();
				} else {
					Sound.Error();
					TIS.requestPOPUP("そのユーザーは未登録です", "ログインエラー", JOptionPane.ERROR_MESSAGE);
				}
			} else if (ar.size() > 1) {
				Sound.Complete();
				tss.setList(ar);
				tss.setVisible(true);
				Delayer(new DelayListener() {
					public void invoke() {
						TIS.setVisible(false);
					}
				});
			} else {
				Sound.Error();
			}
			TIS.clearTextField();
		});

		tss.setBtnListener(e -> {
			Delayer(new DelayListener() {
				public void invoke() {
					tss.setVisible(false);
				}
			});
			Sound.touch();
			if (UserMaster.Login(((JButton) e.getSource()).getText().split(",")[0])) {
				SM.setName(UserMaster.getActiveUserId());
				SM.setVisible(true);
				Delayer(new DelayListener() {
					public void invoke() {
						TIS.setVisible(false);
					}
				});
				processLogin();
			} else {
				Sound.CritricalError();
			}
			TIS.clearTextField();
		});

		//===起動===
		Delayer(new DelayListener() {
			public void invoke() {
				ss.setVisible(false);
			}
		});

		//=====表示=====
		AM.setVisible(true);
		Sound.touch();
		schedule_restart();

		//===コマンドログイン

		if (args.length == 1) {
			if (UserMaster.Login(args[0])) {
				SM.setName(UserMaster.getActiveUserId());
				SM.setVisible(true);
				processLogin();
			} else {
				Sound.Error();
			}
		}

		//=====天気判定=====
		sisRainToday = Weather.isRainy(35.46, 139.48);
		//sisRainToday = true;
		new Timer(60000 * 60, e -> {
			new Thread(() -> {
				if (!sisRainToday)
					sisRainToday = Weather.isRainy(35.46, 139.48);
			}).start();
		}).start();
	}

	public static boolean isRain_today() {
		return sisRainToday;
	}

	private static void Umbrella() {
		new Thread(() -> {
			try {
				if (sisRainToday) {
					Sound.Ding();
					ProcessingMenu.showImageMessage(2800, "./Lab_TimeRecoader/assets/leaveU.jpg", "傘をお忘れなく！", true);
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}).start();
	}

	private static void schedule_restart() {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		Runnable task = () -> {
			// 実行したい処理を書く
			try {
				Runtime.getRuntime().exec("shutdown -r -t 5");
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		};

		long initialDelay = getInitialDelay();
		long period = TimeUnit.DAYS.toSeconds(1);
		scheduler.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd(E)", Locale.JAPANESE);

	private static void processLogin() {
		List<LocalDate> list = AttendanceReader.getMissingEndDays(UserMaster.getActiveUserId());
		if (list.size() > 0) {
			Sound.Error();
			for (LocalDate l : list) {
				AttendanceInputGUI AIG = new AttendanceInputGUI();
				LocalTime lt = AttendanceReader.getTime(UserMaster.getActiveUserId(), true, l);
				if (lt != null) {
					AIG.selectCombo(lt);
				}
				AIG.setMessage("前回の終了時間が未入力です。【対象日：" + l.format(formatter) + "】");
				AIG.setBackgroundColor(new Color(125, 0, 0));

				AIG.addOKListener(e4 -> {
					if (AIG.getEndTime().isAfter(AIG.getStartTime())) {
						LocalDateTime start = LocalDateTime.of(l, AIG.getStartTime());
						LocalDateTime end = LocalDateTime.of(l, AIG.getEndTime());
						AttendanceLogger.write(true, start);
						AttendanceLogger.write(false, end);
						AIG.dispose();
						Sound.Chime();
					} else {
						Sound.Error();
						AIG.showDialog("終了時間は開始時間よりも後の時刻にしてください。", "入力時刻エラー", JOptionPane.ERROR_MESSAGE);
					}
				});
				AIG.setVisible(true);
			}
		} else {
			Sound.Chime();
		}

	}

	private static long getInitialDelay() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nextRun = now.withHour(3).withMinute(0).withSecond(0).withNano(0);

		if (now.compareTo(nextRun) >= 0) {
			nextRun = nextRun.plusDays(1);
		}

		return Duration.between(now, nextRun).getSeconds();
	}

	private static void Delayer(DelayListener d) {
		Timer t = new Timer(700, e2 -> {
			d.invoke();
		});
		t.setRepeats(false);
		t.start();
	}

	private interface DelayListener {
		void invoke();
	}
}
