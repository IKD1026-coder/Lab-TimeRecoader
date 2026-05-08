import nfc
import re
import csv
from datetime import datetime
import time
import winsound
import threading

def extract_first_line(lines):
    for line in lines:
        match = re.search(r'\|([ -~]+)\|', line)
        if match:
            text = match.group(1)
            if set(text) != {'.'}:
                return text
    return None

def write_to_csv(student_id):
    file_path = "id_reader.csv"
    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    with open(file_path, mode='a', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow([now, student_id, "", "", ""])

# ★ 最後にカードを検出した時刻を記録
last_activity = threading.Event()

# ===== 連続読み取り防止 =====
last_read_id = None
last_read_time = 0
COOLDOWN_SECONDS = 2  # 同じカードは3秒間無視

def on_connect(tag):
    global last_read_id, last_read_time
    last_activity.set()
    print("カード検出")
    try:
        dump_lines = tag.dump()
        first_line = extract_first_line(dump_lines)
        if first_line:
            student_id = first_line[2:9]
            now = time.time()

            # ★ 同じIDが COOLDOWN_SECONDS 以内なら無視
            if student_id == last_read_id and (now - last_read_time) < COOLDOWN_SECONDS:
                print(f"連続読み取りスキップ: {student_id}")
                return False

            last_read_id = student_id
            last_read_time = now

            print("RAW:", first_line, end=" ")
            print("抽出:", student_id)
            write_to_csv(student_id)
            winsound.PlaySound("ding.wav", winsound.SND_FILENAME | winsound.SND_ASYNC)
            time.sleep(1.4)
        else:
            print("データが見つかりません")
            winsound.PlaySound("error.wav", winsound.SND_FILENAME | winsound.SND_ASYNC)
    except Exception as e:
        print("カード読み取りエラー:", e)
        winsound.PlaySound("error.wav", winsound.SND_FILENAME | winsound.SND_ASYNC)
    return False

RECONNECT_INTERVAL = 3 * 60  # 20（秒）

def run_reader(stop_event):
    """clf.connect() を別スレッドで実行"""
    clf = None
    try:
        clf = nfc.ContactlessFrontend('usb')
        print("\nリーダー接続OK Ver5")
        # ★ terminate でstop_eventが立ったら終了
        clf.connect(
            rdwr={'on-connect': on_connect},
            terminate=lambda: stop_event.is_set()
        )
    except Exception as e:
        print(f"リーダーエラー: {e}")
    finally:
        if clf:
            try:
                clf.close()
            except Exception:
                pass
        stop_event.set()  # ★ スレッド終了をメインに通知

# ===== メイン =====
print("待機中... (Ctrl+C で終了)")
while True:
    try:
        stop_event = threading.Event()
        t = threading.Thread(target=run_reader, args=(stop_event,), daemon=True)
        t.start()

        session_start = time.time()

        while not stop_event.is_set():
            stop_event.wait(timeout=5)

            if not stop_event.is_set() and not t.is_alive():
                print(" - スレッド異常終了 -")
                stop_event.set()
                break

            # ★ 30分経過で定期再接続
            if not stop_event.is_set() and (time.time() - session_start) >= RECONNECT_INTERVAL:
                print("- 定期再接続 -")
                stop_event.set()
                break

        t.join(timeout=3)

    except KeyboardInterrupt:
        print(" - 終了 -")
        break

    # ★ ループの最後に必ず待機（高速ループ防止）
    print("再接続(0.6s)")
    time.sleep(0.6)