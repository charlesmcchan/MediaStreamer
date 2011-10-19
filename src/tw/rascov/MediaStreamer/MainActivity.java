package tw.rascov.MediaStreamer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
	EditText editText1, editText2;
	Button button1;
	SeekBar volume;
	TextView textView1;
	String ip;
	int port;
    MediaStreamServer mss;
    MediaStreamClient msc;
       
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        // initialize layout variables        
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        button1 = (Button) findViewById(R.id.button1);
        volume = (SeekBar) findViewById(R.id.volume);
        volume.setMax(100);
        volume.setProgress(20); 
        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.append("Current IP: "+getLocalIpAddress()+"\n");

        button1.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(button1.getText().toString().equals("Start")) {
					button1.setText("Stop");
			        ip = editText1.getText().toString();
			        port = Integer.valueOf(editText2.getText().toString());
					if(ip.equals("127.0.0.1") || ip.equals("0.0.0.0")) {
						textView1.append("Starting server\n");
						mss = new MediaStreamServer(MainActivity.this, port);
					}
					if(!ip.equals("0.0.0.0")) {
						textView1.append("Starting client, " + ip + ":" + port + "\n");
						msc = new MediaStreamClient(MainActivity.this, ip, port);
					}
				}
				else if(button1.getText().toString().equals("Stop")) {
					button1.setText("Start");
					if(mss!=null) {
						textView1.append("Stopping server\n");
						mss.stop();
					}
					if(msc!=null) {
						textView1.append("Stopping client\n");
						msc.stop();	
					}
				}
			}
		});
        
        volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				float vol = (float)(arg0.getProgress())/(float)(arg0.getMax());  
                if(msc!=null) msc.setVolume(vol, vol);
			} 
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {}
        });
        
        BroadcastReceiver receiver = new BroadcastReceiver() {
    		public void onReceive(Context context, Intent intent) {
    			if(intent.getAction().equals("tw.rascov.MediaStreamer.ERROR")) {
    				textView1.append("Error: " + intent.getStringExtra("msg") + "\n");
    				button1.setText("Start");
    			}
    		}
    	};
    	IntentFilter filter = new IntentFilter();
        filter.addAction("tw.rascov.MediaStreamer.ERROR");
        registerReceiver(receiver, filter);
    }
    
    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }
        catch (SocketException e) { e.printStackTrace(); }
        return null;
    }
}