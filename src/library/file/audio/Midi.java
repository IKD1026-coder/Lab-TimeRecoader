package library.file.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;


public class Midi implements SoundData{

	Sequencer sequencer;
	boolean a;

	public Midi() {
		a=false;
	}

	@Override
	public int Gettime() {
		// TODO 自動生成されたメソッド・スタブ
		return (int) (sequencer.getTickLength()*1000/(sequencer.getTickLength()/60));
	}
	public int GetPosition() {
		// TODO 自動生成されたメソッド・スタブ
		return (int) (sequencer.getTickPosition()*1000/(sequencer.getTickLength()/60));
	}
		@Override
		public void Start(int Time) {
			// TODO 自動生成されたメソッド・スタブ
			if(a) {
				Thread thread = new Thread() {
					public void run() {

						sequencer.setLoopEndPoint(0);
						sequencer.setLoopCount(Time);
						sequencer.start();
					}
				};
				thread.setPriority(8);
				thread.start();
			}else {
				sequencer.setLoopEndPoint(0);
				sequencer.setLoopCount(Time);
				sequencer.start();


				while(sequencer.isRunning()) {
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
		sequencer.stop();
	}

	@Override
	public void setFile(File file) {
		// TODO 自動生成されたメソッド・スタブ
		 try {

			sequencer = MidiSystem.getSequencer();

			   sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			   sequencer.open();
			   FileInputStream in=new FileInputStream(file);
			   Sequence sequence=MidiSystem.getSequence(in);
			   in.close();//ファイルをクローズ
			   sequencer.setSequence(sequence);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (MidiUnavailableException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
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
		sequencer.setTickPosition((long) (sequencer.getTempoInBPM()*ms/sequencer.getTickLength()));
	}

}

