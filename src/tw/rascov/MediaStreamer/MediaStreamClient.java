package tw.rascov.MediaStreamer;

import java.net.Socket;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class MediaStreamClient {
	static final int frequency = 44100;
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    boolean isPlaying;
	int playBufSize;
    Socket connfd;
	AudioTrack audioTrack;
	
	public MediaStreamClient(final Context ctx, final String ip, final int port) {
		playBufSize=AudioTrack.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		audioTrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, frequency, channelConfiguration, audioEncoding, playBufSize, AudioTrack.MODE_STREAM);
		audioTrack.setStereoVolume(1f, 1f);
		
		new Thread() {
			byte[] buffer = new byte[playBufSize];
			public void run() {
				try { connfd = new Socket(ip, port); }
				catch (Exception e) {
					e.printStackTrace();
					Intent intent = new Intent()
						.setAction("tw.rascov.MediaStreamer.ERROR")
						.putExtra("msg", e.toString());
					ctx.sendBroadcast(intent);
					return;
				}
				audioTrack.play();
				isPlaying = true;
		        while (isPlaying) {
		        	int readSize = 0;
		            try { readSize = connfd.getInputStream().read(buffer); }
		            catch (Exception e) {
	            		e.printStackTrace();
						Intent intent = new Intent()
							.setAction("tw.rascov.MediaStreamer.ERROR")
							.putExtra("msg", e.toString());
						ctx.sendBroadcast(intent);
						break;
					}
		        	audioTrack.write(buffer, 0, readSize);
		        }  
		        audioTrack.stop();
				try { connfd.close(); }
				catch (Exception e) { e.printStackTrace(); }
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