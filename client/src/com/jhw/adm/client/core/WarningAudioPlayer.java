package com.jhw.adm.client.core;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
 * For deal with warning audio
 * Amend 2010.06.10
 */
@Component(WarningAudioPlayer.ID)
public class WarningAudioPlayer {

	private AudioInputStream ais;
	private SourceDataLine sDLine;
	private AudioFormat baseFormat;
	private static final int BUFFER_SIZE = 4000 * 4;
	public static final String ID = "warningAudioPlayer";
	// Amend 2010.06.12,新增播放状态，初始值为false,表示未播放，true，表示正在播放
	public static boolean playStatus = false;
	private static final Logger log = LoggerFactory
			.getLogger(WarningAudioPlayer.class);

	// get audioInputSteam
	private AudioInputStream getDataSource(URL fileURL)
			throws UnsupportedAudioFileException, IOException {

		if (null == fileURL) {
			return null;
		}
//		File file = new File(fileURL);
//		// // AudioInputStream ais = AudioSystem.getAudioInputStream(new
//		// File(fileURL));
//		AudioInputStream ais = AudioSystem.getAudioInputStream(file);
//		// InputStream is = new FileInputStream(file);
//		// AudioInputStream ais = AudioSystem.getAudioInputStream(is);
		
		AudioInputStream ais = AudioSystem.getAudioInputStream(fileURL);
		
		return ais;
	}

	// make data readable
	private SourceDataLine getLine(AudioFormat audioFormat) {
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,
				audioFormat);
		try {
			res = (SourceDataLine) AudioSystem.getLine(info);
			res.open(audioFormat);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	// play warning audio
	public void play(int repeatNum, URL fileURL, boolean isLoopPlay) {
		playThread = new WarningAudioPlayThread(repeatNum, fileURL, isLoopPlay);
		playThread.run();
	}

	public void play(URL fileURL) {
		this.play(0, fileURL, true);
	}

	// close audioInputStream
	public void stop() {
		if (null != ais) {
			try {
				ais.close();
			} catch (IOException e) {
				log.error("when stop play audio,close audioInputStream error",
						e);
			} finally {
				ais = null;
			}
		}
	}

	public static void main(String args[])
			throws UnsupportedAudioFileException, LineUnavailableException,
			IOException {
//		WarningAudioPlayer audioPlay = new WarningAudioPlayer();
//		URL flieURL = new URL("D:/ADMClient/client/src/com/jhw/adm/client/resources/sound/warning.WAV");
//		audioPlay.play(3, flieURL, true);
	}

	private WarningAudioPlayThread playThread;

	private class WarningAudioPlayThread implements Runnable {

		@SuppressWarnings("unused")
		public WarningAudioPlayThread() {
		}

		private int repeatNum = 0;
//		private String fileURL = "";
		private URL fileURL = null;
		private boolean isLoopPlay;

		public WarningAudioPlayThread(int repeatNum, URL fileURL,
				boolean isLoopPlay) {
			this.repeatNum = repeatNum;
			this.fileURL = fileURL;
			this.isLoopPlay = isLoopPlay;
		}

		@Override
		public void run() {
			if (isLoopPlay) {
				try {
					while (true)// always play
					{
						ais = getDataSource(fileURL);
						if (null == ais) {
							return;
						}

						baseFormat = ais.getFormat();

						sDLine = getLine(baseFormat);
						sDLine.start();
						int inBytes = 0;
						byte[] audioData = new byte[BUFFER_SIZE];
						while (inBytes != -1) {
							// when an audio is playing,stop it
							if (null == ais) {
								return;
							}
							inBytes = ais.read(audioData, 0, BUFFER_SIZE);
							if (inBytes >= 0) {
								sDLine.write(audioData, 0, inBytes);
							}
						}
					}
				} catch (UnsupportedAudioFileException e) {
					log.error("Play warning audio error", e);
				} catch (IOException e) {
					log.error("Play warning audio error", e);
				} finally {
					playStatus = false;
					if (null != ais) {
						try {
							ais.close();
						} catch (IOException e) {
							log.error("close audioInputStream error", e);
						}
					}
					if (null != sDLine) {
						sDLine.close();
					}
				}
			} else {
				try {
					for (int i = 0; i < repeatNum; i++) {
						ais = getDataSource(fileURL);
						if (null == ais) {
							return;
						}

						baseFormat = ais.getFormat();

						sDLine = getLine(baseFormat);
						if (sDLine == null) {
							return;
						}
						sDLine.start();
						int inBytes = 0;
						byte[] audioData = new byte[BUFFER_SIZE];
						while (inBytes != -1) {
							// when an audio is playing,stop it
							if (null == ais) {
								return;
							}
							inBytes = ais.read(audioData, 0, BUFFER_SIZE);
							if (inBytes >= 0) {
								sDLine.write(audioData, 0, inBytes);
							}
						}
					}
				} catch (UnsupportedAudioFileException e) {
					log.error("Play warning audio error", e);
				} catch (IOException e) {
					log.error("Play warning audio error", e);
				} finally {
					playStatus = false;
					if (null != ais) {
						try {
							ais.close();
						} catch (IOException e) {
							log.error("close audioInputStream error", e);
						}
					}
					if (null != sDLine) {
						sDLine.close();
					}
				}
			}
		}
	}
}
