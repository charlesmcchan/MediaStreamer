package tw.rascov.MediaStreamer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MediaStreamServer {
	static final int frequency = 44100;
    static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    boolean isRecording;
    int recBufSize;
    //ServerSocket sockfd;
    //Socket connfd;
    DatagramSocket udpsock;
	AudioRecord audioRecord;
	
	public MediaStreamServer(final Context ctx, final int port) {
		recBufSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, recBufSize);
		
		try { udpsock = new DatagramSocket(port); }
		catch (Exception e) {
			e.printStackTrace();
			Intent intent = new Intent()
				.setAction("tw.rascov.MediaStreamer.ERROR")
				.putExtra("msg", e.toString());
			ctx.sendBroadcast(intent);
			return;
		}
		
		/*
		try { sockfd = new ServerSocket(port); }
		catch (Exception e) {
			e.printStackTrace();
			Intent intent = new Intent()
				.setAction("tw.rascov.MediaStreamer.ERROR")
				.putExtra("msg", e.toString());
			ctx.sendBroadcast(intent);
			return;
		}
		*/
		
		new Thread() {
			byte[] buffer = new byte[recBufSize];
			public void run() {
				/*
				try { connfd = sockfd.accept(); }
				catch (Exception e) {
					e.printStackTrace();
					Intent intent = new Intent()
						.setAction("tw.rascov.MediaStreamer.ERROR")
						.putExtra("msg", e.toString());
					ctx.sendBroadcast(intent);
					return;
				}
				*/
				DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
				try { udpsock.receive(receivePacket); }
				catch (Exception e) {
					e.printStackTrace();
					Intent intent = new Intent()
						.setAction("tw.rascov.MediaStreamer.ERROR")
						.putExtra("msg", e.toString());
					ctx.sendBroadcast(intent);
					return;
				}
				InetAddress peerip = receivePacket.getAddress();
                int peerport = receivePacket.getPort();
                
		        audioRecord.startRecording();
		        isRecording = true;
		        while (isRecording && peerip!=null) {  
		            int readSize = audioRecord.read(buffer, 0, recBufSize);
		            try {
		            	//connfd.getOutputStream().write(buffer, 0, readSize);
		            	DatagramPacket sendPacket = new DatagramPacket(buffer, readSize, peerip, peerport);
		                udpsock.send(sendPacket);
		            }
		            catch (Exception e) {
						e.printStackTrace();
						Intent intent = new Intent()
							.setAction("tw.rascov.MediaStreamer.ERROR")
							.putExtra("msg", e.toString());
						ctx.sendBroadcast(intent);
						break;
					}
		        }  
		        audioRecord.stop();
				//try { connfd.close(); }
		        try { udpsock.close(); }
				catch (Exception e) { e.printStackTrace(); }
			}
		}.start();
	}
	
	public void stop() {
		isRecording = false;
		/*
		try { sockfd.close(); }
		catch (Exception e) { e.printStackTrace(); }
		*/
	}
}