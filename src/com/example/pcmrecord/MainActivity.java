package com.example.pcmrecord;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private String TAG = "pcmrecord";
	private Button mStart;
	private Button mStop;
	private boolean mIsStarted = false;
	
	private static final int AUDIO_SAMPLE   = 44100;
	private static final int AUDIO_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int AUDIO_FORMAT   = AudioFormat.ENCODING_PCM_16BIT;	
	
	private int bufferSize = 0;
	private AudioRecord mAudioRecorder = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		bufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE, AUDIO_CHANNELS, AUDIO_FORMAT);
		Log.d(TAG, "bufferSize:" + bufferSize);
		
		mStart = (Button)findViewById(R.id.btnStart);
		mStop  = (Button)findViewById(R.id.btnStop);
	
		mStart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				start();
			}
			
		});
		
		mStop.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				stop();
			}
			
		});		
		
	}

	protected void start() {
		// TODO Auto-generated method stub
		mAudioRecorder = new AudioRecord(MediaRecorder.AudioSource.CAMCORDER, AUDIO_SAMPLE, AUDIO_CHANNELS,
				AUDIO_FORMAT, bufferSize);
		mAudioRecorder.startRecording();
		mIsStarted = true;
		
		mStart.setEnabled(false);
		mStop.setEnabled(true);
		
		new Thread(){

			public void run() {
				write2File();
			}
		}.start();		
		
	}

	 private void stop() {
		 
		 if (null != mAudioRecorder) {
			mStart.setEnabled(true);
			mStop.setEnabled(false);
			mIsStarted = false;	
		
			mAudioRecorder.stop();
			mAudioRecorder.release();
		
			mAudioRecorder = null;
		}
	}	

	private void write2File() {

		String path = "/mnt/sdcard/record1.pcm";
		  
		byte audioData[] = new byte[bufferSize];
		
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		while (mIsStarted) {

			mAudioRecorder.read(audioData, 0, bufferSize);
				try {
				os.write(audioData, 0, bufferSize);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		   try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 
	 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
