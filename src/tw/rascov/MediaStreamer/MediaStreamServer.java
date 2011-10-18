package tw.rascov.MediaStreamer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MediaStreamServer {
	static final int frequency = 44100;
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    boolean isRecording;
    int recBufSize;
    ServerSocket sockfd;
    Socket connfd;
	AudioRecord audioRecord;
	
	public MediaStreamServer(final int port) {
		recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recBufSize);

		new Thread() {
			byte[] buffer = new byte[recBufSize];
			public void run() {
				try {
					sockfd = new ServerSocket(port);
					connfd = sockfd.accept();
				}
				catch (IOException e) { e.printStackTrace(); }
		        audioRecord.startRecording();
		        isRecording = true;
		        while (isRecording) {  
		            int readSize = audioRecord.read(buffer, 0, recBufSize);
		            try { connfd.getOutputStream().write(buffer, 0, readSize); }
		            catch (IOException e) { e.printStackTrace(); }
		        }  
		        audioRecord.stop();
			}
		}.start();
	}
	
	public void stop() {
		isRecording = false;
	}
}