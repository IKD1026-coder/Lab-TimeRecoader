package library.file.audio;

import java.io.File;

public interface SoundData{
	int Gettime();
	void Start(int Time);
	void Stop();
	void SetPosition(int ms);
	int GetPosition();
	void setFile(File file);
	void setThread(boolean a);
	boolean getThread();
}

