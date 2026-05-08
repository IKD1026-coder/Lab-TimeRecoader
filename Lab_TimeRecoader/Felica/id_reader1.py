import nfc
import re
import csv
from datetime import datetime
import time
import winsound

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

def on_connect(tag):
    print("\nカード検出")

    try:
        dump_lines = tag.dump()
        first_line = extract_first_line(dump_lines)

        if first_line:
            print("RAW:", first_line)

            student_id = first_line[2:9]
            print("抽出:", student_id)

            write_to_csv(student_id)
        else:
            print("データが見つかりません")
            winsound.PlaySound("error.wav", winsound.SND_FILENAME | winsound.SND_ASYNC)

    except Exception as e:
        print("エラー:", e)
        winsound.PlaySound("error.wav", winsound.SND_FILENAME | winsound.SND_ASYNC)

    return True

# ===== メイン（再接続対応） =====
print("待機中...")

clf = None
last_reset = time.time()  # ★リセット時刻

while True:
    try:
        # ★30分ごとにリセット
        if time.time() - last_reset > 1800:
            print("=== 30分リセット ===")
            if clf:
                try:
                    clf.close()
                except Exception:
                    pass
                clf = None
            last_reset = time.time()
            time.sleep(2)
            continue

        if clf is None:
            print("リーダーに接続中...")
            clf = nfc.ContactlessFrontend('usb')
            print("接続完了")

        # ★ここ重要：1秒で必ず戻る
        start = time.time()
        clf.connect(
            rdwr={
                'on-connect': on_connect,
                'interval': 1.5
            },
            terminate=lambda: time.time() - start > 1
        )

        time.sleep(0.5)

    except IOError as e:
        print("デバイスエラー:", e)

        if clf:
            try:
                clf.close()
            except Exception:
                pass
            clf = None

        time.sleep(2)

    except Exception as e:
        print("エラー:", e)
        time.sleep(0.5)