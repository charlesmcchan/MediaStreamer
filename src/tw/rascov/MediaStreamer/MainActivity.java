package tw.rascov.MediaStreamer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class MainActivity extends Activity {
	EditText editText1, editText2;
	Button button1;
	SeekBar volume;
	String ip;
	int port;
    MediaStreamServer mss;
    MediaStreamClient msc;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // initialize layout variables        
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        button1 = (Button) findViewById(R.id.button1);
        volume = (SeekBar) findViewById(R.id.volume);
        volume.setMax(100);
        volume.setProgress(50); 
        ip = editText1.getText().toString();
        port = Integer.valueOf(editText2.getText().toString());
        
        button1.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(button1.getText().toString().equals("Start")) {
					button1.setText("Stop");
					if(ip.equals("127.0.0.1") || ip.equals("0.0.0.0")) {
						mss = new MediaStreamServer(port);
					}
					if(!ip.equals("0.0.0.0")) {
						msc = new MediaStreamClient(ip, port);
					}
				}
				else if(button1.getText().toString().equals("Stop")) {
					button1.setText("Start");
					mss.stop();
					msc.stop();
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
    }
}