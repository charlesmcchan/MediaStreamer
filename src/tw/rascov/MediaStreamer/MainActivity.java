package tw.rascov.MediaStreamer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	EditText editText1, editText2;
	Button button1;
	TextView textView1;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // initialize layout variables        
        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        button1 = (Button) findViewById(R.id.button1);
        textView1 = (TextView) findViewById(R.id.textView1);
        
        final String ip = editText1.getText().toString();
        final int port = Integer.valueOf(editText2.getText().toString());
        
        button1.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(ip.equals("127.0.0.1")) {
					new MediaStreamServer(port);
					new MediaStreamClient(ip, port);
				}
				if(ip.equals("0.0.0.0")) new MediaStreamServer(port);
				else new MediaStreamClient(ip, port);
			}
		});
        
    }
}