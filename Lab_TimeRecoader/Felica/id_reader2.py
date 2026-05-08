import nfc
import re
import csv
from datetime import datetime

def extract_first_line(lines):
    for line in lines:
        match = re.search(r'\|([ -~]+)\|', line)
        if match:
            text = match.group(1)

            # '.'だけの行を除外
            if set(text) != {'.'}:
                return text
    return None

def write_to_csv(student_id):
    file_path = "id_reader.csv"

    # 現在時刻を取得
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

            # ← 抽出後すぐ書き込み
            write_to_csv(student_id)

        else:
            print("データが見つかりません")

    except Exception as e:
        print("エラー:", e)

    return True

clf = nfc.ContactlessFrontend('usb')

print("待機中...")

while True:
    clf.connect(rdwr={'on-connect': on_connect})