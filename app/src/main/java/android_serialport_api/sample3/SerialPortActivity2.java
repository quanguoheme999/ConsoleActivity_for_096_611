/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api.sample3;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android_serialport_api.SerialPort;

public abstract class SerialPortActivity2 extends Activity {



	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	protected ReadThread mReadThread;
	//private static final String TAG = "SerialPortActivity2";
	private int n = 0;
	private SerialPort mSerialPort = null;

	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Read serial port parameters */
			//SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
			//String path ="/dev/ttyS1"; //;HdxUtil.GetPrinterPort();;//dev/ttyS3
			//String path = "/dev/ttyS3";
			String path =  "/dev/ttyS3";

			path =  "/dev/ttyS3";  //116是 3
			Log.d("rj11","rj11 =="+path);
			int baudrate = 9600;//Integer.decode(sp.getString("BAUDRATE", "-1"));

			/* Open the serial port */
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
	class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while(!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[64];
					
					if (mInputStream == null) return;
					size = mInputStream.read(buffer);
					if (size > 0) {
						onDataReceived(buffer, size,n);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			mSerialPort = getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
		} catch (SecurityException e) {
			//DisplayError(R.string.error_security);
		} catch (IOException e) {
			//DisplayError(R.string.error_unknown);
		} catch (InvalidParameterException e) {
			//DisplayError(R.string.error_configuration);
		}
	}

	protected abstract void onDataReceived(final byte[] buffer, final int size,final int n);

	@Override
	protected void onDestroy() {
		if (mReadThread != null)
			mReadThread.interrupt();
		 closeSerialPort();
		mSerialPort = null;
		try
		{
			mOutputStream.close();
			mInputStream.close();
		} catch (IOException e) {
		}
		super.onDestroy();
	}
}
