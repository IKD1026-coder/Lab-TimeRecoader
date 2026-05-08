package library.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
	BufferedWriter BW;

	public Writer(File file) throws IOException {
		this.BW = new BufferedWriter(new FileWriter(file));
	}

	public void close() throws IOException {
		this.BW.close();
	}

	public void write(String str) throws IOException {
		this.BW.write(str);
	}

	public void newLine() throws IOException {
		this.BW.newLine();
	}

	public void flush() throws IOException {
		this.BW.flush();
	}
}
