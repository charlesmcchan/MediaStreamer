package tw.rascov.MediaStreamer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class MediaStreamClient {
	static final int frequency = 44100;
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    boolean isPlaying;
	int playBufSize;
    ServerSocket sockfd;
    Socket connfd;
	AudioTrack audioTrack;
	
	public MediaStreamClient(final String ip, final int port) {
		playBufSize=AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfiguration, audioEncoding, playBufSize, AudioTrack.MODE_STREAM);
		audioTrack.setStereoVolume(0.5f, 0.5f);
		
		new Thread() {
			byte[] buffer = new byte[playBufSize];
			public void run() {
				try {
					connfd = new Socket(ip, port);
				}
				catch (IOException e) { e.printStackTrace(); }
				audioTrack.play();
				isPlaying = true;
		        while (isPlaying) {
		        	int readSize = 0;
		            try { readSize = connfd.getInputStream().read(buffer); }
		            catch (IOException e) { e.printStackTrace(); }
		        	audioTrack.write(buffer, 0, readSize);
		        }  
		        audioTrack.stop();
			}
		}.start();
	}
	
	public void stop() {
		isPlaying = false;
	}
	
	public void setVolume(float lvol, float rvol) {
		audioTrack.setStereoVolume(lvol, rvol);
	}
}