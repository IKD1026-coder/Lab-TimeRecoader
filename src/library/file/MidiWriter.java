package library.file;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiWriter {

	private File file;
	private Sequence sequence;
	private Track[] track;

	private ShortMessage message;
	MidiEvent noteEvent;

	public MidiWriter(File file) {
		this.file = file;
		message = new ShortMessage();
		noteEvent = new MidiEvent(message, 0);
		try {

			// シーケンスを作成
			sequence = new Sequence(Sequence.PPQ, 480);

			// トラックを作成
			track = new Track[16];
			for (int i = 0; i < 16; i++)
				track[i] = sequence.createTrack();

		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public void noteon(int channel, int noteNumber, int velocity, int trackn, int tick) {
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
			MidiEvent noteOnEvent = new MidiEvent(message, tick);
			track[trackn].add(noteOnEvent);
		} catch (InvalidMidiDataException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public void noteoff(int channel, int noteNumber, int velocity, int trackn, int tick) {

		try {
			ShortMessage noteOff = new ShortMessage();
			noteOff.setMessage(ShortMessage.NOTE_OFF, channel, noteNumber, 0); // ノートオフのvelocityは0
			MidiEvent noteOffEvent = new MidiEvent(noteOff, tick);
			track[trackn].add(noteOffEvent);
		} catch (InvalidMidiDataException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public void setTempo(int tempoBPM) {
		// テンポメッセージを追加
		int microsecondsPerMinute = 60000000; // 1分あたりのマイクロ秒数
		long microsPerBeat = microsecondsPerMinute / tempoBPM;
		MetaMessage tempoMessage = new MetaMessage();
		byte[] data = new byte[3];
		data[0] = (byte) ((microsPerBeat >> 16) & 0xFF);
		data[1] = (byte) ((microsPerBeat >> 8) & 0xFF);
		data[2] = (byte) (microsPerBeat & 0xFF);
		try {
			tempoMessage.setMessage(0x51, data, 3);
			MidiEvent tempoEvent = new MidiEvent(tempoMessage, 0);
			track[0].add(tempoEvent);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public void programChange(int channel, int program, int trackn, int tick, int bank) {
		// プログラム変更メッセージを追加して楽器を変更（ピアノ）
		try {
			MidiEvent programChangePiano = new MidiEvent(
					new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0x00, bank), tick);
			MidiEvent programChangePiano2 = new MidiEvent(
					new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0x20, 0), tick);
			MidiEvent programChangePiano3 = new MidiEvent(
					new ShortMessage(ShortMessage.PROGRAM_CHANGE, channel, program - 1, 0), tick);
			track[trackn].add(programChangePiano);
			track[trackn].add(programChangePiano2);
			track[trackn].add(programChangePiano3);
		} catch (InvalidMidiDataException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void pitchBend(int channel, int bendValue, int trackn, int tick) {
		bendValue += 8192;
		try {
			// ピッチベンドメッセージを作成
			ShortMessage pitchBendMessage = new ShortMessage();

			// メッセージの種類を設定 (0xE0 は Pitch Bend メッセージのステータスバイト)
			pitchBendMessage.setMessage(ShortMessage.PITCH_BEND, channel, bendValue & 0x7F, (bendValue >> 7) & 0x7F);

			// ピッチベンドイベントを作成し、指定したティックに配置
			MidiEvent pitchBendEvent = new MidiEvent(pitchBendMessage, tick);

			// トラックにピッチベンドイベントを追加
			track[trackn].add(pitchBendEvent);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	public static double midiNoteToFrequency(int noteNumber) {
		return 440.0 * Math.pow(2.0, (noteNumber - 69) / 12.0);
	}

	public void flush() {

		try {
			MidiSystem.write(sequence, 1, file);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

}