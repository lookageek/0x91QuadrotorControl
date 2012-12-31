package com.koka.quadx91;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;


/*public class BtSerialInter implements Runnable {
	serial UUID "00001101-0000-1000-8000-00805F9B34FB"
	private BluetoothAdapter btadap =  BluetoothAdapter.getDefaultAdapter();
	private BluetoothDevice btdev;
	private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothSocket mSocket;
public void run()
{
	Set<BluetoothDevice> btset =  btadap.getBondedDevices();
	for(BluetoothDevice btdeviter : btset)
	{
		if(btdeviter.getName()=="Quadrator1")
		{
			btdev = btdeviter;
			break;
		}
	}
	btdev.
			
	
	
	}
	

}*/
public class BtSerialInter implements Runnable {
private Context ctx;

public final static String VERSION = "##version##";

/* Bluetooth */
private BluetoothAdapter mAdapter;
private BluetoothDevice mDevice;
private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

/* Socket & streams for BT communication */
private BluetoothSocket mSocket;
private ConnectedThread mConnectedThread;
private boolean connected = false;

/* Buffer */
private int bufferlength = 128;
private int available = 0;
private byte[] buffer;
private byte[] rawbuffer;

/* Debug variables */
public static boolean DEBUG = false;
public static String DEBUGTAG = "##name## ##version## Debug message: ";


public BtSerialInter(Context ctx) {
	this.ctx = ctx;
	welcome();
	mAdapter = BluetoothAdapter.getDefaultAdapter();
	/* Init the adapter */
	//new Handler(Looper.getMainLooper()).post(new Runnable() {
		//@Override
		//public void run() {
			//mAdapter = BluetoothAdapter.getDefaultAdapter();
		//}
	//});
}

/**
 * Returns the status of the connection.
 * 
 * @return
 */
public boolean isConnected() {
	return connected;
}

/**
 * Returns whether the adapter is enabled.
 * 
 * @return
 */
public boolean isEnabled() {
	if (mAdapter!=null) 
		return mAdapter.isEnabled();
	else return false;
}

/**
 * Returns the list of bonded devices.
 * 
 * @return
 */


public String[] list() {
	Vector<String> list = new Vector<String>();
	Set<BluetoothDevice> devices;

	try {
		devices = mAdapter.getBondedDevices();
		// convert the devices 'set' into an array so that we can
		// perform string functions on it
		Object[] deviceArray = devices.toArray();
		// step through it and assign each device in turn to
		// remoteDevice and then print it's name
		for (int i = 0; i < devices.size(); i++) {
			BluetoothDevice thisDevice = mAdapter
			.getRemoteDevice(deviceArray[i].toString());
			list.addElement(thisDevice.getAddress());
		}
	} catch (UnsatisfiedLinkError e) {
		// errorMessage("devices", e);
	} catch (Exception e) {
		// errorMessage("devices", e);
	}

	String outgoing[] = new String[list.size()];
	list.copyInto(outgoing);
	return outgoing;
}

/*
 * Some stubs for future implementation:
 * 
 */
public void startDiscovery() {
	// this method will start a separate thread to handle discovery
}

public void pairWith(String thisAddress) {
	// this method will pair with a device given a MAC address
}
public boolean discoveryComplete() {
	// this method will return whether discovery is complete,
	// so the user can then list devices
	return false;
}


public String getName() {
	if (mDevice !=null) return mDevice.getName();
	else return "no device connected";
}


public synchronized boolean connect(String mac) {
	/* Before we connect, make sure to cancel any discovery! */
	if (mAdapter.isDiscovering()) {
		mAdapter.cancelDiscovery();
		if (DEBUG) Log.i("System.out", "Cancelled ongoing discovery");
	}

	/* Make sure we're using a real bluetooth address to connect with */
	if (BluetoothAdapter.checkBluetoothAddress(mac)) {
		/* Get the remote device we're trying to connect to */
		mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
		/* Create the RFCOMM sockets */
		try {
			mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
			mSocket.connect();



			// Start the thread to manage the connection and perform transmissions
			mConnectedThread = new ConnectedThread(mSocket, bufferlength);
			mConnectedThread.start();

			if (DEBUG) Log.i("System.out", "Connected to device " + mDevice.getName()
					+ " [" + mDevice.getAddress() + "]");
			// Set the status 
			connected = true;
			return connected;
		} catch (IOException e) {
			Log.i("System.out", "Couldn't get a connection");
			connected = false;
			return connected;
		}

	} else {

		if (DEBUG) Log.i("System.out", "Address is not Bluetooth, please verify MAC.");
		connected = false;
		return connected;
	}
}

/**
 * Returns the available number of bytes in the buffer.
 * 
 * @return
 */
public int available() {
	return mConnectedThread.available();
}

/**
 * 
 */
private void welcome() {
	System.out.println("##name## ##version## by ##author##");
}

/**
 * return the version of the library.
 * 
 * @return String
 */
public static String version() {
	return VERSION;
}

public void run() {
	/* Init the buffer */
	buffer = new byte[bufferlength];
	rawbuffer = new byte[bufferlength];

	/* Set the connected state */
	connected = true;

	while (connected) {
		/* Read the available bytes into the buffer */
		rawbuffer= mConnectedThread.read();
		//available = mConnectedThread.available();
		/* Clone the raw buffer */
		buffer = rawbuffer.clone();
	}
}

/**
 * Writes a byte[] buffer to the output stream.
 * 
 * @param buffer
 */
public  void write(byte[] buffer) {
	// Create temporary object
	ConnectedThread r;
	// Synchronize a copy of the ConnectedThread
	synchronized (this) {
		if (!connected) return;
		r = mConnectedThread;
	}
	// Perform the write unsynchronized
	r.write(buffer);
}

/**
 * Writes a  String to the output stream.
 * 
 * @param thisString
 */
public  void write(String thisString) {
	byte[] thisBuffer = thisString.getBytes();
	write(thisBuffer);
}

/**
 * Writes a  int to the output stream.
 * 
 * @param buffer
 */
public  void write(int thisInt) {
	byte[] thisBuffer = {(byte)thisInt};
	write(thisBuffer);
}
/**
 * Returns the first available byte in "buffer" and then removes it.
 * 
 * @return
 */
private int readByte() {
	return mConnectedThread.readByte();
}

/**
 * Returns the next byte in the buffer as an int (0-255);
 * 
 * @return
 */
public int read() {
	return readByte();
}

/**
 * Returns the whole byte buffer.
 * 
 * @return
 */
public byte[] readBytes() {
	return mConnectedThread.read();
}

/**
 * Returns the available number of bytes in the buffer, and copies the
 * buffer contents to the passed byte[]
 * 
 * @param buffer
 * @return
 */
public int readBytes(byte[] buffer) {
	buffer = mConnectedThread.read().clone();
	return mConnectedThread.available();
}

/**
 * Returns a bytebuffer until the byte b. If the byte b doesn't exist in the
 * current buffer, null is returned.
 * 
 * @param b
 * @return
 */
public byte[] readBytesUntil(byte b) {
	/* Read the buffer until the value 'b' is found */
	for (int i = 0; i < buffer.length; i++) {
		if (buffer[i] == b) {
			/* Found the byte, buffer until this index, and return */
			byte[] returnbuffer = new byte[i];
			/* Populate the returnbuffer */
			for (int j = 0; j < returnbuffer.length; j++) {
				returnbuffer[j] = buffer[j];
			}

			/* Return buffer */
			return returnbuffer;
		}
	}
	return null;
}

/**
 * TODO
 * 
 * @param b
 * @param buffer
 */
public void readBytesUntil(byte b, byte[] buffer) {
	if (DEBUG) Log.i("System.out", "Will do a.s.a.p.");
}

/**
 * Returns the next byte in the buffer as a char, if nothing is there it
 * returns -1.
 * 
 * @return
 */
public char readChar() {
	return (char) readByte();
}

/**
 * Returns the buffer as a string.
 * 
 * @return
 */
public String readString() {
	String returnstring = new String(readBytes());
	return returnstring;
}

/**
 * Returns the buffer as string until character c.
 * 
 * @param c
 * @return
 */
public String readStringUntil(char c) {
	/* Get the buffer as string */
	String stringbuffer = readString();

	int index;
	/* Make sure that the character exists in the string */
	if ((index = stringbuffer.indexOf(c)) > 0) {
		return stringbuffer.substring(0, index);
	} else {
		return null;
	}
}

/**
 * Sets the number of bytes to buffer.
 * 
 * @param bytes
 * @return
 */
public int buffer(int bytes) {
	bufferlength = bytes;

	buffer = new byte[bytes];
	rawbuffer = buffer.clone();

	return bytes;
}

/**
 * Returns the last byte in the buffer.
 * 
 * @return
 */
public int last() {
	return buffer[buffer.length - 1];
}

/**
 * Returns the last byte in the buffer as char.
 * 
 * @return
 */
public char lastChar() {
	return (char) buffer[buffer.length - 1];
}

/**
 * Clears the byte buffer.
 */
public void clear() {
	buffer = new byte[bufferlength];
	mConnectedThread.clear();
}

/**
 * Disconnects the bluetooth socket.
 * 

 */
public synchronized void disconnect() {
	if (connected) {
		try {
			// kill the connected thread if it's running:
			if (mConnectedThread != null) {
				mConnectedThread.cancel();
				mConnectedThread = null;
			}

			/* Close the socket */
			mSocket.close();

			/* Set the connected state */
			connected = false;
			/* If it successfully closes I guess we just return a success? */
			//return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			/* Otherwise we'll go ahead and say "no, this didn't work well!" */
			//return 1;
		}
	}
}

/**
 * Kills the main thread. Shouldn't stop when the connection disconnects.
 * 
 * @return
 */
public void stop() {

}
}
