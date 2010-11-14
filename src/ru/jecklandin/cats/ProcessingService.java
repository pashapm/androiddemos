package ru.jecklandin.cats;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ProcessingService extends Service {

	public static final String TAG = "ru.jecklandin.cats.ProcessingService";
	
	static {
		System.loadLibrary("pipes");
	}
	
	private native int process(String pipe);
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		final String pipename = intent.getStringExtra("fn");
		new Thread(new Runnable() {
			
			@Override
			public void run() { 
				process(pipename);
			}
		}).start();
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
