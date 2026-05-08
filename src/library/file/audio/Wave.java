package library.file.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Wave implements SoundData {
	Clip line;
	boolean a;

	public Wave() {
		a = false;
	}

	@Override
	public int Gettime() {
		// TODO 自動生成されたメソッド・スタブ
		return (int) line.getMicrosecondLength();
	}

	@Override
	public int GetPosition() {
		// TODO 自動生成されたメソッド・スタブ
		return (int) line.getMicrosecondPosition();
	}

	@Override
	public void Start(int Time) {
		// TODO 自動生成されたメソッド・スタブ
		if (a) {
			Thread thread = new Thread() {
				public void run() {

					line.loop(Time - 1);

				}
			};
			thread.setPriority(8);
			thread.start();
		} else {

			line.loop(Time - 1);

			while (line.getMicrosecondLength() != line.getMicrosecondPosition()) {
				try {

					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public void Stop() {
		// TODO 自動生成されたメソッド・スタブ
		line.stop();
	}

	@Override
	public void setFile(File file) {
		// TODO 自動生成されたメソッド・スタブ
		AudioFormat format = null;
		DataLine.Info info = null;
		line = null;
		File audioFile = null;

		audioFile = file;
		try {
			format = AudioSystem.getAudioFileFormat(audioFile).getFormat();
			info = new DataLine.Info(Clip.class, format);
			line = (Clip) AudioSystem.getLine(info);
			line.open(AudioSystem.getAudioInputStream(audioFile));
		} catch (UnsupportedAudioFileException | IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	@Override
	public void setThread(boolean a) {
		// TODO 自動生成されたメソッド・スタブ
		this.a = a;
	}

	@Override
	public boolean getThread() {
		// TODO 自動生成されたメソッド・スタブ
		return a;
	}

	@Override
	public void SetPosition(int ms) {
		// TODO 自動生成されたメソッド・スタブ

		line.setMicrosecondPosition(ms);

	}
} //Wave.class End...
