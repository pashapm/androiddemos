package ru.jecklandin.cats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import ru.jecklandin.cats.*;

public class PipesDemo extends Activity { 
	
	public static final String TAG = "ru.jecklandin.cats.PipesDemo";
	
	static {
		System.loadLibrary("pipes");
	} 
	
	private native int mkfifo(String pipe);
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final TextView disp = new TextView(this);
		disp.setText("0");
		setContentView(disp);
		 
		final String pipename = getDir("pipedemo", Context.MODE_WORLD_WRITEABLE).getAbsolutePath() + "/pipe";
		final File pipe = new File(pipename);
		
		new AsyncTask<Void, Integer, Integer>() {

			@Override
			protected void onProgressUpdate(Integer... values) {
				disp.setText(""+values[0]);
			};
			 
			protected Integer doInBackground(Void... params) {
				
				//create a pipe
				if (mkfifo(pipename) == -1) {
					Log.d(TAG, "Pipe error");
					return -1;
				} 
				
				FileInputStream fis;
				try {
					fis = new FileInputStream(pipe);
					int res = 0;
					while (res != -1) { //blocks until someone write to this pipe
						res = fis.read();
						publishProgress(res);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				return 0;
			}
		}.execute();
		
		Intent i = new Intent(this, ProcessingService.class);
		i.putExtra("fn", pipename);
		startService(i);
    }
}